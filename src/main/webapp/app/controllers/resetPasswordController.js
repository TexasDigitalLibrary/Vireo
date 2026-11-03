vireo.controller('ResetPasswordController', function ($controller, $location, $scope, $window, PasswordResetService, StorageService) {

    angular.extend(this, $controller('AbstractController', { $scope: $scope }));

    // Mirrors weaver's RegistrationController reading the token from /register?token=.
    $scope.token = $location.search().token;

    $scope.resetPassword = {
        newPassword: '',
        confirm: ''
    };

    // RestApi.anonymousPost already routes the ApiResponse meta through AlertService, so the
    // expiry-specific and generic failure messages surface without any extra handling here.
    $scope.submitResetPassword = function () {
        return PasswordResetService.redeem($scope.token, $scope.resetPassword.newPassword, $scope.resetPassword.confirm).then(function (data) {
            if (data && data.meta && data.meta.status === 'SUCCESS') {
                $scope.resetPassword.newPassword = '';
                $scope.resetPassword.confirm = '';

                // Set the post-sign-in destination so the user lands on My Profile after signing
                // in with the new password, rather than back on this public page. The value is
                // consumed as a full browser path (context prefix included), so derive the prefix
                // by stripping the Angular route ($location.path()) off the end of the full path
                // ($window.location.pathname); this keeps the redirect correct under a context path.
                var currentPath = $window.location.pathname;
                var contextPath = currentPath.slice(0, currentPath.length - $location.path().length);

                StorageService.set('post_authorize_url', contextPath + '/myprofile');

                $location.path('/login');
            }
        });
    };

});
