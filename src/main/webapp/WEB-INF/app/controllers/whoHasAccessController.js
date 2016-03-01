vireo.controller("WhoHasAccessController", function ($controller, $q, $scope, User, UserRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

        $scope.modalData = {};

        $scope.currentUser = User.get();
	$scope.userRepo = UserRepo.get();
        $scope.ready = $q.all([UserRepo.ready()]);
	$scope.ready.then(function() {
          console.info($scope.userRepo);
        });

        $scope.search = function(user){
            if (!$scope.multiFilter
                || (user.firstName.toLowerCase().indexOf($scope.multiFilter.toLowerCase()) != -1)
                || (user.lastName.toLowerCase().indexOf($scope.multiFilter.toLowerCase()) != -1)
                || (user.email.toLowerCase().indexOf($scope.multiFilter.toLowerCase()) != -1) ){
                return true;
            }
            return false;
        };

  //To be deprecated. We should get arbitrary/dynamic roles from the service.
  $scope.roles = {'ROLE_ADMIN': 'Admin',
                  'ROLE_MANAGER': 'Manager',
                  'ROLE_REVIEWER': 'Reviewer',
                  'ROLE_STUDENT': 'Student'};

        $scope.addSelectedUser = function () {
            UserRepo.addAccess($scope.modalData);
        }

        $scope.setSelectedUser = function (selectedUser) {
            $scope.modalData = selectedUser;
        }

        $scope.setUserPermissions = function (targetUserEmail, newRole) {
            UserRepo.updateRole($scope.currentUser, targetUserEmail, newRole);
        }
});
