describe("controller: NoteManagementController", function () {

    var controller, q, scope, selectedOrg, selectedOrgId, OrganizationRepo, WorkflowStepRepo, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _NoteRepo_, _OrganizationRepo_, _WorkflowStepRepo_, _WsApi_) {
            q = $q;

            OrganizationRepo = _OrganizationRepo_;
            WorkflowStepRepo = _WorkflowStepRepo_;
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $injector, $rootScope, $timeout, SubmissionStates, _AccordionService_, _DragAndDropListenerFactory_, _ManagedConfigurationRepo_, _ModalService_, _Note_, _NoteRepo_, _RestApi_, _SidebarService_, _StorageService_, _StudentSubmissionRepo_, _UserService_, _UserSettings_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            // The scope.step must be assigned based on the current noteManagementController design.
            scope.step = mockWorkflowStep(q);

            // The controller being tested is included inside of a OrganizationSettingsController scope.
            // This results in having additional methods on the scope that the controller being tested requires.
            $controller("AbstractController", {
                $injector: $injector,
                $scope: scope,
                $timeout: $timeout,
                UserService: _UserService_,
                UserSettings: _UserSettings_,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                SubmissionStates: SubmissionStates
            });

            $controller("OrganizationSettingsController", {
                $scope: scope,
                $window: mockWindow(),
                AccordionService: _AccordionService_,
                ModalService: _ModalService_,
                OrganizationRepo: OrganizationRepo,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                WsApi: WsApi
            });

            controller = $controller("NoteManagementController", {
                $scope: scope,
                $window: mockWindow(),
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                Note: _Note_,
                NoteRepo: _NoteRepo_,
                OrganizationRepo: OrganizationRepo,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WorkflowStepRepo: WorkflowStepRepo,
                WsApi: WsApi
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.dragAndDropListenerFactory");
        module("mock.modalService");
        module("mock.note", function($provide) {
            $provide.value("Note", mockParameterModel(q, mockNote));
        });
        module("mock.noteRepo");
        module("mock.organization");
        module("mock.organizationRepo");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.workflowStep");
        module("mock.workflowStepRepo");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("createNote should be defined", function () {
            expect(scope.createNote).toBeDefined();
            expect(typeof scope.createNote).toEqual("function");
        });
        it("editNote should be defined", function () {
            expect(scope.editNote).toBeDefined();
            expect(typeof scope.editNote).toEqual("function");
        });
        it("isEditable should be defined", function () {
            expect(scope.isEditable).toBeDefined();
            expect(typeof scope.isEditable).toEqual("function");
        });
        it("openNewModal should be defined", function () {
            expect(scope.openNewModal).toBeDefined();
            expect(typeof scope.openNewModal).toEqual("function");
        });
        it("removeNote should be defined", function () {
            expect(scope.removeNote).toBeDefined();
            expect(typeof scope.removeNote).toEqual("function");
        });
        it("reorderNotes should be defined", function () {
            expect(scope.reorderNotes).toBeDefined();
            expect(typeof scope.reorderNotes).toEqual("function");
        });
        it("resetNotes should be defined", function () {
            expect(scope.resetNotes).toBeDefined();
            expect(typeof scope.resetNotes).toEqual("function");
        });
        it("selectNote should be defined", function () {
            expect(scope.selectNote).toBeDefined();
            expect(typeof scope.selectNote).toEqual("function");
        });
        it("updateNote should be defined", function () {
            expect(scope.updateNote).toBeDefined();
            expect(typeof scope.updateNote).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("createNote should create a new note", function () {
            scope.modalData = new mockNote(q);

            spyOn(WorkflowStepRepo, "addNote");

            scope.createNote();

            expect(WorkflowStepRepo.addNote).toHaveBeenCalled();
        });
        it("isEditable should return a boolean", function () {
            var response;
            var note = mockNote(q);
            note.overrideable = true;

            response = scope.isEditable(note);
            expect(response).toBe(true);

            note.overrideable = false;
            note.originatingWorkflowStep = -1;
            scope.step = {
                id: 0
            };

            response = scope.isEditable(note);
            expect(response).toBe(false);

            note.originatingWorkflowStep = 0;
            scope.selectedOrganization = {
                originalWorkflowSteps: [0]
            };

            note.originatingWorkflowStep = scope.step.id;

            response = scope.isEditable(note);
            expect(response).toBe(true);
        });
        it("openNewModal should open a modal", function () {
            spyOn(scope, "openModal");

            scope.openNewModal();

            expect(scope.openModal).toHaveBeenCalled();
        });
        it("editNote should open a modal", function () {
            spyOn(scope, "selectNote");
            spyOn(scope, "openModal");

            scope.editNote(1);

            expect(scope.selectNote).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it("removeNote should delete a note", function () {
            spyOn(WorkflowStepRepo, "removeNote");

            scope.removeNote();

            expect(WorkflowStepRepo.removeNote).toHaveBeenCalled();
        });
        it("reorderNotes should reorder a note", function () {
            spyOn(WorkflowStepRepo, "reorderNotes");

            scope.reorderNotes("a", "b");

            expect(WorkflowStepRepo.reorderNotes).toHaveBeenCalled();
        });
        it("resetNotes should reset the note", function () {
            var note = new mockNote(q);
            scope.forms = [];
            scope.modalData = note;

            spyOn(scope.workflowStepRepo, "clearValidationResults");
            spyOn(scope.noteRepo, "clearValidationResults");
            spyOn(note, "refresh");
            spyOn(scope, "closeModal");

            scope.resetNotes();

            expect(scope.workflowStepRepo.clearValidationResults).toHaveBeenCalled();
            expect(scope.noteRepo.clearValidationResults).toHaveBeenCalled();
            expect(note.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();

            scope.step.aggregateNotes = [ note ];
            scope.forms.myForm = mockForms();
            scope.resetNotes();

            scope.step = undefined;
            scope.forms.myForm.$pristine = false;
            scope.resetNotes();
        });
        it("selectNote should select a note", function () {
            scope.modalData = null;
            scope.step.aggregateNotes = [
                new mockNote(q),
                new mockNote(q)
            ];
            scope.step.aggregateNotes[1].mock(dataNote2);

            scope.selectNote(1);
            expect(scope.modalData.id).toBe(dataNote2.id);
        });
        it("updateNote should should save a note", function () {
            spyOn(WorkflowStepRepo, "updateNote");

            scope.updateNote();

            expect(WorkflowStepRepo.updateNote).toHaveBeenCalled();
        });
    });

});
