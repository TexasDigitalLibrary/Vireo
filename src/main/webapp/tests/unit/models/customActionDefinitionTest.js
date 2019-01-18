describe('model: CustomActionDefinition', function () {
    var model, rootScope, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, CustomActionDefinition, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = angular.extend(new CustomActionDefinition(), dataCustomActionDefinition1);
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
