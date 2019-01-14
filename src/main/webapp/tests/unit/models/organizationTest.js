describe('model: Organization', function () {
    var rootScope, q, model, scope, RestApi, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.restApi');
        module('mock.wsApi');

        inject(function ($q, $rootScope, Organization, _RestApi_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;
            scope = $rootScope.$new();

            RestApi = _RestApi_;
            WsApi = _WsApi_;

            model = Organization();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
