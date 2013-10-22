package org.tdl.vireo.export;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.tdl.vireo.search.SearchFilter;

/**
 * Export service. This service's purpose is to contain all the nitty details of
 * generating an export. The client is only responsible for reading the
 * ChunkStream and writing the result to a file somewhere. The generation of the
 * export will occur on a background thread, but the caller needs to handle the
 * output generated. This probably means delievering it to the clients browser
 * using an algorithm like:
 * 
 * ChunkStream stream = exportService.export(package,filter);
 * 
 * response.contentType = stream.getContentType();
 * response.setHeader("Content-Disposition", stream.getContentDisposition());
 * 
 * while(stream.hasNextChunk()) { Promise<byte[]> nextChunk =
 * stream.nextChunk(); byte[] chunk = await(nextChunk);
 * response.writeChunk(chunk); }
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface ExportService {

	/**
	 * Generate an export.
	 * 
	 * Exports may take a substantial amount of time, and they are not stored on
	 * the server. So it is imperative that the caller handle the ChunkStream
	 * and send it to the user's browser.
	 * 
	 * @param packager
	 *            The packager format.
	 * @param filter
	 *            The filter to select submissions.
	 * @return A stream of the export.
	 */
	public ChunkStream export(Packager packager, SearchFilter filter);
	
}
