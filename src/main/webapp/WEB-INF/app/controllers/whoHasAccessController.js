vireo.controller("WhoHasAccessController", function ($controller, $q, $scope, User, UserRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.modalData = {};

    $scope.user = User.get();
    
    $scope.userRepo = UserRepo.get();
    
    $scope.ready = $q.all([User.ready(), UserRepo.ready()]);


    //To be deprecated. We should get arbitrary/dynamic roles from the service.
    $scope.roles = [
      {value: 'ROLE_ADMIN', label: 'Admin'},
      {value: 'ROLE_MANAGER', label: 'Manager'},
      {value: 'ROLE_REVIEWER', label: 'Reviewer'},
      {value: 'ROLE_STUDENT', label: 'Student'}
    ];

    $scope.selectedRole = $scope.roles[0];
    
    $scope.ready.then(function() {
      $scope.search = function(user){
        if (!$scope.multiFilter
            || (user.firstName.toLowerCase().indexOf($scope.multiFilter.toLowerCase()) != -1)
            || (user.lastName.toLowerCase().indexOf($scope.multiFilter.toLowerCase()) != -1)
            || (user.email.toLowerCase().indexOf($scope.multiFilter.toLowerCase()) != -1) ){
            return true;
          }
          return false;
        };
    
        $scope.updateRole = function(selectedUser, selectedRole) {
          UserRepo.updateRole($scope.user, selectedUser.email, selectedRole.value);
        };
   
    });

});
