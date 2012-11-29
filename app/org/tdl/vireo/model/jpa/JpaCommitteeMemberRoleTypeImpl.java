package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.DegreeLevel;

/**
 *  Jpa specific implementation of Vireo's Committee Member Role Type interface.
 *  
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 *
 */
@Entity
@Table(name = "committee_member_role_type",
       uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "level" } ) } )
public class JpaCommitteeMemberRoleTypeImpl extends JpaAbstractModel<JpaCommitteeMemberRoleTypeImpl> implements CommitteeMemberRoleType {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, length=255)
	public String name;

	@Column(nullable = false)
	public DegreeLevel level;

	/**
	 * Create a new JpaCommitteeMemberRoleTypeImpl
	 * 
	 * @param name
	 *            The name of the new role.
	 * @param level
	 *            The level of the new role.
	 */
	protected JpaCommitteeMemberRoleTypeImpl(String name, DegreeLevel level) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		if (level == null)
			throw new IllegalArgumentException("Role level is required");
		
		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
		this.level = level;
	}

	@Override
	public JpaCommitteeMemberRoleTypeImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaCommitteeMemberRoleTypeImpl delete() {
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
		return name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManager();
		
		this.name = name;
	}

	@Override
	public DegreeLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(DegreeLevel level) {
		
		if (level == null)
			throw new IllegalArgumentException("Role level is required");
		
		assertManager();
		
		this.level = level;
	}

}
