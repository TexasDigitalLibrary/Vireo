describe('controller: NewSubmissionController', function () {

    var controller, location, q, scope, OrganizationRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $q, $rootScope, $window, SubmissionStates, _ManagedConfigurationRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            location = $location;
            q = $q;

            OrganizationRepo = _OrganizationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('NewSubmissionController', {
                $location: $location,
                $q: q,
                $scope: scope,
                $window: $window,
                SubmissionStates: SubmissionStates,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                RestApi: _RestApi_,
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
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmission');
        module('mock.studentSubmissionRepo');
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
        it('createSubmission should be defined', function () {
            expect(scope.createSubmission).toBeDefined();
            expect(typeof scope.createSubmission).toEqual("function");
        });
        it('getSelectedOrganization should be defined', function () {
            expect(scope.getSelectedOrganization).toBeDefined();
            expect(typeof scope.getSelectedOrganization).toEqual("function");
        });
        it('gotoSubmission should be defined', function () {
            expect(scope.gotoSubmission).toBeDefined();
            expect(typeof scope.gotoSubmission).toEqual("function");
        });
        it('hasSubmission should be defined', function () {
            expect(scope.hasSubmission).toBeDefined();
            expect(typeof scope.hasSubmission).toEqual("function");
        });
        it('setSelectedOrganization should be defined', function () {
            expect(scope.setSelectedOrganization).toBeDefined();
            expect(typeof scope.setSelectedOrganization).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createSubmission should create a new submission', function () {
            scope.creatingSubmission = null;
            OrganizationRepo.selectedId = 1;

            scope.createSubmission();
            scope.$digest();

            expect(scope.creatingSubmission).toBe(false);
        });
        it('getSelectedOrganization should return an organization', function () {
            var response;
            OrganizationRepo.selectedId = 1;

            response = scope.getSelectedOrganization();

            expect(response.id).toBe(OrganizationRepo.selectedId);
        });
        it('gotoSubmission should change the URL path', function () {
            var organization = new mockOrganization(q);
            scope.studentSubmissions = [ new mockStudentSubmission(q) ];
            scope.studentSubmissions[0].organization = organization;
            scope.studentSubmissions[0].submissionStatus = { submissionState: "IN_PROGRESS" };

            spyOn(location, "path");

            scope.gotoSubmission(organization);
            expect(location.path).toHaveBeenCalled();

            scope.studentSubmissions[0].submissionStatus.submissionState = "SUBMITTED";

            scope.gotoSubmission(organization);
        });
        it('hasSubmission should return a boolean', function () {
            var response;
            var organization = new mockOrganization(q);
            scope.studentSubmissions = [];

            response = scope.hasSubmission(organization);
            expect(response).toBe(false);

            scope.studentSubmissions = [ new mockStudentSubmission(q) ];
            scope.studentSubmissions[0].organization = organization;

            response = scope.hasSubmission(organization);
            expect(response).toBe(true);
        });
        it('setSelectedOrganization should select the organization', function () {
            var organization = new mockOrganization(q);
            OrganizationRepo.selectedId = null;

            scope.setSelectedOrganization(organization);

            expect(OrganizationRepo.selectedId).toBe(organization.id);
        });
    });
});
