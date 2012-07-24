package org.tdl.vireo.model.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

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
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SettingsRepository;

import play.db.jpa.JPA;

/**
 * Jpa specific implementation of the Vireo Person Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaPersonRepositoryImpl implements PersonRepository {

	// //////////////
	// Person Model
	// //////////////
	
	@Override
	public Person createPerson(String netid, String email, String firstName,
			String lastName, RoleType role) {
		return new JpaPersonImpl(netid, email, firstName, lastName, role);
	}

	@Override
	public Person findPerson(Long id) {
		return (Person) JpaPersonImpl.findById(id);
	}

	@Override
	public Person findPersonByEmail(String email) {
		return JpaPersonImpl.find("email = ?", email).first();
	}

	@Override
	public Person findPersonByNetId(String netid) {
		return JpaPersonImpl.find("netid = ?", netid).first();

	}
	
	@Override
	public List<Person> findPersonsByRole(RoleType type) {
		
		final String select = "SELECT p FROM JpaPersonImpl AS p WHERE p.role >= :type";
		TypedQuery<JpaPersonImpl> query = JPA.em().createQuery(select, JpaPersonImpl.class);
		query.setParameter("type", type);
		
		List<JpaPersonImpl> results = query.getResultList();

		return (List) results;
	}

	@Override
	public List<Person> findAllPersons() {
		return (List) JpaPersonImpl.findAll();
	}
	
	@Override
	public List<Person> searchPersons(String query,int offset, int limit) {
		
		
		// If the user has provided a query then generate a WHERE clause.
		final String where;
		if (query == null || query.trim().length() == 0)
			where = "";
		else
			where = "WHERE " +
					"p.netid LIKE :query OR " +
					"p.email LIKE :query OR " +
					"p.firstName LIKE :query OR " +
					"p.middleName LIKE :query OR "+
					"p.lastName LIKE :query OR "+
					"p.displayName LIKE :query ";
		
		// Combine to the final select statement;
		final String select = 
			"SELECT p " +
			"FROM JpaPersonImpl AS p " +
			where +
			"ORDER BY p.lastName ASC, p.firstName ASC, p.id ASC";
		
		
		TypedQuery<JpaPersonImpl> typedQuery = JPA.em().createQuery(select, JpaPersonImpl.class);
		if (query != null && query.trim().length() > 0)
			typedQuery.setParameter("query", "%"+query+"%");
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		
		List results =  typedQuery.getResultList();
		return results;
	}

	// ///////////////////////////
	// Personal Preference Model
	// ///////////////////////////
	
	@Override
	public Preference findPreference(Long id) {
		return (Preference) JpaPreferenceImpl.findById(id);
	}

}
