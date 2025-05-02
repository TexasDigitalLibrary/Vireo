vireo.controller("EmailWorkflowRulesController", function ($controller, $location, $scope, $q, SubmissionStatusRepo, EmailTemplateRepo, OrganizationRepo, EmailRecipientType) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.active = 'email-by-status';

    $scope.isByStatus = function () {
        return $scope.active === 'email-by-status';
    };

    $scope.isByAction = function () {
        return $scope.active === 'email-by-action';
    };

    $scope.submissionActions = [
        {
            enum: 'ADD_MESSAGE',
            name: 'Add Message'
        },
        {
            enum: 'UPDATE_ADVISOR_APPROVAL',
            name: 'Update Advisor Approval'
        }
    ];

    $scope.submissionStatuses = SubmissionStatusRepo.getAll();
    $scope.emailTemplates = EmailTemplateRepo.getAll();
    $scope.emailRecipientType = EmailRecipientType;
    $scope.stateRules = {};
    $scope.recipients = [];

    $scope.buildRecipients = function (organization) {
        $scope.recipients = !!organization && !!organization.id ? organization.getWorkflowEmailContacts() : [];
    };

    $q.all([SubmissionStatusRepo.ready(), EmailTemplateRepo.ready()]).then(function () {

        $scope.openAddEmailWorkflowRuleModal = function (id) {
            $scope.buildRecipients($scope.getSelectedOrganization());

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
            var recipient = angular.copy(newRecipient);
            var organization = $scope.getSelectedOrganization();
            organization.$dirty = true;

            if (recipient.type === EmailRecipientType.ORGANIZATION) {
                recipient.data = recipient.data.id;
            }

            OrganizationRepo.addEmailWorkflowRule(organization, newTemplate.id, recipient, submissionStatus.id).then(function () {
                $scope.resetEmailWorkflowRule();
            });

        };

        $scope.openEditEmailWorkflowRule = function (rule) {
            $scope.buildRecipients($scope.getSelectedOrganization());
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
            var organization = $scope.getSelectedOrganization();
            organization.$dirty = true;

            if ($scope.emailWorkflowRuleToEdit.emailRecipient.type == EmailRecipientType.ORGANIZATION) {
                $scope.emailWorkflowRuleToEdit.emailRecipient.data = $scope.emailWorkflowRuleToEdit.emailRecipient.data.id;
            }

            OrganizationRepo.editEmailWorkflowRule(organization, $scope.emailWorkflowRuleToEdit).then(function () {
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
            var organization = $scope.getSelectedOrganization();
            organization.$dirty = true;

            $scope.emailWorkflowRuleDeleteWorking = true;
            OrganizationRepo.removeEmailWorkflowRule(organization, $scope.emailWorkflowRuleToDelete).then(function () {
                $scope.emailWorkflowRuleDeleteWorking = false;
            });
        };

        $scope.changeEmailWorkflowRuleActivation = function (rule, changeEmailWorkflowRuleActivation) {
            var organization = $scope.getSelectedOrganization();
            organization.$dirty = true;

            OrganizationRepo.changeEmailWorkflowRuleActivation(organization, rule).then(function () {
                changeEmailWorkflowRuleActivation = false;
            });
        };

        $scope.cancelDeleteEmailWorkflowRule = function () {
            $scope.emailWorkflowRuleDeleteWorking = false;
            $scope.closeModal();
        };
    });

});
