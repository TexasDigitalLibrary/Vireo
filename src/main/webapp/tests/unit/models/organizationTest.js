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
        it('getWorkflowEmailContacts should be defined', function () {
            expect(model.getWorkflowEmailContacts).toBeDefined();
            expect(typeof model.getWorkflowEmailContacts).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
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
    });
});
