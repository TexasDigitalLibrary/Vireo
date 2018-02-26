package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestCodesService;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

/**
 * Controller in which to manage languages.
 *
 */
@RestController
@RequestMapping("/settings/language")
public class LanguageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private ProquestCodesService proquestCodesService;

    /**
     *
     * @return
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllLanguages() {
        return new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc());
    }

    /**
     *
     * @return
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createLanguage(@WeaverValidatedModel Language language) {
        logger.info("Creating language with name " + language.getName());
        return new ApiResponse(SUCCESS, languageRepo.create(language.getName()));
    }

    /**
     *
     * @return
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateLanguage(@WeaverValidatedModel Language language) {
        logger.info("Updating language with name " + language.getName());
        return new ApiResponse(SUCCESS, languageRepo.update(language));
    }

    /**
     *
     * @return
     */
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { FieldGloss.class, ControlledVocabulary.class }) })
    public ApiResponse removeLanguage(@WeaverValidatedModel Language language) {
        logger.info("Removing language with name " + language.getName());
        languageRepo.remove(language);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to reorder languages.
     *
     * @param src
     *            source position
     * @param dest
     *            destination position
     * @return ApiResponse indicating success
     */
    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = Language.class, params = { "0", "1" }) })
    public ApiResponse reorderLanguage(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering languages");
        languageRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to sort languages.
     *
     * @param column
     *            column to sort by
     * @return ApiResponse indicating success
     */
    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = Language.class, params = { "0" }) })
    public ApiResponse sortLanguage(@PathVariable String column) {
        logger.info("Sorting languages by " + column);
        languageRepo.sort(column);
        return new ApiResponse(SUCCESS);
    }

    /**
     *
     * @return
     */
    @RequestMapping("/proquest")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getProquestLanguageCodes() {
        return new ApiResponse(SUCCESS, proquestCodesService.getCodes("languages"));
    }

}
