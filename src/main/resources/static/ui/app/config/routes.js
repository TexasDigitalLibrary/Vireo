seedApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$locationProvider.html5Mode(true);
	$routeProvider.
		when('/myview', {
			templateUrl: 'views/myview.html'
		}).
		when('/users', {
			templateUrl: 'views/users.html'
		}).
		otherwise({
			redirectTo: '/home',
			templateUrl: 'views/home.html'
		});
}]);