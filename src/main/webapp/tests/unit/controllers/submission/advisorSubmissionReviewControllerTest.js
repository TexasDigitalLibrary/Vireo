describe('controller: AdvisorSubmissionReviewController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, $window, _AdvisorSubmissionRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('AdvisorSubmissionReviewController', {
                $scope: scope,
                $window: $window,
                AdvisorSubmissionRepo: _AdvisorSubmissionRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                Submission: mockSubmission,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
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

});
