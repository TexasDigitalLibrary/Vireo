package org.tdl.vireo.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdl.vireo.model.User;
import org.tdl.vireo.service.UserCredentialsService;

import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppStompInterceptor extends CoreStompInterceptor<User> {

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Override
    public Credentials getAnonymousCredentials() {
        return userCredentialsService.buildAnonymousCredentials();
    }

    @Override
    public User confirmCreateUser(Credentials credentials) {
        System.out.println("\n\n\nINTERCEPTED STOMP REQUEST\n\n\n");
        return userCredentialsService.updateUserByCredentials(credentials);
    }
}
