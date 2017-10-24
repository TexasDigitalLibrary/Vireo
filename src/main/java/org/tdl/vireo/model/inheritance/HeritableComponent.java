package org.tdl.vireo.model.inheritance;

import org.tdl.vireo.model.WorkflowStep;

import edu.tamu.weaver.data.model.WeaverEntity;

public interface HeritableComponent<M> extends WeaverEntity {

    public void setOriginating(M originatingHeritableModel);

    public M getOriginating();

    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);

    public WorkflowStep getOriginatingWorkflowStep();

    public Boolean getOverrideable();

    public M clone();

}
