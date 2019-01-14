describe('model: DepositLocation', function () {
    var rootScope, model, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, DepositLocation, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = DepositLocation();
        });
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
