describe('model: UserSettings', function () {
    var rootScope, model, scope, UserService, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.userService');
        module('mock.wsApi');

        inject(function ($rootScope, UserSettings, _UserService_, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            UserService = _UserService_;
            WsApi = _WsApi_;

            model = angular.extend(new UserSettings(), dataUserSettings1);
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
