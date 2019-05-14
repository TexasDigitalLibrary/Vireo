describe('controller: HeaderController', function () {

    var controller, location, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $rootScope, $timeout, _AbstractRepo_, _AbstractAppRepo_, _AlertService_, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            location = $location;
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('HeaderController', {
                $scope: scope,
                $location: $location,
                $timeout: $timeout,
                $window: mockWindow(),
                AbstractRepo: _AbstractRepo_,
                AbstractAppRepo: _AbstractAppRepo_,
                AlertService: _AlertService_,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
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
        module('mock.abstractAppRepo');
        module('mock.abstractRepo');
        module('mock.alertService');
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
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
        it('activeAdminSection should be defined', function () {
            expect(scope.activeAdminSection).toBeDefined();
            expect(typeof scope.activeAdminSection).toEqual("function");
        });
        it('activeTab should be defined', function () {
            expect(scope.activeTab).toBeDefined();
            expect(typeof scope.activeTab).toEqual("function");
        });
        it('logoImage should be defined', function () {
            expect(scope.logoImage).toBeDefined();
            expect(typeof scope.logoImage).toEqual("function");
        });
        it('viewSelect should be defined', function () {
            expect(scope.viewSelect).toBeDefined();
            expect(typeof scope.viewSelect).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('activeAdminSection should return a bool', function () {
            var response;
            var originalUrl = location.url;

            spyOn(location, "url").and.callThrough();

            response = scope.activeAdminSection();

            expect(location.url).toHaveBeenCalled();
            expect(response).toBe(false);

            location.url = originalUrl;
            spyOn(location, 'url').and.returnValue('/admin');

            response = scope.activeAdminSection();
            expect(response).toBe(true);
        });
        it('activeTab should return a bool', function () {
            var response;

            spyOn(location, "url").and.callThrough();

            response = scope.activeTab("/inactive");
            expect(location.url).toHaveBeenCalled();
            expect(response).toBe(false);

            response = scope.activeTab("/");
            expect(location.url).toHaveBeenCalled();
            expect(response).toBe(true);
        });
        it('logoImage should return the logoPath', function () {
            var response = scope.logoImage();

            expect(response).toBe("");

            scope.configurable = {
                lookAndFeel: {
                    left_logo: {
                        value: "left.logo"
                    },
                    right_logo: {
                        value: "right.logo"
                    }
                }
            };

            response = scope.logoImage();
            expect(response).toBe("left.logo");

            delete scope.configurable.lookAndFeel;
            spyOn(location, 'url').and.returnValue('/admin');

            response = scope.logoImage();
            expect(response).toBe("resources/images/logo.png");
        });
        it('viewSelect should change the path', function () {
            var originalPath = location.path;
            spyOn(location, "path");

            scope.viewSelect();
            expect(location.path).toHaveBeenCalled();

            location.path = originalPath;
            spyOn(location, "path");
            spyOn(location, 'url').and.returnValue('/admin/view');

            scope.viewSelect();
            expect(location.path).not.toHaveBeenCalled();
        });
    });

});
