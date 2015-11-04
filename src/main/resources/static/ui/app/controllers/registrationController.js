vireo.controller('RegistrationController', function ($controller, $scope, RestApi) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
	$scope.verifyEmail = function(email) {
		console.log(email);

		RestApi.get({
			controller: 'user',
			method: 'register?email=' + email
		}).then(function(data) {
			console.log(data);		
		});
	};

});
