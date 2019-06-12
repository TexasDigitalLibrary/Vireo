describe('model: SavedFilter', function () {
    var model, rootScope, scope, SavedFilter, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($rootScope, _SavedFilter_, _WsApi_) {
            rootScope = $rootScope;

            SavedFilter = _SavedFilter_;
            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (_WsApi_) {
            scope = rootScope.$new();

            model = angular.extend(new SavedFilter(), dataSavedFilter1);
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        initializeVariables();
        initializeModel();
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
