vireo.controller('RegistrationController', function ($controller, $location, $scope, $timeout, AlertService, RestApi) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.registration = {
    	email: '',
    	token: ''
    };

	$scope.verifyEmail = function(email) {
		RestApi.anonymousGet({
			controller: 'auth',
			method: 'register?email=' + email
		}).then(function(data) {
			AlertService.add(data.meta, 'auth/register')
			$scope.registration.email = '';
		});
	};

	if(typeof $location.search().token != 'undefined') {
		$scope.registration.token = $location.search().token;
	}

	$scope.register = function() {
		RestApi.anonymousGet({
			controller: 'auth',
			method: 'register',
			data: $scope.registration
		}).then(function(data) {			
			$location.path("/home");
			$timeout(function() {
				AlertService.add(data.meta, 'auth/register');
			}, 500);
		});
	};

});
