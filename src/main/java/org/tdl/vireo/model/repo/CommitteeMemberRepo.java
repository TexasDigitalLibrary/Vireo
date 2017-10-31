package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.repo.custom.CommitteeMemberRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface CommitteeMemberRepo extends WeaverRepo<CommitteeMember>, CommitteeMemberRepoCustom {

}
