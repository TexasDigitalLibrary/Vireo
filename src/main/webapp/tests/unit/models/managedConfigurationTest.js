describe('model: ManagedConfiguration', function () {
    var rootScope, sanitize, scope, WsApi, ManagedConfiguration;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, $sanitize, _WsApi_, _ManagedConfiguration_) {
            rootScope = $rootScope;
            sanitize = $sanitize;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            ManagedConfiguration = _ManagedConfiguration_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(ManagedConfiguration).toBeDefined();
        });
    });
});
