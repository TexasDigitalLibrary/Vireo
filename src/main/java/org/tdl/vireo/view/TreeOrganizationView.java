package org.tdl.vireo.view;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Set;
import org.tdl.vireo.model.OrganizationCategory;

public interface TreeOrganizationView extends SimpleNamedModelView {

    public OrganizationCategory getCategory();

    public Boolean getAcceptsSubmissions();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = TreeOrganizationView.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public TreeOrganizationView getParentOrganization();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Set<TreeOrganizationView> getChildrenOrganizations();

}
