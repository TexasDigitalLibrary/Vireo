vireo.controller('LoginController', function ($controller, $location, $scope, RestApi, StorageService, User) {
	
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));
    
    $scope.account = {
    	email: '',
    	password: ''
    };

	$scope.login = function() {
		RestApi.anonymousGet({
			controller: 'user',
			method: 'login',
			data: $scope.account
		}).then(function(data) {

			if(typeof data.payload.JWTtoken == 'undefined') {
				console.log("User does not exist!");
			}
			else {
				StorageService.set("token", data.payload.JWTtoken.tokenAsString);

				delete sessionStorage.role;

				User.login();

				var user = User.get();

				User.ready().then(function() {
					StorageService.set("role", user.role);
				});
			}
		});
	};

});
