vireo.controller('UserRepoController', function ($controller, $location, $route, $q, $scope, $timeout, StorageService, User, UserRepo) {
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.user = new User();

    $scope.users = UserRepo.getAll();
    
    $scope.ready = $q.all([$scope.user.ready(), UserRepo.ready()]);

    $scope.roles = {
    	'ADMINISTRATOR' : 'Administrator',
        'MANAGER' : 'Manager' ,
        'REVIEWER': 'Reviewer',
        'STUDENT' : 'Student'
    };

    $scope.modalData = {};

    $scope.serverErrors = [];
    
    $scope.ready.then(function() {

		$scope.updateRole = function(user, role) {
			user.role = role !== undefined ? role : user.role;
			user.save().then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					angular.element('#addMemberModal').modal('hide');
					if($scope.serverErrors !== undefined) {
						$scope.serverErrors.errors = undefined;
					}
					$scope.modalData = {};
				}
			});
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
        	$scope.serverErrors = [];
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
