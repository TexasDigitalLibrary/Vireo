vireo.controller('RegistrationController', function ($controller, $location, $scope, RestApi) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.registration = {
    	email: '',
    	token: ''
    };

	$scope.verifyEmail = function(email) {
		RestApi.anonymousGet({
			controller: 'user',
			method: 'register?email=' + email
		}).then(function(data) {
			$scope.registration.email = '';
		});
	};

	if(typeof $location.search().token != 'undefined') {
		$scope.registration.token = $location.search().token;
	}

	$scope.register = function() {
		RestApi.anonymousGet({
			controller: 'user',
			method: 'register',
			data: $scope.registration
		}).then(function(data) {
			$location.path("/home");
		});
	};

});
