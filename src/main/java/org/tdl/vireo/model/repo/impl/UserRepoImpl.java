package org.tdl.vireo.model.repo.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.UserRepoCustom;
import org.tdl.vireo.util.FileIOUtility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRepoImpl implements UserRepoCustom {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NamedSearchFilterGroupRepo namedSearchFilterRepo;
    
    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;
    
    @Autowired
    private FileIOUtility fileIOUtility;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public User create(String email, String firstName, String lastName, AppRole role) {

        User newUser = new User(email, firstName, lastName, role);

        newUser = userRepo.save(newUser);

        NamedSearchFilterGroup activeFilter = namedSearchFilterRepo.create(newUser);

        newUser.putSetting("id", newUser.getId().toString());
        newUser.putSetting("displayName", newUser.getFirstName() + " " + newUser.getLastName());
        newUser.putSetting("preferedEmail", newUser.getEmail());        
        newUser.setActiveFilter(activeFilter);
        
        List<SubmissionListColumn> defaultFilterColumns = null;
		try {
			defaultFilterColumns = objectMapper.readValue(fileIOUtility.getFileFromResource("classpath:/filter_columns/default_filter_columns.json"), new TypeReference<List<SubmissionListColumn>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (SubmissionListColumn defaultFilterColumn : defaultFilterColumns) {
			SubmissionListColumn dbDefaultFilterColumn = submissionListColumnRepo.findByTitle(defaultFilterColumn.getTitle());
			newUser.addFilterColumn(dbDefaultFilterColumn);
        }       

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
