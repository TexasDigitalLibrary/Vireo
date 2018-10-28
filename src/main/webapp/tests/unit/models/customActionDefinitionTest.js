describe('model: CustomActionDefinition', function () {
    var rootScope, scope, WsApi, CustomActionDefinition;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _CustomActionDefinition_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            CustomActionDefinition = _CustomActionDefinition_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(CustomActionDefinition).toBeDefined();
        });
    });
});
