describe('model: DocumentType', function () {
    var rootScope, scope, WsApi, DocumentType;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _DocumentType_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            DocumentType = _DocumentType_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(DocumentType).toBeDefined();
        });
    });
});
