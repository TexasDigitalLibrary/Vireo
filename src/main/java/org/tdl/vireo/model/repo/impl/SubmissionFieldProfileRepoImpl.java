package org.tdl.vireo.model.repo.impl;

import static org.tdl.vireo.model.repo.specification.SubmissionFieldProfileSpecifications.existing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.custom.SubmissionFieldProfileRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class SubmissionFieldProfileRepoImpl extends AbstractWeaverRepoImpl<SubmissionFieldProfile, SubmissionFieldProfileRepo> implements SubmissionFieldProfileRepoCustom {

    @Autowired
    private SubmissionFieldProfileRepo submissionFieldProfileRepo;

    @Override
    @Transactional
    public SubmissionFieldProfile create(FieldProfile fieldProfile) {

        SubmissionFieldProfile submissionfieldProfile = submissionFieldProfileRepo.findOne(existing(fieldProfile));

        if (submissionfieldProfile == null) {
            submissionfieldProfile = new SubmissionFieldProfile();

            submissionfieldProfile.setFieldPredicate(fieldProfile.getFieldPredicate());
            submissionfieldProfile.setInputType(fieldProfile.getInputType());
            submissionfieldProfile.setRepeatable(fieldProfile.getRepeatable());
            submissionfieldProfile.setOptional(fieldProfile.getOptional());
            submissionfieldProfile.setHidden(fieldProfile.getHidden());
            submissionfieldProfile.setLogged(fieldProfile.getLogged());
            submissionfieldProfile.setUsage(fieldProfile.getUsage());
            submissionfieldProfile.setHelp(fieldProfile.getHelp());
            submissionfieldProfile.setGloss(fieldProfile.getGloss());
            submissionfieldProfile.setControlledVocabulary(fieldProfile.getControlledVocabulary());
            submissionfieldProfile.setMappedShibAttribute(fieldProfile.getMappedShibAttribute());
            submissionfieldProfile.setFlagged(fieldProfile.getFlagged());
            submissionfieldProfile.setDefaultValue(fieldProfile.getDefaultValue());
            submissionfieldProfile.setEnabled(fieldProfile.getEnabled());

            submissionfieldProfile = submissionFieldProfileRepo.save(submissionfieldProfile);
        }

        return submissionfieldProfile;
    }

    @Override
    protected String getChannel() {
        return "/channel/submission-field-profile";
    }

}
