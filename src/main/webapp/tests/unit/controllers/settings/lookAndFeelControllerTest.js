describe('controller: LookAndFeelController', function () {

    var controller, q, scope, window, FileService, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _FileService_, _WsApi_) {
            q = $q;
            window = mockWindow();

            FileService = _FileService_;
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, _ModalService_, _RestApi_, _StorageService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

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
                $q: q,
                $scope: scope,
                $window: window,
                FileService: FileService,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WsApi: WsApi
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
        initializeVariables();
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
        it('previewLogo should open a modal', function () {
            var mockData = "body { color: #000000; }";
            var mockBlob = new Blob([mockData], {type: "text/css"});
            var mockFile = new File([mockBlob], "mocked.css", {type:"text/css"});

            FileReader = function() {
                return {
                  result: mockData,
                  readAsDataURL: function (data) {
                      this.onload();
                    return mockFile;
                  }
              };
            };

            scope.previewLogo([]);
            scope.$digest();

            scope.previewLogo([mockFile]);
            scope.$digest();
        });
        it('resetLogo should reset the logo', function () {
            var mockSetting = {};

            scope.resetLogo(mockSetting);
            scope.$digest();

            WsApi.mockFetchResponse({ type: "payload", messageStatus: "OTHER" });
            scope.resetLogo(mockSetting);
            scope.$digest();

            // FIXME: resetLogo() is handling response.payload, but abstractController.js reportError() handles the error data as response.data.
            //        until that is resolved, simulate the response.payload using valueResponse.
            var valueResponse = {
                meta: {
                    status: 'INVALID',
                },
                payload: {},
                status: 500
            };

            WsApi.mockFetchResponse({ type: "value", payload: valueResponse, valueType: "reject" });
            scope.resetLogo(mockSetting);
            scope.$digest();

            valueResponse.payload = undefined;
            WsApi.mockFetchResponse({ type: "value", payload: valueResponse, valueType: "reject" });
            scope.resetLogo(mockSetting);
            scope.$digest();
        });
        it('resetModalData should reset the modal data', function () {
            scope.modalData = {
                newLogo: null
            };

            scope.resetModalData();

            expect(scope.modalData.newLogo.setting).toBeDefined();
        });
    });

    describe('Are the scope.modalData methods defined', function () {
        it('confirmLogoUpload should be defined', function () {
            expect(scope.modalData.confirmLogoUpload).toBeDefined();
            expect(typeof scope.modalData.confirmLogoUpload).toEqual("function");
        });
        it('cancelLogoUpload should be defined', function () {
            expect(scope.modalData.cancelLogoUpload).toBeDefined();
            expect(typeof scope.modalData.cancelLogoUpload).toEqual("function");
        });
    });


    describe('Do the scope.modalData methods work as expected', function () {
        it('confirmLogoUpload should upload the logo', function () {
            var data = {};

            FileService.upload = function() {
                return dataPromise(q.defer(), data);
            };
            scope.modalData.confirmLogoUpload();
            scope.$digest();

            data.DefaultConfiguration = {
                name: "left_logo",
                value: ""
            };
            scope.modalData.confirmLogoUpload();
            scope.$digest();

            data.ManagedConfiguration = {
                name: "right_logo",
                value: ""
            };
            scope.modalData.confirmLogoUpload();
            scope.$digest();

            FileService.upload = function() {
                return dataPromise(q.defer(), data, "OTHER");
            };
            scope.modalData.confirmLogoUpload();
            scope.$digest();

            data.payload = {};
            FileService.upload = function() {
                return valuePromise(q.defer(), data, "reject");
            };
            scope.modalData.confirmLogoUpload();
            scope.$digest();

            data.payload = undefined;
            scope.modalData.confirmLogoUpload();
            scope.$digest();
        });
        it('cancelLogoUpload should cancel the upload', function () {
            spyOn(scope, "resetModalData");

            scope.modalData.cancelLogoUpload();
            scope.$digest();

            expect(scope.resetModalData).toHaveBeenCalled();
        });
    });


});
