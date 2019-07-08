describe("controller: SubmissionViewController", function () {

    var controller, q, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _WsApi_) {
            q = $q;

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, $routeParams, _CustomActionDefinitionRepo_, _FieldPredicateRepo_, _FileUploadService_, _ModalService_, _StorageService_, _RestApi_, _StudentSubmission_, _StudentSubmissionRepo_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("SubmissionViewController", {
                $q: q,
                $routeParams: $routeParams,
                $scope: scope,
                $window: mockWindow(),
                CustomActionDefinitionRepo: _CustomActionDefinitionRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FileUploadService: _FileUploadService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
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
        module("mock.customActionDefinition");
        module("mock.customActionDefinitionRepo");
        module("mock.fieldPredicate");
        module("mock.fieldPredicateRepo");
        module("mock.fileUploadService");
        module("mock.modalService");
        module("mock.restApi");
        module("mock.storageService");
        module("mock.studentSubmission");
        module("mock.studentSubmissionRepo");
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
        it("addMessage should be defined", function () {
            expect(scope.addMessage).toBeDefined();
            expect(typeof scope.addMessage).toEqual("function");
        });
        it("archiveManuscript should be defined", function () {
            expect(scope.archiveManuscript).toBeDefined();
            expect(typeof scope.archiveManuscript).toEqual("function");
        });
        it("cancelUpload should be defined", function () {
            expect(scope.cancelUpload).toBeDefined();
            expect(typeof scope.cancelUpload).toEqual("function");
        });
        it("getFileType should be defined", function () {
            expect(scope.getFileType).toBeDefined();
            expect(typeof scope.getFileType).toEqual("function");
        });
        it("isPrimaryDocument should be defined", function () {
            expect(scope.isPrimaryDocument).toBeDefined();
            expect(typeof scope.isPrimaryDocument).toEqual("function");
        });
        it("queueRemove should be defined", function () {
            expect(scope.queueRemove).toBeDefined();
            expect(typeof scope.queueRemove).toEqual("function");
        });
        it("removeAdditionalUploads should be defined", function () {
            expect(scope.removeAdditionalUploads).toBeDefined();
            expect(typeof scope.removeAdditionalUploads).toEqual("function");
        });
        it("removableDocuments should be defined", function () {
            expect(scope.removableDocuments).toBeDefined();
            expect(typeof scope.removableDocuments).toEqual("function");
        });
        it("updateActionLogLimit should be defined", function () {
            expect(scope.updateActionLogLimit).toBeDefined();
            expect(typeof scope.updateActionLogLimit).toEqual("function");
        });
        it("uploadableFieldPredicates should be defined", function () {
            expect(scope.uploadableFieldPredicates).toBeDefined();
            expect(typeof scope.uploadableFieldPredicates).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("addMessage should add a message", function () {
            scope.messaging = null;
            scope.message = "test";

            scope.addMessage();
            scope.$digest();

            expect(scope.messaging).toBe(false);
            expect(scope.message).toEqual("");
        });
        it("archiveManuscript should add archived status to a manuscript", function () {
            scope.archivingManuscript = null;
            scope.submission = new mockSubmission(q);

            scope.archiveManuscript();
            scope.$digest();

            expect(scope.archivingManuscript).toBe(false);
        });
        it("cancelUpload should close a modal", function () {
            spyOn(scope, "closeModal");

            scope.cancelUpload();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it("getFileType should return a file type", function () {
            var response;
            var fieldPredicate = new mockFieldPredicate(q);

            fieldPredicate.mock(dataFieldPredicate6);

            response = scope.getFileType(fieldPredicate);
            expect(response).toEqual(fieldPredicate.value);
        });
        it("isPrimaryDocument should return a boolean", function () {
            var response;
            var fieldPredicate = new mockFieldPredicate(q);

            response = scope.isPrimaryDocument(fieldPredicate);
            expect(response).toBe(false);

            fieldPredicate.value = "PRIMARY";

            response = scope.isPrimaryDocument(fieldPredicate);
            expect(response).toBe(true);
        });
        it("queueRemove should remove a field value", function () {
            var fieldValue = new mockFieldValue(q);

            scope.queueRemove(fieldValue);

            scope.removeQueue = [ fieldValue ];

            scope.queueRemove(fieldValue);
        });
        it("removeAdditionalUploads should remove uploads", function () {
            var fieldValue = new mockFieldValue(q);
            scope.removeQueue = [ fieldValue ];
            scope.removingUploads = null;

            scope.removeAdditionalUploads();
            scope.$digest();

            expect(scope.removingUploads).toBe(false);
        });
        it("removableDocuments should return a boolean", function () {
            var response;
            var fieldValue = new mockFieldValue(q);

            response = scope.removableDocuments(fieldValue);
        });
        it("updateActionLogLimit should assign an action log limit", function () {
            var response;
            scope.submission = new mockSubmission(q);
            scope.submission.actionLogs = [];

            response = scope.updateActionLogLimit();
            expect(scope.actionLogCurrentLimit).toBe(0);

            scope.actionLogLimit = 1;

            response = scope.updateActionLogLimit();
            expect(scope.actionLogCurrentLimit).toBe(1);
        });
        it("uploadableFieldPredicates should return a boolean", function () {
            var response;
            var fieldPredicate = new mockFieldPredicate(q);

            response = scope.uploadableFieldPredicates(fieldPredicate);
            expect(typeof response).toBe("boolean");
        });
    });
});
