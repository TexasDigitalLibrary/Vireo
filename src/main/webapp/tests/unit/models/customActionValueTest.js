describe('model: CustomActionValue', function () {
    var rootScope, scope, WsApi, CustomActionValue;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _CustomActionValue_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            CustomActionValue = _CustomActionValue_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(CustomActionValue).toBeDefined();
        });
    });
});
