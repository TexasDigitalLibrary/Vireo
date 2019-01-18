describe('model: ManagedConfiguration', function () {
    var model, rootScope, sanitize, scope, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, $sanitize, ManagedConfiguration, _WsApi_) {
            rootScope = $rootScope;
            sanitize = $sanitize;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            model = angular.extend(new ManagedConfiguration(), dataManagedConfiguration1);
        });
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
