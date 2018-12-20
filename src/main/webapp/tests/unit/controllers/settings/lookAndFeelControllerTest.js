describe('controller: LookAndFeelController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.fileService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.wsApi');

        inject(function ($controller, _$q_, $rootScope, $window, _FileService_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            // TODO: should this instead be initialized in the controller itself?
            scope.settings = {
                configurable: {
                    lookAndFeel: {
                        left_logo: {
                            value: ""
                        },
                        right_logo: {
                            value: ""
                        }
                    }
                }
            };

            controller = $controller('LookAndFeelController', {
                $q: _$q_,
                $scope: scope,
                $window: $window,
                FileService: _FileService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
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
