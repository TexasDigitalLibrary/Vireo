describe('controller: SidebarController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.sidebarService');

        inject(function ($controller, $rootScope, $window, _ModalService_, _RestApi_, _SidebarService_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('SidebarController', {
                $scope: scope,
                $window: $window,
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
