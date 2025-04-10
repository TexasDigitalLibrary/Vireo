vireo.controller('UserRepoController', function ($controller, $location, $scope, $timeout, TableFactory, User, UserRepo, UserService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.user = UserService.getCurrentUser();

    $scope.userRepo = UserRepo;

    $scope.table = TableFactory.buildTable({
        pageNumber: sessionStorage.getItem('users-page') ? sessionStorage.getItem('users-page') : 1,
        pageSize: sessionStorage.getItem('users-size') ? sessionStorage.getItem('users-size') : 10,
        filters: {},
        counts: [5, 10, 25, 50, 100],
        name: 'users',
        repo: $scope.userRepo
    });

    $scope.weaverTable = {
        pageSettings: $scope.table.getPageSettings(),
        tableParams: $scope.table.getTableParams(),
        columns: [{
            gloss: 'Email',
            property: 'email',
            filterable: true,
            sortable: true
        },
        {
            gloss: 'First Name',
            property: 'firstName',
            filterable: true,
            sortable: true
        },
        {
            gloss: 'Last Name',
            property: 'lastName',
            filterable: true,
            sortable: true
        },
        {
            gloss: 'Role',
            property: 'role',
            filterable: true,
            sortable: true
        }],
        activeSort: [{
            property: 'role',
            direction: 'DESC'
        }]
    };

    $scope.roles = {};

    $scope.updateRole = function(user, role) {
        if (role !== undefined) {
            user.role = role;
        }
        user.dirty(true);
        user.save();
    };

    $scope.setRole = function(user) {
        $scope.roles[user.email] = $scope.allowableRoles(user.role);
    };

    $scope.disableUpdateRole = function(user) {
        return $scope.allowableRoles($scope.user.role).indexOf(user.role) < 0 || $scope.user.email === user.email;
    };

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

    UserRepo.listen(function() {
        $scope.closeModal();

        $scope.user = new User();

        $timeout(function() {
            if ($scope.user.role === 'ROLE_STUDENT' || $scope.user.role === 'ROLE_REVIEWER') {
                $location.path('/myprofile');
            }
        }, 250);
    });

});
