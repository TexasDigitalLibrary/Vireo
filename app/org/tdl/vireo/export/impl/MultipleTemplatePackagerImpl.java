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
import org.tdl.vireo.proquest.ProquestVocabularyRepository;
import org.tdl.vireo.services.StringVariableReplacement;

import play.Play;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

/**
 * Generic packager that uses the standard play templating system to generate
 * files. Unlike the TemplatePackagerImpl, this class is capable of generating
 * multiple "manifests", there's just called templates here. Each one is a
 * groovy template that can produce a file for the export.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Micah Cooper
 * @author Jeremy Huff
 */
public class MultipleTemplatePackagerImpl extends AbstractPackagerImpl {
	
	/* Spring injected paramaters */
	public Map<String,VirtualFile> templates = new HashMap<String,VirtualFile>();
	public String format = null;
	public String entryName = null;
	public List<AttachmentType> attachmentTypes = new ArrayList<AttachmentType>();
	public LinkedHashMap<String, Properties> attachmentAttributes = new LinkedHashMap<String, Properties>(); 
	public Map<String,Object> templateArguments = new HashMap<String,Object>();
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
	 * (REQUIRED) Configure the multiple templates that will be generated for
	 * this packager format. The map will contain file names, to template paths.
	 * 
	 * @param templates
	 *            A map of filenames, to templates paths.
	 */
	public void setTemplatePaths(Map<String, String> templates) {

		for (String name : templates.keySet()) {
			
			String templatePath = templates.get(name);
			VirtualFile templateFile = Play.getVirtualFile(templatePath);
			
			if ( templatePath == null || !templateFile.exists())
				throw new IllegalArgumentException("Template '"+templatePath+"' does not exist.");
			
			this.templates.put(name, templateFile);
		
			
			
		}
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
	public void setTemplateArguments(Map<String,Object> arguments) {
		templateArguments = arguments;
	}
	
	
	@Override
	public ExportPackage generatePackage(Submission submission) {

		// Check that we have everything that we need.
		if (submission == null || submission.getId() == null)
			throw new IllegalArgumentException("Unable to generate a package because the submission is null, or has not been persisted.");

		if (templates == null || templates.size() == 0)
			throw new IllegalStateException("Unable to generate package because no template file exists.");

		if (format == null)
			throw new IllegalStateException("Unable to generate package because no package format name has been defined.");

		try {
			
			//Set String Replacement Parameters
			Map<String, String> parameters = new HashMap<String, String>();
			parameters = StringVariableReplacement.setParameters(submission);
			
			//Customize Entry Name
			String entryNameTemplate = entryName;
			String customEntryName = StringVariableReplacement.applyParameterSubstitution(entryName, parameters);
			
			File pkg;
			
			//Check the package type set in the spring configuration.
			if(packageType.equals("zip")) {
			
				// Generate the package export directory
				pkg = File.createTempFile("template-export-", ".zip");
				
				FileOutputStream fos = new FileOutputStream(pkg);
				ZipOutputStream zos = new ZipOutputStream(fos);
				
				byte[] buf = new byte[1024];
				int len;
				
				// Generate each of the export files
				for (String name : templates.keySet()) {
					VirtualFile templateFile = templates.get(name);
					
					//customize after retrieving the template file
					name = StringVariableReplacement.applyParameterSubstitution(name, parameters);
					
					Map<String, Object> templateBinding = new HashMap<String,Object>();
					templateBinding.put("sub", submission);
					templateBinding.put("personRepo", personRepo);
					templateBinding.put("subRepo",subRepo);
					templateBinding.put("settingRepo",settingRepo);
					templateBinding.put("proquestRepo",proquestRepo);
					templateBinding.put("format", format);
					templateBinding.put("entryName", entryName);
					templateBinding.put("attachmentTypes", attachmentTypes);
					templateBinding.put("template",name);
					templateBinding.put("templates",templates);
					
					if (templateArguments != null)
						templateBinding.putAll(templateArguments);
					Template template = TemplateLoader.load(templateFile);
					String rendered = template.render(templateBinding);
						
		
					// Copy the manifest
					File outputFile = new File(name);
					FileUtils.writeStringToFile(outputFile, rendered);
					
					ZipEntry ze = new ZipEntry(outputFile.getName());
					zos.putNextEntry(ze);
					FileInputStream in = new FileInputStream(outputFile);
					
					while((len = in.read(buf)) > 0) {
						zos.write(buf, 0, len);
					}
					
					in.close();
					zos.closeEntry();
					
					outputFile.delete();
				}
				
				// Add all the attachments
				for (Attachment attachment : submission.getAttachments()) {
					// Do we include this type?
					if (!attachmentTypes.contains(attachment.getType()))
						continue;
	
					String shortFileName = attachment.getName().replaceAll("."+FilenameUtils.getExtension(attachment.getName()), "");
					
					String fileName = attachment.getName();
					
					if(attachmentAttributes.get(attachment.getType().name()).get("customName")!=null) {
						fileName = attachmentAttributes.get(attachment.getType().name()).get("customName")+"."+FilenameUtils.getExtension(attachment.getName());
						fileName = fileName.replace("{FILE_NAME}", shortFileName);
						fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
					}
					
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
					
					
					//cleaning up either temp directory or temp files 
					if(hasDir) {
						FileUtils.deleteDirectory(exportFile.getParentFile());
					} else {
						exportFile.delete();
					}
				}
				
				zos.close();
				fos.close();
				
			} else {
				
				// Generate the package export directory
				pkg = File.createTempFile("template-export-", ".dir");
				pkg.delete();
				pkg.mkdir();
				
				// Generate each of the export files
				for (String name : templates.keySet()) {
					VirtualFile templateFile = templates.get(name);
					
					Map<String, Object> templateBinding = new HashMap<String,Object>();
					templateBinding.put("sub", submission);
					templateBinding.put("personRepo", personRepo);
					templateBinding.put("subRepo",subRepo);
					templateBinding.put("settingRepo",settingRepo);
					templateBinding.put("proquestRepo",proquestRepo);
					templateBinding.put("format", format);
					templateBinding.put("entryName", entryName);
					templateBinding.put("attachmentTypes", attachmentTypes);
					templateBinding.put("template",name);
					templateBinding.put("templates",templates);
					
					if (templateArguments != null)
						templateBinding.putAll(templateArguments);
					Template template = TemplateLoader.load(templateFile);
					String rendered = template.render(templateBinding);
						
					name = StringVariableReplacement.applyParameterSubstitution(name, parameters);
		
					// Copy the manifest
					File outputFile = new File(pkg,name);
					FileUtils.writeStringToFile(outputFile, rendered);
				}
				
				// Add all the attachments
				for (Attachment attachment : submission.getAttachments()) {
					// Do we include this type?
					if (!attachmentTypes.contains(attachment.getType()))
						continue;
					
					/* The string substitution only works on items we can retrieve from the submission
					 *		so we have to get the file name for each attachment here in the attachment loop.
					 */
					String shortFileName = attachment.getName().replaceAll("."+FilenameUtils.getExtension(attachment.getName()), "");
					
					String fileName = attachment.getName(); 
					
					//customize Attachment Name 
					if(attachmentAttributes.get(attachment.getType().name()).get("customName")!=null) {
						fileName = attachmentAttributes.get(attachment.getType().name()).get("customName")+"."+FilenameUtils.getExtension(attachment.getName());
						fileName = fileName.replace("{FILE_NAME}", shortFileName);
						fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
					}						
					
					
					//Check for Custom Directory Structure
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
				}
				
			}//End for loop
			
			this.setEntryName(entryNameTemplate);
			
			// Create the actual package!
			return new TemplatePackage(submission, null, format, pkg, customEntryName);
			
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
