describe('model: DegreeLevel', function () {
    var rootScope, scope, WsApi, DegreeLevel;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _DegreeLevel_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            DegreeLevel = _DegreeLevel_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(DegreeLevel).toBeDefined();
        });
    });
});
