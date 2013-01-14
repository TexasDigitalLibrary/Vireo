package org.tdl.vireo.export.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.tdl.vireo.export.ChunkStream;

import play.libs.F.Promise;

/**
 * Okay, this is awesome!
 * 
 * This class is an output stream, that can produce play style promises for
 * chunked content. This means that anytime we want to do file IO we can use
 * this class to stream that file directly to the browser. Play is really
 * powerful.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ChunkStreamImpl extends OutputStream implements ChunkStream {

	// Parameters
	public final String contentType;
	public final String contentDisposition;
	
	// The maximum size of chunks.
	protected final int bufferSize; 
	
	// The queue of chunks
    protected final ConcurrentLinkedQueue<byte[]> chunks = new ConcurrentLinkedQueue<byte[]>();
    
    // A promise for the next chunk that arrives on the queue.
    protected Promise<byte[]> nextChunk;
    
    // Whether the stream is currently open or closed.
    protected boolean open;
   
	/**
	 * Construct a new chunk.
	 * 
	 * @param contentType
	 *            The content type, typically "application/zip".
	 * @param contentDisposition
	 *            The content disposition, typically
	 *            "attachment; filename=[name]"
	 * @param bufferSize
	 *            The maximum size of chunks to keep in the queue before
	 *            throtteling the producer.
	 */
	public ChunkStreamImpl(String contentType, String contentDisposition, int bufferSize) {
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
		this.bufferSize = bufferSize;
		this.open = true;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getContentDisposition() {
		return contentDisposition;
	}

	@Override
	public synchronized void close() {
		this.open = false;
		if (nextChunk != null) {
			nextChunk.invoke(new byte[0]);
		}
	}
	
	@Override
	public boolean hasNextChunk() {
		if (chunks.isEmpty())
			return open;
		else
			return !chunks.isEmpty();
	}
	
	/**
	 * One of two cases, here is what they mean:
	 * 
	 * 1) The queue is empty, but we're sure to get some more chunks pretty
	 * soon. So we return an unfulfilled promise for the next chunk. We store a
	 * pointer to this promise, so that when we do recieve the next chunk we can
	 * add the data to the promise. The consumer will see this and then grab the
	 * chunk's data.
	 * 
	 * 2) The queue has a back log of chunks. We just grab the next chunk and
	 * hand it over to the consumer.
	 * 
	 */
	@Override
    public synchronized Promise<byte[]> nextChunk() {
		
		if (nextChunk != null)
			throw new IllegalStateException("ChunkStream is consuming chunks without waiting for the previous chunk to be consumed.");
		
		if (chunks.isEmpty()) {
			// The queue of chunks is empty right now, so return a promise that
			// when we get the next one we'll send the chunk.

			nextChunk = new Promise<byte[]>();
			return nextChunk;
		} else {
			// There is a backlog in the queue, so send the chunk.

			Promise<byte[]> nextChunk = new Promise<byte[]>();
			nextChunk.invoke(chunks.remove());
			return nextChunk;
		}
    }

	@Override
	public void write(int b) throws IOException {
		
		byte[] chunk = new byte[1];
		chunk[0] = (byte) b;
		
		writeOrWait(chunk);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		
		byte[] chunk = new byte[b.length];
		System.arraycopy(b, 0, chunk, 0, b.length);
		
		writeOrWait(chunk);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		
		byte[] chunk = new byte[len];
		System.arraycopy(b, off, chunk, 0, len);
		
		writeOrWait(chunk);
	}
	
	@Override
	public void flush() {
		while(!chunks.isEmpty()) {
			Thread.yield();
		}
	}
	
	/**
	 * Our first internal method to write chunks into the stream. This method
	 * will attempt to write the chunk, but if it fails because the buffer is
	 * full then it will progressively back of publishing. This is to give the
	 * consumer time to catch up. However if after about one minute the consumer
	 * hasn't caught up then we assume there's a big problem and blow up with an
	 * IO Exception.
	 * 
	 * Note, this method is not synchronized so that it does not block. The
	 * lower level write method synchronizedWrite is.
	 * 
	 * @param event
	 *            The chunk to be written into the stream.
	 */
	protected void writeOrWait(byte[] event) throws IOException {
		for (int attempts = 0; attempts < 350; attempts++) {
			try {
				// First try to publish with out any overhead
				synchronizedWrite(event);
				return;
			} catch (BufferFullException bfe) {
				try {
					Thread.sleep(attempts);
				} catch (InterruptedException ie) {
					// wake up and try to publish again.
				}
			}
		}
		throw new IOException("ChunkStream buffer is full, the client probably disconnected.");
	}
	
	/**
	 * Internal method for writing chunks into the stream. No one but
	 * writeOrWait() should call this method. If the buffer is full, then an
	 * exception will be thrown for writeOrWait to catch and retry.
	 * 
	 * Assuming the buffer is fine, the new chunk is added to the queue. Then we
	 * check to see if there are any outstanding unfulfilled promise out there.
	 * If so, we send the next chunk to that promise so the consumer can now to
	 * start reading the stream again.
	 * 
	 * @param event
	 *            The new chunk to be written.
	 * @throws BufferFullException
	 *             When the buffer is full.
	 */
	private synchronized void synchronizedWrite(byte[] event) throws BufferFullException, IOException {
    	if (!open) 
    		throw new IOException("Unable to publish chunks into a closed ChunkStream.");
    	
        if (chunks.size() > bufferSize) {
			// The buffer so throw an exception that can be caught by the
			// nonsynchronized method.
            throw new BufferFullException();
        }
        chunks.offer(event);
        
		if (nextChunk != null) {
			// A promise already exists, so now that something has been
			// published into the queue let the consumer know about the new
			// chunk.
			nextChunk.invoke(chunks.remove());
			nextChunk = null;
		}
    }
	
	/**
	 * Exception to indicate that the buffer is full, and the producer should
	 * slow down.
	 */
	public static class BufferFullException extends Exception {
    }
}
