describe('model: NamedSearchFilterGroup', function () {
    var model, rootScope, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($rootScope, _WsApi_) {
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (NamedSearchFilterGroup) {
            scope = rootScope.$new();

            model = angular.extend(new NamedSearchFilterGroup(), dataNamedSearchFilterGroup1);
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
        it('addFilter should be defined', function () {
            expect(model.addFilter).toBeDefined();
            expect(typeof model.addFilter).toEqual("function");
        });
        it('clearFilters should be defined', function () {
            expect(model.clearFilters).toBeDefined();
            expect(typeof model.clearFilters).toEqual("function");
        });
        it('removeFilter should be defined', function () {
            expect(model.removeFilter).toBeDefined();
            expect(typeof model.removeFilter).toEqual("function");
        });
        it('set should be defined', function () {
            expect(model.set).toBeDefined();
            expect(typeof model.set).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
        it('addFilter should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.addFilter();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('clearFilters should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.clearFilters();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('removeFilter should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.removeFilter();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('set should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.set();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
    });
});
