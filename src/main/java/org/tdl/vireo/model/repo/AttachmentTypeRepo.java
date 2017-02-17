package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.custom.AttachmentTypeRepoCustom;

public interface AttachmentTypeRepo extends JpaRepository<AttachmentType, Long>, AttachmentTypeRepoCustom {

    public AttachmentType findByName(String name);

    public AttachmentType findByNameAndFieldPredicate(String name, FieldPredicate fieldPredicate);

    public List<AttachmentType> findAllByOrderByPositionAsc();


}
