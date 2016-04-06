package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

import edu.tamu.framework.validation.ModelBindingResult;

public class EmbargoRepoImpl implements EmbargoRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Override
    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive) {
        Embargo embargo = new Embargo(name, description, duration, guarantor, isActive);
        embargo.setPosition(embargoRepo.count() + 1);
        return embargoRepo.save(embargo);
    }

    @Override
    public void reorder(Long src, Long dest, EmbargoGuarantor guarantor) {
        orderedEntityService.reorder(Embargo.class, src, dest, "guarantor", guarantor);
    }

    @Override
    public void sort(String column, EmbargoGuarantor guarantor) {
        orderedEntityService.sort(Embargo.class, column, "guarantor", guarantor);
    }

    @Override
    public void remove(Long index) {
        orderedEntityService.remove(Embargo.class, index);
    }

    @Override
    public void validateCreate(Embargo embargo) {
        // make sure we won't get a unique constraint violation from the DB
        Embargo existing = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargo.getName(), embargo.getGuarantor(), false);
        if (existing != null) {
            embargo.getBindingResult().addError(new ObjectError("embargo", "Cannot create embargo that already exists!"));
        }
    }

    @Override
    public void validateUpdate(Embargo embargo) {
        if (embargo.getId() != null) {
            Embargo embargoToUpdate = embargoRepo.findOne(embargo.getId());
            if (embargoToUpdate != null) {
                // make sure we're not editing a system required one
                if (embargoToUpdate.isSystemRequired()) {
                    Embargo customEmbargo = embargoRepo.findByNameAndGuarantorAndIsSystemRequired(embargoToUpdate.getName(), embargoToUpdate.getGuarantor(), false);
                    // if we're editing a system required one and a custom one with the same name doesn't exist, create it with the incoming parameters
                    if (customEmbargo == null) {
                        // if we didn't have any pre-existing errors with the incoming embargo (missing properties)
                        if (!embargo.getBindingResult().hasErrors()) {
                            // make sure we're not just trying to disable a system embargo
                            if (embargoToUpdate.equals(embargo)) {
                                // if our persisted embargo doesn't have the same isActive() as the one coming in from the front-end
                                if (!embargo.isActive().equals(embargoToUpdate.isActive())) {
                                    ModelBindingResult bindingResult = embargo.getBindingResult();
                                    embargoToUpdate.isActive(embargo.isActive());
                                    embargo = embargoToUpdate;
                                    bindingResult.addInfo(new ObjectError("embargo", "System Embargo " + (embargo.isActive() ? "Enabled" : "Disabled") + "!"));
                                    embargo.setBindingResult(bindingResult);
                                }
                            }
                            // make a copy of the system embargo since we're trying to change it (!equals)
                            else {
                                // make sure we copy the binding result to the new embargo... for the controller to use if it needs it
                                ModelBindingResult bindingResult = embargo.getBindingResult();
                                embargo = embargoRepo.create(embargo.getName(), embargo.getDescription(), embargo.getDuration(), embargo.getGuarantor(), embargo.isActive());
                                bindingResult.addWarning(new ObjectError("embargo", "System Embargo cannot be edited, a custom user-copy has been made!"));
                                embargo.setBindingResult(bindingResult);
                            }
                        }
                    } else {
                        embargo.getBindingResult().addError(new ObjectError("embargo", "System Embargo cannot be edited and a custom one with this name already exists!"));
                    }
                }
                // we're allowed to edit!
                else {
                    embargoToUpdate.setName(embargo.getName());
                    embargoToUpdate.setDescription(embargo.getDescription());
                    embargoToUpdate.isActive(embargo.isActive());
                    embargoToUpdate.setDuration(embargo.getDuration());
                    // make sure we copy the binding result to the updated embargo... for the controller to use if it needs it
                    embargoToUpdate.setBindingResult(embargo.getBindingResult());
                    embargo = embargoToUpdate;

                }
            } else {
                embargo.getBindingResult().addError(new ObjectError("embargo", "Cannot edit Embargo that doesn't exist!"));
            }
        } else {
            embargo.getBindingResult().addError(new ObjectError("embargo", "Cannot edit Embargo, no id was passed in!"));
        }
    }
}
