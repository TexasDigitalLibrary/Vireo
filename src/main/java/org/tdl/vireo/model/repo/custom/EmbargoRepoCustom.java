package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;

public interface EmbargoRepoCustom {

    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive);

    public void reorder(Long srcPosition, Long destPosition, EmbargoGuarantor guarantor);

    public void sort(String column, EmbargoGuarantor guarantor);

    public void remove(Embargo embargo);

}
