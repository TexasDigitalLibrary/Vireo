package org.tdl.vireo.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdl.vireo.model.User;
import org.tdl.vireo.service.UserCredentialsService;

import edu.tamu.framework.interceptor.CoreRestInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppRestInterceptor extends CoreRestInterceptor<User> {

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Override
    public Credentials getAnonymousCredentials() {
        return userCredentialsService.buildAnonymousCredentials();
    }

    @Override
    public User confirmCreateUser(Credentials credentials) {
        return userCredentialsService.updateUserByCredentials(credentials);
    }
}
