describe('model: Embargo', function () {
    var rootScope, scope, WsApi, Embargo;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _Embargo_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            Embargo = _Embargo_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Embargo).toBeDefined();
        });
    });
});
