vireo.config(function ($locationProvider, $routeProvider) {

    $locationProvider.html5Mode(true);

    $routeProvider.when('/myprofile', {
        templateUrl: 'views/myprofile.html',
        controller: 'SettingsController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/submission/history', {
        templateUrl: 'views/submission/submissionHistory.html',
        controller: 'SubmissionHistoryController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/submission/new', {
        templateUrl: 'views/submission/submissionNew.html',
        controller: 'NewSubmissionController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/submission/complete', {
        templateUrl: 'views/submission/submissionComplete.html',
        controller: 'CompleteSubmissionController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/submission/corrected', {
        templateUrl: 'views/submission/submissionCorrected.html',
        controller: 'CompleteSubmissionController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/submission/:submissionId', {
        templateUrl: 'views/submission/submission.html',
        controller: 'StudentSubmissionController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/submission/:submissionId/step/:stepNum', {
        templateUrl: 'views/submission/submission.html',
        controller: 'StudentSubmissionController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"],
        reloadOnUrl: false
    }).
    when('/submission/:submissionId/view', {
        templateUrl: 'views/submission/submissionView.html',
        controller: 'SubmissionViewController',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER", "ROLE_STUDENT"]
    }).
    when('/review/:advisorAccessHash', {
        templateUrl: 'views/submission/advisorReview.html',
        controller: 'AdvisorSubmissionReviewController'
    }).
    when('/users', {
        templateUrl: 'views/users.html',
        access: ["ROLE_ADMIN", "ROLE_MANAGER"]
    }).
    when('/register', {
        templateUrl: 'views/register.html'
    }).
    when('/admin', {
        redirectTo: '/admin/list',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER"]
    }).
    when('/admin/list', {
        templateUrl: 'views/admin/admin.html',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER"]
    }).
    when('/admin/view/:id/:tab', {
        templateUrl: 'views/admin/admin.html',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER"],
        controller: 'AdminSubmissionViewController',
        reloadOnUrl: false
    }).
    when('/admin/viewError', {
        templateUrl: 'views/admin/admin.html',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER"]
    }).
    when('/admin/log', {
        templateUrl: 'views/admin/admin.html',
        access: ["ROLE_ADMIN", "ROLE_MANAGER", "ROLE_REVIEWER"]
    }).
    when('/admin/settings', {
        redirectTo: '/admin/settings/application',
        access: ["ROLE_ADMIN", "ROLE_MANAGER"]
    }).
    when('/admin/settings/:tab', {
        templateUrl: 'views/admin/admin.html',
        access: ["ROLE_ADMIN", "ROLE_MANAGER"],
        controller: 'SettingsController',
        reloadOnSearch: false
    }).
    when('/', {
        templateUrl: 'views/home.html'
    }).

    // Error Routes
    when('/error/401', {
        templateUrl: 'views/errors/401.html',
        controller: 'ErrorPageController'
    }).
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

});
