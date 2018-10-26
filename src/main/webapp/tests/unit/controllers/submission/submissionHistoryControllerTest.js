describe('controller: SubmissionHistoryController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.modalService');
        module('mock.ngTableParams');
        module('mock.restApi');
        module('mock.submissionStatuses');
        module('mock.studentSubmissionRepo');

        inject(function ($controller, $location, $rootScope, $timeout, $window, _ModalService_, _NgTableParams_, _RestApi_, _SubmissionStatuses_, _StudentSubmissionRepo_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('SubmissionHistoryController', {
                $location: $location,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                ModalService: _ModalService_,
                NgTableParams: _NgTableParams_,
                RestApi: _RestApi_,
                SubmissionStatuses: _SubmissionStatuses_,
                StudentSubmissionRepo: _StudentSubmissionRepo_
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
