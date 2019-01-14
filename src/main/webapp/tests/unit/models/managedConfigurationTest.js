describe('model: ManagedConfiguration', function () {
    var rootScope, model, sanitize, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, $sanitize, ManagedConfiguration, _WsApi_) {
            rootScope = $rootScope;
            sanitize = $sanitize;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = ManagedConfiguration();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
