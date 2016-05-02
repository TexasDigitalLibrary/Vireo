vireo.controller("WhoHasAccessController", function ($controller, $q, $scope, User, UserRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.modalData = {};

    $scope.user = User.get();
    
    $scope.userRepo = UserRepo.get();
    
    $scope.ready = $q.all([User.ready(), UserRepo.ready()]);

    $scope.roles = {'ADMINISTRATOR'   : 'Administrator'   ,
                    'MANAGER' : 'Manager' ,
                    'REVIEWER': 'Reviewer',
                    'STUDENT' : 'Student'};

    $scope.ready.then(function() {

        $scope.updateRole = function(userToEdit, selectedRole) {
        	userToEdit.role = selectedRole;
            UserRepo.updateRole($scope.user, userToEdit);
        };

        $scope.setSelectedUser = function (selectedUser) {
            $scope.modalData = selectedUser;
        }
   
    });

});
