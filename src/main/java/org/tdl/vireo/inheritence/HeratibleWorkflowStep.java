package org.tdl.vireo.inheritence;

import java.util.List;

import org.tdl.vireo.model.WorkflowStep;

@SuppressWarnings("rawtypes")
public interface HeratibleWorkflowStep<M extends Heritable> {
    
    public void removeAggregateHeritableModel(M heritableModel);
    
    public void addOriginalHeritableModel(M heritableModal);
    
    public void addAggregateHeritableModel(M heritableModel);
    
    public void removeOriginalHeritableModel(M heritableModel);
    
    public List<M> getOriginalHeritableModels(M heritableModel);
    
    public List<M> getAggregateHeritableModels(M heritableModel);
    
    public boolean replaceAggregateHeritableModel(M srcHM, M detHM);
    
    public WorkflowStep clone();

}
