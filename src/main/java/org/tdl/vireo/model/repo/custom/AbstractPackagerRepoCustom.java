package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.formatter.AbstractFormatter;
import org.tdl.vireo.model.packager.Packager;

public interface AbstractPackagerRepoCustom {

    public Packager createDSpaceMetsPackager(String name, AbstractFormatter formatter);

    public Packager createProQuestUmiPackager(String name, AbstractFormatter formatter);

}
