package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypesRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Controller
@ApiMapping("/settings/document-type")
public class AvailableDocumentTypesController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private DocumentTypesRepo documentTypesRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDocumentTypes() {
        return new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc());
    }
    
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createDocumentType(@ApiValidatedModel DocumentType documentType) {
        
        // will attach any errors to the BindingResult when validating the incoming documentType
        documentType = documentTypesRepo.validateCreate(documentType);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(documentType);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel());
                documentTypesRepo.create(documentType.getName(), documentType.getDegreeLevel());
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(VALIDATION_WARNING, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't create document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateDocumentType(@ApiValidatedModel DocumentType documentType) {
        
        // will attach any errors to the BindingResult when validating the incoming documentType
        documentType = documentTypesRepo.validateUpdate(documentType);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(documentType);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel());
                documentTypesRepo.save(documentType);
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(VALIDATION_WARNING, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't update document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeDocumentType(@ApiValidatedModel DocumentType documentType) {

        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(documentType);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing document type with name " + documentType.getName());
                documentTypesRepo.remove(documentType);
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(VALIDATION_WARNING, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't remove document type with name " + documentType.getName() + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderDocumentTypes(@ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "documentType");
        
        // will attach any errors to the BindingResult when validating the incoming src, dest
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering document types");
                documentTypesRepo.reorder(longSrc, longDest);
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(VALIDATION_WARNING, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't reorder document types because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sortDocumentTypes(@ApiVariable String column) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(column, "documentType");
        
        // will attach any errors to the BindingResult when validating the incoming column
        validationService.validateColumn(DocumentType.class, column, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Sorting document types by " + column);
                documentTypesRepo.sort(column);
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(SUCCESS, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/document-type", new ApiResponse(VALIDATION_WARNING, documentTypesRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't sort document types because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
  
}
