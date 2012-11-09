package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import play.Logger;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

/**
 * Submission settings
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class SubmissionSettingsTab extends SettingsTab {
	
	// Static variable to keep track of all the possible configuration settings.
	public static final List<String> allSettings = new ArrayList<String>();
	static {
		for (FieldConfig field : FieldConfig.values()) {
			allSettings.add(field.LABEL);
			allSettings.add(field.HELP);
			allSettings.add(field.ENABLED);			
		}
	}
	
	// Static variable to keep track of all sticky notes possible.
	public static final List<String> stickySettings = new ArrayList<String>();
	static {
		stickySettings.add(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
		stickySettings.add(AppConfig.SUBMIT_DOCUMENT_INFO_STICKIES);
		stickySettings.add(AppConfig.SUBMIT_UPLOAD_FILES_STICKIES);
	}
	
	/**
	 * Display the submission settings page.
	 */
	@Security(RoleType.MANAGER)
	public static void submissionSettings() throws IOException {
		
		renderArgs.put("PERSONAL_INFO_FIELDS",FieldConfig.PERSONAL_INFO_FIELDS);
		renderArgs.put("LICENSE_AGREEMENT_FIELDS",FieldConfig.LICENSE_AGREEMENT_FIELDS);
		renderArgs.put("DOCUMENT_INFO_FIELDS", FieldConfig.DOCUMENT_INFO_FIELDS);
		renderArgs.put("UPLOAD_FILES_FIELDS", FieldConfig.UPLOAD_FILES_FIELDS);
		
		renderArgs.put("SUBMIT_PERSONAL_INFO_STICKIES",AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
		renderArgs.put("SUBMIT_DOCUMENT_INFO_STICKIES",AppConfig.SUBMIT_DOCUMENT_INFO_STICKIES);
		renderArgs.put("SUBMIT_UPLOAD_FILES_STICKIES",AppConfig.SUBMIT_UPLOAD_FILES_STICKIES);
		
		
		List<String> personalInfoStickies = new ArrayList<String>();
		String personalInfoStickiesValue = settingRepo.getConfigValue(SUBMIT_PERSONAL_INFO_STICKIES);
		if (personalInfoStickiesValue != null && !"null".equals(personalInfoStickiesValue)) {
			CSVReader reader = new CSVReader(new StringReader(personalInfoStickiesValue));
			personalInfoStickies = Arrays.asList(reader.readNext());
			reader.close();
		}

		List<String> documentInfoStickies = new ArrayList<String>();
		String documentInfoStickiesValue = settingRepo.getConfigValue(SUBMIT_DOCUMENT_INFO_STICKIES);
		if (documentInfoStickiesValue != null && !"null".equals(documentInfoStickiesValue)) {
			CSVReader reader = new CSVReader(new StringReader(documentInfoStickiesValue));
			documentInfoStickies = Arrays.asList(reader.readNext());
			reader.close();
		}
		
		List<String> uploadFilesStickies = new ArrayList<String>();
		String uploadFilesStickiesValue = settingRepo.getConfigValue(SUBMIT_UPLOAD_FILES_STICKIES);
		if (uploadFilesStickiesValue != null && !"null".equals(uploadFilesStickiesValue)) {
			CSVReader reader = new CSVReader(new StringReader(uploadFilesStickiesValue));
			uploadFilesStickies = Arrays.asList(reader.readNext());
			reader.close();
		}
		
		String nav = "settings";
		String subNav = "submission";
		renderTemplate("SettingTabs/submissionSettings.html",nav, subNav, personalInfoStickies, documentInfoStickies, uploadFilesStickies);
	}
	
	/**
	 * Handle updating the individual values under the submission settings tab.
	 * 
	 * @param field
	 *            The field being updated.
	 * @param value
	 *            The value of the new field.
	 */
	@Security(RoleType.MANAGER)
	public static void updateSubmissionSettingsJSON(String field, String value) {

		try {
			
			if (allSettings.contains(field)) {
				
				Configuration config = settingRepo.findConfigurationByName(field);
				
				if (config == null && value != null) {
					// Create a new setting
					settingRepo.createConfiguration(field, value.trim()).save();
					
			    } else if (config != null && value == null) {
			    	// Delete an old setting
					config.delete();
					
				} else if (config != null && value != null) {
					// Update the setting's value
					config.setValue(value.trim());
					config.save();
				}
				
			} else {
				throw new IllegalArgumentException("Unknown field '"+field+"'");
			}
		
			field = escapeJavaScript(field);
			value = escapeJavaScript(value);
			
			
			renderJSON("{ \"success\": \"true\", \"field\": \""+field+"\", \"value\": \""+value+"\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to update submission settings");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
	
	/**
	 * Update a sticky note. Sticky notes are stored as an array for each page.
	 * So the particular note is identified by the page and an index. This
	 * method will take those values and update the individual sticky note.
	 * 
	 * @param field
	 *            The set of sticky notes.
	 * @param index
	 *            The position within the set.
	 * @param value
	 *            The new value of the sticky note, null means remove.
	 */
	@Security(RoleType.MANAGER)
	public static void updateStickySettingsJSON(String field, int index, String value) {
		
		if (value != null && value.trim().length() == 0)
			value = null;
				
		try {
			if (stickySettings.contains(field)) {
	
				String rawValue = settingRepo.getConfigValue(field);
				CSVReader reader = new CSVReader(new StringReader(rawValue));
				List<String> stickies = new ArrayList<String>(Arrays.asList(reader.readNext()));
				reader.close();
	
				if (stickies.size() > index) {
					// Update existing
					if (value != null)
						stickies.set(index, value);
					else
						stickies.remove(index);
				} else if (stickies.size() == index) {
					// Add new
					stickies.add(value);
				} else {
					throw new RuntimeException("Sticky index out of bounds");
				}
				
				String encoded = "null";
				if (stickies.size() > 0) {
					StringWriter out = new StringWriter();
					CSVWriter writer = new CSVWriter(out);
					writer.writeNext(stickies.toArray(new String[stickies.size()]));
					writer.close();
					encoded = out.toString();
				}
				
				Configuration config = settingRepo.findConfigurationByName(field);
				if (config == null) {
					config = settingRepo.createConfiguration(field, "");
				}
				config.setValue(encoded);
				config.save();
			} else {
				throw new IllegalArgumentException("Unknown field '" + field + "'");
			}
			
			
			field = escapeJavaScript(field);
			value = escapeJavaScript(value);
			
			renderJSON("{ \"success\": \"true\", \"field\": \""+field+"\", \"index\": "+index+", \"value\": \""+value+"\" }");
		
		} catch (Exception e) {
			Logger.error(e,"Unable to update submission sticky notes");
			String message = escapeJavaScript(e.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} 
	}
	
	/**
	 * Reset a group of settings, either all fields for a particular page or a
	 * sticky note.
	 * 
	 * @param group
	 *            An identifier of a group of settings.
	 */
	public static void resetSettings(String group) throws IOException {
		
		List<String> reset = new ArrayList<String>();
		
		// Sticky groups
		if ("personal-info-stickies".equals(group)) {
			reset.add(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
			flash.put("open", "submissionStep1");
			
		} else if ("document-info-stickies".equals(group)) {
			reset.add(AppConfig.SUBMIT_DOCUMENT_INFO_STICKIES);
			flash.put("open", "submissionStep3");
			
		} else if ("upload-file-stickies".equals(group)) {
			reset.add(AppConfig.SUBMIT_UPLOAD_FILES_STICKIES);
			flash.put("open", "submissionStep4");
		} 
		
		// Field groups
		else if ("personal-info-fields".equals(group)) {
			for (FieldConfig field : FieldConfig.PERSONAL_INFO_FIELDS) {
				reset.add(field.LABEL);
				reset.add(field.HELP);
				reset.add(field.ENABLED);	
			}
			flash.put("open", "submissionStep1");
			
		} else if ("license-agreement-fields".equals(group)) {
			for (FieldConfig field : FieldConfig.LICENSE_AGREEMENT_FIELDS) {
				reset.add(field.LABEL);
				reset.add(field.HELP);
				reset.add(field.ENABLED);	
			}
			flash.put("open", "submissionStep2");
			
		} else if ("document-info-fields".equals(group)) {
			for (FieldConfig field : FieldConfig.DOCUMENT_INFO_FIELDS) {
				reset.add(field.LABEL);
				reset.add(field.HELP);
				reset.add(field.ENABLED);	
			}
			flash.put("open", "submissionStep3");
			
		} else if ("upload-files-fields".equals(group)) {
			for (FieldConfig field : FieldConfig.UPLOAD_FILES_FIELDS) {
				reset.add(field.LABEL);
				reset.add(field.HELP);
				reset.add(field.ENABLED);	
			}
			flash.put("open", "submissionStep4");
		}
		
		// Delete all the fields
		for (String field : reset) {
			Configuration config = settingRepo.findConfigurationByName(field);
			if (config != null)
				config.delete();
		}
		
		// Redirect back to the page
		submissionSettings();
	}
	
}
