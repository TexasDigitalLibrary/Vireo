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
		when('/admin', {
			redirectTo: '/admin/list',
		}).
		when('/admin/list', {
			templateUrl: 'views/admin/list.html',
		}).
		when('/admin/view', {
			templateUrl: 'views/admin/view.html',
		}).
		when('/admin/log', {
			templateUrl: 'views/admin/log.html',
		}).
		when('/admin/settings', {
			redirectTo: '/admin/settings/user',
		}).
		when('/admin/settings/:tab', {
			templateUrl: 'views/admin/settings/settings.html',
			controller: 'SettingsController'
		}).
		otherwise({
			redirectTo: '/home',
			templateUrl: 'views/home.html'
		});
}]);