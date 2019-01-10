describe('controller: LookAndFeelController', function () {

    var controller, q, scope, window;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, $window, _FileService_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;
            window = $window;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

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
                $q: $q,
                $scope: scope,
                $window: $window,
                FileService: _FileService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.fileService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('previewLogo should be defined', function () {
            expect(scope.previewLogo).toBeDefined();
            expect(typeof scope.previewLogo).toEqual("function");
        });
        it('resetModalData should be defined', function () {
            expect(scope.resetModalData).toBeDefined();
            expect(typeof scope.resetModalData).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        // TODO: more research and work needed to get this test working.
        /*
        it('previewLogo should open a modal', function () {
            var mockEventListener = jasmine.createSpy();
            var mockReadAsDataURL = jasmine.createSpy();
            var mockFileReader = {
                addEventListener: mockEventListener,
                readAsDataURL: mockReadAsDataURL
            };

            spyOn(window, "FileReader").and.returnValue(mockFileReader)
            spyOn(angular, "element").and.callThrough();

            scope.previewLogo([{}]);
            scope.$digest();

            expect(angular.element).toHaveBeenCalled();
            expect(mockReadAsDataURL).toHaveBeenCalled();
        });
        */
        it('resetModalData should reset the modal data', function () {
            scope.modalData = {
                newLogo: null
            };

            scope.resetModalData();

            expect(scope.modalData.newLogo.setting).toBeDefined();
        });
    });

    // FIXME: there are methods not on the scope that are added in the controller that may need to be tested.
    // scope.modalData.confirmLogoUpload()
    // scope.modalData.cancelLogoUpload()

});
