package org.tdl.vireo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
use = JsonTypeInfo.Id.MINIMAL_CLASS,
include = JsonTypeInfo.As.PROPERTY,
property = "_class")
@JsonSubTypes({
    @Type(value=EmailRecipientAssignee.class), 
    @Type(value=EmailRecipientContact.class),
    @Type(value=EmailRecipientOrganization.class),
    @Type(value=EmailRecipientSubmitter.class)})
public interface EmailRecipient {

    public List<String> getEmails(Submission submission);

}
