describe("controller: AdminSubmissionViewController", function () {

    var controller, location, q, scope, timeout, mockedUser, EmailTemplateRepo, FileUploadService, SubmissionRepo, User, UserSettings, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $timeout, $location, _EmailTemplateRepo_, _FileUploadService_, _SubmissionRepo_, _UserSettings_, _WsApi_) {
            q = $q;
            timeout = $timeout;
            location = $location;
            mockedUser = mockParameterModel(q, mockUser);

            EmailTemplateRepo = _EmailTemplateRepo_;
            FileUploadService = _FileUploadService_;
            SubmissionRepo = _SubmissionRepo_;
            UserSettings = _UserSettings_;
            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($anchorScroll, $controller, $q, $route, $routeParams, $rootScope, _DepositLocationRepo_, _EmailRecipient_, _EmailRecipientType_, _EmbargoRepo_, _FieldPredicateRepo_, _FieldValue_, _ModalService_, _RestApi_, _SidebarService_, _StorageService_, _SubmissionStatuses_, _SubmissionStatusRepo_, _User_, _UserRepo_, _UserService_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("AdminSubmissionViewController", {
                $anchorScroll: $anchorScroll,
                $location: location,
                $route: $route,
                $routeParams: $routeParams,
                $scope: scope,
                $window: mockWindow(),
                DepositLocationRepo: _DepositLocationRepo_,
                EmailRecipient: mockParameterModel(q, mockEmailRecipient),
                EmailRecipientType: _EmailRecipientType_,
                EmailTemplateRepo: EmailTemplateRepo,
                EmbargoRepo: _EmbargoRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FieldValue: mockParameterModel(q, mockFieldValue),
                FileUploadService: FileUploadService,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                SubmissionRepo: SubmissionRepo,
                SubmissionStatuses: _SubmissionStatuses_,
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                User: _User_,
                UserRepo: _UserRepo_,
                UserService: _UserService_,
                UserSettings: mockParameterConstructor(UserSettings),
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
        module("mock.depositLocationRepo");
        module("mock.emailRecipient");
        module("mock.emailTemplateRepo");
        module("mock.embargoRepo");
        module("mock.fieldPredicate");
        module("mock.fieldPredicateRepo");
        module("mock.fieldProfile");
        module("mock.fieldValue");
        module("mock.fileUploadService");
        module("mock.modalService");
        module("mock.restApi");
        module("mock.sidebarService");
        module("mock.storageService");
        module("mock.submission");
        module("mock.submissionRepo");
        module("mock.submissionStatusRepo");
        module("mock.user", function($provide) {
            User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
        module("mock.userRepo");
        module("mock.userService");
        module("mock.userSettings");
        module("mock.vocabularyWord");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined for admin", function () {
            expect(controller).toBeDefined();
        });
        it("should be defined for manager", function () {
            initializeController({role: "ROLE_MANAGER"});
            expect(controller).toBeDefined();
        });
        it("should be defined for reviewer", function () {
            initializeController({role: "ROLE_REVIEWER"});
            expect(controller).toBeDefined();
        });
        it("should be defined for student", function () {
            initializeController({role: "ROLE_STUDENT"});
            expect(controller).toBeDefined();
        });
        it("should be defined for anonymous", function () {
            initializeController({role: "ROLE_ANONYMOUS"});
            expect(controller).toBeDefined();
        });
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("addComment should be defined", function () {
            expect(scope.addComment).toBeDefined();
            expect(typeof scope.addComment).toEqual("function");
        });
        it("addEmailAddressee should be defined", function () {
            expect(scope.addEmailAddressee).toBeDefined();
            expect(typeof scope.addEmailAddressee).toEqual("function");
        });
        it("cancel should be defined", function () {
            expect(scope.cancel).toBeDefined();
            expect(typeof scope.cancel).toEqual("function");
        });
        it("cancelReviewerNotes should be defined", function () {
            expect(scope.cancelReviewerNotes).toBeDefined();
            expect(typeof scope.cancelReviewerNotes).toEqual("function");
        });
        it("deleteDocumentFieldValue should be defined", function () {
            expect(scope.deleteDocumentFieldValue).toBeDefined();
            expect(typeof scope.deleteDocumentFieldValue).toEqual("function");
        });
        it("disableAddComment should be defined", function () {
            expect(scope.disableAddComment).toBeDefined();
            expect(typeof scope.disableAddComment).toEqual("function");
        });
        it("disableSubmitAddFile should be defined", function () {
            expect(scope.disableSubmitAddFile).toBeDefined();
            expect(typeof scope.disableSubmitAddFile).toEqual("function");
        });
        it("editReviewerNotes should be defined", function () {
            expect(scope.editReviewerNotes).toBeDefined();
            expect(typeof scope.editReviewerNotes).toEqual("function");
        });
        it("getDocumentTypePredicates should be defined", function () {
            expect(scope.getDocumentTypePredicates).toBeDefined();
            expect(typeof scope.getDocumentTypePredicates).toEqual("function");
        });
        it("getFile should be defined", function () {
            expect(scope.getFile).toBeDefined();
            expect(typeof scope.getFile).toEqual("function");
        });
        it("getFileType should be defined", function () {
            expect(scope.getFileType).toBeDefined();
            expect(typeof scope.getFileType).toEqual("function");
        });
        it("getPattern should be defined", function () {
            expect(scope.getPattern).toBeDefined();
            expect(typeof scope.getPattern).toEqual("function");
        });
        it("getTabPath should be defined", function () {
            expect(scope.getTabPath).toBeDefined();
            expect(typeof scope.getTabPath).toEqual("function");
        });
        it("hasPrimaryDocument should be defined", function () {
            expect(scope.hasPrimaryDocument).toBeDefined();
            expect(typeof scope.hasPrimaryDocument).toEqual("function");
        });
        it("isEmailAddresseeInvalid should be defined", function () {
            expect(scope.isEmailAddresseeInvalid).toBeDefined();
            expect(typeof scope.isEmailAddresseeInvalid).toEqual("function");
        });
        it("isPrimaryDocument should be defined", function () {
            expect(scope.isPrimaryDocument).toBeDefined();
            expect(typeof scope.isPrimaryDocument).toEqual("function");
        });
        it("queueUpload should be defined", function () {
            expect(scope.queueUpload).toBeDefined();
            expect(typeof scope.queueUpload).toEqual("function");
        });
        it("removeEmailAddressee should be defined", function () {
            expect(scope.removeEmailAddressee).toBeDefined();
            expect(typeof scope.removeEmailAddressee).toEqual("function");
        });
        it("removeFiles should be defined", function () {
            expect(scope.removeFiles).toBeDefined();
            expect(typeof scope.removeFiles).toEqual("function");
        });
        it("resetFileData should be defined", function () {
            expect(scope.resetFileData).toBeDefined();
            expect(typeof scope.resetFileData).toEqual("function");
        });
        it("resetCommentModal should be defined", function () {
            expect(scope.resetCommentModal).toBeDefined();
            expect(typeof scope.resetCommentModal).toEqual("function");
        });
        it("saveDocumentFieldValue should be defined", function () {
            expect(scope.saveDocumentFieldValue).toBeDefined();
            expect(typeof scope.saveDocumentFieldValue).toEqual("function");
        });
        it("saveReviewerNotes should be defined", function () {
            expect(scope.saveReviewerNotes).toBeDefined();
            expect(typeof scope.saveReviewerNotes).toEqual("function");
        });
        it("showTab should be defined", function () {
            expect(scope.showTab).toBeDefined();
            expect(typeof scope.showTab).toEqual("function");
        });
        it("showVocabularyWord should be defined", function () {
            expect(scope.showVocabularyWord).toBeDefined();
            expect(typeof scope.showVocabularyWord).toEqual("function");
        });
        it("submitAddFile should be defined", function () {
            expect(scope.submitAddFile).toBeDefined();
            expect(typeof scope.submitAddFile).toEqual("function");
        });
        it("toggleConfirm should be defined", function () {
            expect(scope.toggleConfirm).toBeDefined();
            expect(typeof scope.toggleConfirm).toEqual("function");
        });
        it("updateActionLogLimit should be defined", function () {
            expect(scope.updateActionLogLimit).toBeDefined();
            expect(typeof scope.updateActionLogLimit).toEqual("function");
        });
        it("validateEmailAddressee should be defined", function () {
            expect(scope.validateEmailAddressee).toBeDefined();
            expect(typeof scope.validateEmailAddressee).toEqual("function");
        });
    });

    describe("Are the scope.submissionStatusBox methods defined", function () {
        it("isUmiRelease should be defined", function () {
            expect(scope.submissionStatusBox.isUmiRelease).toBeDefined();
            expect(typeof scope.submissionStatusBox.isUmiRelease).toEqual("function");
        });
        it("getLastActionDate should be defined", function () {
            expect(scope.submissionStatusBox.getLastActionDate).toBeDefined();
            expect(typeof scope.submissionStatusBox.getLastActionDate).toEqual("function");
        });
        it("getLastActionEntry should be defined", function () {
            expect(scope.submissionStatusBox.getLastActionEntry).toBeDefined();
            expect(typeof scope.submissionStatusBox.getLastActionEntry).toEqual("function");
        });
        it("sendAdvisorEmail should be defined", function () {
            expect(scope.submissionStatusBox.sendAdvisorEmail).toBeDefined();
            expect(typeof scope.submissionStatusBox.sendAdvisorEmail).toEqual("function");
        });
        it("changeStatus should be defined", function () {
            expect(scope.submissionStatusBox.changeStatus).toBeDefined();
            expect(typeof scope.submissionStatusBox.changeStatus).toEqual("function");
        });
        it("publish should be defined", function () {
            expect(scope.submissionStatusBox.publish).toBeDefined();
            expect(typeof scope.submissionStatusBox.publish).toEqual("function");
        });
        it("deleteSubmission should be defined", function () {
            expect(scope.submissionStatusBox.deleteSubmission).toBeDefined();
            expect(typeof scope.submissionStatusBox.deleteSubmission).toEqual("function");
        });
        it("changeAssignee should be defined", function () {
            expect(scope.submissionStatusBox.changeAssignee).toBeDefined();
            expect(typeof scope.submissionStatusBox.changeAssignee).toEqual("function");
        });
        it("resetStatus should be defined", function () {
            expect(scope.submissionStatusBox.resetStatus).toBeDefined();
            expect(typeof scope.submissionStatusBox.resetStatus).toEqual("function");
        });
        it("resetAssigneeWorking should be defined", function () {
            expect(scope.submissionStatusBox.resetAssigneeWorking).toBeDefined();
            expect(typeof scope.submissionStatusBox.resetAssigneeWorking).toEqual("function");
        });
        it("setSubmitDate should be defined", function () {
            expect(scope.submissionStatusBox.setSubmitDate).toBeDefined();
            expect(typeof scope.submissionStatusBox.setSubmitDate).toEqual("function");
        });
    });

    describe("Are the scope.customActionsBox methods defined", function () {
        it("updateCustomActionValue should be defined", function () {
            expect(scope.customActionsBox.updateCustomActionValue).toBeDefined();
            expect(typeof scope.customActionsBox.updateCustomActionValue).toEqual("function");
        });
    });

    describe("Are the scope.activeDocumentBox methods defined", function () {
        it("downloadPrimaryDocument should be defined", function () {
            expect(scope.activeDocumentBox.downloadPrimaryDocument).toBeDefined();
            expect(typeof scope.activeDocumentBox.downloadPrimaryDocument).toEqual("function");
        });
        it("getPrimaryDocumentFileName should be defined", function () {
            expect(scope.activeDocumentBox.getPrimaryDocumentFileName).toBeDefined();
            expect(typeof scope.activeDocumentBox.getPrimaryDocumentFileName).toEqual("function");
        });
        it("gotoAllFiles should be defined", function () {
            expect(scope.activeDocumentBox.gotoAllFiles).toBeDefined();
            expect(typeof scope.activeDocumentBox.gotoAllFiles).toEqual("function");
        });
        it("uploadNewFile should be defined", function () {
            expect(scope.activeDocumentBox.uploadNewFile).toBeDefined();
            expect(typeof scope.activeDocumentBox.uploadNewFile).toEqual("function");
        });
    });

    describe("Does the scope initialize as expected", function () {
        it("EmailTemplateRepo.ready() should handle default template", function () {
            var defaultEmailTemplate = new mockEmailTemplate(q);
            defaultEmailTemplate.mock({
                name: "Choose a Message Template"
            });
            EmailTemplateRepo.mockedList.push(defaultEmailTemplate);

            initializeController();
        });
        it("Listen on '/channel/submission/id' should work as expected", function () {
            var submission1 = new mockSubmission(q);
            var submission2 = new mockSubmission(q);
            var fieldValue1 = new mockFieldValue(q);
            var fieldValue2 = new mockFieldValue(q);
            var fieldPredicate1 = new mockFieldPredicate(q);
            var fieldPredicate2 = new mockFieldPredicate(q);

            fieldPredicate2.documentTypePredicate = undefined;
            submission2.fieldValues = [
                fieldValue1,
                fieldValue2
            ];
            fieldValue1.fieldPredicate = fieldPredicate1;
            fieldValue2.fieldPredicate = fieldPredicate2;

            WsApi.listen = function(path) {
                var payload = {
                    Submission: submission1
                };
                return notifyPromise(timeout, q.defer(), payload);
            };

            initializeController();
            scope.$digest();
            timeout.flush();

            SubmissionRepo.fetchSubmissionById = function() {
                return valuePromise(q.defer(), submission2);
            };

            initializeController();
            scope.$digest();
            timeout.flush();

            WsApi.listen = function(path) {
                var payload = {
                    Submission: submission2
                };
                return notifyPromise(timeout, q.defer(), payload);
            };

            fieldPredicate2.documentTypePredicate = true;

            initializeController();
            scope.$digest();
            timeout.flush();
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("addComment should update the comment modal", function () {
            var commentModal = {};
            scope.resetCommentModal(commentModal);
            commentModal.sendEmailToRecipient = true;
            commentModal.sendEmailToCCRecipient = true;
            commentModal.recipientEmails = ["stub"];
            commentModal.ccRecipientEmails = ["ccStub"];
            commentModal.needsCorrection = true;
            scope.submission = mockSubmission(q);

            spyOn(scope, "resetCommentModal");
            spyOn(scope.submission, "changeStatus");

            scope.addComment(commentModal);
            scope.$digest();

            expect(scope.addCommentModal.recipientEmail).toBeDefined();
            expect(scope.addCommentModal.ccRecipientEmail).toBeDefined();
            expect(scope.submission.changeStatus).toHaveBeenCalled();
            expect(scope.resetCommentModal).toHaveBeenCalled();

            commentModal.needsCorrection = false;

            scope.addComment(commentModal);
            scope.$digest();
        });
        it("addEmailAddressee should update the destination", function () {
            var mockEmails = { push: jasmine.createSpy() };
            var mockFormField =  {
              $$rawModelValue: { type: "mock" },
              $$attr: { name: "" }
            };

            scope.validateEmailAddressee = function() { return false; };

            scope.addEmailAddressee(mockEmails, mockFormField);
            expect(mockEmails.push).toHaveBeenCalled();

            scope.validateEmailAddressee = function() { return true; };

            scope.addEmailAddressee(mockEmails, mockFormField);

            mockFormField.$$rawModelValue = "mock@example.com";

            scope.addEmailAddressee(mockEmails, mockFormField);

            mockFormField.$$rawModelValue = "invalid email mock";

            scope.addEmailAddressee(mockEmails, mockFormField);

            mockFormField.$$rawModelValue = false;

            scope.addEmailAddressee(mockEmails, mockFormField);
        });
        it("cancel should open a modal close a modal", function () {
            var fieldValue = { refresh: jasmine.createSpy() };

            spyOn(scope, "closeModal");

            scope.cancel(fieldValue);

            expect(fieldValue.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it("cancelReviewerNotes should cancel reviewer notes", function () {
            scope.editingReviewerNotes = null;
            scope.submission = mockSubmission(q);
            scope.submission.reviewerNotes = null;
            scope.reviewerNotes = {};

            scope.cancelReviewerNotes();

            expect(scope.editingReviewerNotes).toBe(false);
            expect(scope.submission.reviewerNotes).toEqual(scope.reviewerNotes);
        });
        it("deleteDocumentFieldValue should close a modal", function () {
            scope.confirm = null;

            spyOn(scope, "closeModal");

            scope.deleteDocumentFieldValue(new mockFieldPredicate(q));
            scope.$digest();
            timeout.flush();

            expect(scope.confirm).toBe(false);
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it("disableAddComment should return a boolean", function () {
            var response;
            scope.addCommentModal = {};
            scope.resetCommentModal(scope.addCommentModal);
            scope.addCommentModal.commentVisibility = "public";
            scope.addCommentModal.sendEmailToRecipient = true;
            scope.addCommentModal.sendEmailToCCRecipient = true;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.sendEmailToCCRecipient = false;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.recipientEmails = [{}];

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.subject = 1;
            scope.addCommentModal.message = undefined;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.message = "";

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.message = 1;

            response = scope.disableAddComment();
            expect(response).toBe(false);

            scope.addCommentModal.sendEmailToCCRecipient = true;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.ccRecipientEmails = [{}];
            scope.addCommentModal.subject = undefined;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.subject = "";
            scope.addCommentModal.message = undefined;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.subject = 1;
            scope.addCommentModal.message = undefined;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.message = "";

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.message = 1;

            response = scope.disableAddComment();
            expect(response).toBe(false);

            scope.addCommentModal.sendEmailToRecipient = false;

            response = scope.disableAddComment();
            expect(response).toBe(false);

            scope.addCommentModal.subject = undefined;
            scope.addCommentModal.commentVisibility = "private";

            response = scope.disableAddComment();
            expect(typeof response).toBe("boolean");

            scope.addCommentModal.subject = 1;
            scope.addCommentModal.message = undefined;

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.message = "";

            response = scope.disableAddComment();
            expect(response).toBe(true);

            scope.addCommentModal.message = 1;

            response = scope.disableAddComment();
            expect(response).toBe(false);

            scope.addCommentModal.commentVisibility = "unknown";

            response = scope.disableAddComment();
            expect(response).toBe(true);
        });
        it("disableSubmitAddFile should return a boolean", function () {
            var response;
            scope.addFileData = {};
            scope.addFileData.addFileSelection = "replace";
            scope.addFileData.sendEmailToRecipient = true;
            scope.addFileData.sendEmailToCCRecipient = true;
            scope.addFileData.files = [ {} ];
            scope.addFileData.recipientEmails = [ "a" ];
            scope.addFileData.ccRecipientEmails = [ "b" ];
            scope.addFileData.uploading = false;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToCCRecipient = false;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToRecipient = false;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.addFileSelection = "different";

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToRecipient = true;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToCCRecipient = true;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");
        });
        it("editReviewerNotes should copy the reviewer notes", function () {
            scope.submission = mockSubmission(q);
            scope.submission.reviewerNotes = {};
            scope.editingReviewerNotes = null;

            scope.editReviewerNotes();

            expect(scope.editingReviewerNotes).toBe(true);
            expect(scope.reviewerNotes).toEqual(scope.submission.reviewerNotes);
        });
        it("getDocumentTypePredicates should return an array", function () {
            var response;
            scope.submission = mockSubmission(q);
            scope.submission.submissionWorkflowSteps = [ new mockWorkflowStep(q) ];
            scope.submission.submissionWorkflowSteps[0].aggregateFieldProfiles = [ new mockFieldProfile(q), new mockFieldProfile(q) ];
            scope.submission.submissionWorkflowSteps[0].aggregateFieldProfiles[1].mock(dataFieldProfile3);

            response = scope.getDocumentTypePredicates();

            expect(typeof response).toBe("object");
        });
        it("getFile should save a file", function () {
            var fieldValue = new mockFieldValue(q);
            fieldValue.fileInfo = {};
            scope.submission = mockSubmission(q);

            // manually override the FileSaver.saveAs method, which is installed globally.
            var _global = typeof window === "object" && window.window === window ? window : typeof self === "object" && self.self === self ? self : typeof global === "object" && global.global === global ? global : void 0;
            _global.saveAs = function() { return true; };

            scope.getFile(fieldValue);
            scope.$digest();
        });
        it("getFileType should get a file type", function () {
            spyOn(FileUploadService, "getFileType");

            scope.getFileType(new mockFieldPredicate(q));

            expect(FileUploadService.getFileType).toHaveBeenCalled();
        });
        it("getPattern should return a pattern", function () {
            var response;
            scope.fieldPredicates = [ new mockFieldPredicate(q), new mockFieldPredicate(q) ];
            scope.fieldPredicates[1].mock(dataFieldPredicate3);
            scope.submission = mockSubmission(q);
            scope.submission.mockWorkflowSteps = { aggregateFieldProfiles: [ mockFieldProfile(q) ] };
            scope.submission.mockWorkflowSteps.aggregateFieldProfiles[0].mock(dataFieldProfile2);
            scope.submission.mockWorkflowSteps.aggregateFieldProfiles[0].fieldPredicate = scope.fieldPredicates[1];

            spyOn(FileUploadService, "getPattern").and.returnValue(".pdf");
            response = scope.getPattern("_doctype_primary");
            expect(response).toBeDefined();
            expect(response).not.toEqual("*");

            FileUploadService.getPattern.and.returnValue(".pdf");
            response = scope.getPattern("does not exist");
            expect(response).toEqual("*");
        });
        it("getTabPath should return a path with the submission id", function () {
            var response;
            var path = "/a/b/c";
            scope.submission = mockSubmission(q);

            response = scope.getTabPath(path);

            expect(response).toBe(path + "/" + scope.submission.id);
        });
        it("hasPrimaryDocument should return a boolean", function () {
            var response;
            scope.submission = mockSubmission(q);
            scope.submission.primaryDocumentFieldValue = {id: 1};

            response = scope.hasPrimaryDocument();
            expect(response).toBe(true);

            delete scope.submission.primaryDocumentFieldValue;

            response = scope.hasPrimaryDocument();
            expect(response).toBe(false);
        });
        it("isEmailAddresseeInvalid should return a boolean", function () {
            var response;
            var formField = {
                $$attr: {
                    name: "mock"
                },
                $$rawModelValue: {
                    type: "mock"
                },
                $invalid: false
            };

            response = scope.isEmailAddresseeInvalid(formField);
            // TODO

            formField.$invalid = true;

            response = scope.isEmailAddresseeInvalid(formField);
            // TODO
        });
        it("isPrimaryDocument should return a boolean", function () {
            var response;

            spyOn(scope, "getFileType").and.returnValue("PRIMARY");

            response = scope.isPrimaryDocument();
            expect(response).toBe(true);

            scope.getFileType = function() {};
            spyOn(scope, "getFileType").and.returnValue("NOT PRIMARY");

            response = scope.isPrimaryDocument();
            expect(response).toBe(false);
        });
        it("queueUpload should assign the files", function () {
            scope.errorMessage = null;
            scope.addFileData = { files: [] };

            scope.queueUpload([ {} ]);

            expect(typeof scope.errorMessage).toBe("string");
            expect(scope.addFileData.files.length).toBe(1);
        });
        it("resetFileData should close a modal", function () {
            var fileData = {};
            scope.errorMessage = null;
            spyOn(scope, "closeModal");

            scope.resetFileData();

            expect(typeof scope.errorMessage).toBe("string");
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it("removeEmailAddressee should remove an email address", function () {
            var destination = { indexOf: jasmine.createSpy(), splice: jasmine.createSpy() };

            scope.removeEmailAddressee(true, destination);

            expect(destination.indexOf).toHaveBeenCalled();
            expect(destination.splice).toHaveBeenCalled();
        });
        it("removeFiles should remove files", function () {
            scope.errorMessage = null;
            scope.addFileData = { files: [] };

            scope.removeFiles();

            expect(typeof scope.errorMessage).toBe("string");
            expect(scope.addFileData.files).not.toBeDefined();
        });
        it("resetCommentModal should close a modal", function () {
            spyOn(scope, "closeModal");

            scope.resetCommentModal({});

            expect(scope.closeModal).toHaveBeenCalled();

            UserSettings = {
                notes_cc_student_advisor_by_default: "true",
                notes_mark_comment_as_private_by_default: "true",
                notes_email_student_by_default: "true"
            };

            initializeController();
            scope.resetCommentModal({});

            UserSettings.notes_email_student_by_default = "false";
            UserSettings.notes_cc_student_advisor_by_default = "true";

            initializeController();
            scope.resetCommentModal({});
        });
        it("saveDocumentFieldValue should close a modal", function () {
            var fieldValue = new mockFieldValue(q);
            fieldValue.updating = null;

            spyOn(scope, "closeModal");

            scope.saveDocumentFieldValue(fieldValue);
            scope.$digest();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(fieldValue.updating).toBe(false);

            scope.submission.saveFieldValue = function() {
                var payload = {};
                return payloadPromise(q.defer(), payload, "INVALID");
            };

            scope.saveDocumentFieldValue(fieldValue);
            scope.$digest();
        });
        it("saveReviewerNotes should save the reviewer notes", function () {
            scope.submission = new mockSubmission(q);
            scope.savingReviewerNotes = null;
            scope.editingReviewerNotes = null;

            scope.saveReviewerNotes();
            scope.$digest();

            expect(typeof scope.savingReviewerNotes).toBe("boolean");
            expect(scope.editingReviewerNotes).toBe(false);
        });
        it("showTab should return a boolean", function () {
            var response;
            var workflowStep = new mockWorkflowStep(q);

            response = scope.showTab(workflowStep);
            expect(response).toBe(false);

            workflowStep.aggregateFieldProfiles = [ new mockFieldProfile(q) ];
            workflowStep.aggregateFieldProfiles[0].mock(dataFieldProfile2);

            response = scope.showTab(workflowStep);
            expect(response).toBe(true);
        });
        it("showVocabularyWord should return a boolean", function () {
            var response;
            var vocabularyWord = new mockVocabularyWord(q);
            var fieldProfile = new mockFieldProfile(q);
            var fieldPredicate = new mockFieldPredicate(q);
            var fieldValue1 = new mockFieldValue(q);
            var fieldValue2 = new mockFieldValue(q);
            var submission = new mockSubmission(q);
            var embargo = new mockEmbargo(q);

            fieldPredicate.id = 90;
            fieldValue2.mock(dataFieldValue3);
            fieldValue2.fieldPredicate = fieldPredicate;
            submission.fieldValues = [
                fieldValue1
            ];
            scope.submission = submission;

            response = scope.showVocabularyWord(vocabularyWord);
            expect(response).toBe(true);

            delete fieldProfile.fieldPredicate;
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldProfile.fieldPredicate = fieldPredicate;
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldPredicate.value = "proquest_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            fieldPredicate.id = embargo.id;
            submission.fieldValues.push(fieldValue2);

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            vocabularyWord.identifier = embargo.id;

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);

            embargo.mock(dataEmbargo3);
            embargo.isActive = false;
            fieldPredicate.id = embargo.id;
            vocabularyWord.identifier = embargo.id;

            fieldPredicate.value = "default_embargos";
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(false);
            vocabularyWord.identifier = embargo.id;

            fieldValue2.value = embargo.name;
            submission.fieldValues.push(fieldValue2);
            response = scope.showVocabularyWord(vocabularyWord, fieldProfile);
            expect(response).toBe(true);
        });
        it("submitAddFile should submit a file", function () {
            var fileData = {};
            scope.fieldPredicates = [ new mockFieldPredicate(q), new mockFieldPredicate(q) ];
            scope.fieldPredicates[1].mock(dataFieldPredicate3);
            scope.submission = mockSubmission(q);
            scope.submission.primaryDocumentFieldValue = new mockFieldValue(q);
            scope.addFileData = {};
            scope.addFileData.addFileSelection = "replace";
            scope.addFileData.sendEmailToRecipient = true;
            scope.addFileData.sendEmailToCCRecipient = true;
            scope.addFileData.files = [ {} ];
            scope.recipientEmails = [ "a" ];
            scope.ccRecipientEmails = [ "b" ];
            scope.addFileData.uploading = null;

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.addFileSelection = "replace";
            scope.addFileData.files = [ {} ];
            delete scope.submission.primaryDocumentFieldValue;

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            scope.addFileData.addFileSelection = null;

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            scope.submission.saveFieldValue = function (fieldValue, fieldProfile) {
                return messagePromise(q.defer(), "This is an accept response sending a INVALID status", "INVALID");
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            scope.submission.saveFieldValue = function (fieldValue, fieldProfile) {
                return messagePromise(q.defer(), undefined, "INVALID");
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            FileUploadService.uploadFile = function (submission, fieldValue) {
                var payload = {};
                return dataPromise(q.defer(), payload);
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.needsCorrection = true;
            scope.addFileData.files = [ {} ];
            FileUploadService.uploadFile = function (submission, fieldValue) {
                var response = {
                    meta: {
                        status: "INVALID",
                    },
                    status: 500
                };
                var payload = null;
                return valuePromise(q.defer(), response, "reject");
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            FileUploadService.uploadFile = function (submission, fieldValue) {
                var response = {
                    meta: {
                        status: "INVALID",
                    },
                    payload: {
                        meta: {
                            message: "message",
                            status: 500
                        }
                    },
                    status: 500
                };
                var payload = null;
                return valuePromise(q.defer(), response, "reject");
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            FileUploadService.uploadFile = function (submission, fieldValue) {
                var payload;
                return dataPromise(q.defer(), payload, "INVALID");
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.files = [ {} ];
            FileUploadService.uploadFile = function (submission, fieldValue) {
                var response = {
                    data: {
                        meta: {
                            status: "INVALID",
                        },
                    },
                    payload: {
                        meta: {
                            message: "message",
                            status: 500
                        }
                    },
                    status: 500
                };
                return valuePromise(q.defer(), response);
            };

            scope.submitAddFile();
            scope.$digest();
        });
        it("toggleConfirm should toggle a boolean", function () {
            scope.confirm = false;

            scope.toggleConfirm();
            expect(scope.confirm).toBe(true);

            scope.toggleConfirm();
            expect(scope.confirm).toBe(false);
        });
        it("updateActionLogLimit should assign the action log limit", function () {
            scope.actionLogCurrentLimit = null;
            scope.actionLogLimit = 1;

            scope.updateActionLogLimit();
            expect(scope.actionLogCurrentLimit).toBe(1);

            scope.submission = { actionLogs: { length: 100 } };

            scope.updateActionLogLimit();
            expect(scope.actionLogCurrentLimit).toBe(100);
        });
        it("validateEmailAddressee should validate email addresses", function () {
            var response;
            var formField = {
                $$attr: {
                    name: "mock"
                },
                $$rawModelValue: {
                    type: "mock"
                },
                $invalid: false
            };

            response = scope.validateEmailAddressee(formField);
            scope.$digest();
            // TODO
        });
    });

    describe("Do the scope.activeDocumentBox methods work as expected", function () {
        it("downloadPrimaryDocument should execute getFile", function () {
            spyOn(scope, "getFile");

            scope.activeDocumentBox.downloadPrimaryDocument();

            expect(scope.getFile).toHaveBeenCalled();
        });
        it("getPrimaryDocumentFileName should update the location", function () {
            var response;

            scope.submission = new mockSubmission(q);
            scope.submission.primaryDocumentFieldValue = {
                fileInfo: {
                    name: "mock"
                }
            };

            response = scope.activeDocumentBox.getPrimaryDocumentFileName();

            expect(response).toBe("mock");

            scope.submission.primaryDocumentFieldValue.fileInfo = undefined;
            response = scope.activeDocumentBox.getPrimaryDocumentFileName();

            scope.submission.primaryDocumentFieldValue = undefined;
            response = scope.activeDocumentBox.getPrimaryDocumentFileName();

            expect(response).toBe("");
        });
        it("gotoAllFiles should update the location", function () {
            spyOn(location, "hash");

            scope.activeDocumentBox.gotoAllFiles();

            expect(location.hash).toHaveBeenCalled();
        });
        it("uploadNewFile should open a modal", function () {
            spyOn(scope, "openModal");

            scope.activeDocumentBox.uploadNewFile();

            expect(scope.openModal).toHaveBeenCalled();
        });
    });

    describe("Do the scope.submissionStatusBox methods work as expected", function () {
        it("isUmiRelease should return a string", function () {
            var response;
            var fieldValue = new mockFieldValue(q);
            var fieldPredicate = new mockFieldPredicate(q);

            fieldValue.fieldPredicate = fieldPredicate;
            fieldValue.value = "false";
            scope.submission.fieldValues = [ fieldValue ];

            fieldPredicate.value = "not_umi_publication";
            response = scope.submissionStatusBox.isUmiRelease();
            expect(response).toBe("no");

            fieldPredicate.value = "umi_publication";
            response = scope.submissionStatusBox.isUmiRelease();
            expect(response).toBe("no");

            fieldValue.value = "true";
            fieldPredicate.value = "umi_publication";
            response = scope.submissionStatusBox.isUmiRelease();
            expect(response).toBe("yes");
        });
        it("getLastActionDate should return an action log date", function () {
            var response;
            var submission = new mockSubmission(q);
            var actionLog = new mockActionLog(q);

            submission.actionLogs.push(actionLog);
            scope.submission = submission;

            response = scope.submissionStatusBox.getLastActionDate();
            expect(response).toBe(actionLog.actionDate);
        });
        it("getLastActionEntry should return a string", function () {
            var response;
            var submission = new mockSubmission(q);
            var actionLog = new mockActionLog(q);

            submission.actionLogs.push(actionLog);
            scope.submission = submission;

            response = scope.submissionStatusBox.getLastActionEntry();
            expect(response).toBe(actionLog.entry);
        });
        it("sendAdvisorEmail should work", function () {
            var response;
            response = scope.submissionStatusBox.sendAdvisorEmail();
            scope.$digest();
            // TODO
        });
        it("changeStatus should work", function () {
            var response;
            var state = {};
            response = scope.submissionStatusBox.changeStatus(state);
            scope.$digest();
            // TODO
        });
        it("publish should work", function () {
            var response;
            var state = {};
            response = scope.submissionStatusBox.publish(state);
            scope.$digest();
            // TODO
        });
        it("deleteSubmission should work", function () {
            var response;
            response = scope.submissionStatusBox.deleteSubmission();
            scope.$digest();
            // TODO
        });
        it("changeAssignee should work", function () {
            var response;
            var assignee = new mockUser(q);
            response = scope.submissionStatusBox.changeAssignee(assignee);
            scope.$digest();
            // TODO
        });
        it("resetStatus should work", function () {
            var response;
            response = scope.submissionStatusBox.resetStatus();
            // TODO
        });
        it("resetAssigneeWorking should work", function () {
            var response;
            response = scope.submissionStatusBox.resetAssigneeWorking();
            // TODO
        });
        it("setSubmitDate should work", function () {
            var response;
            var date = "1425393875282";
            response = scope.submissionStatusBox.setSubmitDate(date);
            scope.$digest();
            // TODO
        });
    });

    describe("Do the scope.customActionsBox methods work as expected", function () {
        it("updateCustomActionValue should work", function () {
            var response;
            response = scope.customActionsBox.updateCustomActionValue();
            // TODO
        });
    });
});
