describe('controller: ControlledVocabularyRepoController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.apiResponseActions');
        module('mock.controlledVocabularyRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.ngTableParams');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $timeout, $window, _ApiResponseActions_, _ControlledVocabularyRepo_, _DragAndDropListenerFactory_, _ModalService_, _NgTableParams_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('ControlledVocabularyRepoController', {
                $q: $q,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                ApiResponseActions: _ApiResponseActions_,
                ControlledVocabularyRepo: _ControlledVocabularyRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                NgTableParams: _NgTableParams_,
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
