describe('model: Degree', function () {
    var rootScope, scope, WsApi, Degree;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _Degree_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            Degree = _Degree_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Degree).toBeDefined();
        });
    });
});
