package org.tdl.vireo.export.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;

import play.Play;
import play.exceptions.TemplateNotFoundException;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

/**
 * Generic packager that uses a the standard play templating system to generate
 * manifests for packages. Each packaged produced will consist of a
 * manifest file, along with a series of files. This packaged is ziped together
 * into a single bundle ready for deposit.
 * 
 * The values that define what format, which files, etc, are all injected by
 * spring. This allows for many different package formats to be created by just
 * adding a new spring bean definition. See each of the injection methods below
 * for a description of the various injectable settings.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class FilePackagerImpl extends AbstractPackagerImpl {
	
	/* Spring injected paramaters */
	public List<AttachmentType> attachmentTypes = new ArrayList<AttachmentType>();
			
	/**
	 * (OPITONAL) Set the attachment types which will be included in the
	 * package. Since not all attachments should be deposited, this allows the
	 * package to filter which files to include. They must be the exact name
	 * (all uppercase) of types listed in the AttachmentType enum.
	 * 
	 * If no types are specified then no attachments will be included.
	 * 
	 * @param attachmentTypeNames
	 *            List of attachment types to include.
	 */
	public void setAttachmentTypeNames(List<String> attachmentTypeNames) {
		
		this.attachmentTypes = new ArrayList<AttachmentType>();
		if (attachmentTypeNames != null ) {
			for (String name : attachmentTypeNames) {
				AttachmentType type = AttachmentType.valueOf(name);
				this.attachmentTypes.add(type);
			}
		}
	}	
	
	@Override
	public ExportPackage generatePackage(Submission submission) {
		if (attachmentTypes.size() == 0 ) {
			throw new IllegalArgumentException("Unable to generate package because not attachment types have been defined.");
		}
		
		// Check that we have everything that we need.
		if (submission == null || submission.getId() == null)
			throw new IllegalArgumentException("Unable to generate a package because the submission is null, or has not been persisted.");
		
		try {			
			File pkg = File.createTempFile("template-export-", ".dir");
				
			// The package has more than one file, so export as a directory.
			pkg.delete();
			pkg.mkdir();
						
			// Add all the attachments
			for(Attachment attachment : submission.getAttachments())
			{
				// Do we include this type?
				if (!attachmentTypes.contains(attachment.getType()))
					continue;

				File exportFile = new File(pkg.getPath(),attachment.getName());
				FileUtils.copyFile(
						attachment.getFile(),
						exportFile
						);
			}
		
			// Create the actual package!
			return new FilePackage(submission, pkg);
			
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to generate package",ioe);
		}
	}
	
	
	/**
	 * The package interface.
	 * 
	 * This is the class that represents the actual package. It contains the
	 * file we've built along with some basic metadata.
	 * 
	 */
	public static class FilePackage implements ExportPackage {

		// Members
		public final Submission submission;
		public final File file;

		public FilePackage(Submission submission, File file) {
			this.submission = submission;
			this.file = file;
		}

		@Override
		public Submission getSubmission() {
			return submission;
		}
		
		@Override
		public String getMimeType() {
			return null;
		}

		@Override
		public String getFormat() {
			return "File System";
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public void delete() {
			if (file != null && file.exists()) {

				if (file.isDirectory()) {
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException ioe) {
						throw new RuntimeException("Unable to cleanup export package: " + file.getAbsolutePath(),ioe);
					}
				} else {
					file.delete();
				}

			}
		}

		/**
		 * If we do get garbage collected, delete the file resource.
		 */
		public void finalize() {
			delete();
		}

	}
	
}
