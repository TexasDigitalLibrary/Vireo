describe('model: Submission', function () {
    var q, rootScope, scope, ActionLog, FieldValue, FileService, WsApi, Submission;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.actionLog');
        module('mock.fieldValue');
        module('mock.fileService');
        module('mock.wsApi');

        inject(function ($q, $rootScope, _ActionLog_, _FieldValue_, _FileService_, _WsApi_, _Submission_) {
            q = $q;
            rootScope = $rootScope;
            scope = $rootScope.$new();

            ActionLog = _ActionLog_;
            FieldValue = _FieldValue_;
            FileService = _FileService_;

            WsApi = _WsApi_;

            Submission = _Submission_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Submission).toBeDefined();
        });
    });
});
