describe('controller: EmailTemplateRepoController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.apiResponseActions');
        module('mock.dragAndDropListenerFactory');
        module('mock.emailTemplateRepo');
        module('mock.fieldPredicateRepo');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $window, _ApiResponseActions_, _DragAndDropListenerFactory_, _EmailTemplateRepo_, _FieldPredicateRepo_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('EmailTemplateRepoController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                ApiResponseActions: _ApiResponseActions_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                EmailTemplateRepo: _EmailTemplateRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
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
