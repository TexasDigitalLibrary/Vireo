describe('controller: AdminController', function () {

    var controller, scope, location;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $rootScope, $window, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();

            location = $location;
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('AdminController', {
                $scope: scope,
                $location: $location,
                $window: $window,
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
        it('isList should be defined', function () {
            expect(scope.isList).toBeDefined();
            expect(typeof scope.isList).toEqual("function");
        });
        it('isLog should be defined', function () {
            expect(scope.isLog).toBeDefined();
            expect(typeof scope.isLog).toEqual("function");
        });
        it('isSettings should be defined', function () {
            expect(scope.isSettings).toBeDefined();
            expect(typeof scope.isSettings).toEqual("function");
        });
        it('isView should be defined', function () {
            expect(scope.isView).toBeDefined();
            expect(typeof scope.isView).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('isList should return a boolean', function () {
            var result;

            result = scope.isList();
            expect(result).toBe(false);

            spyOn(location, 'path').and.returnValue('/admin/list');
            result = scope.isList();
            expect(result).toBe(true);
        });
        it('isLog should return a boolean', function () {
            var result;

            result = scope.isLog();
            expect(result).toBe(false);

            spyOn(location, 'path').and.returnValue('/admin/log');
            result = scope.isLog();
            expect(result).toBe(true);
        });
        it('isSettings should return a boolean', function () {
            var result;

            result = scope.isSettings();
            expect(result).toBe(false);

            spyOn(location, 'path').and.returnValue('/admin/settings');
            result = scope.isSettings();
            expect(result).toBe(true);
        });
        it('isView should return a boolean', function () {
            var result;

            result = scope.isView();
            expect(result).toBe(false);

            spyOn(location, 'path').and.returnValue('/admin/view');
            result = scope.isView();
            expect(result).toBe(true);
        });
    });
});
