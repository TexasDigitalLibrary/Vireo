vireo.model("Organization", function Organization($q, WsApi, InputTypes, EmailRecipientType) {

    return function Organization() {

      var organization = this;

      organization.defaultRecipients = [{
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
          name: "Advisor",
          type: EmailRecipientType.ADVISOR,
          data: "Advisor"
        },
        {
          name: "Organization",
          type: EmailRecipientType.ORGANIZATION,
          data: null
        }
      ];

        organization.getWorkflowEmailContacts = function() {
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

          return angular.copy(organization.defaultRecipients).concat(dynamicRecipients);
        };

        return organization;

    };
});
