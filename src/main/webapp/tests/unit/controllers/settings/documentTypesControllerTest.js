describe('controller: DocumentTypesRepoController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.documentTypeRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $rootScope, $window, _DocumentTypeRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('DocumentTypesRepoController', {
                $scope: scope,
                $window: $window,
                DocumentTypeRepo: _DocumentTypeRepo_,
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
