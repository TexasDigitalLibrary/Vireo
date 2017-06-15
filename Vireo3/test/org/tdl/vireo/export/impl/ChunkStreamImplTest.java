package org.tdl.vireo.export.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import play.libs.F.Promise;
import play.test.UnitTest;

/**
 * Test the chunk stream.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ChunkStreamImplTest extends UnitTest{

	/**
	 * Test the simple static properties are set on a stream.
	 */
	@Test
	public void testSimpleProperties() {
		ChunkStreamImpl stream = new ChunkStreamImpl("type","disposition",100);
		
		assertEquals("type",stream.getContentType());
		assertEquals("disposition",stream.getContentDisposition());
		assertEquals(100,stream.bufferSize);
		
		stream.close();
	}
	
	/**
	 * Test the stream when the producer generates more than the consumer can
	 * consume.
	 */
	@Test
	public void testEagerProducer() throws IOException, InterruptedException, ExecutionException {
		
		
		ChunkStreamImpl stream = new ChunkStreamImpl("type","disposition",100);
		
		for (int i=0; i < 10; i++) {
			String chunk = "Chunk #"+i;
			stream.write(chunk.getBytes());
		}
		
		for (int i=0; i < 10; i++) {
			
			Promise<byte[]> nextChunk = stream.nextChunk();
			assertTrue(nextChunk.isDone());
			
			String chunk = new String(nextChunk.get());
			assertEquals("Chunk #"+i, chunk);
		}
		
		stream.close();
	}
	
	/**
	 * Test the stream when the consumer can handle more than the producer can produce.
	 * consume.
	 */
	@Test
	public void testEagerConsumer() throws IOException, InterruptedException, ExecutionException {
		
		
		ChunkStreamImpl stream = new ChunkStreamImpl("type","disposition",100);
		
		Promise<byte[]> nextChunk = stream.nextChunk();
		assertFalse(nextChunk.isDone());
		
		
		for (int i=0; i < 10; i++) {
			// Write a chunk.
			String chunkIn = "Chunk #"+i;
			stream.write(chunkIn.getBytes());
			
			// The previous promise should now be ready.
			assertTrue(nextChunk.isDone());
			String chunkOut = new String(nextChunk.get());
			assertEquals("Chunk #"+i, chunkOut);
			
			// Ask for the next one before it's ready.
			nextChunk = stream.nextChunk();
			assertFalse(nextChunk.isDone());
		}
		stream.close();
	}
	
	
}
