vireo.controller('UserRepoController', function ($controller, $location, $route, $q, $scope, $timeout, StorageService, User, UserRepo, UserService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.user = UserService.getCurrentUser();

    $scope.users = UserRepo.getAll();

    UserRepo.listen(function(data) {
		$scope.closeModal();
	});

    $scope.ready = $q.all([UserRepo.ready()]);

    $scope.roles = {
    	'ROLE_ADMIN' : 'Administrator',
        'ROLE_MANAGER' : 'Manager' ,
        'ROLE_REVIEWER': 'Reviewer',
        'ROLE_STUDENT' : 'Student'
    };

    $scope.ready.then(function() {

		$scope.updateRole = function(user, role) {
			if(role !== undefined) {
				user.role = role;
			}
			user.save();
		};

		$scope.disableUpdateRole = function(role) {
			return $scope.allowableRoles($scope.user.role).indexOf(role) < 0;
		};

		$scope.allowableRoles = function(role) {
			if(sessionStorage.role === 'ROLE_ADMIN') {
				return ['ROLE_ADMIN','ROLE_MANAGER', 'ROLE_REVIEWER', 'ROLE_STUDENT', 'ROLE_ANONYMOUS'];
			}
			else if(sessionStorage.role === 'ROLE_MANAGER') {
				if(role === 'ROLE_ADMIN') {
					return ['ROLE_ADMIN'];
				}
				return ['ROLE_MANAGER', 'ROLE_REVIEWER', 'ROLE_STUDENT', 'ROLE_ANONYMOUS'];
			}
			else if(sessionStorage.role === 'ROLE_REVIEWER') {
				if(role === 'ROLE_ADMIN') {
					return ['ROLE_ADMIN'];
				}
				return ['ROLE_REVIEWER', 'ROLE_STUDENT', 'ROLE_ANONYMOUS'];
			}
			else {
				return [role];
			}
		};

		UserRepo.listen(function() {
	    	$scope.user = new User();
	    	$timeout(function() {
		    	if($scope.user.role === 'ROLE_STUDENT' || $scope.user.role === 'ROLE_REVIEWER') {
					$location.path('/myprofile');
				}
	    	}, 250);

		});

    });
});
