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

    var getRole = function(role) {
      for(var i in $scope.roles) {
        if($scope.roles[i].value == role) {
          return $scope.roles[i];
        }
      }
    }

    $scope.selectedRole = {};
    
    $scope.ready.then(function() {

      for(var i in $scope.userRepo.list) {
        var userObj = $scope.userRepo.list[i];
        $scope.selectedRole[userObj.email] = getRole(userObj.role);
      }
      
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


        $scope.addSelectedUser = function () {
          UserRepo.addAccess($scope.modalData).then(function() {
            //TODO: ensure list of users is being updated from broadcast
          });
        }

        $scope.setSelectedUser = function (selectedUser) {
            $scope.modalData = selectedUser;
        }
   
    });

});
