describe('controller: GraduationMonthRepoController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.dragAndDropListenerFactory');
        module('mock.graduationMonthRepo');
        module('mock.modalService');
        module('mock.restApi');

        inject(function ($controller, $q, $rootScope, $window, _DragAndDropListenerFactory_, _GraduationMonthRepo_, _ModalService_, _RestApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('GraduationMonthRepoController', {
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                GraduationMonthRepo: _GraduationMonthRepo_,
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
