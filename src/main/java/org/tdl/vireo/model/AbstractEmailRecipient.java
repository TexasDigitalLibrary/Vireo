package org.tdl.vireo.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Inheritance
public class AbstractEmailRecipient extends BaseEntity {}
