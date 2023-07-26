package org.tdl.vireo.view;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.tamu.weaver.data.resolver.BaseEntityIdResolver;
import java.util.List;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;

public interface SimpleWorkflowStepView extends SimpleNamedModelView {

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = TreeOrganizationView.class, resolver = BaseEntityIdResolver.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public TreeOrganizationView getOriginatingOrganization();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SimpleWorkflowStepView.class, resolver = BaseEntityIdResolver.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public SimpleWorkflowStepView getOriginatingWorkflowStep();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, resolver = BaseEntityIdResolver.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public List<FieldProfile> getOriginalFieldProfiles();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Note.class, resolver = BaseEntityIdResolver.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public List<Note> getOriginalNotes();

    public Boolean getOverrideable();

    public List<FieldProfile> getAggregateFieldProfiles();

    public List<Note> getAggregateNotes();

    public String getInstructions();
}
