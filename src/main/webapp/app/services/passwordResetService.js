// Redemption happens before sign-in, and the weaver User model (node_modules/@wvr/core) has no
// Vireo override, so this anonymous call needs its own Vireo-side service.
vireo.service('PasswordResetService', function (RestApi) {

    var PasswordResetService = this;

    PasswordResetService.redeem = function (token, newPassword, confirm) {
        return RestApi.anonymousPost({
            controller: 'password',
            method: 'reset/redeem',
            data: { token: token, newPassword: newPassword, confirm: confirm }
        });
    };

    return PasswordResetService;

});
