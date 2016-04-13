package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_INFO;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    private ApiResponse createOrUpdate(Embargo embargo, Boolean create) {
        // if errors, with no warnings
        if (embargo.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, embargo.getBindingResult().getAll());
        }
        // else if no errors, with warnings
        else if (!embargo.getBindingResult().hasErrors() && embargo.getBindingResult().hasWarnings()) {
            simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(VALIDATION_WARNING, getAll()));
            return new ApiResponse(VALIDATION_WARNING, embargo.getBindingResult().getAllWarningsAndInfos());
        }
        // else if no errors, no warnings, maybe infos
        else {
            // if we're creating
            if (create) {
                embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive());
            }
            // else, were updating
            else {
                embargoRepo.save(embargo);
            }
            simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
            // deal with infos being set
            if (embargo.getBindingResult().hasInfos()) {
                return new ApiResponse(VALIDATION_INFO, embargo.getBindingResult().getAllInfos());
            } else {
                return new ApiResponse(SUCCESS);
            }
        }
    }

    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse getEmbargoes() {
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createEmbargo(@ApiValidatedModel Embargo embargo) {

        // will attach any errors to the BindingResult when validating the incoming embargo
        embargoRepo.validateCreate(embargo);

        return createOrUpdate(embargo, true);
    }

    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateEmbargo(@ApiValidatedModel Embargo embargo) {

        // will attach any errors to the BindingResult when validating the incoming embargo
        embargoRepo.validateUpdate(embargo);

        return createOrUpdate(embargo, false);
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeEmbargo(@ApiVariable String idString) {
        Long id = -1L;

        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException nfe) {
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid embargo id!");
        }

        if (id >= 0) {
            Embargo toRemove = embargoRepo.findOne(id);
            if (toRemove != null) {
                if (toRemove.isSystemRequired()) {
                    return new ApiResponse(VALIDATION_ERROR, "Cannot remove a System Embargo!");
                }
                embargoRepo.delete(toRemove);
                logger.info("Embargo with id " + id);
                simpMessagingTemplate.convertAndSend("/channel/settings/embargo", new ApiResponse(SUCCESS, getAll()));
                return new ApiResponse(SUCCESS);
            } else {
                return new ApiResponse(VALIDATION_ERROR, "Id for embargo not found!");
            }
        } else {
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid embargo id!");
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
        return new ApiResponse(VALIDATION_ERROR);
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
        return new ApiResponse(VALIDATION_ERROR);
    }
}
