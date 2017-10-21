package org.tdl.vireo.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;

@Service
public class VireoUserDetailsService extends AbstractWeaverUserDetailsService<User, UserRepo> {

    @Override
    public UserDetails buildUserDetails(User user) {
        return user;
    }

}
