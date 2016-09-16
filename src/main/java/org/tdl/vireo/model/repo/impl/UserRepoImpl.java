package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.NamedSearchFilterCriteria;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterCriteriaRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;

public class UserRepoImpl implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private NamedSearchFilterCriteriaRepo namedSearchFilterCriteriaRepo;

    @Override
    public User create(String email, String firstName, String lastName, AppRole role) {
        
        User newUser = new User(email, firstName, lastName, role);
        
        newUser = userRepo.save(newUser);
        
        NamedSearchFilterCriteria activeFilter = namedSearchFilterCriteriaRepo.create(newUser, "Default");
        
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

}
