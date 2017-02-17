vireo.controller('UserRepoController', function ($controller, $location, $route, $q, $scope, $timeout, StorageService, User, UserRepo, UserService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.user = UserService.getCurrentUser();

    $scope.users = UserRepo.getAll();

    UserRepo.listen(function(data) {
      $scope.modalData = {};
    $scope.closeModal();
  });

    $scope.ready = $q.all([$scope.user.ready(), UserRepo.ready()]);

    $scope.roles = {
      'ADMINISTRATOR' : 'Administrator',
        'MANAGER' : 'Manager' ,
        'REVIEWER': 'Reviewer',
        'STUDENT' : 'Student'
    };

    $scope.modalData = {};

    $scope.ready.then(function() {

    $scope.updateRole = function(user, role) {
      user.role = role !== undefined ? role : user.role;
      user.save();
    };

    $scope.allowableRoles = function(userRole) {
      if(sessionStorage.role == 'ADMINISTRATOR') {
        return ['ADMINISTRATOR','MANAGER', 'REVIEWER', 'STUDENT', 'NONE'];
      }
      else if(sessionStorage.role == 'MANAGER') {
        if(userRole == 'ADMINISTRATOR') {
          return ['ADMINISTRATOR'];
        }
        return ['MANAGER', 'REVIEWER', 'STUDENT', 'NONE'];
      }
      else if(sessionStorage.role == 'REVIEWER') {
        if(userRole == 'ADMINISTRATOR') {
          return ['ADMINISTRATOR'];
        }
        return ['REVIEWER', 'STUDENT', 'NONE'];
      }
      else {
        return [userRole];
      }
    };

    $scope.selectUser = function (selectedUser) {
            $scope.modalData = selectedUser;
        }

    UserRepo.listen(function() {
        $scope.user = new User();
        $timeout(function() {
          if($scope.user.role == 'STUDENT' || $scope.user.role == 'REVIEWER') {
          $location.path('/myprofile');
        }
        }, 250);

    });

    });
});
