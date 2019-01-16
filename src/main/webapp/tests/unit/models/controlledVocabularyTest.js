describe('model: ControlledVocabulary', function () {
    var model, rootScope, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, ControlledVocabulary, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = angular.extend(new ControlledVocabulary(), dataControlledVocabulary1);
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
