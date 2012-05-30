import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.*;
import play.modules.spring.Spring;

/**
 * When running in a test environment pre-load some configuration and test data
 * just to make things easier.
 * 
 * 
 * @author Micah Cooper
 * 
 */
@OnApplicationStart
public class TestDataLoader extends Job {

	/**
	 * Initial Persons to create
	 */
	private static final PersonsArray[] PERSONS_DEFINITIONS = {
		new PersonsArray("000000001", "bthornton@gmail.com", "Billy", "Thornton", RoleType.ADMINISTRATOR),
		new PersonsArray("000000002", "mdriver@gmail.com", "Minnie", "Driver", RoleType.MANAGER),
		new PersonsArray("000000003", "jdimaggio@gmail.com", "John", "Di Maggio", RoleType.REVIEWER),
		new PersonsArray("000000004", "cdanes@gmail.com", "Claire", "Danes", RoleType.STUDENT),
		new PersonsArray("000000005", "bcrudup@gmail.com", "Billy", "Crudup", RoleType.STUDENT),
		new PersonsArray("000000006", "ganderson@gmail.com", "Gillian", "Anderson", RoleType.STUDENT)
	};
	
	/**
	 * Initial Colleges to create
	 */
	private static final String[] COLLEGES_DEFINITIONS = {
		"College of Agriculture and Life Sciences",
		"College of Architecture",
		"College of Education and Human Development",
		"College of Geosciences",
		"College of Liberal Arts",
		"College of Science",
		"College of Veterinary Medicine and Biomedical Sciences",
		"Dwight Look College of Engineering",
		"Interdisciplinary Degree Programs",
		"Mays Business School",
		"Texas A&M University at Galveston",
		"Texas A&M University at Qatar",
		"Undergraduate Honors Fellows",
		"Undergraduate Scholars"
	};
	
	/**
	 * Initial Departments to create
	 */
	
	private static final String[] DEPARTMENTS_DEFINITIONS = {
		"Accounting",
		"Aerospace Engineering",
		"Agricultural Economics",
		"Agricultural Leadership, Education, and Communications",
		"Animal Science",
		"Anthropology",
		"Architecture",
		"Atmospheric Sciences",
		"Biochemistry and Biophysics",
		"Biological and Agricultural Engineering",
		"Biology",
		"Biomedical Engineering",
		"Chemical Engineering",
		"Chemistry",
		"Civil Engineering",
		"College of Agriculture and Life Sciences",
		"College of Architecture",
		"College of Education and Human Development",
		"College of Engineering",
		"College of Geosciences",
		"College of Liberal Arts",
		"College of Science",
		"College of Veterinary Medicine and Biomedical Sciences",
		"Communication",
		"Computer Science and Engineering",
		"Construction Science",
		"Economics",
		"Ecosystem Science and Management",
		"Educational Administration and Human Resource Development",
		"Educational Psychology",
		"Electrical and Computer Engineering",
		"English",
		"Entomology",
		"Finance",
		"Geography",
		"Geology and Geophysics",
		"Health and Kinesiology",
		"Hispanic Studies",
		"History",
		"Horticultural Sciences",
		"Industrial and Systems Engineering",
		"Information and Operations Management",
		"Landscape Architecture and Urban Planning",
		"Management",
		"Marine Biology",
		"Marine Sciences",
		"Marketing",
		"Mathematics",
		"Mays Business School",
		"Mechanical Engineering",
		"Nuclear Engineering",
		"Nutrition and Food Science",
		"Oceanography",
		"Performance Studies",
		"Petroleum Engineering",
		"Philosophy and Humanities",
		"Physics and Astronomy",
		"Plant Pathology and Microbiology",
		"Political Science",
		"Poultry Science",
		"Psychology",
		"Recreation, Park, and Tourism Sciences",
		"Sociology",
		"Soil and Crop Sciences",
		"Statistics",
		"Teaching, Learning, and Culture",
		"Veterinary Integrative Biosciences",
		"Veterinary Large Animal Clinical Sciences",
		"Veterinary Pathobiology",
		"Veterinary Physiology and Pharmacology",
		"Veterinary Small Animal Clinical Sciences",
		"Visualization",
		"Wildlife and Fisheries Sciences"
	};
	
	/**
	 * Initial Majors to create
	 */
	
	private static final String[] MAJORS_DEFINITIONS = {
		"Accounting",
		"Aerospace Engineering",
		"Agribusiness",
		"Agribusiness and Managerial Economics",
		"Agricultural Economics",
		"Agricultural Leadership, Education, and Communications",
		"Agricultural Systems Management",
		"Agronomy",
		"Animal Breeding",
		"Animal Science",
		"Anthropology",
		"Applied Physics",
		"Architecture",
		"Atmospheric Sciences",
		"Bilingual Education",
		"Biochemistry",
		"Biological and Agricultural Engineering",
		"Biology",
		"Biomedical Engineering",
		"Biomedical Sciences",
		"Biotechnology",
		"Botany",
		"Business Administration",
		"Chemical Engineering",
		"Chemistry",
		"Civil Engineering",
		"Communication",
		"Comparative Literature and Culture",
		"Computer Engineering",
		"Computer Science",
		"Construction Management",
		"Counseling Psychology",
		"Curriculum and Instruction",
		"Dairy Science",
		"Economics",
		"Educational Administration",
		"Educational Human Resource Development",
		"Educational Psychology",
		"Electrical Engineering",
		"Engineering",
		"Engineering Systems Management",
		"English",
		"Entomology",
		"Epidemiology",
		"Finance",
		"Floriculture",
		"Food Science and Technology",
		"Forestry",
		"Genetics",
		"Geography",
		"Geology",
		"Geophysics",
		"Health Education",
		"Health Physics",
		"Hispanic Studies",
		"History",
		"Horticulture",
		"Industrial Engineering",
		"Interdisciplinary Engineering",
		"Kinesiology",
		"Laboratory Animal Medicine",
		"Management",
		"Management Information Systems",
		"Marine Biology",
		"Marine Resources Management",
		"Marketing",
		"Materials Science and Engineering",
		"Mathematics",
		"Mechanical Engineering",
		"Microbiology",
		"Modern Languages",
		"Molecular and Environmental Plant Sciences",
		"Nuclear Engineering",
		"Nutrition",
		"Ocean Engineering",
		"Oceanography",
		"Performance Studies",
		"Petroleum Engineering",
		"Philosophy",
		"Physical Education",
		"Physics",
		"Physiology of Reproduction",
		"Plant Breeding",
		"Plant Pathology",
		"Political Science",
		"Poultry Science",
		"Psychology",
		"Rangeland Ecology and Management",
		"Recreation, Park, and Tourism Sciences",
		"Safety Engineering",
		"School Psychology",
		"Science and Technology Journalism",
		"Sociology",
		"Soil Science",
		"Sport Management",
		"Statistics",
		"Toxicology",
		"University Studies",
		"Urban and Regional Planning",
		"Urban and Regional Sciences",
		"Veterinary Anatomy",
		"Veterinary Microbiology",
		"Veterinary Pathology",
		"Veterinary Medical Sciences",
		"Veterinary Parasitology",
		"Veterinary Physiology",
		"Veterinary Public Health",
		"Visualization Sciences",
		"Water Management and Hydrological Science",
		"Wildlife and Fisheries Sciences",
		"Wildlife Science",
		"Zoology"
	};
	
	/**
	 * Initial Degrees to create
	 */

	private static final DegreeLevelArray[] DEGREES_DEFINITIONS = {
		new DegreeLevelArray("Doctor of Philosophy", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Engineering", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Education", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Musical Arts", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Master of Arts", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Landscape Architecture", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Marine Resources Management", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Public Affairs", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Science", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Urban Planning", DegreeLevel.MASTERS),
		new DegreeLevelArray("Bachelor of Arts", DegreeLevel.UNDERGRADUATE),
		new DegreeLevelArray("Bachelor of Science", DegreeLevel.UNDERGRADUATE),
		new DegreeLevelArray("Bachelor of Environmental Design", DegreeLevel.UNDERGRADUATE)
	};
	
	/**
	 * Initial Document Types to create
	 */
	
	private static final DegreeLevelArray[] DOCTYPES_DEFINITIONS = {
		new DegreeLevelArray("Record of Study", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Dissertation", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Thesis", DegreeLevel.MASTERS),
		new DegreeLevelArray("Thesis", DegreeLevel.UNDERGRADUATE)
	};
	
	/**
	 * Initial Graduation Months to create
	 */
	
	private static final int[] GRAD_MONTHS_DEFINITIONS = {
		4, 7, 11 // 0 = january, 11 = december
	};	
		
	/**
	 * Generate Persons, Colleges, Departments, Majors,
	 * Degrees, Document Types and Graduation Months. 
	 */
	
	@Override
	public void doJob() {
		try {
			
		
			SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
			PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
			SettingsRepository settingsRepo = Spring.getBeanOfType(SettingsRepository.class);
			
			// Turn off authorizations.
			context.turnOffAuthorization(); 
			try {
				
				// Create all persons
				for(PersonsArray personDefinition : PERSONS_DEFINITIONS) {
					personRepo.createPerson(personDefinition.netId, personDefinition.email, personDefinition.firstName, personDefinition.lastName, personDefinition.role).save();
				}			
				
				for(String collegeDefinition : COLLEGES_DEFINITIONS) {
					settingsRepo.createCollege(collegeDefinition).save();
				}
				
				for(String departmentDefinition : DEPARTMENTS_DEFINITIONS) {
					settingsRepo.createDepartment(departmentDefinition).save();
				}
				
				for(String majorDefinition : MAJORS_DEFINITIONS) {
					settingsRepo.createMajor(majorDefinition).save();
				}
				
				for(DegreeLevelArray degreeDefinition : DEGREES_DEFINITIONS) {
					settingsRepo.createDegree(degreeDefinition.name, degreeDefinition.degreeLevel).save();
				}
				
				for(DegreeLevelArray docTypeDefinition : DOCTYPES_DEFINITIONS) {
					settingsRepo.createDocumentType(docTypeDefinition.name, docTypeDefinition.degreeLevel).save();
				}
				
				for(int gradMonthDefinition : GRAD_MONTHS_DEFINITIONS) {
					settingsRepo.createGraduationMonth(gradMonthDefinition).save();
				}
				
				// Save the database state
				JPA.em().flush();
			} finally {
				context.restoreAuthorization();
			}
			
		} catch (Exception e) {Logger.error(e, "Unable to load test data.");}
	}
	
	private static class DegreeLevelArray {
		String name;
		DegreeLevel degreeLevel;
		
		DegreeLevelArray(String name, DegreeLevel degreeLevel) {
			this.name = name;
			this.degreeLevel = degreeLevel;
		}
	}
	
	private static class PersonsArray {
		String netId;
		String email;
		String firstName;
		String lastName;
		RoleType role;
		
		PersonsArray(String netId, String email, String firstName, String lastName, RoleType role) {
			this.netId = netId;
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
			this.role = role;
		}
	}
}

