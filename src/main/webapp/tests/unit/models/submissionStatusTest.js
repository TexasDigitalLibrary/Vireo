describe('model: SubmissionStatus', function () {
    var rootScope, scope, WsApi, SubmissionStatus;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _SubmissionStatus_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            SubmissionStatus = _SubmissionStatus_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(SubmissionStatus).toBeDefined();
        });
    });
});
