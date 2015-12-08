vireo.config(['$routeProvider', '$locationProvider', '$anchorScrollProvider', function($routeProvider, $locationProvider) {
	
	$locationProvider.html5Mode(true);
	
	$routeProvider.
		when('/myprofile', {
			templateUrl: 'views/myprofile.html',
			controller: 'SettingsController'
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
			redirectTo: '/admin/settings/application',
		}).
		when('/admin/settings/:tab', {
			templateUrl: 'views/admin/settings/settings.html',
			controller: 'SettingsController',
			reloadOnSearch: false
		}).
		otherwise({
			redirectTo: '/home',
			templateUrl: 'views/home.html'
		});
}]);