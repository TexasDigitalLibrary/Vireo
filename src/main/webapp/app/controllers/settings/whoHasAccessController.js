vireo.controller('WhoHasAccessController', function ($controller, $location, $scope, $timeout, User, UserRepo, UserService) {

    angular.extend(this, $controller('UserRepoController', { $scope: $scope, $location: $location, $timeout: $timeout, User: User, UserRepo: UserRepo, UserService: UserService }));

    $scope.assignableUsers = $scope.userRepo.getAssignableUsers();
    $scope.unassignableUsers = [];

    $scope.openAddMemberModal = function () {
        $scope.unassignableUsers = $scope.userRepo.getUnassignableUsers();
        $scope.openModal('#addMemberModal');
    };

});
