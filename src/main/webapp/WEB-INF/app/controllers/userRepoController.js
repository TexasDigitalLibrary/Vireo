vireo.controller('UserRepoController', function ($controller, $location, $route, $q, $scope, StorageService, User, UserRepo) {
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.user = User.get();

    $scope.userRepo = UserRepo.get();
    
    $scope.ready = $q.all([User.ready()]);
    
    $scope.ready.then(function() {

		$scope.updateRole = function(userToEdit) {
			UserRepo.updateRole($scope.user, userToEdit);
			if($scope.user.email == userToEdit.email) {
				StorageService.set("role", userToEdit.role);
				if(userToEdit.role == 'ROLE_STUDENT' || userToEdit.role == 'ROLE_REVIEWER') {
					$location.path('/myprofile');
				}
				else {
					$route.reload();
				}
			}
		};
		
		$scope.allowableRoles = function(userRole) {
			if(sessionStorage.role == 'ROLE_ADMIN') {				
				return ['ROLE_ADMIN','ROLE_MANAGER', 'ROLE_REVIEWER', 'ROLE_STUDENT'];
			}
			else if(sessionStorage.role == 'ROLE_MANAGER') {
				if(userRole == 'ROLE_ADMIN') {
					return ['ROLE_ADMIN'];
				}
				return ['ROLE_MANAGER', 'ROLE_REVIEWER', 'ROLE_STUDENT'];
			}
			else if(sessionStorage.role == 'ROLE_REVIEWER') {
				if(userRole == 'ROLE_ADMIN') {
					return ['ROLE_ADMIN'];
				}
				return ['ROLE_REVIEWER', 'ROLE_STUDENT'];
			}
			else {
				return [userRole];
			}
		};
		
		UserRepo.listen().then(null, null, function(data) {

	    	if(typeof $scope.user != 'undefined') {
	    		var payload = angular.fromJson(data.body).payload;
	    		console.log(payload);
	    		if(payload.HashMap.changedUserEmail == $scope.user.email) {
					$scope.user = User.refresh();
					$route.reload();						
				}	
	    	}		
		});
    });
});
