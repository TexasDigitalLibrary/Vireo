describe("controller: NewSubmissionController", function () {

    var controller, location, q, scope, Organization, OrganizationRepo, Submission, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($location, $q, _OrganizationRepo_, _WsApi_) {
            location = $location;
            q = $q;

            OrganizationRepo = _OrganizationRepo_;
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, SubmissionStates, _ManagedConfigurationRepo_, _ModalService_, _Organization_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _Submission_) {
            if (!settings || !settings.keepScope) {
                scope = $rootScope.$new();
            }

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("NewSubmissionController", {
                $location: location,
                $q: q,
                $scope: scope,
                $window: mockWindow(),
                SubmissionStates: SubmissionStates,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                Organization: _Organization_,
                OrganizationRepo: OrganizationRepo,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                Submission: _Submission_,
                RestApi: _RestApi_,
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
        module("mock.managedConfiguration");
        module("mock.managedConfigurationRepo");
        module("mock.modalService");
        module("mock.organization", function($provide) {
            $provide.value("Organization", mockParameterModel(q, mockOrganization));
        });
        module("mock.organizationRepo");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.submission", function($provide) {
            $provide.value("Submission", mockParameterModel(q, mockSubmission));
        });
        module("mock.studentSubmission");
        module("mock.studentSubmissionRepo");
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
        it("createSubmission should be defined", function () {
            expect(scope.createSubmission).toBeDefined();
            expect(typeof scope.createSubmission).toEqual("function");
        });
        it("getSelectedOrganization should be defined", function () {
            expect(scope.getSelectedOrganization).toBeDefined();
            expect(typeof scope.getSelectedOrganization).toEqual("function");
        });
        it("getSelectedOrganizationAcceptsSubmissions should be defined", function () {
            expect(scope.getSelectedOrganizationAcceptsSubmissions).toBeDefined();
            expect(typeof scope.getSelectedOrganizationAcceptsSubmissions).toEqual("function");
        });
        it("getSelectedOrganizationId should be defined", function () {
            expect(scope.getSelectedOrganizationId).toBeDefined();
            expect(typeof scope.getSelectedOrganizationId).toEqual("function");
        });
        it("getSelectedOrganizationName should be defined", function () {
            expect(scope.getSelectedOrganizationName).toBeDefined();
            expect(typeof scope.getSelectedOrganizationName).toEqual("function");
        });
        it("gotoSubmission should be defined", function () {
            expect(scope.gotoSubmission).toBeDefined();
            expect(typeof scope.gotoSubmission).toEqual("function");
        });
        it("hasSubmission should be defined", function () {
            expect(scope.hasSubmission).toBeDefined();
            expect(typeof scope.hasSubmission).toEqual("function");
        });
        it("setSelectedOrganization should be defined", function () {
            expect(scope.setSelectedOrganization).toBeDefined();
            expect(typeof scope.setSelectedOrganization).toEqual("function");
        });
        it("rebuildOrganizationTree should be defined", function () {
            expect(scope.rebuildOrganizationTree).toBeDefined();
            expect(typeof scope.rebuildOrganizationTree).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("createSubmission should create a new submission", function () {
            scope.creatingSubmission = null;
            OrganizationRepo.selectedId = 1;

            scope.createSubmission();
            scope.$digest();

            expect(scope.creatingSubmission).toEqual(false);
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
        it("gotoSubmission should change the URL path", function () {
            var organization = new mockOrganization(q);
            scope.studentSubmissions = [ new mockStudentSubmission(q) ];
            scope.studentSubmissions[0].organization = organization;
            scope.studentSubmissions[0].submissionStatus = { submissionState: "IN_PROGRESS" };

            spyOn(location, "path");

            scope.gotoSubmission();
            expect(location.path).not.toHaveBeenCalled();

            scope.gotoSubmission({});
            expect(location.path).not.toHaveBeenCalled();

            scope.gotoSubmission({ id: "should not match" });
            expect(location.path).not.toHaveBeenCalled();

            scope.gotoSubmission(organization);
            expect(location.path).toHaveBeenCalled();

            scope.studentSubmissions[0].submissionStatus.submissionState = "SUBMITTED";

            scope.gotoSubmission(organization);
        });
        it("hasSubmission should return a boolean", function () {
            var response;
            var organization = new mockOrganization(q);
            scope.studentSubmissions = [];

            response = scope.hasSubmission();

            expect(response).toEqual(false);

            response = scope.hasSubmission({});

            expect(response).toEqual(false);

            response = scope.hasSubmission(organization);
            expect(response).toEqual(false);

            scope.studentSubmissions = [ new mockStudentSubmission(q) ];
            scope.studentSubmissions[0].organization = organization;

            response = scope.hasSubmission(organization);
            expect(response).toEqual(true);
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
        it("setSelectedOrganization should select the organization", function () {
            var organization = new mockOrganization(q);
            OrganizationRepo.selectedId = null;

            scope.setSelectedOrganization(organization);

            expect(OrganizationRepo.selectedId).toEqual(organization.id);
        });
    });
});
