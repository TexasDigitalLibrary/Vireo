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

                // Leave the user signed out; direct them to sign in with the new password.
                // LoginController.login() checks post_authorize_url after a successful local
                // sign-in and navigates there; without it, it just reloads the current page
                // (/login), which then shows "You are already logged in" with nowhere to go.
                StorageService.set('post_authorize_url', '/myprofile');

                $location.path('/login');
            }
        });
    };

});
