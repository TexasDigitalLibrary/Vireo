describe("controller: UserRepoController", function () {

    var controller, q, scope, timeout, mockedUser, User, UserService, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $timeout, _WsApi_) {
            q = $q;
            timeout = $timeout;

            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $location, $route, $rootScope, _ModalService_, _RestApi_, _StorageService_, _TableFactory_, _User_, _UserRepo_, _UserService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            UserService = _UserService_;

            controller = $controller("UserRepoController", {
                $location: $location,
                $q: q,
                $route: $route,
                $scope: scope,
                $timeout: timeout,
                $window: mockWindow(),
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                TableFactory: _TableFactory_,
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
        it("allowableRoles should be defined", function () {
            expect(scope.allowableRoles).toBeDefined();
            expect(typeof scope.allowableRoles).toEqual("function");
        });
        it("disableUpdateRole should be defined", function () {
            expect(scope.disableUpdateRole).toBeDefined();
            expect(typeof scope.disableUpdateRole).toEqual("function");
        });
        it("setRole should be defined", function () {
            expect(scope.setRole).toBeDefined();
            expect(typeof scope.setRole).toEqual("function");
        });
        it("updateRole should be defined", function () {
            expect(scope.updateRole).toBeDefined();
            expect(typeof scope.updateRole).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("allowableRoles should return allowed roles", function () {
            var response;

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(5);

            response = scope.allowableRoles("ROLE_MANAGER");
            expect(response.length).toBe(5);

            response = scope.allowableRoles("ROLE_REVIEWER");
            expect(response.length).toBe(5);

            response = scope.allowableRoles("ROLE_STUDENT");
            expect(response.length).toBe(5);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(5);


            initializeController({role: "ROLE_MANAGER"});

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_MANAGER");
            expect(response.length).toBe(4);

            response = scope.allowableRoles("ROLE_REVIEWER");
            expect(response.length).toBe(4);

            response = scope.allowableRoles("ROLE_STUDENT");
            expect(response.length).toBe(4);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(4);


            initializeController({role: "ROLE_USER"});

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_MANAGER");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_REVIEWER");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_STUDENT");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(1);


            initializeController({role: "ROLE_REVIEWER"});

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_MANAGER");
            expect(response.length).toBe(3);

            response = scope.allowableRoles("ROLE_REVIEWER");
            expect(response.length).toBe(3);

            response = scope.allowableRoles("ROLE_STUDENT");
            expect(response.length).toBe(3);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(3);


            initializeController({role: "ROLE_ANONYMOUS"});

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_MANAGER");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_REVIEWER");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_STUDENT");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(1);

            timeout.flush();
        });
        it("disableUpdateRole should return a boolean", function () {
            var user = mockUser(q);
            var response;

            user.mock(dataUser1);
            user.role = "ROLE_ADMIN";

            scope.user = mockUser(q);
            scope.user.mock(dataUser1);
            scope.user.role = "ROLE_ADMIN";

            response = scope.disableUpdateRole(user);
            expect(response).toBe(true);

            user.role = "ROLE_NO_MATCH";

            response = scope.disableUpdateRole(user);
            expect(response).toBe(true);

            user.role = scope.user.role;
            user.email = "different";

            response = scope.disableUpdateRole(user);
            expect(response).toBe(false);
        });
        it("setRole should assign the role", function () {
            var user = mockUser(q);

            scope.roles = [];
            scope.setRole(user);
            expect(scope.roles[dataUser1.email]).toBeDefined();
        });
        it("updateRole should update the role", function () {
            var user = mockUser(q);
            delete user.role;

            spyOn(user, "save");

            scope.updateRole(user);
            expect(user.save).toHaveBeenCalled();
            expect(user.role).not.toBeDefined();

            scope.updateRole(user, "test");
            expect(user.role).toBe("test");
        });
    });

});
