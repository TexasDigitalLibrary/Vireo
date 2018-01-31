vireo.controller('ApplicationAuthenticationController', function ($controller, $scope) {
	
    angular.extend(this, $controller('AuthenticationController', {$scope: $scope}), $controller('AbstractController', {$scope: $scope}));

});