describe('controller: OrganizationManagementController', function () {

    var controller, q, scope, timeout, AccordionService, AlertService, OrganizationRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $q, $rootScope, $route, $timeout, $window, _AccordionService_, _AlertService_, _ModalService_, _OrganizationRepo_, _RestApi_, _SidebarService_, _StorageService_, _WorkflowStepRepo_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();
            timeout = $timeout;

            AccordionService = _AccordionService_;
            AlertService = _AlertService_;
            OrganizationRepo = _OrganizationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            // OrganizationManagementController is included inside of a OrganizationSettingsController scope.
            // This results in having additional methods on the scope that OrganizationManagementController requires.
            $controller('OrganizationSettingsController', {
                $scope: scope,
                $window: $window,
                AccordionService: _AccordionService_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                WsApi: _WsApi_
            });

            controller = $controller('OrganizationManagementController', {
                $q: q,
                $location: $location,
                $route: $route,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                AccordionService: _AccordionService_,
                AlertService: _AlertService_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WorkflowStepRepo: _WorkflowStepRepo_,
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
        module('mock.accordionService');
        module('mock.alertService');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.workflowStepRepo');
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
        it('addWorkflowStep should be defined', function () {
            expect(scope.addWorkflowStep).toBeDefined();
            expect(typeof scope.addWorkflowStep).toEqual("function");
        });
        it('cancelDeleteOrganization should be defined', function () {
            expect(scope.cancelDeleteOrganization).toBeDefined();
            expect(typeof scope.cancelDeleteOrganization).toEqual("function");
        });
        it('cancelRestoreOrganizationDefaults should be defined', function () {
            expect(scope.cancelRestoreOrganizationDefaults).toBeDefined();
            expect(typeof scope.cancelRestoreOrganizationDefaults).toEqual("function");
        });
        it('deleteOrganization should be defined', function () {
            expect(scope.deleteOrganization).toBeDefined();
            expect(typeof scope.deleteOrganization).toEqual("function");
        });
        it('deleteWorkflowStep should be defined', function () {
            expect(scope.deleteWorkflowStep).toBeDefined();
            expect(typeof scope.deleteWorkflowStep).toEqual("function");
        });
        it('openConfirmDeleteModal should be defined', function () {
            expect(scope.openConfirmDeleteModal).toBeDefined();
            expect(typeof scope.openConfirmDeleteModal).toEqual("function");
        });
        it('reorderWorkflowStepDown should be defined', function () {
            expect(scope.reorderWorkflowStepDown).toBeDefined();
            expect(typeof scope.reorderWorkflowStepDown).toEqual("function");
        });
        it('reorderWorkflowStepUp should be defined', function () {
            expect(scope.reorderWorkflowStepUp).toBeDefined();
            expect(typeof scope.reorderWorkflowStepUp).toEqual("function");
        });
        it('resetManageOrganization should be defined', function () {
            expect(scope.resetManageOrganization).toBeDefined();
            expect(typeof scope.resetManageOrganization).toEqual("function");
        });
        it('resetWorkflowSteps should be defined', function () {
            expect(scope.resetWorkflowSteps).toBeDefined();
            expect(typeof scope.resetWorkflowSteps).toEqual("function");
        });
        it('restoreOrganizationDefaults should be defined', function () {
            expect(scope.restoreOrganizationDefaults).toBeDefined();
            expect(typeof scope.restoreOrganizationDefaults).toEqual("function");
        });
        it('showOrganizationManagement should be defined', function () {
            expect(scope.showOrganizationManagement).toBeDefined();
            expect(typeof scope.showOrganizationManagement).toEqual("function");
        });
        it('updateOrganization should be defined', function () {
            expect(scope.updateOrganization).toBeDefined();
            expect(typeof scope.updateOrganization).toEqual("function");
        });
        it('updateWorkflowStep should be defined', function () {
            expect(scope.updateWorkflowStep).toBeDefined();
            expect(typeof scope.updateWorkflowStep).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('addWorkflowStep should add a workflow step', function () {
            scope.modalData = mockWorkflowStep(q);

            spyOn(OrganizationRepo, "addWorkflowStep");

            scope.addWorkflowStep();

            expect(OrganizationRepo.addWorkflowStep).toHaveBeenCalled();
        });
        it('cancelDeleteOrganization should close a modal', function () {
            OrganizationRepo.selectedId = 1;

            spyOn(scope, "closeModal");

            scope.cancelDeleteOrganization();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('cancelRestoreOrganizationDefaults should close a modal', function () {
            OrganizationRepo.selectedId = 1;

            spyOn(scope, "closeModal");

            scope.cancelRestoreOrganizationDefaults();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('deleteOrganization should delete the organization', function () {
            var organization = new mockOrganization(q);
            OrganizationRepo.selectedId = organization.id;

            spyOn(AlertService, "add");
            spyOn(scope, "closeModal");

            scope.deleteOrganization(organization);
            scope.$digest();
            timeout.flush();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(AlertService.add).toHaveBeenCalled();

            // FIXME: should the implementation return a defer.reject instead of a defer.promise with INVALID text?
            organization.delete = function() {
                return messagePromise(q.defer(), "", "INVALID", 404);
            };
            scope.closeModal = function() {};

            spyOn(scope, "closeModal");

            scope.deleteOrganization(organization);
            scope.$digest();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('deleteWorkflowStep should delete a workflow step', function () {
            spyOn(AccordionService, "close");

            scope.deleteWorkflowStep(new mockWorkflowStep(q));
            scope.$digest();

            expect(AccordionService.close).toHaveBeenCalled();
        });
        it('openConfirmDeleteModal should open a modal', function () {
            spyOn(scope, "openModal");

            scope.openConfirmDeleteModal({id: 1});

            expect(scope.openModal).toHaveBeenCalled();
        });
        it('reorderWorkflowStepDown should reorder a workflow step', function () {
            spyOn(AccordionService, "closeAll");
            spyOn(OrganizationRepo, "reorderWorkflowStep");

            scope.reorderWorkflowStepDown(1);

            expect(AccordionService.closeAll).toHaveBeenCalled();
            expect(OrganizationRepo.reorderWorkflowStep).toHaveBeenCalled();
        });
        it('reorderWorkflowStepUp should reorder a workflow step', function () {
            spyOn(AccordionService, "closeAll");
            spyOn(OrganizationRepo, "reorderWorkflowStep");

            scope.reorderWorkflowStepUp(1);

            expect(AccordionService.closeAll).toHaveBeenCalled();
            expect(OrganizationRepo.reorderWorkflowStep).toHaveBeenCalled();
        });
        // FIXME: this test cannot be performed because OrganizationManagementController.setSelectedOrganization() is not defined.
        /*
        it('resetManageOrganization should reset the selected organization', function () {
            var organization;
            OrganizationRepo.selectedId = 1;
            OrganizationRepo.mockSpyAssist = true;
            organization = scope.getSelectedOrganization();

            spyOn(organization, "refresh");

            scope.resetManageOrganization();

            expect(organization.refresh).toHaveBeenCalled();
        });*/
        it('resetWorkflowSteps should reset the workflow steps', function () {
            var organization = new mockOrganization(q);
            scope.forms = [];
            scope.modalData = organization;

            spyOn(scope.organizationRepo, "clearValidationResults");
            spyOn(organization, "refresh");
            spyOn(scope, "closeModal");

            scope.resetWorkflowSteps();

            expect(scope.organizationRepo.clearValidationResults).toHaveBeenCalled();
            expect(organization.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('restoreOrganizationDefaults should restore defaults', function () {
            var organization = new mockOrganization(q);

            spyOn(AlertService, "add");
            spyOn(scope, "closeModal");

            scope.restoreOrganizationDefaults(organization);
            scope.$digest();
            timeout.flush();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(AlertService.add).toHaveBeenCalled();
        });
        it('showOrganizationManagement should return a boolean', function () {
            var response;

            OrganizationRepo.selectedId = 1;
            sessionStorage.role = "ROLE_MANAGER";

            response = scope.showOrganizationManagement();
            expect(response).toBe(false);

            sessionStorage.role = "ROLE_ADMIN";

            response = scope.showOrganizationManagement();
            expect(response).toBe(true);
        });
        // FIXME: this test cannot be performed because OrganizationManagementController.setSelectedOrganization() is not defined.
        /*
        it('updateOrganization should should save an organization', function () {
            var organization = new mockOrganization(q);
            scope.updatingOrganization = null;

            spyOn(scope, "setSelectedOrganization");

            scope.updateOrganization(organization);
            scope.$digest();

            expect(scope.setSelectedOrganization).toHaveBeenCalled();
            expect(scope.updatingOrganization).toBe(false);
        });
        */
        it('updateWorkflowStep should update a workflow step', function () {
            var workflowStep = new mockWorkflowStep(q);

            spyOn(OrganizationRepo, "setToUpdate");
            spyOn(OrganizationRepo, "updateWorkflowStep");

            scope.updateWorkflowStep(workflowStep);

            expect(OrganizationRepo.setToUpdate).toHaveBeenCalled();
            expect(OrganizationRepo.updateWorkflowStep).toHaveBeenCalled();
        });
    });

});
