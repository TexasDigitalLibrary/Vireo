vireo.model("Organization", function Organization($q, WsApi, InputTypes, EmailRecipientType) {

    return function Organization() {

      var organization = this;

      organization.addEmailWorkflowRule = function (templateId, recipient, submissionStatusId) {
            angular.extend(apiMapping.Organization.addEmailWorkflowRule, {
                'method': organization.id + "/add-email-workflow-rule",
                'data': {
                    templateId: templateId,
                    recipient: recipient,
                    submissionStatusId: submissionStatusId
                }
            });

            var promise = WsApi.fetch(apiMapping.Organization.addEmailWorkflowRule);

            return promise;
        };

        organization.removeEmailWorkflowRule = function (rule) {
            angular.extend(apiMapping.Organization.removeEmailWorkflowRule, {
                'method': organization.id + "/remove-email-workflow-rule/" + rule.id,
            });

            var promise = WsApi.fetch(apiMapping.Organization.removeEmailWorkflowRule);

            return promise;
        };

        organization.editEmailWorkflowRule = function (rule) {
            angular.extend(apiMapping.Organization.editEmailWorkflowRule, {
                'method': organization.id + "/edit-email-workflow-rule/" + rule.id,
                'data': {
                    templateId: rule.emailTemplate.id,
                    recipient: rule.emailRecipient
                }
            });

            var promise = WsApi.fetch(apiMapping.Organization.editEmailWorkflowRule);

            return promise;
        };

        organization.getWorkflowEmailContacts = function() {
          var assumedRecipients = [{
              name: "Submitter",
              type: EmailRecipientType.SUBMITTER,
              data: "Submitter"
            },
            {
              name: "Assignee",
              type: EmailRecipientType.ASSIGNEE,
              data: "Assignee"
            },
            {
              name: "Organization",
              type: EmailRecipientType.ORGANIZATION,
              data: null
            }
          ];

          var recipientInputTypes = [
            InputTypes.INPUT_CONTACT,
            InputTypes.INPUT_CONTACT_SELECT
          ];
          
          var dynamicRecipients = [];

          angular.forEach(organization.aggregateWorkflowSteps, function (aggregateWorkflowStep) {
              angular.forEach(aggregateWorkflowStep.aggregateFieldProfiles, function (aggregateFieldProfile) {
                  if (recipientInputTypes.indexOf(aggregateFieldProfile.inputType.name) !== -1) {
                    dynamicRecipients.push({
                          name: aggregateFieldProfile.gloss,
                          type: EmailRecipientType.CONTACT,
                          data: aggregateFieldProfile.fieldPredicate.id
                      });
                  }
              });
          });
          
          return assumedRecipients.concat(dynamicRecipients);
        };

        organization.changeEmailWorkflowRuleActivation = function (rule) {
            angular.extend(apiMapping.Organization.changeEmailWorkflowRuleActivation, {
                'method': organization.id + "/change-email-workflow-rule-activation/" + rule.id,
            });
            var promise = WsApi.fetch(apiMapping.Organization.changeEmailWorkflowRuleActivation);

            return promise;
        };

        return organization;

    };
});
