package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.FieldPredicate;

public interface AttachmentTypeRepoCustom {

    public AttachmentType create(String name);

    public AttachmentType create(String name, FieldPredicate fieldPredicate);

    public void reorder(Long src, Long dest);

    public void sort(String column);

    public void remove(AttachmentType attachmentType);

}
