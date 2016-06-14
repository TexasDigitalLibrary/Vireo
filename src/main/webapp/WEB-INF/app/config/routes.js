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
			access: ["ADMINISTRATOR"]
		}).
		when('/submit', {
			templateUrl: 'views/student/submit.html',
			access: ["STUDENT"]
		}).
		when('/admin/list', {
			templateUrl: 'views/admin/list.html',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/view', {
			templateUrl: 'views/admin/view.html',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/log', {
			templateUrl: 'views/admin/log.html',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/settings', {
			redirectTo: '/admin/settings/application',
			access: ["ADMINISTRATOR"]
		}).
		when('/admin/settings/:tab', {
			templateUrl: 'views/admin/settings/settings.html',
			access: ["ADMINISTRATOR"],
			controller: 'SettingsController',
			reloadOnSearch: false
		}).
		when('/home', {
			templateUrl: 'views/home.html'
		}).
		when('/', {
			redirectTo: '/home'
		}).

		// Error Routes
		when('/error/403', {
			templateUrl: 'views/errors/403.html',
			controller: 'ErrorPageController'
		}).
		when('/error/404', {
			templateUrl: 'views/errors/404.html',
			controller: 'ErrorPageController'

		}).
		when('/error/500', {
			templateUrl: 'views/errors/500.html',
			controller: 'ErrorPageController'
		}).
		otherwise({
			redirectTo: '/error/404'
		});

}]);