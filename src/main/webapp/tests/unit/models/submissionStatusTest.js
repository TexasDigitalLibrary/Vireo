describe('model: SubmissionStatus', function () {
    var model, rootScope, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($rootScope, _WsApi_) {
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (AbstractAppModel) {
            scope = rootScope.$new();

            model = angular.extend(new AbstractAppModel());
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, SubmissionStatus, _WsApi_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            initializeVariables();
            model = angular.extend(new SubmissionStatus(), dataSubmissionStatus1);
        });
        initializeVariables();
        initializeModel();
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });
});
