describe('model: ControlledVocabulary', function () {
    var rootScope, scope, WsApi, ControlledVocabulary;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _ControlledVocabulary_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            ControlledVocabulary = _ControlledVocabulary_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(ControlledVocabulary).toBeDefined();
        });
    });
});
