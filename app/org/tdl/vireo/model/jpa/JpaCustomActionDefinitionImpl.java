package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.CustomActionDefinition;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's CustomActionDefinition interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "CustomActionDefinition")
public class JpaCustomActionDefinitionImpl extends Model implements
		CustomActionDefinition {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public String label;

	/**
	 * Construct a new JpaCustomActionDefinitonImpl
	 * 
	 * @param label
	 *            The new label for this action definition
	 */
	protected JpaCustomActionDefinitionImpl(String label) {
		// TODO: check the arguments.

		this.displayOrder = 0;
		this.label = label;
	}

	@Override
	public JpaCustomActionDefinitionImpl save() {
		return super.save();
	}

	@Override
	public JpaCustomActionDefinitionImpl delete() {
		return super.delete();
	}

	@Override
	public JpaCustomActionDefinitionImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaCustomActionDefinitionImpl merge() {
		return super.merge();
	}

    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		
		// TODO: check label
		
		this.label = label;
	}

}
