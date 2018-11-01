describe('model: FieldValue', function () {
    var rootScope, scope, WsApi, FieldValue;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _FieldValue_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            FieldValue = _FieldValue_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(FieldValue).toBeDefined();
        });
    });
});
