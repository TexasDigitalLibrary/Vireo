package org.tdl.vireo.model.jpa;

import javax.persistence.MappedSuperclass;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.GenericModel;
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
		return super.save();
	}

	@Override
	public T delete() {
		return super.delete();
	}

	@Override
	public T refresh() {
		return super.refresh();
	}

	@Override
	public T merge() {
		return super.merge();
	}
}
