describe('model: ManagedConfiguration', function () {
    var rootScope, model, sanitize, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, $sanitize, _WsApi_, _ManagedConfiguration_) {
            rootScope = $rootScope;
            sanitize = $sanitize;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = _ManagedConfiguration_();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
