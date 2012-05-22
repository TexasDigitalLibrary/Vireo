package org.tdl.vireo.model.jpa;

import java.util.List;
import java.util.Set;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SettingsRepository;

/**
 * Jpa specific implementation of the Vireo Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSettingsRepositoryImpl implements SettingsRepository {
	
	// ///////////////////////////////////////////////
	// Degree, Major, College, and Department Models
	// ///////////////////////////////////////////////
	
	@Override
	public Degree createDegree(String name, DegreeLevel level) {
		return new JpaDegreeImpl(name, level);
	}

	@Override
	public Degree findDegree(Long id) {
		return (Degree) JpaDegreeImpl.findById(id);
	}

	@Override
	public List<Degree> findAllDegrees() {
		return (List) JpaDegreeImpl.find("order by displayOrder").fetch();
	}

	@Override
	public Major createMajor(String name) {
		return new JpaMajorImpl(name);
	}

	@Override
	public Major findMajor(Long id) {
		return (Major) JpaMajorImpl.findById(id);
	}

	@Override
	public List<Major> findAllMajors() {
		return (List) JpaMajorImpl.find("order by displayOrder").fetch();
	}

	@Override
	public College createCollege(String name) {
		return new JpaCollegeImpl(name);
		}

	@Override
	public College findCollege(Long id) {
		return (College) JpaCollegeImpl.findById(id);
	}

	@Override
	public List<College> findAllColleges() {
		return (List) JpaCollegeImpl.find("order by displayOrder").fetch();
	}

	@Override
	public Department createDepartment(String name) {
		return new JpaDepartmentImpl(name);
	}

	@Override
	public Department findDepartment(Long id) {
		return (Department) JpaDepartmentImpl.findById(id);

	}

	@Override
	public List<Department> findAllDepartments() {
		return (List) JpaDepartmentImpl.find("order by displayOrder").fetch();
	}

	// /////////////////////
	// Document Type Model
	// /////////////////////
	
	@Override
	public DocumentType createDocumentType(String name, DegreeLevel level) {
		return new JpaDocumentTypeImpl(name, level);
	}

	@Override
	public DocumentType findDocumentType(Long id) {
		return (DocumentType) JpaDocumentTypeImpl.findById(id);
	}

	@Override
	public List<DocumentType> findAllDocumentTypes(DegreeLevel level) {
		return JpaDocumentTypeImpl.find("level = ?", level).fetch();
	}

	@Override
	public List<DocumentType> findAllDocumentTypes() {
		return (List) JpaDocumentTypeImpl.find("order by displayOrder").fetch();
	}

	// ////////////////////
	// Embargo Type Model
	// ////////////////////
	
	@Override
	public EmbargoType createEmbargoType(String name, String description,
			Long duration, boolean active) {
		return new JpaEmbargoTypeImpl(name, description, duration, active);
	}

	@Override
	public EmbargoType findEmbargoType(Long id) {
		return (EmbargoType) JpaEmbargoTypeImpl.findById(id);
	}

	@Override
	public List<EmbargoType> findAllEmbargoTypes() {
		return (List) JpaEmbargoTypeImpl.find("order by displayOrder").fetch();
	}

	// ////////////////////////
	// Graduation Month Model
	// ////////////////////////
	
	@Override
	public GraduationMonth createGraduationMonth(int month) {
		return new JpaGraduationMonthImpl(month);
	}
	
	@Override
	public GraduationMonth findGraduationMonth(Long id) {
		return (GraduationMonth) JpaGraduationMonthImpl.findById(id);
	}

	@Override
	public List<GraduationMonth> findAllGraduationMonths() {
		return (List) JpaGraduationMonthImpl.find("order by displayOrder").fetch();
	}

	// //////////////////////
	// Email Template Model
	// //////////////////////
	
	@Override
	public EmailTemplate createEmailTemplate(String subject, String message) {
		return new JpaEmailTemplateImpl(subject, message);
	}

	@Override
	public EmailTemplate findEmailTemplate(Long id) {
		return (EmailTemplate) JpaEmailTemplateImpl.findById(id);
	}

	@Override
	public List<EmailTemplate> findAllEmailTemplates() {
		return (List) JpaEmailTemplateImpl.find("order by displayOrder").fetch();
	}

	// ///////////////////////////
	// Custom action definitions
	// ///////////////////////////
	
	@Override
	public CustomActionDefinition createCustomActionDefinition(String label) {
		return new JpaCustomActionDefinitionImpl(label);
	}

	@Override
	public CustomActionDefinition findCustomActionDefinition(Long id) {
		return (CustomActionDefinition) JpaCustomActionDefinitionImpl.findById(id);
	}

	@Override
	public List<CustomActionDefinition> findAllCustomActionDefinition() {
		return (List) JpaCustomActionDefinitionImpl.find("order by displayOrder").fetch();
	}

	// ///////////////////////////
	// System wide configuration
	// ///////////////////////////
	
	@Override
	public Configuration createConfiguration(String name, String value) {
		return new JpaConfigurationImpl(name,value); 
	}

	@Override
	public Configuration findConfiguration(Long id) {
		return (Configuration) JpaConfigurationImpl.findById(id);
	}

	@Override
	public Configuration findConfigurationByName(String name) {
		return JpaConfigurationImpl.find("name = ?", name).first();
	}

	@Override
	public List<Configuration> findAllConfigurations() {
		return (List) JpaConfigurationImpl.findAll();

	}

}
