describe("controller: OrganizationSideBarController", function () {

    var controller, q, scope, OrganizationRepo, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _AccordionService_, _OrganizationRepo_, _WsApi_) {
            q = $q;

            OrganizationRepo = _OrganizationRepo_;
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $injector, $q, $rootScope, $timeout, SubmissionStates, _AccordionService_, _ManagedConfigurationRepo_, _ModalService_, _OrganizationCategoryRepo_, _RestApi_, _SidebarService_, _StorageService_, _StudentSubmissionRepo_, _UserService_, _UserSettings_) {
            q = $q;
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            // The controller being tested is included inside of a OrganizationSettingsController scope.
            // This results in having additional methods on the scope that the controller being tested requires.
            $controller("AbstractController", {
                $injector: $injector,
                $scope: scope,
                $timeout: $timeout,
                UserService: _UserService_,
                UserSettings: _UserSettings_,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                SubmissionStates: SubmissionStates
            });

            $controller("OrganizationSettingsController", {
                $scope: scope,
                $window: mockWindow(),
                AccordionService: _AccordionService_,
                ModalService: _ModalService_,
                OrganizationRepo: OrganizationRepo,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                WsApi: WsApi
            });

            controller = $controller("OrganizationSideBarController", {
                $q: q,
                $scope: scope,
                $window: mockWindow(),
                ModalService: _ModalService_,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
                OrganizationRepo: OrganizationRepo,
                RestApi: _RestApi_,
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
        module("mock.organizationCategory");
        module("mock.organizationCategoryRepo");
        module("mock.organization");
        module("mock.organizationRepo");
        module("mock.modalService");
        module("mock.restApi");
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
        it("createNewOrganization should be defined", function () {
            expect(scope.createNewOrganization).toBeDefined();
            expect(typeof scope.createNewOrganization).toEqual("function");
        });
        it("reset should be defined", function () {
            expect(scope.reset).toBeDefined();
            expect(typeof scope.reset).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("createNewOrganization should create a new custom action when hierarchal", function () {
            scope.organizations = [ mockOrganization(q) ];
            scope.creatingNewOrganization = null;

            spyOn(scope, "reset");
            spyOn(OrganizationRepo, "create").and.callThrough();

            scope.createNewOrganization("false");
            scope.$digest();

            expect(scope.reset).toHaveBeenCalled();
            expect(OrganizationRepo.create).toHaveBeenCalled();
            expect(scope.creatingNewOrganization).toBe(false);
        });
        it("createNewOrganization should create a new custom action when not hierarchal", function () {
            var parentOrganization = mockOrganization(q);

            OrganizationRepo.newOrganization = {
                parent: parentOrganization,
                category: ""
            };

            scope.organizations = [ parentOrganization ];
            scope.creatingNewOrganization = null;

            spyOn(scope, "reset");
            spyOn(OrganizationRepo, "create").and.callThrough();

            scope.createNewOrganization("true");
            scope.$digest();

            expect(scope.reset).toHaveBeenCalled();
            expect(OrganizationRepo.create).toHaveBeenCalled();
            expect(scope.creatingNewOrganization).toBe(false);
        });
        it("reset should reset the custom action", function () {
            var organization = new mockOrganization(q);
            scope.forms = [];
            scope.modalData = organization;
            scope.organizations = [ organization ];

            spyOn(scope.organizationRepo, "clearValidationResults");
            spyOn(organization, "refresh");

            scope.reset();

            expect(scope.organizationRepo.clearValidationResults).toHaveBeenCalled();
            expect(scope.newOrganization.category).toBeDefined();
            expect(scope.newOrganization.parent).toBeDefined();

            scope.forms.myForm = mockForms();
            scope.reset();

            scope.forms.myForm.$pristine = false;
            scope.reset();
        });
    });

});
