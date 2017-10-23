package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.AbstractFieldProfile;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.FieldPredicateRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

/**
 * Controller in which to manage controlled vocabulary.
 *
 */
@RestController
@RequestMapping("/settings/field-predicates")
public class FieldPredicateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Endpoint to request all field predicates.
     *
     * @return ApiResponse with all input types.
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getAllFieldPredicates() {
        return new ApiResponse(SUCCESS, fieldPredicateRepo.findAll());
    }

    /**
     * Endpoint to request a field predicate by value.
     *
     * @return ApiResponse with all input types.
     */
    @RequestMapping("/{value}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getFieldPredicateByValue(@PathVariable String value) {
        return new ApiResponse(SUCCESS, fieldPredicateRepo.findByValue(value));
    }

    /**
     * Endpoint to create a field predicate
     *
     * @return ApiResponse with all input types.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createFieldPredicate(@WeaverValidatedModel FieldPredicate fieldPredicate) {
        logger.info("Creating Field Predicate:  " + fieldPredicate.getValue());
        FieldPredicate fp = fieldPredicateRepo.create(fieldPredicate.getValue(), new Boolean(false));
        simpMessagingTemplate.convertAndSend("/channel/settings/field-predicates", new ApiResponse(SUCCESS, fieldPredicateRepo.findAll()));
        return new ApiResponse(SUCCESS, fp);
    }

    /**
     * Endpoint to remove a field predicate
     *
     * @return ApiResponse with all input types.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { AbstractFieldProfile.class, FieldValue.class }) })
    public ApiResponse removeFieldPredicate(@WeaverValidatedModel FieldPredicate fieldPredicate) {
        logger.info("Deleting Field Predicate:  " + fieldPredicate.getValue());
        fieldPredicateRepo.delete(fieldPredicate);
        simpMessagingTemplate.convertAndSend("/channel/settings/field-predicates", new ApiResponse(SUCCESS, fieldPredicateRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to update a field predicate
     *
     * @return ApiResponse with all input types.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateFieldPredicate(@WeaverValidatedModel FieldPredicate fieldPredicate) {
        logger.info("Updating Field Predicate:  " + fieldPredicate.getValue());
        fieldPredicateRepo.save(fieldPredicate);
        simpMessagingTemplate.convertAndSend("/channel/settings/field-predicates", new ApiResponse(SUCCESS, fieldPredicateRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
