package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypesRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.net.SyslogOutputStream;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/document-types")
public class DocumentTypesController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private DocumentTypesRepo documentTypeRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<DocumentType>> getAll() {
        Map<String, List<DocumentType>> map = new HashMap<String, List<DocumentType>>();
        map.put("list", documentTypeRepo.findAllByOrderByOrderAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allDocumentTypes() {
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createDocumentType(@Data String data) {
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        String newDocumentName = null;
        DegreeLevel newDocumentDegreeLevel = null;
        
        JsonNode documentNameNode = dataNode.get("name");
        if(documentNameNode != null) {
            String nameString = documentNameNode.asText();
            if(nameString.length() > 0) {
                newDocumentName = nameString;
            }
            else {
                return new ApiResponse(ERROR, "Name required to create document type!");
            }
        }
        else {
            return new ApiResponse(ERROR, "Name required to create document type!");
        }
        
        JsonNode documentDegreeLevelNode = dataNode.get("degreeLevel");
        if(documentDegreeLevelNode != null) {
            String degreeLevelString = documentDegreeLevelNode.asText();
            if(degreeLevelString.length() > 0) {
                newDocumentDegreeLevel = DegreeLevel.valueOf(degreeLevelString);
            }
            else {
                return new ApiResponse(ERROR, "Name required to create document type!");
            }
        }
        else {
            return new ApiResponse(ERROR, "Name required to create document type!");
        }
        
        DocumentType newDocumentType = documentTypeRepo.create(newDocumentName, newDocumentDegreeLevel);
        
        logger.info("Created document type with name " + newDocumentName + " and degree level " + newDocumentDegreeLevel.toString());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateDocumentType(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        //TODO: proper validation and response
        
        //System.out.println("\n\n" + dataNode + "\n\n");
        
        DocumentType documentType = null;
                
        JsonNode id = dataNode.get("id");
        if(id != null) {
            Long idLong = -1L;
            try {
                idLong = id.asLong();
            }
            catch(NumberFormatException nfe) {
                return new ApiResponse(ERROR, "Id required to update a document type!");
            }
            documentType = documentTypeRepo.findOne(idLong);           
        }
        else {
            return new ApiResponse(ERROR, "Id required to update a document type!");
        }
        
        JsonNode name = dataNode.get("name");
        if(name != null) {
            documentType.setName(name.asText());        
        }
        else {
            return new ApiResponse(ERROR, "Name required to update a documentType!");
        }
        
        JsonNode degreeLevel = dataNode.get("degreeLevel");
        if(degreeLevel != null) {
            documentType.setLevel(DegreeLevel.valueOf(degreeLevel.asText()));  
        }
        else {
            return new ApiResponse(ERROR, "Name required to update a documentType!");
        }
        
        // documentType = validateAndPopulateGraduationMonth(graduationMonth, dataNode);
                
        documentType = documentTypeRepo.save(documentType);
        
        //TODO: logging
        
        logger.info("Updated document type " + documentType.toString());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeDocumentType(@ApiVariable String indexString) {        
        Integer index = -1;
        
        try {
            index = Integer.parseInt(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid document order!");
        }
        
        if(index >= 0) {               
            documentTypeRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid document order!");
        }
        
        logger.info("Deleted document type with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderDocumentTypes(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        documentTypeRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortDocumentTypes(@ApiVariable String column) {
        documentTypeRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/document-types", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
}
