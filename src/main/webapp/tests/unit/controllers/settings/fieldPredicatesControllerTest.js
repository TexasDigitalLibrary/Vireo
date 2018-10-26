describe('controller: FieldPredicatesController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.dragAndDropListenerFactory');
        module('mock.fieldPredicateRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.sidebarService');

        inject(function ($filter, $q, $controller, $rootScope, $timeout, $window, _DragAndDropListenerFactory_, _FieldPredicateRepo_, _ModalService_, _RestApi_, _SidebarService_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('FieldPredicatesController', {
                $filter: $filter,
                $q: $q,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_
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
