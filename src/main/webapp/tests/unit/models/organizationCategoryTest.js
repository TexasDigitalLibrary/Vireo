describe('model: OrganizationCategory', function () {
    var rootScope, scope, WsApi, OrganizationCategory;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _OrganizationCategory_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            OrganizationCategory = _OrganizationCategory_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(OrganizationCategory).toBeDefined();
        });
    });
});
