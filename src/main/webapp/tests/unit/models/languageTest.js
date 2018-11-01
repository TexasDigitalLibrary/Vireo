describe('model: Language', function () {
    var rootScope, scope, WsApi, Language;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _Language_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            Language = _Language_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Language).toBeDefined();
        });
    });
});
