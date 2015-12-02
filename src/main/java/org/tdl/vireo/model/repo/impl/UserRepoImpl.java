package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

public class UserRepoImpl implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Override
    public User create(String email, String firstName, String lastName, Role role) {
        
        User newUser = new User(email, firstName, lastName, role);
        newUser.putSetting("displayName", firstName +" "+lastName);
        newUser.putSetting("preferedEmail", email);
        
        return userRepo.save(newUser);
    }

}
