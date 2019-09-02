describe('model: UserSettings', function () {
    var model, rootScope, scope, UserService, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($rootScope, _UserService_, _WsApi_) {
            rootScope = $rootScope;

            UserService = _UserService_;
            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (UserSettings) {
            scope = rootScope.$new();

            model = angular.extend(new UserSettings(), dataUserSettings1);
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.userService');
        module('mock.wsApi');

        initializeVariables();
        initializeModel();
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
