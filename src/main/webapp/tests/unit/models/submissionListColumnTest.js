describe('model: SubmissionListColumn', function () {
    var rootScope, scope, WsApi, SubmissionListColumn;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _SubmissionListColumn_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            SubmissionListColumn = _SubmissionListColumn_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(SubmissionListColumn).toBeDefined();
        });
    });
});
