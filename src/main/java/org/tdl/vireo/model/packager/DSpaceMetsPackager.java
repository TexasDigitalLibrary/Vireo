package org.tdl.vireo.model.packager;

import javax.persistence.Entity;

@Entity
public class DSpaceMetsPackager extends AbstractPackager implements Packager {
	public DSpaceMetsPackager() {
		setName("DSpace METS");
	}
}
