describe("controller: WhoHasAccessController", function () {

    var controller, q, scope, timeout, mockedUser, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $timeout, _WsApi_) {
            q = $q;
            timeout = $timeout;
            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $location, $route, $rootScope, _ModalService_, _RestApi_, _StorageService_, _User_, _UserRepo_, _UserService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("WhoHasAccessController", {
                $location: $location,
                $q: q,
                $route: $route,
                $scope: scope,
                $timeout: timeout,
                $window: mockWindow(),
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                User: _User_,
                UserRepo: _UserRepo_,
                UserService: _UserService_,
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
        module("mock.modalService");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.user", function($provide) {
            User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
        module("mock.userRepo");
        module("mock.userService");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined for admin", function () {
            expect(controller).toBeDefined();
        });
        it("should be defined for manager", function () {
            initializeController({role: "ROLE_MANAGER"});
            expect(controller).toBeDefined();
        });
        it("should be defined for reviewer", function () {
            initializeController({role: "ROLE_REVIEWER"});
            expect(controller).toBeDefined();
        });
        it("should be defined for student", function () {
            initializeController({role: "ROLE_STUDENT"});
            expect(controller).toBeDefined();
        });
        it("should be defined for anonymous", function () {
            initializeController({role: "ROLE_ANONYMOUS"});
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("openAddMemberModal should be defined", function () {
            expect(scope.openAddMemberModal).toBeDefined();
            expect(typeof scope.openAddMemberModal).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("openAddMemberModal should open a modal", function () {
            var user = mockUser(q);

            spyOn(scope, "openModal");

            scope.openAddMemberModal();

            expect(scope.openModal).toHaveBeenCalled();
        });

    });

});
