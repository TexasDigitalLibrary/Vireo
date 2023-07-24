describe("service: organizationRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, RestApi, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _RestApi_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedUser = mockParameterModel(q, mockUser);

            RestApi = _RestApi_;
            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, OrganizationRepo) {
            scope = rootScope.$new();

            repo = $injector.get('OrganizationRepo');
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.organization");
        module("mock.restApi");
        module("mock.user", function($provide) {
            var User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
        module("mock.userService");
        module("mock.workflowStep");
        module("mock.wsApi");

        initializeVariables();
        initializeRepo();
    });

    describe("Is the repo defined", function () {
        it("should be defined", function () {
            expect(repo).toBeDefined();
        });
    });

    describe("Are the repo methods defined", function () {
        it("addWorkflowStep should be defined", function () {
            expect(repo.addWorkflowStep).toBeDefined();
            expect(typeof repo.addWorkflowStep).toEqual("function");
        });
        it("create should be defined", function () {
            expect(repo.create).toBeDefined();
            expect(typeof repo.create).toEqual("function");
        });
        it("countSubmissions should be defined", function () {
            expect(repo.countSubmissions).toBeDefined();
            expect(typeof repo.countSubmissions).toEqual("function");
        });
        it("deleteById should be defined", function () {
            expect(repo.deleteById).toBeDefined();
            expect(typeof repo.deleteById).toEqual("function");
        });
        it("deleteWorkflowStep should be defined", function () {
            expect(repo.deleteWorkflowStep).toBeDefined();
            expect(typeof repo.deleteWorkflowStep).toEqual("function");
        });
        it("getNewOrganization should be defined", function () {
            expect(repo.getNewOrganization).toBeDefined();
            expect(typeof repo.getNewOrganization).toEqual("function");
        });
        it("getSelectedOrganization should be defined", function () {
            expect(repo.getSelectedOrganization).toBeDefined();
            expect(typeof repo.getSelectedOrganization).toEqual("function");
        });
        it("ready should be defined", function () {
            expect(repo.ready).toBeDefined();
            expect(typeof repo.ready).toEqual("function");
        });
        it("reorderWorkflowSteps should be defined", function () {
            expect(repo.reorderWorkflowSteps).toBeDefined();
            expect(typeof repo.reorderWorkflowSteps).toEqual("function");
        });
        it("resetNewOrganization should be defined", function () {
            expect(repo.resetNewOrganization).toBeDefined();
            expect(typeof repo.resetNewOrganization).toEqual("function");
        });
        it("restoreDefaults should be defined", function () {
            expect(repo.restoreDefaults).toBeDefined();
            expect(typeof repo.restoreDefaults).toEqual("function");
        });
        it("setSelectedOrganization should be defined", function () {
            expect(repo.setSelectedOrganization).toBeDefined();
            expect(typeof repo.setSelectedOrganization).toEqual("function");
        });
        it("updateWorkflowStep should be defined", function () {
            expect(repo.updateWorkflowStep).toBeDefined();
            expect(typeof repo.updateWorkflowStep).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("addWorkflowStep should add a step", function () {
            var workflowStep = new mockWorkflowStep(q);

            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.findById = function() {
                return new mockOrganization(q);
            };

            repo.addWorkflowStep(workflowStep);
            scope.$digest();

            // TODO
        });
        it("create should create an organization", function () {
            var organization1 = new mockOrganization(q);
            var organization2 = new mockOrganization(q);

            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.create(organization1, organization2);
            scope.$digest();

            // TODO
        });
        it("countSubmissions should return a number", function () {
            WsApi.fetch = function() {
                // TODO: having to send an object structure of response.body.payload.payload.Long seems wrong, review implementation for bug.
                var payload = {
                    payload: {
                        Long: 0
                    }
                };
                return payloadPromise(q.defer(), payload);
            };

            repo.countSubmissions(1);
            scope.$digest();

            // TODO
        });
        it("deleteById should delete an organization", function () {
            var organization = new mockOrganization(q);

            WsApi.mockFetchResponse({ type: "payload" });

            repo.deleteById(organization.id).then(function (res) {
                var resObj = angular.fromJson(res.body);

                expect(typeof resObj).toEqual("object");
                expect(typeof resObj.meta).toEqual("object");
                expect(resObj.meta.status).toEqual("SUCCESS");
            });

            scope.$digest();
        });
        it("deleteWorkflowStep should delete a step", function () {
            var workflowStep = new mockWorkflowStep(q);

            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            RestApi.post = function () {
                var payload = {
                    meta: {
                        status: "SUCCESS",
                    },
                    payload: {},
                    status: 200
                };

                return valuePromise(q.defer(), payload);
            };

            repo.findById = function() {
                return new mockOrganization(q);
            };

            repo.deleteWorkflowStep(workflowStep);
            scope.$digest();

            // TODO
        });
        it("getNewOrganization should return an organization", function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.getNewOrganization([]);
            scope.$digest();

            // TODO
        });
        it("getSelectedOrganization should return an organization", function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.getSelectedOrganization([]);
            scope.$digest();

            // TODO
        });
        it("ready should perform additional actions", function () {
            WsApi.listen = function() {
                // FIXME: this is not called because the mocked WsApi is not atually used.
                return payloadPromise(q.defer());
            };

            repo.ready();
            scope.$digest();

            // TODO
        });
        it("reorderWorkflowSteps should reorder the steps", function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.findById = function() {
                return new mockOrganization(q);
            };

            repo.reorderWorkflowSteps("up", 1);
            scope.$digest();

            // TODO
        });
        it("resetNewOrganization should reset the organization", function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.resetNewOrganization([]);
            scope.$digest();

            // TODO
        });
        it("restoreDefaults should restore defaults", function () {
            var organization = new mockOrganization(q);

            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            RestApi.post = function () {
                // TODO: having to use a different format that other RestApi.post() mocks, review implementations for bugs.
                var payload = {
                    meta: {
                        status: "SUCCESS",
                    },
                    payload: {},
                    status: 200
                };

                return valuePromise(q.defer(), payload);
            };

            repo.restoreDefaults(organization);
            scope.$digest();

            // TODO
        });
        it("setSelectedOrganization should select an organization", function () {
            var organization = new mockOrganization(q);

            WsApi.fetch = function() {
                // TODO: having to send an object structure of response.body.payload.payload.Long seems wrong, review implementation for bug.
                var payload = {
                    payload: {
                        Long: 0
                    }
                };
                return payloadPromise(q.defer(), payload);
            };

            repo.findById = function() {
                return organization;
            };

            repo.setSelectedOrganization(organization);
            scope.$digest();

            // TODO
        });
        it("updateWorkflowStep should update the step", function () {
            var workflowStep = new mockWorkflowStep(q);

            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            RestApi.post = function () {
                // TODO: having to use a different format that other RestApi.post() mocks, review implementations for bugs.
                var payload = {
                    meta: {
                        status: "SUCCESS",
                    },
                    payload: {},
                    status: 200
                };

                return valuePromise(q.defer(), payload);
            };

            repo.findById = function() {
                return new mockOrganization(q);
            };

            repo.updateWorkflowStep(workflowStep);
            scope.$digest();

            // TODO
        });
    });
});
