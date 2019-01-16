describe('model: SavedFilter', function () {
    var model, rootScope, scope, SavedFilter, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _SavedFilter_, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            SavedFilter = _SavedFilter_;
            WsApi = _WsApi_;

            model = angular.extend(new SavedFilter(), dataSavedFilter1);
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
