package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;

public interface UserRepoCustom {

    public User create(String email, String firstName, String lastName, AppRole role);

    public void delete(User user);

}
