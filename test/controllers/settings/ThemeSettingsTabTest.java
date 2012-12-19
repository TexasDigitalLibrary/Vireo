package controllers.settings;

import static org.tdl.vireo.constant.AppConfig.*;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the theme setting tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ThemeSettingsTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	@Before
	public void setup() {
		context.turnOffAuthorization();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	@After
	public void cleanup() {
		context.restoreAuthorization();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Just test that the page is displayed without error.
	 */
	@Test
	public void testDisplayOfTHemeSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.ThemeSettingsTab.themeSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	/**
	 * Test changing the text and input areas
	 */
	@Test
	public void testUpdatingThemeSettings() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ThemeSettingsTab.updateThemeSettingsJSON").url;

		List<String> fields = new ArrayList<String>();
		fields.add(FRONT_PAGE_INSTRUCTIONS);
		fields.add(SUBMIT_INSTRUCTIONS);
		fields.add(CORRECTION_INSTRUCTIONS);
		fields.add(CUSTOM_CSS);
		fields.add(BACKGROUND_MAIN_COLOR);
		fields.add(BACKGROUND_HIGHLIGHT_COLOR);
		fields.add(BUTTON_MAIN_COLOR_ON);
		fields.add(BUTTON_HIGHLIGHT_COLOR_ON);
		fields.add(BUTTON_MAIN_COLOR_OFF);
		fields.add(BUTTON_HIGHLIGHT_COLOR_OFF);
		
		for (String field : fields) {
		
			Configuration originalValue = settingRepo.findConfigurationByName(field);
			
			// change the current semester
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","changed \"by test\"");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		
			
			// Check that all the fields are set.
			JPA.em().getTransaction().commit();
			JPA.em().clear();
			JPA.em().getTransaction().begin();
			assertNotNull(settingRepo.findConfigurationByName(field));
			assertEquals("changed \"by test\"",settingRepo.findConfigurationByName(field).getValue());
			
			JPA.em().clear();
			if (originalValue == null) {
				settingRepo.findConfigurationByName(field).delete();
			} else {
				Configuration value = settingRepo.findConfigurationByName(field);
				value.setValue(originalValue.getValue());
				value.save();
			}
		
		}
	}
	
	/**
	 * Test uploading a logo
	 * @throws IOException 
	 */
	@Test
	public void testUploadingLogos() throws IOException {
		LOGIN();
		
		//Get the url
		final String URL = Router.reverse("settings.ThemeSettingsTab.uploadLogos").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("submit_upload", "true");
		
		File file = getResourceFile("SampleFeedbackDocument.png");
		
		Map<String,File> files = new HashMap<String,File>();
		files.put("leftLogo", file);
		
		Response response = POST(URL,params,files);
		assertStatus(302,response);
		
		File logoFile = new File(ThemeSettingsTab.LEFT_LOGO_PATH);
		assertTrue(logoFile.exists());
		
		params.clear();
		params.put("deleteLeftLogo", "true");
		
		response = POST(URL,params);
		assertStatus(302,response);
		
		logoFile = new File(ThemeSettingsTab.LEFT_LOGO_PATH);
		assertFalse(logoFile.exists());
	}
	
	/**
     * Extract the file from the jar and place it in a temporary location for the test to operate from.
     *
     * @param filePath The path, relative to the classpath, of the file to reference.
     * @return A Java File object reference.
     * @throws IOException
     */
    protected static File getResourceFile(String filePath) throws IOException {

        File file = File.createTempFile("ingest-import-test", ".pdf");

        // While we're packaged by play we have to ask Play for the inputstream instead of the classloader.
        //InputStream is = DSpaceCSVIngestServiceImplTests.class
        //		.getResourceAsStream(filePath);
        InputStream is = Play.classloader.getResourceAsStream(filePath);
        OutputStream os = new FileOutputStream(file);

        // Copy the file out of the jar into a temporary space.
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        is.close();
        os.close();

        return file;
    }
	
}
