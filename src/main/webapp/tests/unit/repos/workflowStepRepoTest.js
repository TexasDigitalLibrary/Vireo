describe("service: workflowStepRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, OrganizationRepo, RestApi, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _OrganizationRepo_, _RestApi_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedUser = mockParameterModel(q, mockUser);

            OrganizationRepo = _OrganizationRepo_;
            RestApi = _RestApi_;
            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, WorkflowStepRepo) {
            scope = rootScope.$new();

            repo = $injector.get('WorkflowStepRepo');
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.fieldProfile");
        module("mock.note");
        module("mock.organization");
        module("mock.organizationRepo");
        module("mock.restApi");
        module("mock.user", function($provide) {
            User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
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
        it("addNote should be defined", function () {
            expect(repo.addNote).toBeDefined();
            expect(typeof repo.addNote).toEqual("function");
        });
        it("addFieldProfile should be defined", function () {
            expect(repo.addFieldProfile).toBeDefined();
            expect(typeof repo.addFieldProfile).toEqual("function");
        });
        it("reorderFieldProfiles should be defined", function () {
            expect(repo.reorderFieldProfiles).toBeDefined();
            expect(typeof repo.reorderFieldProfiles).toEqual("function");
        });
        it("removeFieldProfile should be defined", function () {
            expect(repo.removeFieldProfile).toBeDefined();
            expect(typeof repo.removeFieldProfile).toEqual("function");
        });
        it("removeNote should be defined", function () {
            expect(repo.removeNote).toBeDefined();
            expect(typeof repo.removeNote).toEqual("function");
        });
        it("reorderNotes should be defined", function () {
            expect(repo.reorderNotes).toBeDefined();
            expect(typeof repo.reorderNotes).toEqual("function");
        });
        it("updateNote should be defined", function () {
            expect(repo.updateNote).toBeDefined();
            expect(typeof repo.updateNote).toEqual("function");
        });
        it("updateFieldProfile should be defined", function () {
            expect(repo.updateFieldProfile).toBeDefined();
            expect(typeof repo.updateFieldProfile).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("addNote should add a note", function () {
            var workflowStep = new mockWorkflowStep(q);
            var note = new mockNote(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
            };

            repo.addNote(workflowStep, note);
            scope.$digest();

            // TODO
        });
        it("addFieldProfile should add a field profile", function () {
            var workflowStep = new mockWorkflowStep(q);
            var fieldProfile = new mockFieldProfile(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
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

            repo.addFieldProfile(workflowStep, fieldProfile);
            scope.$digest();

            // TODO
        });
        it("reorderFieldProfiles should reorder field profiles", function () {
            var workflowStep = new mockWorkflowStep(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
            };

            repo.reorderFieldProfiles(workflowStep, "src", "dst");
            scope.$digest();

            // TODO
        });
        it("removeFieldProfile should remove a field profile", function () {
            var workflowStep = new mockWorkflowStep(q);
            var fieldProfile = new mockFieldProfile(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
            };

            repo.removeFieldProfile(workflowStep, fieldProfile);
            scope.$digest();

            // TODO
        });
        it("removeNote should remove a note", function () {
            var workflowStep = new mockWorkflowStep(q);
            var note = new mockNote(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
            };

            repo.removeNote(workflowStep, note);
            scope.$digest();

            // TODO
        });
        it("reorderNotes should reorder notes", function () {
            var workflowStep = new mockWorkflowStep(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
            };

            repo.reorderNotes(workflowStep, "src", "dst");
            scope.$digest();

            // TODO
        });
        it("updateNote should update a note", function () {
            var workflowStep = new mockWorkflowStep(q);
            var note = new mockNote(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
            };

            repo.updateNote(workflowStep, note);
            scope.$digest();

            // TODO
        });
        it("updateFieldProfile should update field profiles", function () {
            var workflowStep = new mockWorkflowStep(q);
            var fieldProfile = new mockFieldProfile(q);
            var organization = new mockOrganization(q);

            OrganizationRepo.getSelectedOrganization = function() {
                return organization;
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

            repo.updateFieldProfile(workflowStep, fieldProfile);
            scope.$digest();

            // TODO
        });
    });
});
