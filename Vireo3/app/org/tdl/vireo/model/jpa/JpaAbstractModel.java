package org.tdl.vireo.model.jpa;

import javax.persistence.MappedSuperclass;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.Model;
import play.modules.spring.Spring;

/**
 * Abstract parent class of all JPA-based models. This class merges the return
 * datatypes the basic save, delete, refresh, merge methods between our Vireo
 * interfaces and Play's model object. This is also a place for sharing code
 * between the model implementations such as assertions about the current
 * security context etc.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 * @param <T>
 *            The specific model implementation type.
 */
@MappedSuperclass
public abstract class JpaAbstractModel<T extends JpaAbstractModel> extends Model implements AbstractModel {
	
	@Override
	public T save() {
		T result = super.save();
		
		// Tell the indexer that this object has been updated.
		Indexer indexer = Spring.getBeanOfType(Indexer.class);
		indexer.updated(result);
		
		return result;
	}

	@Override
	public T delete() {
		T result =  super.delete();
		
		// Tell the indexer that this object has been updated.
		Indexer indexer = Spring.getBeanOfType(Indexer.class);
		indexer.updated(result);
		
		return result;
	}

	@Override
	public T refresh() {
		return super.refresh();
	}

	@Override
	public T merge() {
		return super.merge();
	}

	@Override
	public T detach() {
		this.em().detach(this);
		return (T) this;
	}
	
	/**
	 * Assert that the current user is an administrator, if not then a Security
	 * Exception will be thrown.
	 */
	public void assertAdministrator() {
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		
		if (context.isAuthorizationActive() && !context.isAdministrator())
			throw new SecurityException("This operation requires administrative level access.");
	}
	
	/**
	 * Assert that the current user is an manager or above, if not then a Security
	 * Exception will be thrown.
	 */
	public void assertManager() {
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		
		if (context.isAuthorizationActive() && !context.isManager())
			throw new SecurityException("This operation requires manager level access.");
	}
	
	/**
	 * Assert that the current user is an reviewer or above, if not then a Security
	 * Exception will be thrown.
	 */
	public void assertReviewer() {
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		
		if (context.isAuthorizationActive() && !context.isReviewer())
			throw new SecurityException("This operation requires reviewer level access.");
		
	}
	
	/**
	 * Assert that the current user is an administrator or the supplied person.
	 * If not then a Security Exception will be thrown.
	 * 
	 * @param owner
	 *            The owner of the object being asserted.
	 */
	public void assertAdministratorOrOwner(Person owner) {
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);

		if (context.isAuthorizationActive() && !(owner.equals(context.getPerson()) || context.isAdministrator()))
			throw new SecurityException("This operation requires administrative level access, or you must be the owner of this object.");

	}
	
	/**
	 * Assert that the current user is an manager or above, or the supplied person.
	 * If not then a Security Exception will be thrown.
	 * 
	 * @param owner
	 *            The owner of the object being asserted.
	 */
	public void assertManagerOrOwner(Person owner) {
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);

		if (context.isAuthorizationActive() && !(owner.equals(context.getPerson()) || context.isManager()))
			throw new SecurityException("This operation requires manager level access, or you must be the owner of this object.");

	}
	
	/**
	 * Assert that the current user is an reviewer or above, or the supplied person.
	 * If not then a Security Exception will be thrown.
	 * 
	 * @param owner
	 *            The owner of the object being asserted.
	 */
	public void assertReviewerOrOwner(Person owner) {
		
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);

		if (context.isAuthorizationActive() && !(owner.equals(context.getPerson()) || context.isReviewer()))
			throw new SecurityException("This operation requires reviewer level access, or you must be the owner of this object.");
		
	}
}
