package org.tdl.vireo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.MINIMAL_CLASS)
public interface EmailRecipient {

    public List<String> getEmails(Submission submission);

}
