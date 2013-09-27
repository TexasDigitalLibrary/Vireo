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
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.types.resources.Files;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;
import org.tdl.vireo.services.StringVariableReplacement;

import play.Logger;
import play.Play;
import play.exceptions.TemplateNotFoundException;
import play.modules.spring.Spring;
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
public class TemplatePackagerImpl extends AbstractPackagerImpl {
		
	/* Spring injected paramaters */
	public VirtualFile templateFile = null;
	public String mimeType = null;
	public String format = null;
	public String manifestName = "mets.xml";
	public String entryName = null;
	public List<AttachmentType> attachmentTypes = new ArrayList<AttachmentType>();
	public LinkedHashMap<String, Properties> attachmentAttributes = new LinkedHashMap<String, Properties>();
	public Map<String,Object> templateArguments = null;
	public String packageType = "dir";
	
	// Repositories to be injected into template for convenience
	public PersonRepository personRepo;
	public SubmissionRepository subRepo;
	public SettingsRepository settingRepo;
	public ProquestVocabularyRepository proquestRepo;
	
	/**
	 * Inject the repository of people and their preferences.
	 * 
	 * @param personRepo
	 *            Person Repository
	 */
	public void setPersonRepository(PersonRepository personRepo) {
		this.personRepo = personRepo;
	}

	/**
	 * Inject the repository of submission and their related objects: committee
	 * members, attachments, custom action values.
	 * 
	 * @param subRepo
	 *            Submission Repository
	 */
	public void setSubmissionRepository(SubmissionRepository subRepo) {
		this.subRepo = subRepo;
	}

	/**
	 * Inject the repository of system-wide settings & configuration.
	 * 
	 * @param settingRepo
	 *            Settings Repository
	 */
	public void setSettingsRepository(SettingsRepository settingRepo) {
		this.settingRepo = settingRepo;
	}
		
	/**
	 * Inject the repository of proquest vocabulary.
	 * 
	 * @param proquestRepo
	 *            Proquest Vocabulary Repository
	 */
	public void setProquestVocabularyRepository(ProquestVocabularyRepository proquestRepo) {
		this.proquestRepo = proquestRepo;
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
	 * (OPTIONAL) Set the mimetype of the resulting package generated. For most
	 * cases the mimetype should be null, meaning that there are multiple files
	 * generated contained within a directory. However, if the packager format
	 * generates a single file, then the mimetype should be set to the type of
	 * that file. Such as "text/xml". By default if no mimetype is set, then
	 * null is assumed.
	 * 
	 * @param mimeType
	 *            The mime type of the package.
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
	public ExportPackage generatePackage(Submission submission) {
		
		// Check that we have everything that we need.
		if (submission == null || submission.getId() == null)
			throw new IllegalArgumentException("Unable to generate a package because the submission is null, or has not been persisted.");
		
		if (templateFile == null)
			throw new IllegalStateException("Unable to generate package because no template file exists.");
		
		if (manifestName == null)
			throw new IllegalStateException("Unable to generate package because no manifest name has been defined.");
		
		if (format == null)
			throw new IllegalStateException("Unable to generate package because no package format name has been defined.");
		
		try {
			
			// Set String Replacement Parameters
			Map<String, String> parameters = new HashMap<String, String>();
			parameters = StringVariableReplacement.setParameters(submission);
			
			// Customize Manifest Name
			String manifestNameTemplate = manifestName;
			manifestName = StringVariableReplacement.applyParameterSubstitution(manifestName, parameters);
			
			// Cusotmize Entry Name			
			String entryNameTemplate = entryName;
			String customEntryName = StringVariableReplacement.applyParameterSubstitution(entryName, parameters);			
			
			// Generate the manifest.
			Map<String, Object> templateBinding = new HashMap<String,Object>();
			templateBinding.put("sub", submission);
			templateBinding.put("personRepo", personRepo);
			templateBinding.put("subRepo",subRepo);
			templateBinding.put("settingRepo",settingRepo);
			templateBinding.put("proquestRepo",proquestRepo);
			templateBinding.put("packageType", packageType);						
			templateBinding.put("manifestName",manifestName);
			templateBinding.put("entryName", entryName);
			templateBinding.put("format", format);
			templateBinding.put("mimeType", mimeType);
			templateBinding.put("attachmentTypes", attachmentTypes);
			if (templateArguments != null)
				templateBinding.putAll(templateArguments);
			Template template = TemplateLoader.load(templateFile);
			String manifest = template.render(templateBinding);
			
			File pkg = null;
			
			//Check the package type set in the spring configuration.
			if(packageType.equals("zip")) {
					
					pkg = File.createTempFile("template-export-", ".zip");
					
					FileOutputStream fos = new FileOutputStream(pkg);
					ZipOutputStream zos = new ZipOutputStream(fos);
					
					// Copy the manifest
					File manifestFile = new File(manifestName);
					FileUtils.writeStringToFile(manifestFile, manifest);
					
					ZipEntry ze = new ZipEntry(manifestName);
					zos.putNextEntry(ze);
					FileInputStream in = new FileInputStream(manifestFile);
					
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						zos.write(buf, 0, len);						
					}
					
					in.close();
					zos.closeEntry();
					
					manifestFile.delete();
					
					if (attachmentTypes.size() > 0 ) {
						
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
								
								// Attachment Name Customization
								if(attachmentAttributes.get(attachment.getType().name()).get("customName")!=null) {
									fileName = attachmentAttributes.get(attachment.getType().name()).get("customName")+"."+FilenameUtils.getExtension(attachment.getName());									
									fileName = fileName.replace("{FILE_NAME}", shortFileName);
									fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
								}
								
								File exportFile = null;
								
								// Check for custom directory structure set in spring.
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
								ze = new ZipEntry(fileName);
								zos.putNextEntry(ze);
								in = new FileInputStream(exportFile);
								
								while ((len = in.read(buf)) > 0) {
									zos.write(buf, 0, len);
								}
								
								in.close();
								zos.closeEntry();
								
								//cleaning up either temp directory or temp files
								if(hasDir) {
									FileUtils.deleteDirectory(exportFile.getParentFile());
								} else {
									exportFile.delete();
								}				
						}
					
					zos.close();
					fos.close();
					
				} 
					
			} else {
				
				if (attachmentTypes.size() > 0 ) {
					
					// The package has more than one file, so export as a directory.
					pkg = File.createTempFile("template-export-", ".dir");
					pkg.delete();
					pkg.mkdir();

					// Copy the manifest
					File manifestFile = new File(pkg.getPath(),manifestName);
					FileUtils.writeStringToFile(manifestFile, manifest);

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
						
						// Customize Attachment Name
						if(attachmentAttributes.get(attachment.getType().name()).get("customName")!=null) {
							fileName = attachmentAttributes.get(attachment.getType().name()).get("customName")+"."+FilenameUtils.getExtension(attachment.getName());
							fileName = fileName.replace("{FILE_NAME}", shortFileName);
							fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
						}						
						
						// Check for Custom Directory Structure.
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
						
					} //End for loop
					
				} else {
					
					// There's only one file, so export as a single file.
					String extension = FilenameUtils.getExtension(manifestName);
					if (extension.length() > 0)
						extension = "."+extension;
					
					pkg = File.createTempFile("template-export", extension);
					FileUtils.writeStringToFile(pkg, manifest);
				}
			}
		
			// Reset Manifest and Entry Name Placeholders
			this.setManifestName(manifestNameTemplate);
			this.setEntryName(entryNameTemplate);
			
			// Create the actual package!
			return new TemplatePackage(submission, mimeType, format, pkg, customEntryName);
			
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
	public static class TemplatePackage implements ExportPackage {

		// Members
		public final Submission submission;
		public final String mimeType;
		public final String format;
		public final File file;
		public final String entryName;

		public TemplatePackage(Submission submission, String mimeType, String format, File file, String entryName) {
			this.submission = submission;
			this.mimeType = mimeType;
			this.format = format;
			this.file = file;
			this.entryName = entryName;
		}

		@Override
		public Submission getSubmission() {
			return submission;
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
