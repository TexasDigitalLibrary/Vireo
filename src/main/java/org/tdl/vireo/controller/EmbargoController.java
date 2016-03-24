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

        Embargo newEmbargo = null;

        String name = dataNode.get("name").asText();
        String description = dataNode.get("description").asText();
        Integer duration = dataNode.get("duration").asInt();
        Boolean isActive = dataNode.get("isActive").asBoolean();
        if (name != null && description != null && duration != null && isActive != null) {
            newEmbargo = embargoRepo.create(name, description, duration, isActive);
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

        // TODO: proper validation and response

        Embargo embargoToUpdate = embargoRepo.findOne(dataNode.get("id").asLong());

        if (dataNode.get("name") != null)
            embargoToUpdate.setName(dataNode.get("name").asText());
        if (dataNode.get("description") != null)
            embargoToUpdate.setDescription(dataNode.get("description").asText());
        if (dataNode.get("duration") != null)
            embargoToUpdate.setDuration(dataNode.get("duration").asInt());

        embargoRepo.save(embargoToUpdate);
        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);

    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeEmbargo(@ApiVariable String indexString) {
        Integer index = -1;

        try {
            index = Integer.parseInt(indexString);
        } catch (NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid embargo order!");
        }

        if (index >= 0) {
            embargoRepo.remove(index);
        } else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid embargo order!");
        }

        logger.info("Embargo with order " + index);

        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));

        return new ApiResponse(SUCCESS);
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
        if(guarantor != null) {
            embargoRepo.sort(column, guarantor);
            simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
            return new ApiResponse(SUCCESS);
        }
        return new ApiResponse(ERROR);
    }

    // @ApiMapping("/reset")
    // public ApiResponse resetSetting(@Data String data) {
    // Map<String,String> map = new HashMap<String,String>();
    // try {
    // map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // Configuration deletableOverride = embargoRepo.findByNameAndType(map.get("setting"),map.get("type"));
    // if (deletableOverride != null) {
    // System.out.println(deletableOverride.getName());
    // embargoRepo.delete(deletableOverride);
    // }
    // Map<String, Map<String,String>> typeToConfigPair = new HashMap<String, Map<String,String>>();
    // typeToConfigPair.put(map.get("type"),embargoRepo.getAllByType(map.get("type")));
    // this.simpMessagingTemplate.convertAndSend("/channel/settings", new ApiResponse(SUCCESS, typeToConfigPair));
    //
    // return new ApiResponse(SUCCESS);
    // }
}
