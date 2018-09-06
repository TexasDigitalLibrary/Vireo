package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SubmissionFieldProfileRepo extends WeaverRepo<SubmissionFieldProfile>, SubmissionFieldProfileRepoCustom {

}
