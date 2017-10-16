package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.EXISTS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.NONEXISTS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.DocumentTypeRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@Controller
@ApiMapping("/settings/document-type")
public class DocumentTypeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DocumentTypeRepo documentTypeRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDocumentTypes() {
        return new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE), @WeaverValidation.Business(value = EXISTS) })
    public ApiResponse createDocumentType(@WeaverValidatedModel DocumentType documentType) {
        logger.info("Creating document type with name " + documentType.getName());
        documentType = documentTypeRepo.create(documentType.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, documentType);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE), @WeaverValidation.Business(value = NONEXISTS) })
    public ApiResponse updateDocumentType(@WeaverValidatedModel DocumentType documentType) {
        logger.info("Updating document type with name " + documentType.getName());
        documentType = documentTypeRepo.save(documentType);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, documentType);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { FieldValue.class }, path = { "fieldPredicate", "documentTypePredicate" }, restrict = "true"), @WeaverValidation.Business(value = NONEXISTS) })
    public ApiResponse removeDocumentType(@WeaverValidatedModel DocumentType documentType) {
        logger.info("Removing document type with name " + documentType.getName());
        documentTypeRepo.remove(documentType);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = DocumentType.class, params = { "0", "1" }) })
    public ApiResponse reorderDocumentTypes(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering document types");
        documentTypeRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = DocumentType.class, params = { "0" }) })
    public ApiResponse sortDocumentTypes(@ApiVariable String column) {
        logger.info("Sorting document types by " + column);
        documentTypeRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypeRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
