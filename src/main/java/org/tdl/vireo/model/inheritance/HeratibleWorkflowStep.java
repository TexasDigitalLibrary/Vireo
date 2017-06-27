package org.tdl.vireo.model.inheritance;

import java.util.List;

import org.tdl.vireo.model.WorkflowStep;

@SuppressWarnings("rawtypes")
public interface HeratibleWorkflowStep<M extends HeritableComponent> {

    public void removeAggregateHeritableModel(M heritableModel);

    public void addOriginalHeritableModel(M heritableModal);

    public void addAggregateHeritableModel(M heritableModel);

    public void removeOriginalHeritableModel(M heritableModel);

    public List<M> getOriginalHeritableModels(Class M);

    public List<M> getAggregateHeritableModels(Class M);

    public boolean replaceAggregateHeritableModel(M srcHM, M detHM);

    public WorkflowStep clone();

}
