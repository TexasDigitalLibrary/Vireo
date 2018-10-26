describe('controller: LanguagesController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.dragAndDropListenerFactory');
        module('mock.languageRepo');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $timeout, $window, _DragAndDropListenerFactory_, _LanguageRepo_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('LanguagesController', {
                $q: $q,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                LanguageRepo: _LanguageRepo_,
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
