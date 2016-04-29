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
				if(userToEdit.role == 'STUDENT' || userToEdit.role == 'REVIEWER') {
					$location.path('/myprofile');
				}
				else {
					$route.reload();
				}
			}
		};
		
		$scope.allowableRoles = function(userRole) {
			if(sessionStorage.role == 'ADMINISTRATOR') {				
				return ['ADMINISTRATOR','MANAGER', 'REVIEWER', 'STUDENT'];
			}
			else if(sessionStorage.role == 'MANAGER') {
				if(userRole == 'ADMINISTRATOR') {
					return ['ADMINISTRATOR'];
				}
				return ['MANAGER', 'REVIEWER', 'STUDENT'];
			}
			else if(sessionStorage.role == 'REVIEWER') {
				if(userRole == 'ADMINISTRATOR') {
					return ['ADMINISTRATOR'];
				}
				return ['REVIEWER', 'STUDENT'];
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
