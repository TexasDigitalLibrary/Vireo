vireo.controller('ChangePasswordController', function ($controller, $scope, UserRepo) {

    angular.extend(this, $controller('AbstractController', { $scope: $scope }));

    $scope.userRepo = UserRepo;

    $scope.passwordChange = {
        currentPassword: '',
        newPassword: '',
        confirm: ''
    };

    // Matches the server's resolution: trim, case-insensitive compare against 'true'.
    var isEnabled = function (name) {
        var application = $scope.settings && $scope.settings.configurable ? $scope.settings.configurable.application : undefined;
        var setting = application ? application[name] : undefined;
        var value = setting ? setting.value : undefined;
        return typeof value === 'string' && value.trim().toLowerCase() === 'true';
    };

    // allCredentials is a Map<String, String> on the server, so compare against the String.
    var isEligible = function () {
        return !!($scope.user && $scope.user.allCredentials && $scope.user.allCredentials.passwordEligible === 'true');
    };

    $scope.changePasswordAvailable = function () {
        return isEnabled('auth.pass.enabled') && isEnabled('auth.pass.change.enabled') && isEligible();
    };

    var clear = function () {
        $scope.passwordChange.currentPassword = '';
        $scope.passwordChange.newPassword = '';
        $scope.passwordChange.confirm = '';
    };

    $scope.submitPasswordChange = function () {
        return UserRepo.changePassword(
            $scope.passwordChange.currentPassword,
            $scope.passwordChange.newPassword,
            $scope.passwordChange.confirm
        ).then(clear, clear);
    };

});
