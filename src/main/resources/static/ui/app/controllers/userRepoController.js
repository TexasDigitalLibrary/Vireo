seedApp.controller('UserRepoController', function ($controller, $location, $route, $scope, StorageService, User, UserRepo) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.user = User.get();

    $scope.userRepo = UserRepo.get();
     
    User.ready().then(function() {    	
		$scope.updateRole = function(uin, role) {	
			UserRepo.updateRole($scope.user, uin, role);
			if($scope.user.uin == uin) {
				if(role == 'ROLE_USER') {
					$location.path('/myview');
				}
				else {
					$route.reload();
				}
			}
		};		
		$scope.allowableRoles = function(userRole) {
			if(sessionStorage.role == 'ROLE_ADMIN') {				
				return ['ROLE_ADMIN','ROLE_MANAGER','ROLE_USER'];
			}
			else if(sessionStorage.role == 'ROLE_MANAGER') {
				if(userRole == 'ROLE_ADMIN') {
					return ['ROLE_ADMIN'];
				}
				return ['ROLE_MANAGER','ROLE_USER'];
			}
			else {
				return [userRole];
			}
		};

    });

    UserRepo.listen().then(null, null, function(data) {

    	if(typeof $scope.user != 'undefined') {
    		if(JSON.parse(data.body).content.HashMap.changedUserUin == $scope.user.uin) {
				$scope.user = User.refresh();
				$route.reload();						
			}	
    	}		
	});
	
});
