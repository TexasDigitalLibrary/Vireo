vireo.controller("EmailWorkflowRulesController", function ($controller, $scope, $q, SubmissionStatusRepo, EmailTemplateRepo, OrganizationRepo, EmailRecipientType, InputTypes) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();
    $scope.submissionStatuses = SubmissionStatusRepo.getAll();
    $scope.emailTemplates = EmailTemplateRepo.getAll();
    $scope.emailRecipientType = EmailRecipientType;
    $scope.organizations = OrganizationRepo.getAll();
    $scope.stateRules = {};

    $scope.buildRecipients = function () {
        $scope.recipients = [{
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

        angular.forEach(OrganizationRepo.getSelectedOrganization().aggregateWorkflowSteps, function (aggregateWorkflowStep) {
            angular.forEach(aggregateWorkflowStep.aggregateFieldProfiles, function (aggregateFieldProfile) {
                if (aggregateFieldProfile.inputType.name === InputTypes.INPUT_CONTACT) {
                    $scope.recipients.push({
                        name: aggregateFieldProfile.fieldGlosses[0].value,
                        type: EmailRecipientType.CONTACT,
                        data: aggregateFieldProfile.fieldPredicate.id
                    });
                }
            });
        });
    };

    $q.all([SubmissionStatusRepo.ready(), EmailTemplateRepo.ready(), OrganizationRepo.ready()]).then(function () {

        $scope.openAddEmailWorkflowRuleModal = function (id) {
            $scope.buildRecipients();

            $scope.newTemplate = $scope.emailTemplates[0];
            $scope.newRecipient = $scope.recipients[0];

            $scope.openModal(id);

        };

        $scope.resetEmailWorkflowRule = function () {
            $scope.newTemplate = $scope.emailTemplates[0];
            $scope.newRecipient = $scope.recipients[0].data;
            $scope.closeModal();
        };

        $scope.addEmailWorkflowRule = function (newTemplate, newRecipient, submissionStatus) {
            console.log("ewrc controller: adding wf rule to recipient " + newRecipient);
            var recipient = angular.copy(newRecipient);

            if (recipient.type === EmailRecipientType.ORGANIZATION) {
                recipient.data = recipient.data.id;
            }

            OrganizationRepo.getSelectedOrganization().addEmailWorkflowRule(newTemplate.id, recipient, submissionStatus.id).then(function () {
                $scope.resetEmailWorkflowRule();
            });

        };

        $scope.openEditEmailWorkflowRule = function (rule) {
            $scope.buildRecipients();
            $scope.emailWorkflowRuleToEdit = angular.copy(rule);
            for (var i in $scope.recipients) {
                var recipient = $scope.recipients[i];
                if (recipient.name == $scope.emailWorkflowRuleToEdit.emailRecipient.name) {
                    $scope.emailWorkflowRuleToEdit.emailRecipient = recipient;
                    break;
                }
            }

            for (var j in $scope.emailTemplates) {
                var template = $scope.emailTemplates[j];
                if (template.id == $scope.emailWorkflowRuleToEdit.emailTemplate.id) {
                    $scope.emailWorkflowRuleToEdit.emailTemplate = template;
                    break;
                }
            }

            $scope.openModal("#editEmailWorkflowRule");
        };

        $scope.editEmailWorkflowRule = function () {

            if ($scope.emailWorkflowRuleToEdit.emailRecipient.type == EmailRecipientType.ORGANIZATION) $scope.emailWorkflowRuleToEdit.emailRecipient.data = $scope.emailWorkflowRuleToEdit.emailRecipient.data.id;

            OrganizationRepo.getSelectedOrganization().editEmailWorkflowRule($scope.emailWorkflowRuleToEdit).then(function () {
                $scope.resetEditEmailWorkflowRule();
            });
        };

        $scope.resetEditEmailWorkflowRule = function () {
            $scope.closeModal();
        };

        $scope.confirmEmailWorkflowRuleDelete = function (rule) {
            $scope.emailWorkflowRuleToDelete = rule;
            $scope.openModal("#confirmEmailWorkflowRuleDelete");
        };

        $scope.deleteEmailWorkflowRule = function () {
            $scope.emailWorkflowRuleDeleteWorking = true;
            OrganizationRepo.getSelectedOrganization().removeEmailWorkflowRule($scope.emailWorkflowRuleToDelete).then(function () {
                $scope.emailWorkflowRuleDeleteWorking = false;
            });
        };

        $scope.changeEmailWorkflowRuleActivation = function (rule, changeEmailWorkflowRuleActivation) {
            OrganizationRepo.getSelectedOrganization().changeEmailWorkflowRuleActivation(rule).then(function () {
                changeEmailWorkflowRuleActivation = false;
            });
        };

        $scope.cancelDeleteEmailWorkflowRule = function () {
            $scope.emailWorkflowRuleDeleteWorking = false;
            $scope.closeModal();
        };

    });

});
