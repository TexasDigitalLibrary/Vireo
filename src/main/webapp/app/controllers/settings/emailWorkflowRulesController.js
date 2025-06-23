vireo.controller("EmailWorkflowRulesController", function ($controller, $scope, $q, SubmissionStatusRepo, EmailTemplateRepo, OrganizationRepo, EmailRecipientType) {

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

    /**
     * The enum property must match one of the Action in src/main/java/org/tdl/vireo/model/Action.java
     */
    $scope.submissionActions = [
        {
            enum: 'STUDENT_MESSAGE',
            name: 'Student Adds Message'
        },
        {
            enum: 'ADVISOR_MESSAGE',
            name: 'Advisor Adds Message'
        },
        {
            enum: 'ADVISOR_APPROVE_SUBMISSION',
            name: 'Advisor Approves Submission'
        },
        {
            enum: 'ADVISOR_CLEAR_APPROVE_SUBMISSION',
            name: 'Advisor Clears Submission Approval'
        },
        {
            enum: 'ADVISOR_APPROVE_EMBARGO',
            name: 'Advisor Approved Embargo'
        },
        {
            enum: 'ADVISOR_CLEAR_APPROVE_EMBARGO',
            name: 'Advisor Clears Submission Embargo'
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

        $scope.addEmailWorkflowRule = function (newTemplate, newRecipient, statusOrAction) {
            var recipient = angular.copy(newRecipient);
            var organization = $scope.getSelectedOrganization();
            organization.$dirty = true;

            if (recipient.type === EmailRecipientType.ORGANIZATION) {
                recipient.data = recipient.data.id;
            }

            const emailWorkflowRuleAdded = statusOrAction?.id
                ? OrganizationRepo.addEmailWorkflowRule(organization, newTemplate.id, recipient, statusOrAction?.id)
                : OrganizationRepo.addEmailWorkflowRuleByAction(organization, newTemplate.id, recipient, statusOrAction?.enum)

            emailWorkflowRuleAdded.then(function () {
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

            const emailWorkflowRuleEdited = $scope.emailWorkflowRuleToEdit?.submissionStatus?.id
                ? OrganizationRepo.editEmailWorkflowRule(organization, $scope.emailWorkflowRuleToEdit)
                : OrganizationRepo.editEmailWorkflowRuleByAction(organization, $scope.emailWorkflowRuleToEdit);

            emailWorkflowRuleEdited.then(function () {
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

            const emailWorkflowRuleDeleted = $scope.emailWorkflowRuleToDelete?.submissionStatus?.id
                ? OrganizationRepo.removeEmailWorkflowRule(organization, $scope.emailWorkflowRuleToDelete)
                : OrganizationRepo.removeEmailWorkflowRuleByAction(organization, $scope.emailWorkflowRuleToDelete);

            emailWorkflowRuleDeleted.then(function () {
                $scope.emailWorkflowRuleDeleteWorking = false;
                $scope.resetEditEmailWorkflowRule();
            });
        };

        $scope.changeEmailWorkflowRuleActivation = function (rule, changeEmailWorkflowRuleActivation) {
            var organization = $scope.getSelectedOrganization();
            organization.$dirty = true;

            const ruleActivationChanged = rule?.submissionStatus
                ? OrganizationRepo.changeEmailWorkflowRuleActivation(organization, rule)
                : OrganizationRepo.changeEmailWorkflowRuleByActionActivation(organization, rule);

            ruleActivationChanged.then(function () {
                changeEmailWorkflowRuleActivation = false;
            });
        };

        $scope.cancelDeleteEmailWorkflowRule = function () {
            $scope.emailWorkflowRuleDeleteWorking = false;
            $scope.resetEditEmailWorkflowRule();
        };
    });

});
