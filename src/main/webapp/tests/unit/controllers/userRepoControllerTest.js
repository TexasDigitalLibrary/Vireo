describe('controller: UserRepoController', function () {

    var controller, q, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $route, $q, $rootScope, $timeout, $window, _ModalService_, _RestApi_, _StorageService_, _UserRepo_, _UserService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('UserRepoController', {
                $location: $location,
                $q: q,
                $route: $route,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                User: mockParameterModel(q, mockUser),
                UserRepo: _UserRepo_,
                UserService: _UserService_,
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
        module('mock.user');
        module('mock.userRepo');
        module('mock.userService');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined for admin', function () {
            expect(controller).toBeDefined();
        });
        it('should be defined for manager', function () {
            initializeController({role: "ROLE_MANAGER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for reviewer', function () {
            initializeController({role: "ROLE_REVIEWER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for student', function () {
            initializeController({role: "ROLE_STUDENT"});
            expect(controller).toBeDefined();
        });
        it('should be defined for anonymous', function () {
            initializeController({role: "ROLE_ANONYMOUS"});
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('allowableRoles should be defined', function () {
            expect(scope.allowableRoles).toBeDefined();
            expect(typeof scope.allowableRoles).toEqual("function");
        });
        it('disableUpdateRole should be defined', function () {
            expect(scope.disableUpdateRole).toBeDefined();
            expect(typeof scope.disableUpdateRole).toEqual("function");
        });
        it('setRole should be defined', function () {
            expect(scope.setRole).toBeDefined();
            expect(typeof scope.setRole).toEqual("function");
        });
        it('updateRole should be defined', function () {
            expect(scope.updateRole).toBeDefined();
            expect(typeof scope.updateRole).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('allowableRoles should return allowed roles', function () {
            var response;

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(5);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(5);

            initializeController({role: "ROLE_MANAGER"});

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(4);

            initializeController({role: "ROLE_USER"});

            response = scope.allowableRoles("ROLE_ADMIN");
            expect(response.length).toBe(1);

            response = scope.allowableRoles("ROLE_ANONYMOUS");
            expect(response.length).toBe(1);

            initializeController({role: "ROLE_ANONYMOUS"});
        });
        it('disableUpdateRole should return a boolean', function () {
            var user = mockUser(q);
            var response;

            user.mock(dataUser2);

            response = scope.disableUpdateRole(user);
            expect(response).toBe(false);

            initializeController({role: "ROLE_MANAGER"});

            response = scope.disableUpdateRole(user);
            expect(response).toBe(true);

            user.mock(dataUser1);

            response = scope.disableUpdateRole(user);
            expect(response).toBe(true);
        });
        it('setRole should assign the role', function () {
            var user = mockUser(q);

            scope.roles = [];
            scope.setRole(user);
            expect(scope.roles[dataUser1.email]).toBeDefined();
        });
        it('updateRole should update the role', function () {
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
