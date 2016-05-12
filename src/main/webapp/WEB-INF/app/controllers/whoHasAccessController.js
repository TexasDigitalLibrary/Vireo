vireo.controller("WhoHasAccessController", function ($controller, $q, $scope, User, UserRepo) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.modalData = {};

    $scope.user = User.get();
    
    $scope.userRepo = UserRepo.get();
    
    $scope.ready = $q.all([User.ready(), UserRepo.ready()]);
    
    $scope.serverErrors = [];

    $scope.roles = {'ADMINISTRATOR'   : 'Administrator'   ,
                    'MANAGER' : 'Manager' ,
                    'REVIEWER': 'Reviewer',
                    'STUDENT' : 'Student'};

    $scope.ready.then(function() {
    	
    	$scope.closeModal = function(modalId) {
			angular.element('#' + modalId).modal('hide');
			// clear all errors, but not infos or warnings
			if($scope.serverErrors !== undefined) {
				$scope.serverErrors.errors = undefined;
			}
		}

        $scope.updateRole = function(userToEdit, selectedRole) {
        	userToEdit.role = selectedRole;
            UserRepo.updateRole($scope.user, userToEdit).then(function(data){
            	$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.closeModal('addMemberModal');
				}
            });
        };

        $scope.setSelectedUser = function (selectedUser) {
        	$scope.serverErrors = [];
            $scope.modalData = selectedUser;
        }
   
    });

});
