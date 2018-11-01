describe('model: FieldPredicate', function () {
    var rootScope, scope, WsApi, FieldPredicate;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _FieldPredicate_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            FieldPredicate = _FieldPredicate_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(FieldPredicate).toBeDefined();
        });
    });
});
