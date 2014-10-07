package controllers.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.PersistenceException;

import org.tdl.vireo.model.AbstractOrderedModel;
import org.tdl.vireo.model.AdministrativeGroup;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.proquest.ProquestUtilityService;

import play.Logger;
import play.modules.spring.Spring;
import play.mvc.With;
import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class ConfigurableSettingsTab extends SettingsTab {

	public static final ProquestUtilityService proquestUtils = Spring.getBeanOfType(ProquestUtilityService.class);
	
	/**
	 * Display the configurable settings page.
	 * @throws Exception 
	 */
	@Security(RoleType.MANAGER)
	public static void configurableSettings() {
		
		List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();
		List<College> colleges = settingRepo.findAllColleges();
		List<Program> programs = settingRepo.findAllPrograms();
		List<Department> departments = settingRepo.findAllDepartments();
		List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
		List<Major> majors = settingRepo.findAllMajors();
		List<Degree> degrees = settingRepo.findAllDegrees();
		List<DocumentType> docTypes = settingRepo.findAllDocumentTypes();
		List<CommitteeMemberRoleType> roleTypes = settingRepo.findAllCommitteeMemberRoleTypes();
		List<GraduationMonth> gradMonths = settingRepo.findAllGraduationMonths();
				
		Locale locales[] = Locale.getAvailableLocales();
		List<Locale> localeLanguages = new ArrayList<Locale>(Arrays.asList(locales));
		Collections.sort(localeLanguages, ascending(getLocaleComparator(LocaleComparator.LANGUAGE_SORT, LocaleComparator.COUNTRY_SORT)));
		
		List<Language> languages = settingRepo.findAllLanguages();
		renderArgs.put("proquestUtils",proquestUtils);
		
		renderArgs.put("UNDERGRADUATE", DegreeLevel.UNDERGRADUATE);
		renderArgs.put("MASTERS", DegreeLevel.MASTERS);
		renderArgs.put("DOCTORAL", DegreeLevel.DOCTORAL);
		
		String nav = "settings";
		String subNav = "config";
		renderTemplate("SettingTabs/configurableSettings.html", nav, subNav, 
				
				// The lonely tabel on the page
				embargos,
				
				// Sortable lists
				colleges, programs, departments, adminGroups, majors, degrees, docTypes, roleTypes, gradMonths, languages,
				
				// Locales
				localeLanguages);
	}
	
	/**
	 * Bulk add a set of colleges, programs, departments, or majors. The bulkAdd
	 * input is expected to be formated as one item per line. After completing
	 * the user will be redirected back to the configurableSettings tab.
	 * 
	 * @param modelType
	 *            The type of object being added in bulk.
	 * @param bulkAdd
	 *            The items to be added in bulk, one per line.
	 */
	@Security(RoleType.MANAGER)
	public static void bulkAdd(String modelType, String bulkAdd) {
		
		// Build a list triming out any blank or duplicate values.
		String[] rawItems = bulkAdd.split("\n");
		List<String> items = new ArrayList<String>();
		for (String item : rawItems) {
			if (item == null)
				continue;
			
			item = item.trim();
			if (item.length() == 0)
				continue;
			
			if (items.contains(item))
				continue;
			
			items.add(item);
		}
		
		// Retrieve all current models
		List<AbstractOrderedModel> models;
		if ("college".equals(modelType)) {
			models =(List) settingRepo.findAllColleges();
		} else if ("program".equals(modelType)) {
			models= (List) settingRepo.findAllPrograms();
		} else if ("department".equals(modelType)) {
			models = (List) settingRepo.findAllDepartments();
		} else if ("adminGroup".equals(modelType)) {
			models = (List) settingRepo.findAllAdministrativeGroups();
		} else if ("major".equals(modelType)) {
			models = (List) settingRepo.findAllMajors();
		} else {
			throw new IllegalArgumentException("Unknown modelType: "+modelType);
		}	
		
		
		// Eliminate any values already created.
		Iterator<String> itr = items.iterator();
		while (itr.hasNext()) {
			String item = itr.next();
			for (AbstractOrderedModel model : models) {
				if ("college".equals(modelType)) {
					if (item.equals(((College) model).getName()))
						itr.remove();
				} else if ("program".equals(modelType)) {
					if (item.equals(((Program) model).getName()))
						itr.remove();
				} else if ("department".equals(modelType)) {
					if (item.equals(((Department) model).getName()))
						itr.remove();
				} else if ("adminGroup".equals(modelType)) {
					if (item.equals(((AdministrativeGroup) model).getName()))
						itr.remove();
				} else if ("major".equals(modelType)) {
					if (item.equals(((Major) model).getName()))
						itr.remove();
				} else {
					throw new IllegalArgumentException("Unknown modelType: "+modelType);
				}	
			}
		}
		
		
		// Create everything that is left.
		for (String item : items) {
			item = item.trim();
			if ("college".equals(modelType)) {
				models.add(settingRepo.createCollege(item));
			} else if ("program".equals(modelType)) {
				models.add(settingRepo.createProgram(item));
			} else if ("department".equals(modelType)) {
				models.add(settingRepo.createDepartment(item));
			} else if ("adminGroup".equals(modelType)) {
				models.add(settingRepo.createAdministrativeGroup(item));
			} else if ("major".equals(modelType)) {
				models.add(settingRepo.createMajor(item));
			} else {
				throw new IllegalArgumentException("Unknown modelType: "+modelType);
			}	
			
			
		}
		saveModelOrder(models);

			
		// Redirect back the configurable setting tab.
		if ("college".equals(modelType)) {
			flash.put("open","availableColleges");
		} else if ("program".equals(modelType)) {
			flash.put("open","availablePrograms");
		} else if ("department".equals(modelType)) {
			flash.put("open","availableDepartments");
		} else if ("adminGroup".equals(modelType)) {
			flash.put("open","availableAdminGroups");
		} else if ("major".equals(modelType)) {
			flash.put("open","availableMajors");
		} else {
			throw new IllegalArgumentException("Unknown modelType: "+modelType);
		}
		configurableSettings();
	}
	

	// ////////////////////////////////////////////
	// EMBARGO TYPES
	// ////////////////////////////////////////////
	
	/**
	 * Create or edit an existing embargo type.
	 * 
	 * @param embargoId (Optional)
	 *            The id of the embargo type, in the form "embargoType_id". If no id is provided then a new embargo type will be created.
	 * @param name
	 *            The name of the type
	 * @param description
	 *            The description of the type.
	 * @param months
	 *            The duration, in months, for this embargo.
	 * @param active
	 *            Whether this embargo is active.
	 */
	@Security(RoleType.MANAGER)
	public static void editEmbargoTypeJSON(String embargoTypeId, String name, String description, Integer months, boolean active) {
		
		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");
			
			if (description == null || description.trim().length() == 0)
				throw new IllegalArgumentException("Description is required");

			// Create or modify the embargo
			EmbargoType embargo = null;
			if (embargoTypeId != null && embargoTypeId.trim().length() > 0) {
				
				// Modify an existing embargo
				String[] parts = embargoTypeId.split("_");
				Long id = Long.valueOf(parts[1]);
				embargo = settingRepo.findEmbargoType(id);
				embargo.setName(name);
				embargo.setDescription(description);
				embargo.setDuration(months);
				embargo.setActive(active);
				embargo.save();
			} else {
				List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();

				// Create a new embargo
				embargo = settingRepo.createEmbargoType(name, description, months, active);
				embargos.add(embargo);

				saveModelOrder(embargos);
			}
			
			Logger.info("%s (%d: %s) has edited embargo #%d.\nEmbargo Name = '%s'\nEmbargo Description = '%s'\nEmbargo Duration = '%d'\nEmbargo Active = '%b'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					embargo.getId(),
					embargo.getName(),
					embargo.getDescription(),
					embargo.getDuration(),
					embargo.isActive());

			name = escapeJavaScript(embargo.getName());
			description = escapeJavaScript(embargo.getDescription());

			renderJSON("{ \"success\": \"true\", \"id\": " + embargo.getId() + ", \"name\": \"" + name + "\", \"description\": \"" + description + "\", \"active\": \"" + active + "\", \"months\": \"" + months + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another embargo type already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add college");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}	
	}
	
	/**
	 * Remove an existing embargo type.
	 * 
	 * @param embargoId 
	 * 			  The id of the embargo type to be removed.
	 */
	@Security(RoleType.MANAGER)
	public static void removeEmbargoTypeJSON(String embargoTypeId) {
		
		try {
			String[] parts = embargoTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			EmbargoType embargo = settingRepo.findEmbargoType(id);			
			embargo.delete();
			
			Logger.info("%s (%d: %s) has deleted embargo #%d.\nEmbargo Name = '%s'\nEmbargo Description = '%s'\nEmbargo Duration = '%d'\nEmbargo Active = '%b'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST), 
					context.getPerson().getId(), 
					context.getPerson().getEmail(),
					embargo.getId(),
					embargo.getName(),
					embargo.getDescription(),
					embargo.getDuration(),
					embargo.isActive());
			
			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove embargo type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}	
	}
	
	
	/**
	 * Remove all existing embargo types.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllEmbargoTypes() {

		List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();
		for (EmbargoType embargo : embargos) {
			embargo.delete();

			Logger.info(
					"%s (%d: %s) has deleted embargo #%d.\nEmbargo Name = '%s'\nEmbargo Description = '%s'\nEmbargo Duration = '%d'\nEmbargo Active = '%b'",
					context.getPerson().getFormattedName(NameFormat.FIRST_LAST),
					context.getPerson().getId(),
					context.getPerson().getEmail(), embargo.getId(),
					embargo.getName(), embargo.getDescription(),
					embargo.getDuration(), embargo.isActive());
		}

		flash.put("open","availableEmbargoTypes");
		configurableSettings();
	}
	
	
	/**
	 * Reorder a list of embargo types.
	 * 
	 * @param embargoIds
	 *            An ordered list of ids in the form:
	 *            "embargo_1,embargo_3,embargo_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderEmbargoTypesJSON(String embargoTypeIds) {

		try {

			if (embargoTypeIds != null && embargoTypeIds.trim().length() > 0) {
				// Save the new order
				List<EmbargoType> embargos = resolveIds(embargoTypeIds, EmbargoType.class);
				saveModelOrder(embargos);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder embargos");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}
	
	/**
	 * Alphabetize all embargo types.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllEmbargoTypes() {

		List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();
		Collections.sort(embargos, ascending(getModelCompator()));
		saveModelOrder(embargos);
		
		flash.put("open","availableEmbargoTypes");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// COLLEGE AJAX
	// ////////////////////////////////////////////

	/**
	 * Create or edit a new College. The id of the new college will be returned.
	 * 
	 * @param collegeId - The id of the college we're editing
	 * @param name - The name of the new college
	 * @param emails - The list of email addresses to associate with this college
	 */
	@Security(RoleType.MANAGER)
	public static void addEditCollegeJSON(String collegeId, String name, String emails) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");
			
			// make sure emails isn't null
			if(emails == null) {
				emails = "";
			}
			
			// remove whitespace from email address string
			emails = emails.replaceAll("\\s+","");

			// Add the new college to the end of the list.
            List<College> colleges = settingRepo.findAllColleges();
			
			// Create or modify the college
			College college = null;
			String jsonEmails = "";
            if (collegeId != null && collegeId.trim().length() > 0) {
                Long id = Long.valueOf(collegeId);
                college = settingRepo.findCollege(id);
                college.setName(name);
                // create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
                // set the new emails with the hashMap
				college.setEmails(emails_map);
                college.save();
            } else {
				// create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
                // create the new college with the passed-in emails
				college = settingRepo.createCollege(name, emails_map);
    			colleges.add(college);
            }

			saveModelOrder(colleges);

			name = escapeJavaScript(college.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + college.getId()
					+ ", \"name\": \"" + name + "\", \"emails\": " + jsonEmails+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another college already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add college");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove an existing college
	 * 
	 * @param collegeId
	 *            The id of the college to be removed
	 */
	@Security(RoleType.MANAGER)
	public static void removeCollegeJSON(String collegeId) {
		try {
			// Delete the old college
			Long id = Long.valueOf(collegeId);
			College college = settingRepo.findCollege(id);
			college.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove college");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing colleges
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllColleges() {
		List<College> colleges = settingRepo.findAllColleges();
		for (College college : colleges) {
			college.delete();
		}

		flash.put("open","availableColleges");
		configurableSettings();
	}

	/**
	 * Reorder a list of colleges.
	 * 
	 * @param collegeIds
	 *            An ordered list of ids in the form:
	 *            "college_1,college_3,college_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderCollegesJSON(String collegeIds) {

		try {

			if (collegeIds != null && collegeIds.trim().length() > 0) {
				// Save the new order
				List<College> colleges = resolveIds(collegeIds, College.class);
				saveModelOrder(colleges);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder colleges");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Alphabetize all colleges.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllColleges() {

		List<College> colleges = settingRepo.findAllColleges();
		Collections.sort(colleges, ascending(getModelCompator()));
		saveModelOrder(colleges);
		
		flash.put("open","availableColleges");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// PROGRAM AJAX
	// ////////////////////////////////////////////

	/**
	 * Create or edit a new Program. The id of the new program will be returned.
	 * 
	 * @param programId - The id of the program we're editing
	 * @param name - The name of the new program
	 * @param emails - The list of email addresses to associate with this program
	 */
	@Security(RoleType.MANAGER)
	public static void addEditProgramJSON(String programId, String name, String emails) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");
			
			// make sure emails isn't null
			if(emails == null) {
				emails = "";
			}
			
			// remove whitespace from email address string
            emails = emails.replaceAll("\\s+","");

			// Add the new college to the end of the list.
            List<Program> programs = settingRepo.findAllPrograms();
			
			// Create or modify the college
            Program program = null;
			String jsonEmails = "";
            if (programId != null && programId.trim().length() > 0) {
                Long id = Long.valueOf(programId);
                program = settingRepo.findProgram(id);
                program.setName(name);
                // create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
				program.setEmails(emails_map);
				program.save();
            } else {
				// create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
				program = settingRepo.createProgram(name, emails_map);
    			programs.add(program);
            }

			saveModelOrder(programs);

			name = escapeJavaScript(program.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + program.getId()
					+ ", \"name\": \"" + name + "\", \"emails\": " + jsonEmails+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another program already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add program");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Remove an existing program
	 * 
	 * @param programId
	 *            The id of the program to be removed of the form "program_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeProgramJSON(String programId) {
		try {
			// Delete the old program
			Long id = Long.valueOf(programId);
			Program program = settingRepo.findProgram(id);
			program.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove program");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing programs
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllPrograms() {
		List<Program> programs = settingRepo.findAllPrograms();
		for (Program program : programs) {
			program.delete();
		}

		flash.put("open","availablePrograms");
		configurableSettings();
	}

	/**
	 * Reorder a list of programs.
	 * 
	 * @param programIds
	 *            An ordered list of ids in the form:
	 *            "program_1,program_3,program_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderProgramsJSON(String programIds) {

		try {

			if (programIds != null && programIds.trim().length() > 0) {
				// Save the new order
				List<Program> programs = resolveIds(programIds, Program.class);
				saveModelOrder(programs);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder programs");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Alphabetize all programs.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllPrograms() {

		List<Program> programs = settingRepo.findAllPrograms();
		Collections.sort(programs, ascending(getModelCompator()));
		saveModelOrder(programs);
		
		flash.put("open","availablePrograms");
		configurableSettings();
	}

	// ////////////////////////////////////////////
	// DEPARTMENT AJAX
	// ////////////////////////////////////////////

	/**
	 * Create or edit a new Department. The id of the new department will be returned.
	 * 
	 * @param departmentId - The id of the department we're editing
	 * @param name - The name of the new department
	 * @param emails - The list of email addresses to associate with this department
	 */
	@Security(RoleType.MANAGER)
	public static void addEditDepartmentJSON(String departmentId, String name, String emails) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");
			
			// make sure emails isn't null
			if(emails == null) {
				emails = "";
			}
			
			// remove whitespace from email address string
            emails = emails.replaceAll("\\s+","");

			// Add the new college to the end of the list.
            List<Department> departments = settingRepo.findAllDepartments();
			
			// Create or modify the college
            Department department = null;
			String jsonEmails = "";
            if (departmentId != null && departmentId.trim().length() > 0) {
                Long id = Long.valueOf(departmentId);
                department = settingRepo.findDepartment(id);
                department.setName(name);
                // create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
				department.setEmails(emails_map);
				department.save();
            } else {
            	// create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
				department = settingRepo.createDepartment(name, emails_map);
    			departments.add(department);
            }

			saveModelOrder(departments);

			name = escapeJavaScript(department.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + department.getId()
					+ ", \"name\": \"" + name + "\", \"emails\": " + jsonEmails+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another department already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add department");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Remove an existing department
	 * 
	 * @param departmentId
	 *            The id of the department to be removed of the form
	 *            "department_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeDepartmentJSON(String departmentId) {
		try {
			// Delete the old department
			Long id = Long.valueOf(departmentId);
			Department department = settingRepo.findDepartment(id);
			department.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove department");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing departments
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllDepartments() {
		List<Department> departments = settingRepo.findAllDepartments();
		for (Department department : departments) {
			department.delete();
		}

		flash.put("open","availableDepartments");
		configurableSettings();
	}


	/**
	 * Reorder a list of departments.
	 * 
	 * @param departmentIds
	 *            An ordered list of ids in the form:
	 *            "department_1,department_3,department_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDepartmentsJSON(String departmentIds) {

		try {

			if (departmentIds != null && departmentIds.trim().length() > 0) {
				// Save the new order
				List<Department> departments = resolveIds(departmentIds,
						Department.class);
				saveModelOrder(departments);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reoder departments");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Alphabetize all departments.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllDepartments() {

		List<Department> departments = settingRepo.findAllDepartments();
		Collections.sort(departments, ascending(getModelCompator()));
		saveModelOrder(departments);
		
		flash.put("open","availableDepartments");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// ADMINISTRATIVE GROUP AJAX
	// ////////////////////////////////////////////

	/**
	 * Create or edit a new Administrative Group. The id of the new administrative group will be returned.
	 * 
	 * @param adminGroupId - The id of the administrative group we're editing
	 * @param name - The name of the new administrative group
	 * @param emails - The list of email addresses to associate with this administrative group
	 */
	@Security(RoleType.MANAGER)
	public static void addEditAdministrativeGroupJSON(String adminGroupId, String name, String emails) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");
			
			// make sure emails isn't null
			if(emails == null) {
				emails = "";
			}
			
			// remove whitespace from email address string
			emails = emails.replaceAll("\\s+","");

			// Add the new administrative groups to the end of the list.
            List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
			
			// Create or modify the administrative group
            AdministrativeGroup adminGroup = null;
			String jsonEmails = "";
            if (adminGroupId != null && adminGroupId.trim().length() > 0) {
                Long id = Long.valueOf(adminGroupId);
                adminGroup = settingRepo.findAdministrativeGroup(id);
                adminGroup.setName(name);
                // create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
                // set the new emails with the hashMap
                adminGroup.setEmails(emails_map);
                adminGroup.save();
            } else {
				// create a new hashMap for the new emails
                HashMap<Integer, String> emails_map = new HashMap<Integer, String>();
                // create the json for the response and also add them to the created hashMap
                jsonEmails = createEmailsJsonAndAddToMap(emails, emails_map);
                // create the new administrative group with the passed-in emails
                adminGroup = settingRepo.createAdministrativeGroup(name, emails_map);
                adminGroups.add(adminGroup);
            }

			saveModelOrder(adminGroups);

			name = escapeJavaScript(adminGroup.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + adminGroup.getId()
					+ ", \"name\": \"" + name + "\", \"emails\": " + jsonEmails+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another administrative group already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add administrative group");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove an existing administrative group
	 * 
	 * @param adminGroupId
	 *            The id of the administrative group to be removed
	 */
	@Security(RoleType.MANAGER)
	public static void removeAdministrativeGroupJSON(String adminGroupId) {
		try {
			// Delete the old administrative group
			Long id = Long.valueOf(adminGroupId);
			AdministrativeGroup adminGroup = settingRepo.findAdministrativeGroup(id);
			adminGroup.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove administrative group");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing administrative groups
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllAdministrativeGroups() {
		List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
		for (AdministrativeGroup adminGroup : adminGroups) {
			adminGroup.delete();
		}

		flash.put("open","availableAdminGroups");
		configurableSettings();
	}

	/**
	 * Reorder a list of administrative groups.
	 * 
	 * @param adminGroupIds
	 *            An ordered list of ids in the form:
	 *            "adminGroup_1,adminGroup_3,adminGroup_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderAdministrativeGroupsJSON(String adminGroupIds) {

		try {

			if (adminGroupIds != null && adminGroupIds.trim().length() > 0) {
				// Save the new order
				List<AdministrativeGroup> adminGroups = resolveIds(adminGroupIds, AdministrativeGroup.class);
				saveModelOrder(adminGroups);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder administrative groups");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Alphabetize all administrative groups.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllAdministrativeGroups() {

		List<AdministrativeGroup> adminGroups = settingRepo.findAllAdministrativeGroups();
		Collections.sort(adminGroups, ascending(getModelCompator()));
		saveModelOrder(adminGroups);
		
		flash.put("open","availableAdminGroups");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// MAJOR AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new Major. The id of the new major will be returned.
	 * 
	 * @param name
	 *            The name of the new major
	 */
	@Security(RoleType.MANAGER)
	public static void addMajorJSON(String name) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Add the new major to the end of the list.
			List<Major> majors = settingRepo.findAllMajors();

			Major major = settingRepo.createMajor(name);
			majors.add(major);

			saveModelOrder(majors);

			name = escapeJavaScript(major.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + major.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another major already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add major");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Edit an existing major's name. Both the id and new name will be returned.
	 * 
	 * @param majorId
	 *            The id of the major to be edited, in the form "major_id"
	 * @param name
	 *            The new name
	 */
	@Security(RoleType.MANAGER)
	public static void editMajorJSON(String majorId, String name) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Save the new major
			String[] parts = majorId.split("_");
			Long id = Long.valueOf(parts[1]);
			Major major = settingRepo.findMajor(id);
			major.setName(name);
			major.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + major.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another major already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit major");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Remove an existing major
	 * 
	 * @param majorId
	 *            The id of the major to be removed of the form "major_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeMajorJSON(String majorId) {
		try {
			// Delete the old major
			String[] parts = majorId.split("_");
			Long id = Long.valueOf(parts[1]);
			Major major = settingRepo.findMajor(id);
			major.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove major");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing majors
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllMajors() {
		
		List<Major> majors = settingRepo.findAllMajors();
		for (Major major : majors) {
			major.delete();
		}

		flash.put("open","availableMajors");
		configurableSettings();
	}


	/**
	 * Reorder a list of majors.
	 * 
	 * @param majorIds
	 *            An ordered list of ids in the form: "major_1,major_3,major_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderMajorsJSON(String majorIds) {

		try {

			if (majorIds != null && majorIds.trim().length() > 0) {
				// Save the new order
				List<Major> majors = resolveIds(majorIds, Major.class);
				saveModelOrder(majors);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder majors");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Alphabetize all majors.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllMajors() {

		List<Major> majors = settingRepo.findAllMajors();
		Collections.sort(majors, ascending(getModelCompator()));
		saveModelOrder(majors);
		
		flash.put("open","availableMajors");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// DEGREE AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new Degree. The id of the new degree will be returned.
	 * 
	 * @param name
	 *            The name of the new degree
	 * @param level 
	 * 			  The level id for the new degree
	 */
	@Security(RoleType.MANAGER)
	public static void addDegreeJSON(String name, int level) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Add the new degree to the end of the list.
			List<Degree> degrees = settingRepo.findAllDegrees();

			Degree degree = settingRepo.createDegree(name,degreeLevel);
			degrees.add(degree);

			saveModelOrder(degrees);
			
			name = escapeJavaScript(degree.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + degree.getId()	+ ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+"}");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another degree already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add degree");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing degree's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param degreeId
	 *            The id of the degree to be edited, in the form "degree_id"
	 * @param name
	 *            The new name
	 * @param level
	 * 			  The new level id
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void editDegreeJSON(String degreeId, String name, int level) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Save the new degree
			String[] parts = degreeId.split("_");
			Long id = Long.valueOf(parts[1]);
			Degree degree = settingRepo.findDegree(id);
			degree.setName(name);
			degree.setLevel(degreeLevel);
			degree.save();
			
			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + degree.getId() + ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+"}");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another degree already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit degree");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing degree
	 * 
	 * @param degreeId
	 *            The id of the degree to be removed of the form "degree_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeDegreeJSON(String degreeId) {
		try {
			// Delete the old degree
			String[] parts = degreeId.split("_");
			Long id = Long.valueOf(parts[1]);
			Degree degree = settingRepo.findDegree(id);
			degree.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove degree");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing degrees
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllDegrees() {
		List<Degree> degrees = settingRepo.findAllDegrees();
		for (Degree degree : degrees) {
			degree.delete();
		}

		flash.put("open","availableDegrees");
		configurableSettings();
	}


	/**
	 * Reorder a list of degrees.
	 * 
	 * @param degreeIds
	 *            An ordered list of ids in the form:
	 *            "degree_1,degree_3,degree_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDegreesJSON(String degreeIds) {

		try {

			if (degreeIds != null && degreeIds.trim().length() > 0) {
				// Save the new order
				List<Degree> degrees = resolveIds(degreeIds, Degree.class);
				saveModelOrder(degrees);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reoder degrees");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Alphabetize all degrees.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllDegrees() {

		List<Degree> degrees = settingRepo.findAllDegrees();
		Collections.sort(degrees, ascending(getModelCompator()));
		saveModelOrder(degrees);
		
		flash.put("open","availableDegrees");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// DOCUMENT TYPES AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new DocumentType. The id of the new docType will be returned.
	 * 
	 * @param name
	 *            The name of the new docType
	 * @param level 
	 * 			  The level id for the new documentType
	 */
	@Security(RoleType.MANAGER)
	public static void addDocumentTypeJSON(String name, int level) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Add the new type to the end of the list.
			List<DocumentType> documentTypes = settingRepo.findAllDocumentTypes();

			DocumentType documentType = settingRepo.createDocumentType(name,degreeLevel);
			documentTypes.add(documentType);

			saveModelOrder(documentTypes);

			name = escapeJavaScript(documentType.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + documentType.getId()	+ ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another document type already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add document type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing documentType's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param documentTypeId
	 *            The id of the documentType to be edited, in the form "documentType_id"
	 * @param name
	 *            The new name
	 * @param level
	 * 			  The new level id
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void editDocumentTypeJSON(String documentTypeId, String name, int level) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Save the new type
			String[] parts = documentTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			DocumentType documentType = settingRepo.findDocumentType(id);
			documentType.setName(name);
			documentType.setLevel(degreeLevel);
			documentType.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + documentType.getId() + ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another document type already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit document type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing documentType
	 * 
	 * @param documentTypeId
	 *            The id of the documentType to be removed of the form "documentType_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeDocumentTypeJSON(String documentTypeId) {
		try {
			// Delete the old documentType
			String[] parts = documentTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			DocumentType documentType = settingRepo.findDocumentType(id);
			documentType.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove document type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing document types
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllDocumentTypes() {
		List<DocumentType> types = settingRepo.findAllDocumentTypes();
		for (DocumentType type : types) {
			type.delete();
		}

		flash.put("open","availableDocumentTypes");
		configurableSettings();
	}


	/**
	 * Reorder a list of documentTypes.
	 * 
	 * @param documentTypeIds
	 *            An ordered list of ids in the form:
	 *            "documentType_1,documentType_3,documentType_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDocumentTypesJSON(String documentTypeIds) {

		try {

			if (documentTypeIds != null && documentTypeIds.trim().length() > 0) {
				// Save the new order
				List<DocumentType> documentTypes = resolveIds(documentTypeIds, DocumentType.class);
				saveModelOrder(documentTypes);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reoder document types");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}
	
	/**
	 * Alphabetize all document types.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllDocumentTypes() {

		List<DocumentType> types = settingRepo.findAllDocumentTypes();
		Collections.sort(types, ascending(getModelCompator()));
		saveModelOrder(types);
		
		flash.put("open","availableDocumentTypes");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// COMMITTEE MEMBER ROLE TYPES AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new CommitteeMemberRoleType. The id of the new roleType will be returned.
	 * 
	 * @param name
	 *            The name of the new roleType
	 * @param level 
	 * 			  The level id for the new roleType
	 */
	@Security(RoleType.MANAGER)
	public static void addCommitteeMemberRoleTypeJSON(String name, int level) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Add the new type to the end of the list.
			List<CommitteeMemberRoleType> roleTypes = settingRepo.findAllCommitteeMemberRoleTypes();

			CommitteeMemberRoleType roleType = settingRepo.createCommitteeMemberRoleType(name, degreeLevel);
			roleTypes.add(roleType);

			saveModelOrder(roleTypes);

			name = escapeJavaScript(roleType.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + roleType.getId()	+ ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another committee member role type already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add committee member role type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing roleType's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param committeeMemberRoleTypeId
	 *            The id of the roleType to be edited, in the form "committeeMemberRoleType_id"
	 * @param name
	 *            The new name
	 * @param level
	 * 			  The new level id
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void editCommitteeMemberRoleTypeJSON(String committeeMemberRoleTypeId, String name, int level) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Save the new type
			String[] parts = committeeMemberRoleTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			CommitteeMemberRoleType roleType = settingRepo.findCommitteeMemberRoleType(id);
			roleType.setName(name);
			roleType.setLevel(degreeLevel);
			roleType.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + roleType.getId() + ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another committee member role type already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit committee member role type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing committee member role type
	 * 
	 * @param committeeMemberRoleTypeId
	 *            The id of the roleType to be removed of the form "committeeMemberRoleType_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeCommitteeMemberRoleTypeJSON(String committeeMemberRoleTypeId) {
		try {
			// Delete the old documentType
			String[] parts = committeeMemberRoleTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			CommitteeMemberRoleType roleType = settingRepo.findCommitteeMemberRoleType(id);
			roleType.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove committee member role type");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing committee member role types
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllCommitteeMemberRoleTypes() {
		List<CommitteeMemberRoleType> types = settingRepo.findAllCommitteeMemberRoleTypes();
		for (CommitteeMemberRoleType type : types) {
			type.delete();
		}

		flash.put("open","availableCommitteeMemberRoleTypes");
		configurableSettings();
	}


	/**
	 * Reorder a list of committee member role types.
	 * 
	 * @param committeeMemberRoleTypeIds
	 *            An ordered list of ids in the form:
	 *            "committeeMemberRoleType_1,committeeMemberRoleType_3,committeeMemberRoleType_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderCommitteeMemberRoleTypesJSON(String committeeMemberRoleTypeIds) {

		try {

			if (committeeMemberRoleTypeIds != null && committeeMemberRoleTypeIds.trim().length() > 0) {
				// Save the new order
				List<CommitteeMemberRoleType> roleTypes = resolveIds(committeeMemberRoleTypeIds, CommitteeMemberRoleType.class);
				saveModelOrder(roleTypes);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reoder committee member role types");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Alphabetize all committee member role types.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllCommitteeMemberRoleTypes() {

		List<CommitteeMemberRoleType> types = settingRepo.findAllCommitteeMemberRoleTypes();
		Collections.sort(types, ascending(getModelCompator()));
		saveModelOrder(types);
		
		flash.put("open","availableCommitteeMemberRoleTypes");
		configurableSettings();
	}

	// ////////////////////////////////////////////
	// GRADUATION MONTH AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new GraduationMonth. The id of the new month will be returned.
	 * 
	 * @param name
	 *            The name of the new graduation month
	 */
	@Security(RoleType.MANAGER)
	public static void addGraduationMonthJSON(String name) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			int monthInt = monthNameToInt(name);
			
			// Add the new month to the end of the list.
			List<GraduationMonth> months = settingRepo.findAllGraduationMonths();

			GraduationMonth month = settingRepo.createGraduationMonth(monthInt);
			months.add(month);

			saveModelOrder(months);

			name = escapeJavaScript(month.getMonthName());

			renderJSON("{ \"success\": \"true\", \"id\": " + month.getId() + ", \"name\": \"" + name + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another graduation month already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add graduation month");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing month's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param graduationMonthId
	 *            The id of the month to be edited, in the form "graduationMonth_id"
	 * @param name
	 *            The new name of the month
	 */
	@Security(RoleType.MANAGER)
	public static void editGraduationMonthJSON(String graduationMonthId, String name) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Month name is required");

			int monthInt = monthNameToInt(name);
			
			// Save the new month
			String[] parts = graduationMonthId.split("_");
			Long id = Long.valueOf(parts[1]);
			GraduationMonth month = settingRepo.findGraduationMonth(id);
			
			month.setMonth(monthInt);
			month.save();

			name = escapeJavaScript(month.getMonthName());

			renderJSON("{ \"success\": \"true\", \"id\": " + month.getId() + ", \"name\": \"" + name + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
			
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another graduation month already exists with the name: '"+name+"'\" }");
			
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to edit graduation month");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing graduation month
	 * 
	 * @param graduationMonthId
	 *            The id of the graduation month to be removed of the form "graduationMonth_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeGraduationMonthJSON(String graduationMonthId) {
		try {
			// Delete the old college
			String[] parts = graduationMonthId.split("_");
			Long id = Long.valueOf(parts[1]);
			GraduationMonth month = settingRepo.findGraduationMonth(id);
			month.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove graduation month");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove all existing graduation months
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllGraduationMonths() {
		List<GraduationMonth> months = settingRepo.findAllGraduationMonths();
		for (GraduationMonth month : months) {
			month.delete();
		}

		flash.put("open", "availableGraduationMonths");
		configurableSettings();
	}

	
	/**
	 * Reorder a list of graduation months.
	 * 
	 * @param graduationMonthIds
	 *            An ordered list of ids in the form:
	 *            "graduationMonth_1,graduationMonth_3,graduationMonth_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderGraduationMonthsJSON(String graduationMonthIds) {

		try {

			if (graduationMonthIds != null && graduationMonthIds.trim().length() > 0) {
				// Save the new order
				List<GraduationMonth> months = resolveIds(graduationMonthIds, GraduationMonth.class);
				saveModelOrder(months);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder graduation month");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}
	
	/**
	 * Alphabetize all graduation month.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllGraduationMonths() {

		List<GraduationMonth> months = settingRepo.findAllGraduationMonths();
		Collections.sort(months, ascending(getModelCompator()));
		
		saveModelOrder(months);
		
		flash.put("open","availableGraduationMonths");
		configurableSettings();
	}
	
	// ////////////////////////////////////////////
	// LANGUAGE AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new Language. The id of the new language will be returned.
	 * 
	 * @param name
	 *            The description provided by proquest of the new language
	 */
	@Security(RoleType.MANAGER)
	public static void addLanguageJSON(String name) {
		
		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Add the new program to the end of the list.
			List<Language> languages = settingRepo.findAllLanguages();

			Language language = settingRepo.createLanguage(name);
			languages.add(language);

			saveModelOrder(languages);

			
			String displayName = language.getLocale().getDisplayName();
			
			if (proquestUtils.languageCode(language.getLocale()) != null)
				displayName += "&nbsp;&nbsp; &diams;";
			
			displayName = escapeJavaScript(displayName);

			renderJSON("{ \"success\": \"true\", \"id\": " + language.getId()
					+ ", \"name\": \"" + displayName + "\" }");
		} catch (IllegalArgumentException iae) {
			String message = escapeJavaScript(iae.getMessage());			
			renderJSON("{ \"failure\": \"true\", \"message\": \""+message+"\" }");
		} catch (PersistenceException pe) {
			name = escapeJavaScript(name);
			renderJSON("{ \"failure\": \"true\", \"message\": \"Another language already exists with the name: '"+name+"'\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to add program");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Remove an existing language
	 * 
	 * @param languageId
	 *            The id of the language to be removed of the form "language_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeLanguageJSON(String languageId) {
		try {
			// Delete the old college
			String[] parts = languageId.split("_");
			Long id = Long.valueOf(parts[1]);
			Language language = settingRepo.findLanguage(id);
			language.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to remove language");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Remove all existing languages
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void removeAllLanguages() {
		List<Language> languages = settingRepo.findAllLanguages();
		for (Language language : languages) {
			language.delete();
		}

		
		flash.put("open", "availableLanguages");
		configurableSettings();
	}


	/**
	 * Reorder a list of languages.
	 * 
	 * @param languageIds
	 *            An ordered list of ids in the form:
	 *            "language_1,language_3,language_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderLanguagesJSON(String languageIds) {

		try {

			if (languageIds != null && languageIds.trim().length() > 0) {
				// Save the new order
				List<Language> languages = resolveIds(languageIds, Language.class);
				saveModelOrder(languages);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			Logger.error(re,"Unable to reorder languages");
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}
	
	/**
	 * Alphabetize all languages.
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void alphabetizeAllLanguages() {

		List<Language> languages = settingRepo.findAllLanguages();
		Collections.sort(languages, ascending(getModelCompator()));
		saveModelOrder(languages);
		
		flash.put("open","availableLanguages");
		configurableSettings();
	}
	
	/**
	 * Helper function to create a JSON encoded string for a hashMap of email addresses
	 * 
	 * Will also modify the passed-in hashMap to add the email addresses
	 * 
	 * Will validate the email addresses in the process 
	 * 
	 * @param emails - the emails String from the request
	 * @param emails_map - a blank map to add emails to
	 * @return - JSON encoded string representing the hashMap of emails
	 */
	private static String createEmailsJsonAndAddToMap(String emails, HashMap<Integer, String> emails_map){
		String jsonEmails = "[";
		if(emails != null) {
			int i = 0;
			List<String> emails_list = (emails.length() == 0 ? new ArrayList<String>() : Arrays.asList(emails.split(",")));
	        for(String email : emails_list) {
				// validate email
				if(!validateEmailAddress(email)){
					throw new IllegalArgumentException("Invalid E-Mail Address detected! [" + email + "]");
				}
	            emails_map.put(i, email);
	            jsonEmails += "{\"id\":" +i+ ",\"email\":\""+email+"\"},";
	            i++;
	        }
	        if(jsonEmails.length() > 1) {
	        	jsonEmails = jsonEmails.substring(0,jsonEmails.length()-1);
	        }
		}
		jsonEmails += "]";
		
		return jsonEmails;
	}
	
	/**
	 * Helper function to validate a single email address as a String
	 * 
	 * @param email - the String of the email address to validate
	 * @return - true or false if email address is valid
	 */
	private static boolean validateEmailAddress(String email){
		try {
			new InternetAddress(email).validate();
		} catch (AddressException ae) {
			validation.addError("email", "The email provided is invalid.["+email+"]");
			return false;
		}
		return true;
	}
	
	/**
	 * Sort Locales
	 *
	 */
	public enum LocaleComparator implements Comparator<Locale> {

		LANGUAGE_SORT {
			public int compare(Locale o1, Locale o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		},
		COUNTRY_SORT {
			public int compare(Locale o1, Locale o2) {
				return o1.getDisplayCountry().compareTo(o2.getDisplayCountry());
			}
		};
    }
	
	public static <T> Comparator<T> decending(final Comparator<T> other) {
		return new Comparator<T>() {
			public int compare(T o1, T o2) {
				return -1 * other.compare(o1, o2);
			}
		};
	}
    
    public static <T> Comparator<T> ascending(final Comparator<T> other) {
		return new Comparator<T>() {
			public int compare(T o1, T o2) {
				return 1 * other.compare(o1, o2);
			}
		};
	}
	
	public static Comparator<Locale> getLocaleComparator(final LocaleComparator... multipleOptions) {
		return new Comparator<Locale>() {
			public int compare(Locale o1, Locale o2) {
				for (LocaleComparator option : multipleOptions) {
					int result = option.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}
	
	/**
	 * @return A comparator that can sort abstract models. The particular
	 *         sorting is dependent upon the specific model type but is mostly
	 *         based upon the model's name, although sometimes another parameter
	 *         is considered.
	 */
	public static Comparator<AbstractOrderedModel> getModelCompator() {

		return new Comparator<AbstractOrderedModel>() {

			private String _getName(AbstractOrderedModel model) {

				if (model == null)
					return "";

				if (model instanceof EmbargoType) {
					EmbargoType type = (EmbargoType) model;
					if (type.getDuration() != null) {
						// Determinate embargos
						return String.format("%2d - %s", type.getDuration(),type.getName());
					} else {
						return String.format("999 - %s", type.getName());
					}
				}
				if (model instanceof College) {
					return ((College) model).getName();
				}
				if (model instanceof Program) {
					return ((Program) model).getName();
				}
				if (model instanceof Department) {
					return ((Department) model).getName();
				}
				if (model instanceof AdministrativeGroup) {
					return ((AdministrativeGroup) model).getName();
				}
				if (model instanceof Major) {
					return ((Major) model).getName();
				}
				if (model instanceof Degree) {
					Degree degree = ((Degree) model);
					return (degree.getLevel().getId() - 5) + "-"+degree.getName();
				}
				if (model instanceof DocumentType) {
					DocumentType type = ((DocumentType) model);
					return (type.getLevel().getId() - 5) + "-"+type.getName();
				}
				if (model instanceof CommitteeMemberRoleType) {
					CommitteeMemberRoleType type = ((CommitteeMemberRoleType) model);
					return (type.getLevel().getId() - 5) + "-"+type.getName();
				}
				if (model instanceof GraduationMonth) {
					
					int month = ((GraduationMonth) model).getMonth();
					if (month < 10)
						return "0"+month;
					else
						return ""+month;
				}
				if (model instanceof Language) {
					return ((Language) model).getLocale().getDisplayName();
				}
				throw new IllegalArgumentException("Unknown model type: "+model.getClass().getName());
			}


			public int compare(AbstractOrderedModel o1, AbstractOrderedModel o2) {
				return _getName(o1).compareTo(_getName(o2));				
			}

		};
	}

}
