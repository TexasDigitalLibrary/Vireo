describe('model: EmailTemplate', function () {
    var rootScope, scope, WsApi, EmailTemplate;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _EmailTemplate_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            EmailTemplate = _EmailTemplate_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(EmailTemplate).toBeDefined();
        });
    });
});
