describe('controller: DepositLocationRepoController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.depositLocation');
        module('mock.depositLocationRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.packagerRepo');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $window, _DepositLocation_, _DepositLocationRepo_, _DragAndDropListenerFactory_, _ModalService_, _PackagerRepo_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('DepositLocationRepoController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                DepositLocation: _DepositLocation_,
                DepositLocationRepo: _DepositLocationRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                PackagerRepo: _PackagerRepo_,
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
