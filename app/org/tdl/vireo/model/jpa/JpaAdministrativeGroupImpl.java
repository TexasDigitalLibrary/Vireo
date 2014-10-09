/**
 * 
 */
package org.tdl.vireo.model.jpa;

import java.util.Comparator;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AdministrativeGroup;

/**
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 */
@Entity
@Table(name = "administrative_groups")
public class JpaAdministrativeGroupImpl extends JpaAbstractModel<JpaAdministrativeGroupImpl> implements AdministrativeGroup {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length = 255)
	public String name;

	@Column
	public HashMap<Integer, String> emails = new HashMap<Integer, String>();

	/**
	 * Construct a new JpaCollegeImpl
	 * 
	 * @param name
	 *            The name of the new college.
	 */
	protected JpaAdministrativeGroupImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();

		this.displayOrder = 0;
		this.name = name;
	}

	protected JpaAdministrativeGroupImpl(String name, HashMap<Integer, String> emails) {
		this(name);
		this.setEmails(emails);
	}

	@Override
	public JpaAdministrativeGroupImpl save() {
		assertManager();

		return super.save();
	}

	@Override
	public JpaAdministrativeGroupImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {

		assertManager();
		this.displayOrder = displayOrder;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		assertManager();

		this.name = name;
	}

	@Override
	public HashMap<Integer, String> getEmails() {
		return this.emails;
	}

	@Override
	public void setEmails(HashMap emails) {
		this.emails = emails;
	}

	@Override
	public void addEmail(String email) {
		Integer index = this.emails.size();
		this.emails.put(index, email);
	}

	@Override
	public void removeEmail(int index) {
		this.emails.remove(index);
	}
	
	public static class AdminGroupsComparator implements Comparator<AdministrativeGroup> {

        public final static Comparator<AdministrativeGroup> INSTANCE = new AdminGroupsComparator();

        public int compare(AdministrativeGroup adminGroup1, AdministrativeGroup adminGroup2) {
            return adminGroup1.getName().compareTo(adminGroup2.getName());
        }

    }
}
