describe('controller: SubmissionHistoryController', function () {

    var controller, scope, NgTableParams;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $rootScope, $timeout, $window, SubmissionStates, _ModalService_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('SubmissionHistoryController', {
                $location: $location,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                SubmissionStates: SubmissionStates,
                ModalService: _ModalService_,
                NgTableParams: mockNgTableParams,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
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
        module('mock.modalService');
        module('mock.ngTableParams');
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

});
