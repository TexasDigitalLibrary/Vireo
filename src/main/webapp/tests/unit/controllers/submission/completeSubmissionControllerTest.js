describe('controller: CompleteSubmissionController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $rootScope, $window, _ManagedConfigurationRepo_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('CompleteSubmissionController', {
                $scope: scope,
                $window: $window,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_
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
