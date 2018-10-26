describe('controller: CustomActionSettingsController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.customActionDefinitionRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $timeout, $window, _CustomActionDefinitionRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('CustomActionSettingsController', {
                $q: $q,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                CustomActionDefinitionRepo: _CustomActionDefinitionRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
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
