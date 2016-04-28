package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypesRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/document-types")
public class DocumentTypesController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private DocumentTypesRepo documentTypeRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<DocumentType>> getAll() {
        Map<String, List<DocumentType>> map = new HashMap<String, List<DocumentType>>();
        map.put("list", documentTypeRepo.findAllByOrderByPositionAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allDocumentTypes() {
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createDocumentType(@ApiValidatedModel DocumentType documentType) {
        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if(!documentType.getBindingResult().hasErrors() && documentTypeRepo.findByName(documentType.getName()) != null){
            documentType.getBindingResult().addError(new ObjectError("documentType", documentType.getName() + " is already a document type!"));
        }
        
        if(documentType.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, documentType.getBindingResult().getAll());
        }
        
        documentTypeRepo.create(documentType.getName(), documentType.getDegreeLevel());
        
        logger.info("Created document type with name " + documentType.getName() + " and degree level " + documentType.getDegreeLevel());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateDocumentType(@ApiValidatedModel DocumentType documentType) {
        
        // TODO: this needs to go in repo.validateUpdate() -- VIR-201
        DocumentType documentTypeToUpdate = null;
        if(documentType.getId() == null) {
            documentType.getBindingResult().addError(new ObjectError("documentType", "Cannot update a DocumentType without an id!"));
        } else {
            documentTypeToUpdate = documentTypeRepo.findOne(documentType.getId());
            if(documentTypeToUpdate == null) {
                documentType.getBindingResult().addError(new ObjectError("documentType", "Cannot update a DocumentType with an invalid id!"));
            }
        }
        
        if(documentType.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, documentType.getBindingResult().getAll());
        }       
        
        documentTypeToUpdate.setName(documentType.getName());        
        documentTypeToUpdate.setDegreeLevel(documentType.getDegreeLevel());  
                        
        documentTypeToUpdate = documentTypeRepo.save(documentTypeToUpdate);
        
        //TODO: logging
        
        logger.info("Updated document type " + documentTypeToUpdate.toString());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeDocumentType(@ApiVariable String indexString) {        
        Long index = -1L;
        
        try {
            index = Long.parseLong(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid document order!");
        }
        
        if(index >= 0) {               
            documentTypeRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid document order!");
        }
        
        logger.info("Deleted document type with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderDocumentTypes(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        documentTypeRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sortDocumentTypes(@ApiVariable String column) {
        documentTypeRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
}
