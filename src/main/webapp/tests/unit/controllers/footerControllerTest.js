describe("controller: FooterController", function () {

    var controller, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($location, $timeout, _WsApi_) {
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StorageService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            if (settings && settings.configurable) {
                scope.configurable = settings.configurable;
            }

            controller = $controller("FooterController", {
                $scope: scope,
                $window: mockWindow(),
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
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
        module("core");
        module("vireo");
        module("mock.managedConfiguration");
        module("mock.managedConfigurationRepo");
        module("mock.modalService");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("buildLink should be defined", function () {
            expect(scope.buildLink).toBeDefined();
            expect(typeof scope.buildLink).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("buildLink should generate default link", function () {
            var built;

            built = scope.buildLink();
            expect(built).toBe("#");

            built = scope.buildLink(null);
            expect(built).toBe("#");

            built = scope.buildLink({});
            expect(built).toBe("#");

            built = scope.buildLink({ value: null });
            expect(built).toBe("#");
        });
        it("buildLink should generate link as-is", function () {
            var built;

            built = scope.buildLink({ value: "#a" });
            expect(built).toBe("#a");

            built = scope.buildLink({ value: "http://example.com/" });
            expect(built).toBe("http://example.com/");
        });
        it("buildLink should generate e-mail link", function () {
            var built;

            built = scope.buildLink({ value: "a@b" });
            expect(built).toBe("mailto:a@b");

            built = scope.buildLink({ value: "\"user name\"@example.com" });
            expect(built).toBe("mailto:\"user name\"@example.com");
        });
    });

});
