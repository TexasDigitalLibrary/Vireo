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

    // For this REST-backed endpoint WsApi.fetch always resolves (even for an INVALID response) and
    // only rejects on a transport error. The ApiResponse arrives as a JSON string in response.body;
    // normalize it so success/failure is driven by meta.status, not by resolve vs reject.
    var parseResponse = function (response) {
        if (response && typeof response.body === 'string') {
            try {
                return angular.fromJson(response.body);
            } catch (e) {
                return response;
            }
        }

        if (response && response.payload) {
            return response.payload;
        }

        return response;
    };

    var messageFrom = function (apiRes, fallback) {
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
            var apiRes = parseResponse(response);
            var succeeded = apiRes && apiRes.meta && apiRes.meta.status === 'SUCCESS';

            if (succeeded) {
                $scope.passwordChangeFeedback = { type: 'success', message: messageFrom(apiRes, GENERIC_SUCCESS) };
            } else {
                $scope.passwordChangeFeedback = { type: 'danger', message: messageFrom(apiRes, GENERIC_FAILURE) };
            }

            clear();
        }, function (response) {
            $scope.passwordChangeFeedback = { type: 'danger', message: messageFrom(parseResponse(response), GENERIC_FAILURE) };
            clear();
        });
    };

});
