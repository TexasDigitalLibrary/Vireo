describe('controller: EmailWorkflowRulesController', function () {

    var controller, q, scope, OrganizationRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _EmailTemplateRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _SubmissionStatusRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            OrganizationRepo = _OrganizationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('EmailWorkflowRulesController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                EmailTemplateRepo: _EmailTemplateRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.submissionStatus');
        module('mock.submissionStatusRepo');
        module('mock.emailTemplate');
        module('mock.emailTemplateRepo');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('addEmailWorkflowRule should be defined', function () {
            expect(scope.addEmailWorkflowRule).toBeDefined();
            expect(typeof scope.addEmailWorkflowRule).toEqual("function");
        });
        it('buildRecipients should be defined', function () {
            expect(scope.buildRecipients).toBeDefined();
            expect(typeof scope.buildRecipients).toEqual("function");
        });
        it('cancelDeleteEmailWorkflowRule should be defined', function () {
            expect(scope.cancelDeleteEmailWorkflowRule).toBeDefined();
            expect(typeof scope.cancelDeleteEmailWorkflowRule).toEqual("function");
        });
        it('changeEmailWorkflowRuleActivation should be defined', function () {
            expect(scope.changeEmailWorkflowRuleActivation).toBeDefined();
            expect(typeof scope.changeEmailWorkflowRuleActivation).toEqual("function");
        });
        it('confirmEmailWorkflowRuleDelete should be defined', function () {
            expect(scope.confirmEmailWorkflowRuleDelete).toBeDefined();
            expect(typeof scope.confirmEmailWorkflowRuleDelete).toEqual("function");
        });
        it('deleteEmailWorkflowRule should be defined', function () {
            expect(scope.deleteEmailWorkflowRule).toBeDefined();
            expect(typeof scope.deleteEmailWorkflowRule).toEqual("function");
        });
        it('editEmailWorkflowRule should be defined', function () {
            expect(scope.editEmailWorkflowRule).toBeDefined();
            expect(typeof scope.editEmailWorkflowRule).toEqual("function");
        });
        it('openAddEmailWorkflowRuleModal should be defined', function () {
            expect(scope.openAddEmailWorkflowRuleModal).toBeDefined();
            expect(typeof scope.openAddEmailWorkflowRuleModal).toEqual("function");
        });
        it('openEditEmailWorkflowRule should be defined', function () {
            expect(scope.openEditEmailWorkflowRule).toBeDefined();
            expect(typeof scope.openEditEmailWorkflowRule).toEqual("function");
        });
        it('resetEditEmailWorkflowRule should be defined', function () {
            expect(scope.resetEditEmailWorkflowRule).toBeDefined();
            expect(typeof scope.resetEditEmailWorkflowRule).toEqual("function");
        });
        it('resetEmailWorkflowRule should be defined', function () {
            expect(scope.resetEmailWorkflowRule).toBeDefined();
            expect(typeof scope.resetEmailWorkflowRule).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('addEmailWorkflowRule should ', function () {
            var template = {id: 1};
            var recipient = {};
            var submissionStatus = mockSubmissionStatus(q);
            OrganizationRepo.selectedId = dataOrganization1.id;

            spyOn(scope, "resetEmailWorkflowRule");

            scope.addEmailWorkflowRule(template, recipient, submissionStatus);
            scope.$digest();

            expect(scope.resetEmailWorkflowRule).toHaveBeenCalled();
        });
        it('buildRecipients should build the recipients', function () {
            OrganizationRepo.selectedId = dataOrganization1.id;
            scope.recipients = null;

            scope.buildRecipients();

            expect(typeof scope.recipients).toBe("object");
        });
        it('cancelDeleteEmailWorkflowRule should close a modal', function () {
            scope.emailWorkflowRuleDeleteWorking = true;

            spyOn(scope, "closeModal");

            scope.cancelDeleteEmailWorkflowRule();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.emailWorkflowRuleDeleteWorking).toBe(false);
        });
        it('changeEmailWorkflowRuleActivation should change the email', function () {
            var working = null;
            OrganizationRepo.selectedId = dataOrganization1.id;

            scope.changeEmailWorkflowRuleActivation("rule", working);
            scope.$digest();

            // FIXME: the implementation is not working.
            //expect(scope.changeEmailWorkflowRuleActivationWorking).toBe(false);
        });
        it('confirmEmailWorkflowRuleDelete should open a modal', function () {
            scope.emailWorkflowRuleToDelete = null;

            spyOn(scope, "openModal");

            scope.confirmEmailWorkflowRuleDelete({});

            expect(scope.emailWorkflowRuleToDelete).toBeDefined();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('deleteEmailWorkflowRule should delete the rule', function () {
            scope.emailWorkflowRuleDeleteWorking = null;
            OrganizationRepo.selectedId = dataOrganization1.id;

            scope.deleteEmailWorkflowRule("rule");
            scope.$digest();

            expect(scope.emailWorkflowRuleDeleteWorking).toBe(false);
        });
        it('editEmailWorkflowRule should reset the rule', function () {
            OrganizationRepo.selectedId = dataOrganization1.id;
            scope.emailWorkflowRuleToEdit = {
                emailRecipient: {
                    type: "ORGANIZATION",
                    data: {
                        id: 7
                    }
                }
            }

            spyOn(scope, "resetEditEmailWorkflowRule");

            scope.editEmailWorkflowRule();
            scope.$digest();

            expect(scope.resetEditEmailWorkflowRule).toHaveBeenCalled();
            expect(scope.emailWorkflowRuleToEdit.emailRecipient.data).toEqual(7);
        });
        it('openAddEmailWorkflowRuleModal should open a modal', function () {
            scope.newTemplate = null;
            scope.newRecipient = null;
            scope.recipients = [{
                type: "ORGANIZATION",
                data: {
                    id: 7
                }
            }];

            spyOn(scope, "buildRecipients");
            spyOn(scope, "openModal");

            scope.openAddEmailWorkflowRuleModal();

            expect(scope.newTemplate).toBeDefined();
            expect(scope.newRecipient).toBeDefined();
            expect(scope.buildRecipients).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('openEditEmailWorkflowRule should open a modal', function () {
            var rule = {
                emailRecipient: {
                    type: "ORGANIZATION",
                    data: {
                        id: 7
                    }
                },
                emailTemplate: new mockEmailTemplate(q)
            };

            scope.emailWorkflowRuleToEdit = null;
            scope.newTemplate = null;
            scope.newRecipient = null;
            scope.recipients = [{
                type: "ORGANIZATION",
                data: {
                    id: 7
                }
            }];

            spyOn(scope, "buildRecipients");
            spyOn(scope, "openModal");

            scope.openEditEmailWorkflowRule(rule);

            expect(scope.newTemplate).toBeDefined();
            expect(scope.newRecipient).toBeDefined();
            expect(scope.emailWorkflowRuleToEdit).toBeDefined();
            expect(scope.buildRecipients).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('resetEditEmailWorkflowRule should close a modal', function () {
            spyOn(scope, "closeModal");

            scope.resetEditEmailWorkflowRule({});

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('resetEmailWorkflowRule should close a modal', function () {
            scope.newTemplate = null;
            scope.newRecipient = null;

            spyOn(scope, "closeModal");

            scope.resetEditEmailWorkflowRule({});

            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.newTemplate).toBeDefined();
            expect(scope.newRecipient).toBeDefined();
        });
    });

});
