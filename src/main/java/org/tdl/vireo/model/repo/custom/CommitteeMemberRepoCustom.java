package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.CommitteeMember;

public interface CommitteeMemberRepoCustom {

    public CommitteeMember create(String firstName, String lastName, String email);

}
