describe('controller: LookAndFeelController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.fileService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.wsApi');

        inject(function ($controller, $q, $rootScope, $window, _FileService_, _ModalService_, _RestApi_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('LookAndFeelController', {
                $q: $q,
                $scope: scope,
                $window: $window,
                FileService: _FileService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                WsApi: _WsApi_
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
