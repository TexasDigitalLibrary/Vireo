vireo.controller('UserRepoController', function ($controller, $location, $route, $scope, StorageService, User, UserRepo) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.user = User.get();

    $scope.userRepo = UserRepo.get();
     
 	if(!$scope.isAnonymous() && User.ready() !== null) {
	    User.ready().then(function() {
			$scope.updateRole = function(uin, role) {
				UserRepo.updateRole($scope.user, uin, role);
				if($scope.user.uin == uin) {
					if(role == 'ROLE_STUDENT' || role == 'ROLE_REVIEWER') {
						$location.path('/myview');
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

	    });
	}

    UserRepo.listen().then(null, null, function(data) {

    	if(typeof $scope.user != 'undefined') {
    		console.log(data);
    		if(JSON.parse(data.body).payload.HashMap.changedUserEmail == $scope.user.email) {
				$scope.user = User.refresh();
				$route.reload();						
			}	
    	}		
	});
	
});
