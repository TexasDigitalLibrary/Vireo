vireo.controller('UserRepoController', function ($controller, $location, $scope, $timeout, ManagedConfigurationRepo, TableFactory, User, UserRepo, UserService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.user = UserService.getCurrentUser();

    $scope.userRepo = UserRepo;

    // The /users route names no controller, so no SettingsController sits above this one and
    // $scope.settings does not exist here; read the application settings directly.
    $scope.configurable = ManagedConfigurationRepo.getAll();

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
        },
        {
            gloss: 'Password',
            property: 'passwordReset',
            filterable: false,
            sortable: false
        }],
        activeSort: [{
            property: 'role',
            direction: 'DESC'
        }]
    };

    // Matches the server's resolution: trim, case-insensitive compare against 'true'.
    var isEnabled = function (name) {
        var application = $scope.configurable ? $scope.configurable.application : undefined;
        var setting = application ? application[name] : undefined;
        var value = setting ? setting.value : undefined;
        return typeof value === 'string' && value.trim().toLowerCase() === 'true';
    };

    $scope.passwordResetAvailable = function () {
        return isEnabled('auth.pass.enabled') && isEnabled('auth.pass.reset.enabled');
    };

    $scope.confirmResetPassword = function (user) {
        $scope.userToResetPassword = user;
        $scope.openModal('#confirmResetPassword');
    };

    $scope.resetPassword = function () {
        UserRepo.resetPassword($scope.userToResetPassword);
        $scope.closeModal();
        $scope.userToResetPassword = null;
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
