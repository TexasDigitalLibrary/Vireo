describe('model: InputType', function () {
    var rootScope, scope, WsApi, InputType;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _InputType_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            InputType = _InputType_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(InputType).toBeDefined();
        });
    });
});
