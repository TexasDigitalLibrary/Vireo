package org.tdl.vireo.model.repo.simple;

import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.tdl.vireo.model.simple.SimpleFieldValue;

public interface SimpleFieldValueRepo extends Repository<SimpleFieldValue, Long> {

    @Query(value = "SELECT * FROM field_value f INNER JOIN submission_field_values s ON f.id = s.field_values_id WHERE s.submission_id = :submissionId", nativeQuery=true)
    public Set<SimpleFieldValue> findAllBySubmissionId(@Param("submissionId") Long submissionId);
}
