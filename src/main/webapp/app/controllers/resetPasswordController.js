vireo.controller('ResetPasswordController', function ($controller, $location, $scope, PasswordResetService, StorageService) {

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
                // in with the new password, rather than back on this public page.
                StorageService.set('post_authorize_url', '/myprofile');

                $location.path('/login');
            }
        });
    };

});
