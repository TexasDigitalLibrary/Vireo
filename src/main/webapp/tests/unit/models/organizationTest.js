describe('model: Organization', function () {
    var rootScope, q, model, scope, RestApi, WsApi;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.emailTemplate');
        module('mock.restApi');
        module('mock.wsApi');

        inject(function ($q, $rootScope, Organization, _RestApi_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;
            scope = $rootScope.$new();

            RestApi = _RestApi_;
            WsApi = _WsApi_;

            model = Organization();
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });

    describe('Are the model methods defined', function () {
        it('addEmailWorkflowRule should be defined', function () {
            expect(model.addEmailWorkflowRule).toBeDefined();
            expect(typeof model.addEmailWorkflowRule).toEqual("function");
        });
        it('changeEmailWorkflowRuleActivation should be defined', function () {
            expect(model.changeEmailWorkflowRuleActivation).toBeDefined();
            expect(typeof model.changeEmailWorkflowRuleActivation).toEqual("function");
        });
        it('editEmailWorkflowRule should be defined', function () {
            expect(model.editEmailWorkflowRule).toBeDefined();
            expect(typeof model.editEmailWorkflowRule).toEqual("function");
        });
        it('removeEmailWorkflowRule should be defined', function () {
            expect(model.removeEmailWorkflowRule).toBeDefined();
            expect(typeof model.removeEmailWorkflowRule).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
        it('addEmailWorkflowRule should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.addEmailWorkflowRule(1, "test", 1);
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('changeEmailWorkflowRuleActivation should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.changeEmailWorkflowRuleActivation({id: 1});
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('editEmailWorkflowRule should call WsApi', function () {
            var rule = {
                id: 1,
                emailTemplate: new mockEmailTemplate(q),
                recipient: "test"
            };

            spyOn(WsApi, 'fetch');

            model.editEmailWorkflowRule(rule);
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('removeEmailWorkflowRule should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.removeEmailWorkflowRule({id: 1});
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
    });
});
