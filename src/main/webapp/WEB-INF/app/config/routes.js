vireo.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$locationProvider.html5Mode(true);
	$routeProvider.
		when('/myview', {
			templateUrl: 'views/myview.html'
		}).
		when('/users', {
			templateUrl: 'views/users.html'
		}).
		when('/register', {
			templateUrl: 'views/register.html'
		}).
		when('/settings', {
			redirectTo: '/settings/user',
		}).
		when('/settings/:tab', {
			templateUrl: 'views/admin/settings.html',
			controller: 'SettingsController'
		}).
		otherwise({
			redirectTo: '/home',
			templateUrl: 'views/home.html'
		});
}]);