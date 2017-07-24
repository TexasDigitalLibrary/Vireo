package org.tdl.vireo.model.jpa;

import java.util.List;

import javax.persistence.TypedQuery;

import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;

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
		return JpaPersonImpl.find("email = (?1)", email).first();
	}

	@Override
	public Person findPersonByNetId(String netid) {
		return JpaPersonImpl.find("netid = (?1)", netid).first();

	}
	
	@Override
	public List<Person> findPersonsByRole(RoleType type) {
		
		final String select = "SELECT p FROM JpaPersonImpl AS p WHERE p.role >= :type ORDER BY p.lastName ASC, p.firstName ASC, p.id ASC";
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
					"LOWER( p.netid ) LIKE :query OR " +
					"LOWER( p.email ) LIKE :query OR " +
					"LOWER( p.firstName ) LIKE :query OR " +
					"LOWER( p.middleName ) LIKE :query OR "+
					"LOWER( p.lastName ) LIKE :query OR "+
					"LOWER( p.displayName ) LIKE :query OR "+
					"LOWER( CONCAT( p.firstName, ' ', p.lastName ) ) LIKE :query OR " +
					"LOWER( CONCAT( p.firstName, ' ', p.middleName, ' ', p.lastName ) ) LIKE :query "
					;
		
		// Combine to the final select statement;
		final String select = 
			"SELECT p " +
			"FROM JpaPersonImpl AS p " +
			where +
			"ORDER BY p.lastName ASC, p.firstName ASC, p.id ASC";
		
		
		TypedQuery<JpaPersonImpl> typedQuery = JPA.em().createQuery(select, JpaPersonImpl.class);
		if (query != null && query.trim().length() > 0)
			typedQuery.setParameter("query", "%"+query.toLowerCase()+"%");
		typedQuery.setFirstResult(offset);
		typedQuery.setMaxResults(limit);
		
		List results =  typedQuery.getResultList();
		return results;
	}
	
	@Override
	public long findPersonsTotal() {
		return JpaPersonImpl.count();
	}

	// ///////////////////////////
	// Personal Preference Model
	// ///////////////////////////
	
	@Override
	public Preference findPreference(Long id) {
		return (Preference) JpaPreferenceImpl.findById(id);
	}

}
