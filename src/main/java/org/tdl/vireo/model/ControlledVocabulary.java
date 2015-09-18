package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class ControlledVocabulary extends BaseEntity {
	Set<String> values;
}
