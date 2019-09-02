describe('model: DepositLocation', function () {
    var model, rootScope, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($rootScope, _WsApi_) {
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (DepositLocation) {
            scope = rootScope.$new();

            model = angular.extend(new DepositLocation(), dataDepositLocation1);
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
        it('testConnection should be defined', function () {
            expect(model.testConnection).toBeDefined();
            expect(typeof model.testConnection).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
        it('testConnection should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.testConnection();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
    });
});
