package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;

public class MockPersonRepository implements PersonRepository {
	private List<Person> people = new ArrayList<Person>();

	@Override
	public Person createPerson(String netId, String email, String firstName, String lastName, RoleType role) {

		MockPerson mockPerson = new MockPerson();
		mockPerson.netid = netId;
		mockPerson.email = email;
		mockPerson.firstName = firstName;
		mockPerson.lastName = lastName;
		mockPerson.role = role;

		people.add(mockPerson);

		return mockPerson;
	}

	@Override
	public Person findPerson(Long id) {
		// DO NOTHING
		return null;
	}

	@Override
	public Person findPersonByEmail(String email) {
		// DO NOTHING
		return null;
	}

	@Override
	public Person findPersonByNetId(String netId) {
		// DO NOTHING
		return null;
	}

	@Override
	public List<Person> findPersonsByRole(RoleType type) {
		// DO NOTHING
		return null;
	}

	@Override
	public List<Person> searchPersons(String query, int offset, int limit) {
		// DO NOTHING
		return null;
	}

	@Override
	public List<Person> findAllPersons() {
		return people;
	}

	@Override
	public long findPersonsTotal() {
		return people.size();
	}

	@Override
	public Preference findPreference(Long id) {
		// DO NOTHING
		return null;
	}
}
