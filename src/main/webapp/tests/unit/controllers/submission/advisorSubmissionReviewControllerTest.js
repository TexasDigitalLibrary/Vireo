describe('controller: AdvisorSubmissionReviewController', function () {

    var controller, q, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, _AdvisorSubmissionRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('AdvisorSubmissionReviewController', {
                $scope: scope,
                $window: mockWindow(),
                AdvisorSubmissionRepo: _AdvisorSubmissionRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                Submission: mockParameterModel(q, mockSubmission),
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
        module('mock.advisorSubmissionRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.submission');
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
        it('addComment should be defined', function () {
            expect(scope.addComment).toBeDefined();
            expect(typeof scope.addComment).toEqual("function");
        });
        it('disableCheck should be defined', function () {
            expect(scope.disableCheck).toBeDefined();
            expect(typeof scope.disableCheck).toEqual("function");
        });
        it('predicateMatch should be defined', function () {
            expect(scope.predicateMatch).toBeDefined();
            expect(typeof scope.predicateMatch).toEqual("function");
        });
        it('required should be defined', function () {
            expect(scope.required).toBeDefined();
            expect(typeof scope.required).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('addComment should add a comment', function () {
            scope.approval = { updating: null };
            scope.messages = [];
            scope.submission = new mockSubmission(q);

            scope.addComment();
            scope.$digest();

            expect(scope.approval.updating).toBe(false);
            expect(scope.messages.length).toBe(1);
        });
        it('disableCheck should return a boolean', function () {
            var response;
            var approval = {
                embargo: new mockEmbargo(q)
            };

            response = scope.disableCheck(approval);
            expect(response).toBe(true);

            approval.updating = true;
            approval.advisor = {};

            response = scope.disableCheck(approval);
            expect(response).toBe(true);

            approval.message = "test";
            approval.updating = false;

            response = scope.disableCheck(approval);
            expect(response).toBe(false);

            approval.advisor = { approve: true };

            response = scope.disableCheck(approval);
            expect(response).toBe(false);

            approval.embargo = { approve: true };

            response = scope.disableCheck(approval);
            expect(response).toBe(false);
        });
        it('predicateMatch should return a function', function () {
            var response;
            var fieldValue = mockFieldValue(q);
            var aggregateFieldProfile = mockFieldProfile(q);

            response = scope.predicateMatch(fieldValue);
            expect(typeof response).toBe("function");

            response = response(aggregateFieldProfile);
            expect(typeof response).toBe("boolean");
        });
        it('required should return a boolean', function () {
            var response;

            response = scope.required({optional: true});
            expect(response).toBe(false);

            response = scope.required({optional: false});
            expect(response).toBe(true);
        });
    });
});
