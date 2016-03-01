package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.repo.CommitteeMemberRepo;
import org.tdl.vireo.model.repo.custom.CommitteeMemberRepoCustom;

public class CommitteeMemberRepoImpl implements CommitteeMemberRepoCustom {

    @Autowired
    private CommitteeMemberRepo committeeMemberRepo;

    @Override
    public CommitteeMember create(String firstName, String lastName, String email) {
        return committeeMemberRepo.save(new CommitteeMember(firstName, lastName, email));
    }
}
