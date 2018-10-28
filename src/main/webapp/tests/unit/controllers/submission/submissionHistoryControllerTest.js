describe('controller: SubmissionHistoryController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.modalService');
        module('mock.ngTableParams');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmissionRepo');
        module('mock.wsApi');

        inject(function ($controller, $location, $rootScope, $timeout, $window, SubmissionStates, _ModalService_, _NgTableParams_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('SubmissionHistoryController', {
                $location: $location,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                SubmissionStates: SubmissionStates,
                ModalService: _ModalService_,
                NgTableParams: _NgTableParams_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

});
