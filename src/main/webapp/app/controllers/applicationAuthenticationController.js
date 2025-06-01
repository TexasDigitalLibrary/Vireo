vireo.controller('ApplicationAuthenticationController', function ($controller, $scope, $window, StorageService, UserService) {

    angular.extend(this, $controller('AuthenticationController', {$scope: $scope}), $controller('AbstractController', {$scope: $scope}));

    $scope.registerEnabled = appConfig.localAuthentication === true;

    // For login modal state management
    $scope.showVireoLogin = false;

    // Initialize account object for the login form
    $scope.account = {};

    // SSO login function (Johns Hopkins Enterprise Authentication)
    $scope.login = function () {
        StorageService.delete('token');
        StorageService.delete('role');

        var authorizeUrl = StorageService.get('post_authorize_url');
        var path = authorizeUrl ? authorizeUrl : location.pathname;
        var mock = appConfig.mockRole ? '&mock=' + appConfig.mockRole : '';

        $window.open(appConfig.authService + '/token?referrer=' + encodeURIComponent(location.origin + path + location.search) + mock, '_self');
    };

    // Login function for Vireo account
    $scope.loginVireoAccount = function () {
        $scope.user.authenticate($scope.account).then(function (data) {
            $scope.reset();
            UserService.fetchUser().then(function() {
                var authorizeUrl = StorageService.get("post_authorize_url");
                if (authorizeUrl) {
                    StorageService.delete("post_authorize_url");
                    $window.location.assign(authorizeUrl);
                } else {
                    $window.location.reload();
                }
            });
        });
    };
});

