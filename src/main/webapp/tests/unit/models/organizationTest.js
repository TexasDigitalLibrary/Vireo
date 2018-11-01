describe('model: Organization', function () {
    var rootScope, q, scope, WsApi, Organization, RestApi;

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

            Organization = _Organization_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Organization).toBeDefined();
        });
    });
});
