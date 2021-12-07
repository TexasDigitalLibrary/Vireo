package org.tdl.vireo.model;

import javax.persistence.MappedSuperclass;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class HibernateWorkaroundValidatingBaseEntity extends ValidatingBaseEntity implements HibernateWorkaround {

}
