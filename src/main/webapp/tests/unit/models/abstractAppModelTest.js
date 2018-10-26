describe('model: AbstractAppModel', function () {
    var rootScope, scope, WsApi, AbstractAppModel;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _AbstractAppModel_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            AbstractAppModel = _AbstractAppModel_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(AbstractAppModel).toBeDefined();
        });
    });
});
