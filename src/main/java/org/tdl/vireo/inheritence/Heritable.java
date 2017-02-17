package org.tdl.vireo.inheritence;

import org.tdl.vireo.model.WorkflowStep;

public interface Heritable<M> {

    public Long getId();

    public void setId(Long id);

    public void setOriginating(M originatingHeritableModel);

    public M getOriginating();

    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);

    public WorkflowStep getOriginatingWorkflowStep();

    public Boolean getOverrideable();

    public M clone();

}
