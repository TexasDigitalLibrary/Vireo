package org.tdl.vireo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
use = JsonTypeInfo.Id.MINIMAL_CLASS,
property = "_class")
public interface EmailRecipient {

    public List<String> getEmails(Submission submission);

}
