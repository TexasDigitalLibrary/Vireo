vireo.controller('AdminController', function ($controller, $scope, $window, $route, AssumedControl, AuthServiceApi, StorageService, User, UserRepo, WsApi) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	var services = [
		User,
		UserRepo
	];

	$scope.user = User.get();

	$scope.$watch('user.role', function() {
		if($scope.user.role) {
			StorageService.set('role', $scope.user.role);
			if ($scope.user.role == 'ADMINISTRATOR') {
				$scope.admin = true;
			}
			else {
				$scope.admin = false;
			}
		}
	});
		
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
