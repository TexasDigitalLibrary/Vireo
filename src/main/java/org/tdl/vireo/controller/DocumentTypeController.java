package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
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
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypeRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;
import edu.tamu.weaver.validation.model.BusinessValidationType;
import edu.tamu.weaver.validation.results.ValidationResults;

@RestController
@RequestMapping("/settings/document-type")
public class DocumentTypeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DocumentTypeRepo documentTypeRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse allDocumentTypes() {
        return new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createDocumentType(@WeaverValidatedModel DocumentType documentType) {
        logger.info("Creating document type with name " + documentType.getName());
        return new ApiResponse(SUCCESS, documentTypeRepo.create(documentType.getName()));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateDocumentType(@WeaverValidatedModel DocumentType documentType) {
        logger.info("Updating document type with name " + documentType.getName());
        return new ApiResponse(SUCCESS, documentTypeRepo.update(documentType));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    public ApiResponse removeDocumentType(@WeaverValidatedModel DocumentType documentType) {
        if (documentType.getFieldPredicate().getDocumentTypePredicate() == false) {
            logger.info("Removing document type with name " + documentType.getName());
            documentTypeRepo.remove(documentType);
            return new ApiResponse(SUCCESS);
        } else {
            ValidationResults validationResults = new ValidationResults();
            validationResults.setValid(false);
            validationResults.addMessage("business", BusinessValidationType.DELETE.getMessage(),"Document Type predicates can not be deleted");
            return new ApiResponse(INVALID,validationResults);
        }
    }

    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = DocumentType.class, params = { "0", "1" }) })
    public ApiResponse reorderDocumentTypes(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering document types");
        documentTypeRepo.reorder(src, dest);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = DocumentType.class, params = { "0" }) })
    public ApiResponse sortDocumentTypes(@PathVariable String column) {
        logger.info("Sorting document types by " + column);
        documentTypeRepo.sort(column);
        return new ApiResponse(SUCCESS);
    }

}
