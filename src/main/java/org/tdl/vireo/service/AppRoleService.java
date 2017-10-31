package org.tdl.vireo.service;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Role;

import edu.tamu.weaver.user.model.IRole;
import edu.tamu.weaver.user.role.service.WeaverRoleService;

@Service
public class AppRoleService extends WeaverRoleService {

    @Override
    public IRole valueOf(String role) {
        return Role.valueOf(Role.class, role);
    }

}
