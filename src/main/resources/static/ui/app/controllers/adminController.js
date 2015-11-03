vireo.controller('AdminController', function ($controller, $scope, $window, $route, AssumedControl, AuthServiceApi, StorageService, User, UserRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	var services = [
		User,
		UserRepo
	];

	$scope.user = User.get();

    $scope.assumedControl = AssumedControl.get();

    AssumedControl.set({
		'netid': '',
		'button': StorageService.get("assuming") == 'true' ? 'Unassume User' : 'Assume User',
		'status': StorageService.get("assuming") == 'true' ? 'assumed' : '',
	});

	$scope.$watch('user.role', function() {
		if($scope.user.role) {
			StorageService.set('role', $scope.user.role);
			if ($scope.user.role == 'ROLE_ADMIN') {
				$scope.admin = true;
			}
			else {
				$scope.admin = false;
			}
		}
	});

	$scope.assumeUser = function(user) {
	
		if($scope.isAssuming() == 'false') {

			if ((typeof user !== 'undefined') && user.netid) {	
				
				AssumedControl.assume(user, services).then(function(assumed) {
					if(assumed) {
						angular.element("#assumeUserModal").modal("hide");
						console.log(assumed);
						$route.reload();
					}
				});

			}
			else {
				logger.log("User to assume undefined!");
			}

		} else {
			
			AssumedControl.unassume(user, services, $scope.user.role).then(function(unassumed) {
				$route.reload();
			});
			
		}
		
	};
		
	$scope.isMocking = function() {
		if(appConfig.mockRole) {
			return true;
		}
		else {
			return false;
		}
	};

	$scope.logout = function(url) {
		StorageService.delete('token');
		StorageService.delete('role');
		window.open(url, "_self");
	};
	
});
