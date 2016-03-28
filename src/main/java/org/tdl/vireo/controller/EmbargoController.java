package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.WARNING;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/embargo")
public class EmbargoController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Map<String, List<Embargo>> getAll() {
        Map<String, List<Embargo>> allRet = new HashMap<String, List<Embargo>>();
        allRet.put("list", embargoRepo.findAllByOrderByOrderAsc());
        return allRet;
    }

    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse getEmbargoes() {
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createEmbargo(@Data String data) {

        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json [" + e.getMessage() + "]");
        }

        // TODO: proper validation and response
        Embargo incoming = deserializeEmbargo(dataNode);
        
        Embargo newEmbargo;
        if (incoming.getName() != null && incoming.getDescription() != null && incoming.getGuarantor() != null && incoming.isActive() != null) {
            // make sure we won't get a unique constraint violation from the DB
            Embargo existing = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(incoming.getName(), incoming.getGuarantor(), false);
            if (existing != null) {
                return new ApiResponse(ERROR, "Embargo already exists!");
            }
            newEmbargo = embargoRepo.create(incoming.getName(), incoming.getDescription(), incoming.getDuration(), incoming.getGuarantor(), incoming.isActive());
        } else {
            return new ApiResponse(ERROR, "Missing required field to create embargo!");
        }
        newEmbargo.setOrder((int) embargoRepo.count());
        embargoRepo.save(newEmbargo);

        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateEmbargo(@Data String data) {

        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json [" + e.getMessage() + "]");
        }

        Embargo incoming = deserializeEmbargo(dataNode);

        // TODO: proper validation and response
        if (incoming.getId() != null) {
            Embargo embargoToUpdate = embargoRepo.findOne(incoming.getId());
            if(embargoToUpdate != null) {
                // make sure we're not editing a system required one
                if (embargoToUpdate.isSystemRequired()) {
                    Embargo customEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoToUpdate.getName(), embargoToUpdate.getGuarantor(), false);
                    // if we're editing a system required one and a custom one with the same name doesn't exist, create it with the incoming parameters
                    if (customEmbargo == null) {
                        embargoRepo.create(incoming.getName(), incoming.getDescription(), incoming.getDuration(), incoming.getGuarantor(), incoming.isActive());
                        // TODO: deserialize the JSON before this so we can return a warning to the front-end!
                        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(WARNING, getAll()));
                        return new ApiResponse(WARNING, "System Embargo cannot be edited, a custom user-copy has been made!");
                    } else {
                        return new ApiResponse(ERROR, "System Embargo cannot be edited and a custom one with this name already exists!");
                    }
                }
                // we're allowed to edit!
                else {
                    if(incoming.getName() != null) {
                        embargoToUpdate.setName(incoming.getName());
                    }
                    if (incoming.getDescription() != null) {
                        embargoToUpdate.setDescription(incoming.getDescription());
                    }
                    if (incoming.isActive() != null) {
                        embargoToUpdate.isActive(incoming.isActive());
                    }
                    // duration can be null
                    embargoToUpdate.setDuration(incoming.getDuration());
                    
                    embargoRepo.save(embargoToUpdate);
                    simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
                    return new ApiResponse(SUCCESS);
                }
            } else {
                return new ApiResponse(ERROR, "Cannot edit Embargo that doesn't exist!");
            }
        } else {
            return new ApiResponse(ERROR, "Cannot edit Embargo, no id was passed in!");
        }
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeEmbargo(@ApiVariable String idString) {
        Long id = -1L;

        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException nfe) {
            return new ApiResponse(ERROR, "Id is not a valid embargo id!");
        }

        if (id >= 0) {
            Embargo toRemove = embargoRepo.findOne(id);
            if(toRemove != null) {
                if(toRemove.isSystemRequired()) {
                    return new ApiResponse(ERROR, "Cannot remove a System Embargo!");
                }
                embargoRepo.delete(toRemove);
                logger.info("Embargo with id " + id);
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
                return new ApiResponse(SUCCESS);
            } else {
                return new ApiResponse(ERROR, "Id for embargo not found!");
            }
        } else {
            return new ApiResponse(ERROR, "Id is not a valid embargo id!");
        }
    }

    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderEmbargoes(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        embargoRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/sort/{column}/{where}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortEmbargoes(@ApiVariable String column, @ApiVariable String where) {
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(where);
        if (guarantor != null) {
            embargoRepo.sort(column, guarantor);
            simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
            return new ApiResponse(SUCCESS);
        }
        return new ApiResponse(ERROR);
    }
    
    /**
     * This can deserialize even partial objects. Missing parameters are defaults for a new Embargo();
     * 
     * @param parent
     * @return
     */
    private static Embargo deserializeEmbargo(JsonNode parent) {
        Embargo ret = new Embargo();

        // check to see if "name" was defined in the data
        JsonNode tempNode = popNullSafeJsonNode(parent, "id");
        if (tempNode != null) {
            // set the name
            ret.setId(tempNode.asLong());
        }
        // check to see if "name" was defined in the data
        tempNode = popNullSafeJsonNode(parent, "name");
        if (tempNode != null) {
            // set the name
            ret.setName(tempNode.asText());
        }
        // check to see if "description" was defined in the data
        tempNode = popNullSafeJsonNode(parent, "description");
        if (tempNode != null) {
            // set the description
            ret.setDescription(tempNode.asText());
        }
        // check to see if "isActive" was defined in the data
        tempNode = popNullSafeJsonNode(parent, "isActive");
        if (tempNode != null) {
            // set the isActive
            ret.isActive(tempNode.asBoolean());
        }
        // check to see if "duration" was defined in the data
        tempNode = popNullSafeJsonNode(parent, "duration");
        if (tempNode != null) {
            // set the duration
            ret.setDuration(tempNode.asInt());
        }
        // check to see if "guarantor" was defined in the data
        tempNode = popNullSafeJsonNode(parent, "guarantor");
        if (tempNode != null) {
            // set the guarantor
            ret.setGuarantor(EmbargoGuarantor.fromString(tempNode.asText()));
        }

        return ret;
    }

    /**
     * Will make sure that if ${key} exists and its value is null, the entire JsonNode object will be null 
     * @param parent
     * @param key
     * @return
     */
    private static JsonNode popNullSafeJsonNode(JsonNode parent, String key) {
        JsonNode tempNode = parent.get(key);
        // check to see if ${key} was defined in the data
        if (tempNode != null) {
            // check to see if ${key} doesn't have a null value
            if (!tempNode.isNull()) {
                return tempNode;
            }
        }
        // could end up here if tempNode != null but isNull()
        return null;
    }   
}
