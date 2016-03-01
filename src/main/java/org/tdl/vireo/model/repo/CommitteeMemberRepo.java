package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.repo.custom.CommitteeMemberRepoCustom;

public interface CommitteeMemberRepo extends JpaRepository<CommitteeMember, Long>, CommitteeMemberRepoCustom {

}
