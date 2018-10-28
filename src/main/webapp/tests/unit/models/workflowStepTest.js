describe('model: WorkflowStep', function () {
    var rootScope, scope, WsApi, WorkflowStep;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _WorkflowStep_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            WorkflowStep = _WorkflowStep_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(WorkflowStep).toBeDefined();
        });
    });
});
