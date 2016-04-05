package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.WARNING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/embargo")
public class EmbargoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Map<String, List<Embargo>> getAll() {
        Map<String, List<Embargo>> allRet = new HashMap<String, List<Embargo>>();
        allRet.put("list", embargoRepo.findAllByOrderByGuarantorAscPositionAsc());
        return allRet;
    }

    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse getEmbargoes() {
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createEmbargo(@ApiValidatedModel Embargo embargo) {
        
        // if isActive is null this will return errors
        BeanPropertyBindingResult result = embargo.getBindingResult();
        
        if(result.getAllErrors().size() > 0) {
            return new ApiResponse(ERROR, result.getAllErrors());
        }

        // make sure we won't get a unique constraint violation from the DB
        Embargo existing = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargo.getName(), embargo.getGuarantor(), false);
        if (existing != null) {
            return new ApiResponse(ERROR, "Embargo already exists!");
        }
        Embargo newEmbargo = embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive());

        newEmbargo.setPosition(embargoRepo.count());

        embargoRepo.save(newEmbargo);

        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateEmbargo(@ApiValidatedModel Embargo embargo) {
        
        BeanPropertyBindingResult result = embargo.getBindingResult();
        
        if(result.getAllErrors().size() > 0) {
            return new ApiResponse(ERROR, result.getAllErrors());
        }

        // TODO: proper validation and response
        if (embargo.getId() != null) {
            Embargo embargoToUpdate = embargoRepo.findOne(embargo.getId());
            if (embargoToUpdate != null) {
                // make sure we're not editing a system required one
                if (embargoToUpdate.isSystemRequired()) {
                    Embargo customEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoToUpdate.getName(), embargoToUpdate.getGuarantor(), false);
                    // if we're editing a system required one and a custom one with the same name doesn't exist, create it with the incoming parameters
                    if (customEmbargo == null) {
                        embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive());
                        // TODO: deserialize the JSON before this so we can return a warning to the front-end!
                        simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(WARNING, getAll()));
                        return new ApiResponse(WARNING, "System Embargo cannot be edited, a custom user-copy has been made!");
                    } else {
                        return new ApiResponse(ERROR, "System Embargo cannot be edited and a custom one with this name already exists!");
                    }
                }
                // we're allowed to edit!
                else {
                    embargoToUpdate.setName(embargo.getName());
                    embargoToUpdate.setDescription(embargo.getDescription());
                    embargoToUpdate.isActive(embargo.isActive());
                    embargoToUpdate.setDuration(embargo.getDuration());

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
            if (toRemove != null) {
                if (toRemove.isSystemRequired()) {
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

    @ApiMapping("/reorder/{guarantorString}/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderEmbargoes(@ApiVariable String guarantorString, @ApiVariable String src, @ApiVariable String dest) {
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(guarantorString);
        if (guarantor != null) {
            Long intSrc = Long.parseLong(src);
            Long intDest = Long.parseLong(dest);
            embargoRepo.reorder(intSrc, intDest, guarantor);
            simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
            return new ApiResponse(SUCCESS);
        }
        return new ApiResponse(ERROR);
    }

    @ApiMapping("/sort/{guarantorString}/{column}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortEmbargoes(@ApiVariable String guarantorString, @ApiVariable String column) {
        EmbargoGuarantor guarantor = EmbargoGuarantor.fromString(guarantorString);
        if (guarantor != null) {
            embargoRepo.sort(column, guarantor);
            simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
            return new ApiResponse(SUCCESS);
        }
        return new ApiResponse(ERROR);
    }
    
}
