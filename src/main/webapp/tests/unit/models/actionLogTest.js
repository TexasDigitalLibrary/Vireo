describe('model: ActionLog', function () {
    var rootScope, scope, WsApi, ActionLog;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _ActionLog_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            ActionLog = _ActionLog_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(ActionLog).toBeDefined();
        });
    });
});
