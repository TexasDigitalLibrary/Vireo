package org.tdl.vireo.deposit.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.tdl.vireo.deposit.DepositPackage;
import org.tdl.vireo.deposit.Packager;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Submission;

import play.Play;
import play.exceptions.TemplateNotFoundException;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

/**
 * Generic pacakager that uses a the standard play templating system to generate
 * manifests for deposit packages. Each packaged produced will consist of a
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
public class TemplatePackagerImpl extends AbstractPackagerImpl {
	
	/* Spring injected paramaters */
	public VirtualFile templateFile = null;
	public String mimeType = "application/zip";
	public String format = null;
	public String manifestName = "mets.xml";
	public List<AttachmentType> attachmentTypes = null;
	public Map<String,Object> templateArguments = null;
	

	/**
	 * Construct a new template packager.
	 */
	public TemplatePackagerImpl() {
		attachmentTypes = new ArrayList<AttachmentType>();
		attachmentTypes.add(AttachmentType.PRIMARY);
		attachmentTypes.add(AttachmentType.SUPPLEMENTAL);
	}
	
	/**
	 * (REQUIRED) Set the template for generating the manifest file. This
	 * parameter is always required.
	 * 
	 * @param templatePath
	 *            The path (relative to the application's directory)
	 */
	public void setManifestTemplatePath(String templatePath) {
		VirtualFile templateFile = Play.getVirtualFile(templatePath);

		// Blow up at construction time if template not found.
		if (templateFile == null || !templateFile.exists()) 
            throw new TemplateNotFoundException(templatePath);
		
		this.templateFile = templateFile;
	}

	/**
	 * (OPTIONAL) Set the mimetype of the resulting package generated. Since, by
	 * definition this packager always compresses all the files together into a
	 * zip this should almost always be "application/zip". That value is the
	 * default and the parameter only needs to be injected if something else is
	 * desired.
	 * 
	 * @param mimeType
	 *            The mime type of the zip package.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * (REQUIRED) Set the format descriptor of this package. This will vary
	 * widely between package formats. It is typically a URL to an XML schema
	 * describing the format of the mainifest file. In vireo 1, the format was
	 * always "http://purl.org/net/sword-types/METSDSpaceSIP".
	 * 
	 * @param format 
	 * 			The format of the package generated.
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * (OPTIONAL) Set the name of the manifest file file. This will vary between
	 * package formats but some popular ones are "mets.xml", or "mods.xml". See
	 * the specific format to understand what the file is going to be called.
	 * 
	 * If no manifest name is set, then "mets.xml" will be used.
	 * 
	 * @param manifestName
	 *            The name of the manifest file.
	 */
	public void setManifestName(String manifestName) {
		this.manifestName = manifestName;
	}
	
	/**
	 * (OPITONAL) Set the attachment types which will be included in the
	 * package. Since not all attachments should be deposited, this allows the
	 * package to filter which files to include. They must be the exact name
	 * (all uppercase) of types listed in the AttachmentType enum.
	 * 
	 * If no types are specified then "PRIMARY", and "SUPPLEMENTAL" types will
	 * be included. If you do not want any files included in the package then
	 * pass an empty list.
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
	
	/**
	 * (OPTIONAL) Set a list of arguments which may be accessed as variables in
	 * the template syntax. The variable "sub" will always be the submission
	 * which is being packaged.
	 * 
	 * @param arguments
	 *            Template arguments
	 */
	public void setManifestTemplateArguments(Map<String,Object> arguments) {
		templateArguments = arguments;
	}
	
	
	@Override
	public DepositPackage generatePackage(Submission submission) throws IOException {
		
		// Check that we have everything that we need.
		if (submission == null || submission.getId() == null)
			throw new IllegalArgumentException("Unable to generate a package because the submission is null, or has not been persisted.");
		
		if (templateFile == null)
			throw new IllegalStateException("Unable to generate package because no template file exists.");
		
		if (manifestName == null)
			throw new IllegalStateException("Unable to generate package because no manifest name has been defined.");
		
		if (mimeType == null)
			throw new IllegalStateException("Unable to generate package because no MIME type name has been defined.");
		
		if (format == null)
			throw new IllegalStateException("Unable to generate package because no package format name has been defined.");
		
		// Generate the manifest.
		Map<String, Object> templateBinding = new HashMap<String,Object>();
		templateBinding.put("sub", submission);
		if (templateArguments != null)
			templateBinding.putAll(templateArguments);
		Template template = TemplateLoader.load(templateFile);
		String manifest = template.render(templateBinding);
				
		// Create a zip package
		File file = File.createTempFile("submission-"+submission.getId()+"-package-", ".zip");
		
		FileOutputStream fos = new FileOutputStream(file);
		ZipOutputStream zos  = new ZipOutputStream(fos);
		byte[] buffer = new byte[1024];
		int bufferLength;
		
		
		// Add our manifest as the first entry.
		zos.putNextEntry(new ZipEntry(manifestName));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(manifest.getBytes());
		while ((bufferLength = bais.read(buffer)) > 0) {
			zos.write(buffer,0,bufferLength);
		}
		zos.closeEntry();
		bais.close();
		
		
		// Add all the attachments
		for(Attachment attachment : submission.getAttachments())
		{
			// Do we include this type?
			if (!attachmentTypes.contains(attachment.getType()))
				continue;
			
			zos.putNextEntry(new ZipEntry(attachment.getName()));
			
			FileInputStream fis = new FileInputStream(attachment.getFile());
			while ((bufferLength = fis.read(buffer)) > 0) {
				zos.write(buffer,0,bufferLength);
			}
			
			zos.closeEntry();
			fis.close();
		}
		
		// Close out all the resources
		zos.close();
		
		
		// Create the actual package!
		return new TemplatePackage(mimeType, format, file);
	}
	
	
	/**
	 * The package interface.
	 * 
	 * This is the class that represents the actual package. It contains the
	 * file we've built along with some basic metadata.
	 * 
	 */
	public static class TemplatePackage implements DepositPackage {

		// Members
		public final String mimeType;
		public final String format;
		public final File file;

		public TemplatePackage(String mimeType, String format, File file) {
			this.mimeType = mimeType;
			this.format = format;
			this.file = file;
		}

		@Override
		public String getMimeType() {
			return mimeType;
		}

		@Override
		public String getFormat() {
			return format;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public void delete() {
			if (file != null && file.exists())
				file.delete();
		}

		/**
		 * If we do get garbage collected, delete the file resource.
		 */
		public void finalize() {
			delete();
		}

	}
	
}
