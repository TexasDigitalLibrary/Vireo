package org.tdl.vireo.export.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.services.StringVariableReplacement;

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
 * @author Micah Cooper
 * @author Jeremy Huff
 */
public class FilePackagerImpl extends AbstractPackagerImpl {
	
	/* Spring injected paramaters */
	public List<AttachmentType> attachmentTypes = new ArrayList<AttachmentType>();
	public LinkedHashMap<String, Properties> attachmentAttributes = new LinkedHashMap<String, Properties>();
	public String entryName;
	public String packageType = "dir";
	
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
	public void setAttachmentTypeNames(LinkedHashMap<String, Properties> attachmentTypeNames) {
			
		this.attachmentTypes = new ArrayList<AttachmentType>();
		this.attachmentAttributes = new LinkedHashMap<String, Properties>();
		
		if (attachmentTypeNames != null ) {
			this.attachmentAttributes = attachmentTypeNames;
			for (String name : attachmentTypeNames.keySet()) {
				AttachmentType type = AttachmentType.valueOf(name);
				this.attachmentTypes.add(type);
			}
		}
	}
	
	/**
	 * (OPTIONAL) Set the name of the entry. The default will be
	 * submission_{submission ID}, but this can be customized
	 * in application-context.xml.
	 * 
	 * @param entryName
	 * 			The name of the entry.
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
	
	/**
	 * (OPTIONAL) Inject the package type for the export.
	 * 
	 * @param packageType
	 * 			Package Type (directory, zip, etc)
	 */
	public void setPackageType(String packageType) {
		this.packageType = packageType;
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
			
			//Set String replacement parameters 
			Map<String, String> parameters = new HashMap<String, String>();
			parameters = StringVariableReplacement.setParameters(submission);
			
			//Customize Entry Name
			String entryNameTemplate = entryName;
			String customEntryName = StringVariableReplacement.applyParameterSubstitution(entryName, parameters);
			
			File pkg = null;
			
			//Check the package type set in the spring configuration
			if(packageType.equals("zip")) {
			
				pkg = File.createTempFile("template-export-", ".zip");
				
				FileOutputStream fos = new FileOutputStream(pkg);
				ZipOutputStream zos = new ZipOutputStream(fos);
				
				byte[] buf = new byte[1024];
				int len;
							
				// Add all the attachments
				for(Attachment attachment : submission.getAttachments())
				{
					// Do we include this type?
					if (!attachmentTypes.contains(attachment.getType()))
						continue;
					
					/* The string substitution only works on items we can retrieve from the submission
					 *		so we have to get the file name for each attachment here in the attachment loop.
					 */
					String shortFileName = attachment.getName().replaceAll("."+FilenameUtils.getExtension(attachment.getName()), "");
					
					String fileName = attachment.getName();
					
					//Attachment Name Customization
					if(attachmentAttributes.get(attachment.getType().name()).get("customName")!=null) {
						fileName = attachmentAttributes.get(attachment.getType().name()).get("customName")+"."+FilenameUtils.getExtension(attachment.getName());
						fileName = fileName.replace("{FILE_NAME}", shortFileName);
						fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
					}
					
					//Check fir custom directory structure set in spring
					File exportFile = null;
					Boolean hasDir = false;
					
					if(attachmentAttributes.get(attachment.getType().name()).get("directory")!=null) {
						String dirName = (String) attachmentAttributes.get(attachment.getType().name()).get("directory");
						dirName = dirName.replace("{FILE_NAME}", shortFileName);
						dirName = StringVariableReplacement.applyParameterSubstitution(dirName, parameters);
						exportFile = new File(dirName,fileName);
						fileName = dirName + fileName;
						hasDir = true;
					} else {
						exportFile = new File(fileName);
					}
													
					FileUtils.copyFile(
							attachment.getFile(),
							exportFile
							);
					ZipEntry ze = new ZipEntry(fileName);
					zos.putNextEntry(ze);
					FileInputStream in = new FileInputStream(exportFile);
					
					while ((len = in.read(buf)) > 0) {
						zos.write(buf, 0, len);
					}
					
					in.close();
					zos.closeEntry();
					
					if(hasDir) {
						FileUtils.deleteDirectory(exportFile.getParentFile());
					} else {
						exportFile.delete();
					}
				}
				
				zos.close();
				fos.close();
			
			} else {
				
				pkg = File.createTempFile("template-export-", ".dir");
				
				// The package has more than one file, so export as a directory.
				pkg.delete();
				pkg.mkdir();
							
				// Add all the attachments
				for(Attachment attachment : submission.getAttachments())
				{
					// Do we include this type?
					if (!attachmentTypes.contains(attachment.getType()))
						continue;
					
					/* The string substitution only works on items we can retrieve from the submission
					 *		so we have to get the file name for each attachment here in the attachment loop.
					 */
					String shortFileName = attachment.getName().replaceAll("."+FilenameUtils.getExtension(attachment.getName()), "");
					
					String fileName = attachment.getName(); 
					
					//Customize Attachment Name
					if(attachmentAttributes.get(attachment.getType().name()).get("customName")!=null) {
						fileName = attachmentAttributes.get(attachment.getType().name()).get("customName")+"."+FilenameUtils.getExtension(attachment.getName());
						fileName = fileName.replace("{FILE_NAME}", shortFileName);
						fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
					}						
					
					//Check for Custom Directory Structure. 
					String pkgPath = pkg.getPath();						
					
					if(attachmentAttributes.get(attachment.getType().name()).get("directory")!=null) {
						String dirName = (String) attachmentAttributes.get(attachment.getType().name()).get("directory");
						dirName = dirName.replace("{FILE_NAME}", shortFileName);
						dirName = StringVariableReplacement.applyParameterSubstitution(dirName, parameters);
						pkgPath = pkgPath + File.separator + dirName;
					}
						
					File exportFile = new File(pkgPath, fileName);
						
					FileUtils.copyFile(
						attachment.getFile(),
						exportFile
						);
				}//End for loop
				
			}
		
			this.setEntryName(entryNameTemplate);
			
			// Create the actual package!
			return new FilePackage(submission, pkg, customEntryName);
			
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
		public final String entryName;

		public FilePackage(Submission submission, File file, String entryName) {
			this.submission = submission;
			this.file = file;
			this.entryName = entryName;
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
		public String getEntryName() {
			return entryName;
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
