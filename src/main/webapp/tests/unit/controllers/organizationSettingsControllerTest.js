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
            scope = $rootScope.$new();

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
    });

    describe("Are the scope methods defined", function () {
        it("activateManagementPane should be defined", function () {
            expect(scope.activateManagementPane).toBeDefined();
            expect(typeof scope.activateManagementPane).toEqual("function");
        });
        it("getSelectedOrganization should be defined", function () {
            expect(scope.getSelectedOrganization).toBeDefined();
            expect(typeof scope.getSelectedOrganization).toEqual("function");
        });
        it("managementPaneIsActive should be defined", function () {
            expect(scope.managementPaneIsActive).toBeDefined();
            expect(typeof scope.managementPaneIsActive).toEqual("function");
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

            expect(scope.activeManagementPane).toBe(true);
        });
        it("getSelectedOrganization should get the selected organization", function () {
            var response;
            var organization = new mockOrganization(q);
            organization.mock(dataOrganization2);

            response = scope.getSelectedOrganization();

            expect(response).not.toBeDefined();

            scope.selectedOrganization = {};

            response = scope.getSelectedOrganization();

            expect(response).toBeDefined();
            expect(response.id).not.toBeDefined();

            scope.selectedOrganization = organization;

            response = scope.getSelectedOrganization();

            expect(response.id).toEqual(organization.id);
        });
        it("managementPaneIsActive should return a boolean", function () {
            var response = scope.managementPaneIsActive("test");

            expect(response).toBe(false);

            scope.activateManagementPane("test");

            response = scope.managementPaneIsActive("test");
            expect(response).toBe(true);
        });
        it("setDeleteDisabled should assign delete disabled", function () {
            var organization = new mockOrganization(q);
            scope.deleteDisabled = null;

            scope.setDeleteDisabled();
            scope.$digest();
            expect(scope.deleteDisabled).toBe(null);

            scope.deleteDisabled = null;
            scope.selectedOrganization = organization;
            OrganizationRepo.submissionsCount[organization.id] = 0;

            scope.setDeleteDisabled();
            scope.$digest();
            expect(scope.deleteDisabled).toBe(false);

            scope.deleteDisabled = null;
            OrganizationRepo.submissionsCount[organization.id] = 2;

            scope.setDeleteDisabled(organization.id);
            scope.$digest();
            expect(scope.deleteDisabled).toBe(true);
        });
        it("setSelectedOrganization should assign selected organization", function () {
            var organization1 = new mockOrganization(q);
            var organization2 = new mockOrganization(q);
            organization2.mock(dataOrganization2);

            scope.selectedOrganization = organization2;

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

            var defer = q.defer();

            OrganizationRepo.getById = function (id, specific) {
                return defer.promise;
            };

            spyOn(AccordionService, "closeAll");

            scope.setSelectedOrganization(organization1);

            defer.resolve(organization1);
            scope.$digest();

            expect(scope.selectedOrganization.id).toEqual(organization1.id);
            expect(AccordionService.closeAll).toHaveBeenCalled();
        });
    });

});
