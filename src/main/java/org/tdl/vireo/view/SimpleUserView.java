package org.tdl.vireo.view;

import edu.tamu.weaver.user.model.IRole;

public interface SimpleUserView extends SimpleNamedModelView {

    public String getNetid();

    public String getEmail();

    public String getFirstName();

    public String getLastName();

    public String getMiddleName();

    public IRole getRole();

    public String getOrcid();

}
