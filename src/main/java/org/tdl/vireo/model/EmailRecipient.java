package org.tdl.vireo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
use = JsonTypeInfo.Id.NAME,
include = JsonTypeInfo.As.PROPERTY,
property = "type")
@JsonSubTypes({
    @Type(value=EmailRecipientAssignee.class, name="EmailRecipientAssignee"), 
    @Type(value=EmailRecipientContact.class, name="EmailRecipientContact"),
    @Type(value=EmailRecipientOrganization.class, name="EmailRecipientOrganization"),
    @Type(value=EmailRecipientSubmitter.class, name="EmailRecipientSubmitter")})
public interface EmailRecipient {

    public List<String> getEmails(Submission submission);

}
