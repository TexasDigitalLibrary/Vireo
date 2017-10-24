vireo.controller('UserRepoController', function ($controller, $location, $route, $q, $scope, $timeout, StorageService, User, UserRepo, UserService) {

    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    $scope.user = UserService.getCurrentUser();

    $scope.users = UserRepo.getAll();

    UserRepo.listen(function(data) {
    	$scope.modalData = {};
		$scope.closeModal();
	});

    $scope.ready = $q.all([UserRepo.ready()]);

    $scope.roles = {
    	'ROLE_ADMIN' : 'Administrator',
        'ROLE_MANAGER' : 'Manager' ,
        'ROLE_REVIEWER': 'Reviewer',
        'ROLE_STUDENT' : 'Student'
    };

    $scope.modalData = {};

    $scope.ready.then(function() {

		$scope.updateRole = function(user, role) {
			user.role = role !== undefined ? role : user.role;
			user.save();
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

		$scope.selectUser = function (selectedUser) {
            $scope.modalData = selectedUser;
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
