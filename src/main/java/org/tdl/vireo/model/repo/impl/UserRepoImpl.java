package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

public class UserRepoImpl implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Override
    public User create(String email, String firstName, String lastName, AppRole role) {
        
        User newUser = new User(email, firstName, lastName, role);
        
        newUser = userRepo.save(newUser);
        
        // TODO: rethink adding activeFilter to all users. Only a few users will need them!
        NamedSearchFilter activeFilter = namedSearchFilterRepo.create(newUser);
        
        newUser.putSetting("id", newUser.getId().toString());
        newUser.putSetting("displayName", newUser.getFirstName() +" "+ newUser.getLastName());
        newUser.putSetting("preferedEmail", newUser.getEmail());
        
        newUser.setActiveFilter(activeFilter);
        
        return userRepo.save(newUser);
    }
    
    @Override
    public User create(String email, String firstName, String lastName, AppRole role, List<SubmissionListColumn> submissionViewColumns) {
        
        User newUser = create(email, firstName, lastName, role);
        
        newUser.setSubmissionViewColumns(submissionViewColumns);
        
        return userRepo.save(newUser);
    }
    
    @Override
    public void delete(User user) {
        namedSearchFilterRepo.delete(user.getActiveFilter());
        userRepo.delete(user.getId());
    }

}
