package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.Set;

public interface FieldValueRepo extends WeaverRepo<FieldValue>, FieldValueRepoCustom {

    @Query(value = "SELECT * FROM field_value f INNER JOIN submission_field_values s ON f.id = s.field_values_id  INNER JOIN field_predicate p ON f.field_predicate_id = p.id WHERE s.submission_id = :submissionId", nativeQuery = true)
    public Set<FieldValue> findAllBySubmissionId(@Param("submissionId") Long submissionId);
}
