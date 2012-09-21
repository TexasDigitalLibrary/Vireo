package org.tdl.vireo.model.jpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.search.Indexer;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's CustomActionDefinition interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "custom_action_definition")
public class JpaCustomActionDefinitionImpl extends JpaAbstractModel<JpaCustomActionDefinitionImpl> implements
		CustomActionDefinition {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255)
	public String label;

	/**
	 * Construct a new JpaCustomActionDefinitonImpl
	 * 
	 * @param label
	 *            The new label for this action definition
	 */
	protected JpaCustomActionDefinitionImpl(String label) {
		
		if (label == null || label.length() == 0)
			throw new IllegalArgumentException("Label is required");

		assertManager();
		
		this.displayOrder = 0;
		this.label = label;
	}
	
	@Override
	public JpaCustomActionDefinitionImpl save() {
		assertManager();

		return super.save();
	}

	@Override
	public JpaCustomActionDefinitionImpl delete() {
		
		assertManager();
		
		// Tell the indexer about all the submissions that will be effected by
		// this deletion.
		TypedQuery<Long> effectedQuery = em().createQuery(
				"SELECT sub.id "+
				"FROM JpaSubmissionImpl AS sub "+
				"JOIN sub.customActions as action "+
				"WHERE action.definition = :definition ",
				Long.class);
		effectedQuery.setParameter("definition", this);
		List<Long> effectedIds = effectedQuery.getResultList();
		Indexer indexer = Spring.getBeanOfType(Indexer.class);
		indexer.updated(effectedIds);
		
		// Delete all values associated with this definition
		em().createQuery(
			"DELETE FROM JpaCustomActionValueImpl " +
					"WHERE Definition_Id = ? " 
			).setParameter(1, this.getId())
			.executeUpdate();
		
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
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		
		if (label == null || label.length() == 0)
			throw new IllegalArgumentException("Label is required");
		
		assertManager();
		
		this.label = label;
	}

}
