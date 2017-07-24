package org.tdl.vireo.model;

import java.util.List;

public interface EntityControlledVocabulary {

    public String getControlledName();

    public String getControlledDefinition();

    public String getControlledIdentifier();

    public List<String> getControlledContacts();

}
