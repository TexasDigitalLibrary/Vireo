describe('model: GraduationMonth', function () {
    var rootScope, scope, WsApi, GraduationMonth;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _GraduationMonth_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            GraduationMonth = _GraduationMonth_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(GraduationMonth).toBeDefined();
        });
    });
});
