describe('model: Organization', function () {
    var rootScope, q, model, scope, WsApi, RestApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.restApi');
        module('mock.wsApi');

        inject(function ($q, $rootScope, _RestApi_, _WsApi_, _Organization_) {
            q = $q;
            rootScope = $rootScope;
            scope = $rootScope.$new();

            RestApi = _RestApi_;
            WsApi = _WsApi_;

            model = _Organization_();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
