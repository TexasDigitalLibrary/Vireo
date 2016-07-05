package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

public class UserRepoImpl implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Override
    public User create(String email, String firstName, String lastName, AppRole role) {
        
        User newUser = new User(email, firstName, lastName, role);
        
        newUser = userRepo.save(newUser);
        
        newUser.putSetting("id", newUser.getId().toString());
        newUser.putSetting("displayName", newUser.getFirstName() +" "+ newUser.getLastName());
        newUser.putSetting("preferedEmail", newUser.getEmail());
        
        return userRepo.save(newUser);
    }

}
