describe('controller: StudentSubmissionController', function () {

    var controller, q, location, routeParams, scope, timeout, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($location, $q, $routeParams, $timeout, _WsApi_) {
            location = $location;
            q = $q;
            routeParams = $routeParams;
            timeout = $timeout;

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($anchorScroll, $controller, $rootScope, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StorageService_, _StudentSubmissionRepo_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            if (settings && settings.stepNum !== undefined) {
                routeParams.stepNum = settings.stepNum;
            }

            controller = $controller('StudentSubmissionController', {
                $anchorScroll: $anchorScroll,
                $location: location,
                $routeParams: routeParams,
                $scope: scope,
                $timeout: timeout,
                $window: mockWindow(),
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                WsApi: WsApi
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
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmission');
        module('mock.studentSubmissionRepo');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('onLastStep should be defined', function () {
            expect(scope.onLastStep).toBeDefined();
            expect(typeof scope.onLastStep).toEqual("function");
        });
        it('reviewSubmission should be defined', function () {
            expect(scope.reviewSubmission).toBeDefined();
            expect(typeof scope.reviewSubmission).toEqual("function");
        });
        it('setActiveStep should be defined', function () {
            expect(scope.setActiveStep).toBeDefined();
            expect(typeof scope.setActiveStep).toEqual("function");
        });
        it('submit should be defined', function () {
            expect(scope.submit).toBeDefined();
            expect(typeof scope.submit).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('onLastStep should return a boolean', function () {
            var response;
            scope.submission = new mockSubmission(q);
            scope.submission.submissionWorkflowSteps = [ new mockWorkflowStep(q) ];

            response = scope.onLastStep();

            expect(typeof response).toBe("boolean");
        });
        it('reviewSubmission should call set the active step', function () {
            spyOn(scope, "setActiveStep");

            scope.reviewSubmission();

            expect(scope.setActiveStep).toHaveBeenCalled();
        });
        it('setActiveStep should set the active step', function () {
            var step = new mockWorkflowStep(q);
            var hash = "test";
            scope.submission = mockSubmission(q);
            scope.submission.submissionWorkflowSteps = [ step ];

            scope.submitting = true;
            scope.setActiveStep(step, hash);

            scope.submitting = false;
            scope.setActiveStep(step, hash);

            step.name = "review";
            scope.setActiveStep(step, hash);

            step = false;
            initializeController({stepNum: 1});
            scope.setActiveStep(step, hash);

            timeout.flush();
        });
        it('submit should submit and change path', function () {
            spyOn(location, "path");

            scope.submit();
            scope.$digest();

            expect(location.path).toHaveBeenCalled();
        });
    });
});
