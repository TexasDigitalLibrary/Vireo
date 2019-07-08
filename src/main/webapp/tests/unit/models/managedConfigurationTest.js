describe('model: ManagedConfiguration', function () {
    var model, rootScope, sanitize, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($rootScope, $sanitize, _WsApi_) {
            rootScope = $rootScope;
            sanitize = $sanitize;

            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (ManagedConfiguration) {
            scope = rootScope.$new();

            model = angular.extend(new ManagedConfiguration(), dataManagedConfiguration1);
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

    describe('Are the model methods defined', function () {
        it('reset should be defined', function () {
            expect(model.reset).toBeDefined();
            expect(typeof model.reset).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
        it('reset should call WsApi', function () {
            spyOn(WsApi, 'fetch').and.callThrough();

            model.reset();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
    });
});
