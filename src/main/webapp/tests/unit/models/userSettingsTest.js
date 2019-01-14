describe('model: UserSettings', function () {
    var rootScope, model, scope, UserService, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.userService');
        module('mock.wsApi');

        inject(function ($rootScope, _UserService_, _WsApi_, _UserSettings_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            UserService = _UserService_;
            WsApi = _WsApi_;

            model = _UserSettings_();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
