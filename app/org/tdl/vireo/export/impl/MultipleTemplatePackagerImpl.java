package org.tdl.vireo.export.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;

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
 */
public class MultipleTemplatePackagerImpl extends AbstractPackagerImpl {
	
	/* Spring injected paramaters */
	public Map<String,VirtualFile> templates = new HashMap<String,VirtualFile>();
	public String format = null;
	public List<AttachmentType> attachmentTypes = new ArrayList<AttachmentType>();
	public Map<String,Object> templateArguments = new HashMap<String,Object>();
	
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
	 * (OPITONAL) Set the attachment types which will be included in the
	 * package. Since not all attachments should be deposited, this allows the
	 * package to filter which files to include. They must be the exact name
	 * (all uppercase) of types listed in the AttachmentType enum.
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
			// Generate the package export directory
			File pkg = File.createTempFile("template-export-", ".dir");
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
				templateBinding.put("attachmentTypes", attachmentTypes);
				templateBinding.put("template",name);
				templateBinding.put("templates",templates);
				
				if (templateArguments != null)
					templateBinding.putAll(templateArguments);
				Template template = TemplateLoader.load(templateFile);
				String rendered = template.render(templateBinding);
					
	
				// Copy the manifest
				File outputFile = new File(pkg,name);
				FileUtils.writeStringToFile(outputFile, rendered);
			}
			
			// Add all the attachments
			for (Attachment attachment : submission.getAttachments()) {
				// Do we include this type?
				if (!attachmentTypes.contains(attachment.getType()))
					continue;

				File exportFile = new File(pkg,attachment.getName());
				FileUtils.copyFile(attachment.getFile(), exportFile);
			}
			
			// Create the actual package!
			return new TemplatePackage(submission, null, format, pkg);
			
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

		public TemplatePackage(Submission submission, String mimeType, String format, File file) {
			this.submission = submission;
			this.mimeType = mimeType;
			this.format = format;
			this.file = file;
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
