describe('model: SavedFilter', function () {
    var rootScope, scope, WsApi, SavedFilter;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _SavedFilter_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            SavedFilter = _SavedFilter_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(SavedFilter).toBeDefined();
        });
    });
});
