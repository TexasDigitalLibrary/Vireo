vireo.controller('ChangePasswordController', function ($controller, $scope, UserRepo) {

    angular.extend(this, $controller('AbstractController', { $scope: $scope }));

    $scope.userRepo = UserRepo;

    $scope.passwordChange = {
        currentPassword: '',
        newPassword: '',
        confirm: ''
    };

    // Feedback shown above the Current Password field. 'type' drives the Bootstrap alert class.
    $scope.passwordChangeFeedback = null;

    var GENERIC_SUCCESS = 'Your password has been changed.';
    var GENERIC_FAILURE = 'Your password could not be changed.';

    // WsApi resolves with the response on success and rejects with it on failure; in either case
    // the ApiResponse may arrive as a JSON string in response.body or already parsed on
    // response.payload. Pull the server message defensively, falling back to the generic text.
    var messageFrom = function (response, fallback) {
        var apiRes = response;

        if (response && typeof response.body === 'string') {
            try {
                apiRes = angular.fromJson(response.body);
            } catch (e) {
                apiRes = response;
            }
        } else if (response && response.payload) {
            apiRes = response.payload;
        }

        var message = apiRes && apiRes.meta ? apiRes.meta.message : undefined;

        return typeof message === 'string' && message.length > 0 ? message : fallback;
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
        // Clear stale feedback so each submit starts fresh.
        $scope.passwordChangeFeedback = null;

        return UserRepo.changePassword(
            $scope.passwordChange.currentPassword,
            $scope.passwordChange.newPassword,
            $scope.passwordChange.confirm
        ).then(function (response) {
            $scope.passwordChangeFeedback = { type: 'success', message: messageFrom(response, GENERIC_SUCCESS) };
            clear();
        }, function (response) {
            $scope.passwordChangeFeedback = { type: 'danger', message: messageFrom(response, GENERIC_FAILURE) };
            clear();
        });
    };

});
