package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import edu.tamu.weaver.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;

/**
 * Controller in which to manage controlled vocabulary.
 *
 */
@RestController
@RequestMapping("/settings/field-values")
public class FieldValueController {

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    /**
     * Endpoint to request a field predicate by value.
     *
     * @param value The Field Predicate value (not the Field Value).
     *
     * @return ApiResponse with all matching field values.
     */
    @GetMapping("/predicate/{value}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getFieldValuesByPredicateValue(@PathVariable String value) {
        final FieldPredicate fieldPredicate = fieldPredicateRepo.findByValue(value);
        return new ApiResponse(SUCCESS, fieldValueRepo.findAllByFieldPredicate(fieldPredicate));
    }

}
