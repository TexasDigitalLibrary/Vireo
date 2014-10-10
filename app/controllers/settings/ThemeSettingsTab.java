package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.RoleType;

import play.Logger;
import play.Play;
import play.mvc.Router;
import play.mvc.With;
import play.vfs.VirtualFile;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

/**
 * Theme settings
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class ThemeSettingsTab extends SettingsTab {
	
	public static final String THEME_PATH = Play.applicationPath + File.separator + "conf"+File.separator+"theme"+File.separator;
	public static final String LEFT_LOGO_PATH = THEME_PATH + "left-logo";
	public static final String RIGHT_LOGO_PATH = THEME_PATH + "right-logo";
			
	@Security(RoleType.MANAGER)
	public static void themeSettings() {
		
		renderArgs.put("FRONT_PAGE_INSTRUCTIONS", settingRepo.getConfigValue(FRONT_PAGE_INSTRUCTIONS));
		renderArgs.put("SUBMIT_INSTRUCTIONS", settingRepo.getConfigValue(SUBMIT_INSTRUCTIONS));
		renderArgs.put("CORRECTION_INSTRUCTIONS", settingRepo.getConfigValue(CORRECTION_INSTRUCTIONS));
		
		// Colors and CSS
		renderArgs.put("BACKGROUND_MAIN_COLOR", settingRepo.getConfigValue(BACKGROUND_MAIN_COLOR));
		renderArgs.put("BACKGROUND_HIGHLIGHT_COLOR", settingRepo.getConfigValue(BACKGROUND_HIGHLIGHT_COLOR));
		renderArgs.put("BUTTON_MAIN_COLOR_ON", settingRepo.getConfigValue(BUTTON_MAIN_COLOR_ON));
		renderArgs.put("BUTTON_HIGHLIGHT_COLOR_ON", settingRepo.getConfigValue(BUTTON_HIGHLIGHT_COLOR_ON));
		renderArgs.put("BUTTON_MAIN_COLOR_OFF", settingRepo.getConfigValue(BUTTON_MAIN_COLOR_OFF));
		renderArgs.put("BUTTON_HIGHLIGHT_COLOR_OFF", settingRepo.getConfigValue(BUTTON_HIGHLIGHT_COLOR_OFF));
		renderArgs.put("CUSTOM_CSS", settingRepo.getConfigValue(CUSTOM_CSS));
				
		// Logos
		File leftLogo = new File(LEFT_LOGO_PATH);
		File rightLogo = new File(RIGHT_LOGO_PATH);
		
		String nav = "settings";
		String subNav = "theme";
		renderTemplate("SettingTabs/themeSettings.html",nav, subNav, leftLogo, rightLogo);
	}
	
	
	@Security(RoleType.MANAGER)
	public static void updateThemeSettingsJSON(String field, String value) {

		try {
			List<String> booleanFields = new ArrayList<String>();
			// None at the moment but we expect some in the future.
			
			List<String> textFields = new ArrayList<String>();
			textFields.add(FRONT_PAGE_INSTRUCTIONS);
			textFields.add(SUBMIT_INSTRUCTIONS);
			textFields.add(CORRECTION_INSTRUCTIONS);
			textFields.add(CUSTOM_CSS);
			
			List<String> inputFields = new ArrayList<String>();
			inputFields.add(BACKGROUND_MAIN_COLOR);
			inputFields.add(BACKGROUND_HIGHLIGHT_COLOR);
			inputFields.add(BUTTON_MAIN_COLOR_ON);
			inputFields.add(BUTTON_HIGHLIGHT_COLOR_ON);
			inputFields.add(BUTTON_MAIN_COLOR_OFF);
			inputFields.add(BUTTON_HIGHLIGHT_COLOR_OFF);

			if (booleanFields.contains(field)) {
				// This is a boolean field
				boolean booleanValue = true;
				if (value == null || value.trim().length() == 0)
					booleanValue = false;
				
				Configuration config = settingRepo.findConfigurationByName(field);
				if (!booleanValue && config != null)
					config.delete();
				else if (booleanValue && config == null)
					settingRepo.createConfiguration(field, "true").save();
				
				
			} else if (textFields.contains(field)) {
				// This is a free-form text field
				Configuration config = settingRepo.findConfigurationByName(field);
				
				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else {
					config.setValue(value);
				}
				config.save();
			} else if (inputFields.contains(field)) {
				// This is a input field
				Configuration config = settingRepo.findConfigurationByName(field);
				
				if (config == null)
					config = settingRepo.createConfiguration(field, value);
				else
					config.setValue(value);
				
				config.save();
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
			Logger.error(re,"Unable to update application settings");
			String message = escapeJavaScript(re.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		}
	}
	
	@Security(RoleType.MANAGER)
	public static void uploadLogos(File leftLogo, File rightLogo) throws IOException{
		File themeDir = new File(THEME_PATH);
		
		if(!themeDir.exists()){
			themeDir.mkdir();			
		}
		
		if(params.get("deleteLeftLogo") != null) {
			File logoFile = new File(LEFT_LOGO_PATH);
			
			if(logoFile.exists()){
				logoFile.delete();
			}
		}
		
		if(params.get("deleteRightLogo") != null) {
			File logoFile = new File(RIGHT_LOGO_PATH);
			
			if(logoFile.exists()){
				logoFile.delete();
			}
		}
		
		if(leftLogo != null) {
			File logoFile = new File(LEFT_LOGO_PATH);
			
			if(logoFile.exists()){
				logoFile.delete();
			}
			
			FileUtils.copyFile(leftLogo, logoFile);
		}
		
		if(rightLogo != null) {
			File logoFile = new File(RIGHT_LOGO_PATH);
			
			if(logoFile.exists()){
				logoFile.delete();
			}
			
			FileUtils.copyFile(rightLogo, logoFile);
		}
		
		themeSettings();
	}
}
