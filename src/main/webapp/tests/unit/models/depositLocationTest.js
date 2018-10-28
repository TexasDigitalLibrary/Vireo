describe('model: DepositLocation', function () {
    var rootScope, scope, WsApi, DepositLocation;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _DepositLocation_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            DepositLocation = _DepositLocation_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(DepositLocation).toBeDefined();
        });
    });
});
