vireo.controller('WhoHasAccessController', function ($controller, $location, $scope, $timeout, NgTableParams, User, UserRepo, UserService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.userRepo = UserRepo;

    $scope.roles = [];

    $scope.allowableRoles = function(role) {
        if (sessionStorage.role === 'ROLE_ADMIN') {
            return ['ROLE_ADMIN','ROLE_MANAGER', 'ROLE_REVIEWER', 'ROLE_STUDENT', 'ROLE_ANONYMOUS'];
        }
        else if (sessionStorage.role === 'ROLE_MANAGER') {
            if (role === 'ROLE_ADMIN') {
                return ['ROLE_ADMIN'];
            }
            return ['ROLE_MANAGER', 'ROLE_REVIEWER', 'ROLE_STUDENT', 'ROLE_ANONYMOUS'];
        }
        else if (sessionStorage.role === 'ROLE_REVIEWER') {
            if (role === 'ROLE_ADMIN') {
                return ['ROLE_ADMIN'];
            }
            return ['ROLE_REVIEWER', 'ROLE_STUDENT', 'ROLE_ANONYMOUS'];
        }
        else {
            return [role];
        }
    };

    $scope.updateRole = function(user, role) {
        if (role !== undefined) {
            user.role = role;
        }
        user.dirty(true);
        user.save().then(response => {
            if (angular.fromJson(response?.body)?.meta?.status === 'SUCCESS') {
                $scope.unassignableUsersTable.reload();
                $scope.assignableUsersTable.reload();
            }
        });
    };

    $scope.setRole = function(user) {
        $scope.roles[user.email] = $scope.allowableRoles(user.role);
    };

    $scope.openAddMemberModal = function () {
        $scope.unassignableUsers = $scope.userRepo.getUnassignableUsers();
        $scope.openModal('#addMemberModal');
    };

    $scope.setAssignableTable = function () {
        $scope.assignableUsersTable = new NgTableParams({
            page: 1,
            count: 5,
            sorting: { name: 'asc' }
        }, {
            counts: [ 5, 10, 25, 50, 100 ],
            getData: function (params) {
                var name = "";
                if (angular.isDefined(params._params.filter.name)) {
                    name = params._params.filter.name;
                }

                return $scope.userRepo.getAssignableUsersTotal(name).then(function(response) {
                    var total = 0;
                    var resObj = angular.fromJson(response.body);
                    if (resObj.meta.status === 'SUCCESS') {
                        total = resObj.payload.Long;
                    }

                    params.total(total);

                    var list = [];
                    if (total) {
                        list = $scope.userRepo.getAssignableUsers(params.page() - 1, params.count(), name);
                    }

                    return list;
                });
            },
        });
    };

    $scope.setUnassignableTable = function () {
        $scope.unassignableUsersTable = new NgTableParams({
            page: 1,
            count: 5,
            sorting: { name: 'asc' }
        }, {
            counts: [ 5, 10, 25, 50, 100 ],
            getData: function (params) {
                var name = "";
                if (angular.isDefined(params._params.filter.name)) {
                    name = params._params.filter.name;
                }

                return $scope.userRepo.getUnassignableUsersTotal(name).then(function(response) {
                    var total = 0;
                    var resObj = angular.fromJson(response.body);
                    if (resObj.meta.status === 'SUCCESS') {
                        total = resObj.payload.Long;
                    }

                    params.total(total);

                    var list = [];
                    if (total) {
                        list = $scope.userRepo.getUnassignableUsers(params.page() - 1, params.count(), name);
                    }

                    return list;
                });
            },
        });
    };

    UserService.userReady().then(function (event) {
        $scope.user = UserService.getCurrentUser();

        $scope.setAssignableTable();
        $scope.setUnassignableTable();

        UserRepo.listen(function() {
            $scope.closeModal();

            $scope.user = new User();
        });
    });

});
