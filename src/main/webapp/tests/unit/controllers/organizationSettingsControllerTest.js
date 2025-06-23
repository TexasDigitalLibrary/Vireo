describe("controller: OrganizationSettingsController", function () {

    var controller, q, scope, AccordionService, OrganizationRepo, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _AccordionService_, _WsApi_) {
            q = $q;

            AccordionService = _AccordionService_;

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, _ModalService_, _Organization_, _OrganizationRepo_, _RestApi_, _SidebarService_, _StorageService_) {
            if (!settings || !settings.keepScope) {
                scope = $rootScope.$new();
            }

            OrganizationRepo = _OrganizationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("OrganizationSettingsController", {
                $scope: scope,
                $window: mockWindow(),
                AccordionService: AccordionService,
                ModalService: _ModalService_,
                Organization: _Organization_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                WsApi: WsApi
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.accordionService");
        module("mock.modalService");
        module("mock.organization", function($provide) {
            $provide.value("Organization", mockParameterModel(q, mockOrganization));
        });
        module("mock.organizationRepo");
        module("mock.restApi");
        module("mock.sidebarService");
        module("mock.storageService");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
        it("should be defined with organizations tree loaded", function () {
            OrganizationRepo.getAllSpecific = function () {
                return q(function (resolve, reject) {
                    resolve([ new mockOrganization(q) ]);
                });
            };

            initializeController({ keepScope: true });

            expect(scope.organizations).toBeDefined();
            expect(scope.organizations.length).toEqual(1);
        });
    });

    describe("Are the scope methods defined", function () {
        it("activateManagementPane should be defined", function () {
            expect(scope.activateManagementPane).toBeDefined();
            expect(typeof scope.activateManagementPane).toEqual("function");
        });
        it("getSelectedOrganizationAcceptsSubmissions should be defined", function () {
            expect(scope.getSelectedOrganizationAcceptsSubmissions).toBeDefined();
            expect(typeof scope.getSelectedOrganizationAcceptsSubmissions).toEqual("function");
        });
        it("getSelectedOrganizationAggregateWorkflowSteps should be defined", function () {
            expect(scope.getSelectedOrganizationAggregateWorkflowSteps).toBeDefined();
            expect(typeof scope.getSelectedOrganizationAggregateWorkflowSteps).toEqual("function");
        });
        it("getSelectedOrganizationEmailWorkflowRules should be defined", function () {
            expect(scope.getSelectedOrganizationEmailWorkflowRules).toBeDefined();
            expect(typeof scope.getSelectedOrganizationEmailWorkflowRules).toEqual("function");
        });
        it("getSelectedOrganizationId should be defined", function () {
            expect(scope.getSelectedOrganizationId).toBeDefined();
            expect(typeof scope.getSelectedOrganizationId).toEqual("function");
        });
        it("getSelectedOrganizationName should be defined", function () {
            expect(scope.getSelectedOrganizationName).toBeDefined();
            expect(typeof scope.getSelectedOrganizationName).toEqual("function");
        });
        it("getSelectedOrganizationValidations should be defined", function () {
            expect(scope.getSelectedOrganizationValidations).toBeDefined();
            expect(typeof scope.getSelectedOrganizationValidations).toEqual("function");
        });
        it("getSelectedOrganizationValidationResults should be defined", function () {
            expect(scope.getSelectedOrganizationValidationResults).toBeDefined();
            expect(typeof scope.getSelectedOrganizationValidationResults).toEqual("function");
        });
        it("getSelectedOrganization should be defined", function () {
            expect(scope.getSelectedOrganization).toBeDefined();
            expect(typeof scope.getSelectedOrganization).toEqual("function");
        });
        it("managementPaneIsActive should be defined", function () {
            expect(scope.managementPaneIsActive).toBeDefined();
            expect(typeof scope.managementPaneIsActive).toEqual("function");
        });
        it("rebuildOrganizationTree should be defined", function () {
            expect(scope.rebuildOrganizationTree).toBeDefined();
            expect(typeof scope.rebuildOrganizationTree).toEqual("function");
        });
        it("setDeleteDisabled should be defined", function () {
            expect(scope.setDeleteDisabled).toBeDefined();
            expect(typeof scope.setDeleteDisabled).toEqual("function");
        });
        it("setSelectedOrganization should be defined", function () {
            expect(scope.setSelectedOrganization).toBeDefined();
            expect(typeof scope.setSelectedOrganization).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("activateManagementPane should activate the pane", function () {
            scope.activeManagementPane = null;
            scope.activateManagementPane(true);

            expect(scope.activeManagementPane).toEqual(true);
        });
        it("getSelectedOrganization should return an organization", function () {
            var response;
            var organization = new mockOrganization(q);

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganization();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganization();

            expect(response).toBeDefined();
            expect(response.id).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganization();

            expect(response.id).toEqual(organization.id);
        });
        it("getSelectedOrganizationAcceptsSubmissions should return acceptsSubmissions", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.acceptsSubmissions = true;

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationAcceptsSubmissions();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationAcceptsSubmissions();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationAcceptsSubmissions();

            expect(response).toEqual(organization.acceptsSubmissions);
        });
        it("getSelectedOrganizationAggregateWorkflowSteps should return aggregateWorkflowSteps", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.aggregateWorkflowSteps = { steps: [] };

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationAggregateWorkflowSteps();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationAggregateWorkflowSteps();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationAggregateWorkflowSteps();

            expect(response).toEqual(organization.aggregateWorkflowSteps);
        });
        it("getSelectedOrganizationEmailWorkflowRules should return emailWorkflowRules", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.emailWorkflowRules = [];
            organization.emailWorkflowRulesByAction = [];

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationEmailWorkflowRules();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationEmailWorkflowRules();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationEmailWorkflowRules();

            expect(response).toEqual(organization.emailWorkflowRules);
        });
        it("getSelectedOrganizationId should return an ID", function () {
            var response;
            var organization = new mockOrganization(q);

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationId();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationId();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationId();

            expect(response).toEqual(organization.id);
        });
        it("getSelectedOrganizationName should return a name", function () {
            var response;
            var organization = new mockOrganization(q);

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationName();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationName();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationName();

            expect(response).toEqual(organization.name);
        });
        it("getSelectedOrganizationOriginalWorkflowSteps should return originalWorkflowSteps", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.originalWorkflowSteps = { steps: [] };

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationOriginalWorkflowSteps();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationOriginalWorkflowSteps();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationOriginalWorkflowSteps();

            expect(response).toEqual(organization.originalWorkflowSteps);
        });
        it("getSelectedOrganizationValidations should return validations", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.getValidations = { validations: true };

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationValidations();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationValidations();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationValidations();

            expect(response).toEqual(organization.getValidations);
        });
        it("getSelectedOrganizationValidations should return validations", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.getValidationResults = { validations: true };

            scope.selectedOrganization = undefined;

            response = scope.getSelectedOrganizationValidationResults();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganizationValidationResults();

            expect(response).not.toBeDefined();

            OrganizationRepo.selectedId = organization.id;
            OrganizationRepo.selectedOrganization = organization;
            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganizationValidationResults();

            expect(response).toEqual(organization.getValidationResults);
        });
        it("managementPaneIsActive should return a boolean", function () {
            var response = scope.managementPaneIsActive("test");

            expect(response).toEqual(false);

            scope.activateManagementPane("test");

            response = scope.managementPaneIsActive("test");
            expect(response).toEqual(true);
        });
        it("setDeleteDisabled should assign delete disabled", function () {
            var organization = new mockOrganization(q);
            scope.deleteDisabled = null;

            scope.setDeleteDisabled();
            scope.$digest();
            expect(scope.deleteDisabled).toEqual(null);

            scope.deleteDisabled = null;
            scope.selectedOrganization = organization;
            OrganizationRepo.submissionsCount[organization.id] = 0;

            scope.setDeleteDisabled();
            scope.$digest();
            expect(scope.deleteDisabled).toEqual(false);

            scope.deleteDisabled = null;
            OrganizationRepo.submissionsCount[organization.id] = 2;

            scope.setDeleteDisabled(organization.id);
            scope.$digest();
            expect(scope.deleteDisabled).toEqual(true);
        });
        it("rebuildOrganizationTree should rebuild the tree on resolve", function () {
            var organization1 = new mockOrganization(q);
            var organization2 = new mockOrganization(q);
            organization2.mock(dataOrganization2);

            scope.organizations = [ organization1 ];

            OrganizationRepo.getAllSpecific = function () {
                return q(function (resolve, reject) {
                    resolve([]);
                });
            };

            scope.rebuildOrganizationTree();
            scope.$digest();

            expect(scope.organizations.length).toEqual(0);

            OrganizationRepo.getAllSpecific = function () {
                return q(function (resolve, reject) {
                    resolve([organization1, organization2]);
                });
            };

            scope.rebuildOrganizationTree();
            scope.$digest();

            expect(scope.organizations.length).toEqual(2);
            expect(scope.organizations[0].id).toEqual(organization1.id);
            expect(scope.organizations[1].id).toEqual(organization2.id);
        });
        it("rebuildOrganizationTree should not rebuild the tree on reject", function () {
            var organization1 = new mockOrganization(q);
            var organization2 = new mockOrganization(q);
            var reason = "Testing rejection.";
            organization2.mock(dataOrganization2);

            scope.organizations = [ organization1 ];

            OrganizationRepo.getAllSpecific = function () {
                return q(function (resolve, reject) {
                    reject(reason);
                });
            };

            scope.rebuildOrganizationTree().catch(function (error) {
                expect(error).toEqual(reason);
            });
            scope.$digest();

            expect(scope.organizations.length).toEqual(1);
        });
        it("setSelectedOrganization should assign selected organization", function () {
            var organization1 = new mockOrganization(q);
            var organization2 = new mockOrganization(q);
            var organization3;
            organization2.mock(dataOrganization2);

            organization3 = angular.copy(organization2)
            organization3.isModified = "from organization3";

            scope.selectedOrganization = organization2;

            scope.loadingOrganization = true;

            scope.setSelectedOrganization(organization1);

            expect(scope.loadingOrganization).toEqual(true);
            expect(scope.selectedOrganization.id).toEqual(organization2.id);

            scope.loadingOrganization = false;

            scope.setSelectedOrganization();

            expect(scope.selectedOrganization).not.toBeDefined();
            expect(scope.newOrganization).toBeDefined();
            expect(scope.newOrganization.parent).not.toBeDefined();
            expect(scope.deleteDisabled).toEqual(true);

            scope.selectedOrganization = organization2;

            scope.setSelectedOrganization({});

            expect(scope.selectedOrganization).not.toBeDefined();
            expect(scope.newOrganization).toBeDefined();
            expect(scope.newOrganization.parent).not.toBeDefined();
            expect(scope.deleteDisabled).toEqual(true);

            scope.selectedOrganization = organization2;
            OrganizationRepo.selectedOrganization = organization2;
            OrganizationRepo.selectedId = organization2.id;

            OrganizationRepo.getById = function (id, specific) {
                return q(function (resolve, reject) {
                    resolve(organization1);
                });
            };

            spyOn(AccordionService, "closeAll");

            scope.setSelectedOrganization(organization1);

            scope.$digest();

            expect(scope.selectedOrganization.id).toEqual(organization1.id);
            expect(AccordionService.closeAll).toHaveBeenCalled();

            organization2.parentOrganization = organization1;
            organization1.childrenOrganizations = [ organization2 ];
            scope.organizations = [ organization1, organization2 ];
            scope.selectedOrganization = organization2;
            organization1.$dirty = true;

            OrganizationRepo.getById = function (id, specific) {
                return q(function (resolve, reject) {
                    resolve(organization3);
                });
            };

            scope.setSelectedOrganization(organization1);

            scope.$digest();

            expect(scope.selectedOrganization.id).toEqual(organization3.id);
            expect(scope.selectedOrganization.isModified).toEqual(organization3.isModified);

            organization1.complete = true;
            organization1.shallow = false;
            organization1.$dirty = false;

            scope.selectedOrganization = organization2;

            scope.setSelectedOrganization(organization1);

            expect(scope.selectedOrganization.id).toEqual(organization1.id);
        });
    });

});
