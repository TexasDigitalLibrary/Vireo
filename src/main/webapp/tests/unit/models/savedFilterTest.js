describe('model: SavedFilter', function () {
    var rootScope, model, scope, SavedFilter, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _SavedFilter_, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            SavedFilter = _SavedFilter_;
            WsApi = _WsApi_;

            model = SavedFilter();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
