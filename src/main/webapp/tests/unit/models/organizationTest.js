describe('model: Organization', function () {
    var model, rootScope, q, scope, RestApi, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _RestApi_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            RestApi = _RestApi_;
            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (Organization) {
            scope = rootScope.$new();

            model = angular.extend(new Organization(), dataOrganization1);
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.emailTemplate');
        module('mock.restApi');
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
        it('getWorkflowEmailContacts should be defined', function () {
            expect(model.getWorkflowEmailContacts).toBeDefined();
            expect(typeof model.getWorkflowEmailContacts).toEqual("function");
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
        it('getWorkflowEmailContacts should return an array', function () {
            var response;
            var fieldProfile = new mockFieldProfile(q);
            var workflowStep = new mockWorkflowStep(q);

            fieldProfile.inputType.id = 2;
            fieldProfile.inputType.name = "INPUT_CONTACT";

            model.aggregateWorkflowSteps = [ workflowStep ];

            response = model.getWorkflowEmailContacts();

            expect(typeof response).toBe("object");
            expect(typeof response.length).toBe("number");

            workflowStep.mock(dataWorkflowStep2);

            response = model.getWorkflowEmailContacts();

            expect(typeof response).toBe("object");
            expect(typeof response.length).toBe("number");
        });
        it('removeEmailWorkflowRule should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            model.removeEmailWorkflowRule({id: 1});
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
    });
});
