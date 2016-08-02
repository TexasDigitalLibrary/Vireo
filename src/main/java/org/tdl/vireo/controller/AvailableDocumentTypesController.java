package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;
import static edu.tamu.framework.enums.MethodValidationType.SORT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.DocumentTypesRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/document-type")
public class AvailableDocumentTypesController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DocumentTypesRepo documentTypesRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDocumentTypes() {
        return new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc());
    }

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createDocumentType(@ApiValidatedModel DocumentType documentType) {
        logger.info("Creating document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel());
        documentType = documentTypesRepo.create(documentType.getName(), documentType.getDegreeLevel());
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, documentType);
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateDocumentType(@ApiValidatedModel DocumentType documentType) {
        logger.info("Updating document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel());
        documentType = documentTypesRepo.save(documentType);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, documentType);
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE, joins = { FieldValue.class }, path = {"fieldPredicate", "documentTypePredicate"}, restrict = "true"), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeDocumentType(@ApiValidatedModel DocumentType documentType) {
        logger.info("Removing document type with name " + documentType.getName());
        documentTypesRepo.remove(documentType);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = DocumentType.class, params = { "0", "1" }) })
    public ApiResponse reorderDocumentTypes(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering document types");
        documentTypesRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = SORT, model = DocumentType.class, params = { "0" }) })
    public ApiResponse sortDocumentTypes(@ApiVariable String column) {
        logger.info("Sorting document types by " + column);
        documentTypesRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
