vireo.directive("loginButton", function ($window, StorageService) {
    return {
        templateUrl: 'views/directives/loginButton.html',
        replace: true,
        transclude: true,
        restrict: 'A',
        link: function ($scope, elem, attr) {
            $scope.loginButton = function () {
                var simple = appConfig.localAuthentication !== true;

                if (appConfig.localAuthentication === 'alternate') {
                    simple = angular.isDefined(attr) && !angular.isDefined(attr.always);
                }

                if (simple) {
                    // This is a simplified version of the login() from the LoginController in weaver-ui-core.
                    StorageService.delete('token');
                    StorageService.delete('role');

                    var authorizeUrl = StorageService.get('post_authorize_url');
                    var path = authorizeUrl ? authorizeUrl : location.pathname;
                    var mock = appConfig.mockRole ? '&mock=' + appConfig.mockRole : '';

                    $window.open(appConfig.authService + '/token?referrer=' + encodeURIComponent(location.origin + path + location.search) + mock, '_self');
                } else {
                    $scope.openModal('#loginModal');
                }
            };
        }
    };
});
