package org.tdl.vireo.export;

import play.libs.F.Promise;

/**
 * Chunk stream interface.
 * 
 * This is used by the export service (or someone else if needed) to stream an
 * export file directly to the client. We are generating a file dynamically and
 * as we the content is being built it is streamed to this chunkStream which is
 * then transmitted to the browser. This gets around the problem of where to do
 * store this file while it's being generated, with this interface you don't.
 * The user's browser will save it to their disk.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface ChunkStream {

	/**
	 * The "Content-Type" header both helps the webserver and the browser
	 * understand the material being streamed. Typically the mimetype of
	 * "application/zip" will be used.
	 * 
	 * @return The content type HTTP header.
	 */
	public String getContentType();

	/**
	 * The "Content-Disposition" header is a way to tell the browser that the
	 * data being sent should be saved to disk and not attempt to render it in
	 * the browser. The format of this field will be in
	 * "[type]; filename=[name]". The possible types are attachment, or inline.
	 * Attachments are saved to disk, while inline content is attempted to be
	 * rendered in the browser. If the attachment type is used, then an optional
	 * parameter may be supplied to suggest the filename for the data.
	 * 
	 * @return The content disposition HTTP header.
	 */
	public String getContentDisposition();

	/**
	 * @return True if the if the stream is open, or there are events waiting to
	 *         be consumed.
	 */
	public boolean hasNextChunk();

	/**
	 * @return A promise for the next event. When the next chunk is ready the
	 *         promise will report that it is finished, and then the caller can
	 *         get the data of the next chunk.
	 */
	public Promise<byte[]> nextChunk();

}
