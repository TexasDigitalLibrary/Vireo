describe('controller: EmbargoRepoController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.dragAndDropListenerFactory');
        module('mock.embargoRepo');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $filter, $q, $rootScope, $window, _DragAndDropListenerFactory_, _EmbargoRepo_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('EmbargoRepoController', {
                $filter: $filter,
                $q: $q,
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                EmbargoRepo: _EmbargoRepo_,
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
