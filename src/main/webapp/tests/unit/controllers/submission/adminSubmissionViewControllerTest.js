describe('controller: AdminSubmissionViewController', function () {

    var controller, q, scope, FileUploadService;

    var initializeController = function(settings) {
        inject(function ($anchorScroll, $controller, $location, $q, $route, $routeParams, $rootScope, $window, _DepositLocationRepo_, _EmailTemplateRepo_, _FieldPredicateRepo_, _FieldValue_, _FileUploadService_, _ModalService_, _RestApi_, _SidebarService_, _StorageService_, _SubmissionRepo_, _SubmissionStatusRepo_, _UserRepo_, _UserService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            q = $q;

            FileUploadService = _FileUploadService_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('AdminSubmissionViewController', {
                $anchorScroll: $anchorScroll,
                $location: $location,
                $route: $route,
                $routeParams: $routeParams,
                $scope: scope,
                $window: $window,
                DepositLocationRepo: _DepositLocationRepo_,
                EmailTemplateRepo: _EmailTemplateRepo_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FieldValue: mockParameterModel(q, mockFieldValue),
                FileUploadService: _FileUploadService_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                SubmissionRepo: _SubmissionRepo_,
                SubmissionStatus: mockParameterModel(q, mockSubmissionStatus),
                SubmissionStatusRepo: _SubmissionStatusRepo_,
                UserRepo: _UserRepo_,
                UserService: _UserService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.depositLocationRepo');
        module('mock.emailTemplateRepo');
        module('mock.fieldPredicateRepo');
        module('mock.fieldValue');
        module('mock.fileUploadService');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.sidebarService');
        module('mock.storageService');
        module('mock.submissionRepo');
        module('mock.submissionStatus');
        module('mock.submissionStatusRepo');
        module('mock.user');
        module('mock.userRepo');
        module('mock.userService');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined for admin', function () {
            expect(controller).toBeDefined();
        });
        it('should be defined for manager', function () {
            initializeController({role: "ROLE_MANAGER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for reviewer', function () {
            initializeController({role: "ROLE_REVIEWER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for student', function () {
            initializeController({role: "ROLE_STUDENT"});
            expect(controller).toBeDefined();
        });
        it('should be defined for anonymous', function () {
            initializeController({role: "ROLE_ANONYMOUS"});
            expect(controller).toBeDefined();
        });
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('addComment should be defined', function () {
            expect(scope.addComment).toBeDefined();
            expect(typeof scope.addComment).toEqual("function");
        });
        it('addEmailAddressee should be defined', function () {
            expect(scope.addEmailAddressee).toBeDefined();
            expect(typeof scope.addEmailAddressee).toEqual("function");
        });
        it('cancel should be defined', function () {
            expect(scope.cancel).toBeDefined();
            expect(typeof scope.cancel).toEqual("function");
        });
        it('cancelReviewerNotes should be defined', function () {
            expect(scope.cancelReviewerNotes).toBeDefined();
            expect(typeof scope.cancelReviewerNotes).toEqual("function");
        });
        it('deleteDocumentFieldValue should be defined', function () {
            expect(scope.deleteDocumentFieldValue).toBeDefined();
            expect(typeof scope.deleteDocumentFieldValue).toEqual("function");
        });
        it('disableAddComment should be defined', function () {
            expect(scope.disableAddComment).toBeDefined();
            expect(typeof scope.disableAddComment).toEqual("function");
        });
        it('editReviewerNotes should be defined', function () {
            expect(scope.editReviewerNotes).toBeDefined();
            expect(typeof scope.editReviewerNotes).toEqual("function");
        });
        it('getDocumentTypePredicates should be defined', function () {
            expect(scope.getDocumentTypePredicates).toBeDefined();
            expect(typeof scope.getDocumentTypePredicates).toEqual("function");
        });
        it('getFile should be defined', function () {
            expect(scope.getFile).toBeDefined();
            expect(typeof scope.getFile).toEqual("function");
        });
        it('getFileType should be defined', function () {
            expect(scope.getFileType).toBeDefined();
            expect(typeof scope.getFileType).toEqual("function");
        });
        it('getPattern should be defined', function () {
            expect(scope.getPattern).toBeDefined();
            expect(typeof scope.getPattern).toEqual("function");
        });
        it('getTabPath should be defined', function () {
            expect(scope.getTabPath).toBeDefined();
            expect(typeof scope.getTabPath).toEqual("function");
        });
        it('hasPrimaryDocument should be defined', function () {
            expect(scope.hasPrimaryDocument).toBeDefined();
            expect(typeof scope.hasPrimaryDocument).toEqual("function");
        });
        it('isPrimaryDocument should be defined', function () {
            expect(scope.isPrimaryDocument).toBeDefined();
            expect(typeof scope.isPrimaryDocument).toEqual("function");
        });
        it('queueUpload should be defined', function () {
            expect(scope.queueUpload).toBeDefined();
            expect(typeof scope.queueUpload).toEqual("function");
        });
        it('removeEmailAddressee should be defined', function () {
            expect(scope.removeEmailAddressee).toBeDefined();
            expect(typeof scope.removeEmailAddressee).toEqual("function");
        });
        it('removeFiles should be defined', function () {
            expect(scope.removeFiles).toBeDefined();
            expect(typeof scope.removeFiles).toEqual("function");
        });
        it('resetAddCommentModal should be defined', function () {
            expect(scope.resetAddCommentModal).toBeDefined();
            expect(typeof scope.resetAddCommentModal).toEqual("function");
        });
        it('resetAddFile should be defined', function () {
            expect(scope.resetAddFile).toBeDefined();
            expect(typeof scope.resetAddFile).toEqual("function");
        });
        it('resetCommentModal should be defined', function () {
            expect(scope.resetCommentModal).toBeDefined();
            expect(typeof scope.resetCommentModal).toEqual("function");
        });
        it('saveDocumentFieldValue should be defined', function () {
            expect(scope.saveDocumentFieldValue).toBeDefined();
            expect(typeof scope.saveDocumentFieldValue).toEqual("function");
        });
        it('saveReviewerNotes should be defined', function () {
            expect(scope.saveReviewerNotes).toBeDefined();
            expect(typeof scope.saveReviewerNotes).toEqual("function");
        });
        it('showTab should be defined', function () {
            expect(scope.showTab).toBeDefined();
            expect(typeof scope.showTab).toEqual("function");
        });
        it('submitAddFile should be defined', function () {
            expect(scope.submitAddFile).toBeDefined();
            expect(typeof scope.submitAddFile).toEqual("function");
        });
        it('toggleConfirm should be defined', function () {
            expect(scope.toggleConfirm).toBeDefined();
            expect(typeof scope.toggleConfirm).toEqual("function");
        });
        it('updateActionLogLimit should be defined', function () {
            expect(scope.updateActionLogLimit).toBeDefined();
            expect(typeof scope.updateActionLogLimit).toEqual("function");
        });
        it('disableSubmitAddFile should be defined', function () {
            expect(scope.disableSubmitAddFile).toBeDefined();
            expect(typeof scope.disableSubmitAddFile).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('addComment should update the comment modal', function () {
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
        });
        it('addEmailAddressee should update the destination', function () {
            var destination = { push: jasmine.createSpy() };

            scope.addEmailAddressee(true, destination);

            expect(destination.push).toHaveBeenCalled();
        });
        it('cancel should open a modal close a modal', function () {
            var fieldValue = { refresh: jasmine.createSpy() };

            spyOn(scope, "closeModal");

            scope.cancel(fieldValue);

            expect(fieldValue.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('cancelReviewerNotes should cancel reviewer notes', function () {
            scope.editingReviewerNotes = null;
            scope.submission = mockSubmission(q);
            scope.submission.reviewerNotes = null;
            scope.reviewerNotes = {};

            scope.cancelReviewerNotes();

            expect(scope.editingReviewerNotes).toBe(false);
            expect(scope.submission.reviewerNotes).toEqual(scope.reviewerNotes);
        });
        it('deleteDocumentFieldValue should close a modal', function () {
            scope.confirm = null;

            spyOn(scope, "closeModal");

            scope.deleteDocumentFieldValue(new mockFieldPredicate(q));
            scope.$digest();

            expect(scope.confirm).toBe(false);
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('disableAddComment should return a boolean', function () {
            var response;
            scope.addCommentModal = {};
            scope.resetCommentModal(scope.addCommentModal);
            scope.addCommentModal.commentVisiblity = 'public';
            scope.addCommentModal.sendEmailToRecipient = true;
            scope.addCommentModal.sendEmailToCCRecipient = true;

            response = scope.disableAddComment();
            expect(typeof response).toBe("boolean");

            scope.addCommentModal.sendEmailToCCRecipient = false;

            response = scope.disableAddComment();
            expect(typeof response).toBe("boolean");

            scope.addCommentModal.commentVisiblity = 'private';

            response = scope.disableAddComment();
            expect(typeof response).toBe("boolean");
        });
        it('editReviewerNotes should copy the reviewer notes', function () {
            scope.submission = mockSubmission(q);
            scope.submission.reviewerNotes = {};
            scope.editingReviewerNotes = null;

            scope.editReviewerNotes();

            expect(scope.editingReviewerNotes).toBe(true);
            expect(scope.reviewerNotes).toEqual(scope.submission.reviewerNotes);
        });
        it('getDocumentTypePredicates should return an array', function () {
            var response;
            scope.submission = mockSubmission(q);
            scope.submission.submissionWorkflowSteps = [ new mockWorkflowStep(q) ];
            scope.submission.submissionWorkflowSteps[0].aggregateFieldProfiles = [ new mockFieldProfile(q), new mockFieldProfile(q) ];
            scope.submission.submissionWorkflowSteps[0].aggregateFieldProfiles[1].mock(dataFieldProfile3);

            response = scope.getDocumentTypePredicates();

            expect(typeof response).toBe("object");
        });
        // FIXME: this correctly triggers a file download (which needs to be prevented during testing)
        /*
        it('getFile should save a file', function () {
            var fieldValue = new mockFieldValue(q);
            fieldValue.fileInfo = {};
            scope.submission = mockSubmission(q);

            scope.getFile(fieldValue);
            scope.$digest();
        });
        */
        it('getFileType should get a file type', function () {
            spyOn(FileUploadService, "getFileType");

            scope.getFileType(new mockFieldPredicate(q));

            expect(FileUploadService.getFileType).toHaveBeenCalled();
        });
        it('getPattern should return a pattern', function () {
            var response;
            scope.fieldPredicates = [ new mockFieldPredicate(q), new mockFieldPredicate(q) ];
            scope.fieldPredicates[1].mock(dataFieldPredicate3);
            scope.submission = mockSubmission(q);
            scope.submission.mockWorkflowSteps = { aggregateFieldProfiles: [ mockFieldProfile(q) ] };
            scope.submission.mockWorkflowSteps.aggregateFieldProfiles[0].mock(dataFieldProfile2);
            scope.submission.mockWorkflowSteps.aggregateFieldProfiles[0].fieldPredicate = scope.fieldPredicates[1];

            response = scope.getPattern("text/plain");
            expect(response).toBeDefined();
            expect(response).not.toEqual("*");

            response = scope.getPattern("does not exist");
            expect(response).toEqual("*");
        });
        it('getTabPath should return a path with the submission id', function () {
            var response;
            var path = "/a/b/c";
            scope.submission = mockSubmission(q);

            response = scope.getTabPath(path);

            expect(response).toBe(path + "/" + scope.submission.id);
        });
        it('hasPrimaryDocument should return a boolean', function () {
            var response;
            scope.submission = mockSubmission(q);
            scope.submission.primaryDocumentFieldValue = {id: 1};

            response = scope.hasPrimaryDocument();
            expect(response).toBe(true);

            delete scope.submission.primaryDocumentFieldValue;

            response = scope.hasPrimaryDocument();
            expect(response).toBe(false);
        });
        it('isPrimaryDocument should return a boolean', function () {
            var response;

            spyOn(scope, "getFileType").and.returnValue("PRIMARY");

            response = scope.isPrimaryDocument();
            expect(response).toBe(true);

            scope.getFileType = function() {};
            spyOn(scope, "getFileType").and.returnValue("NOT PRIMARY");

            response = scope.isPrimaryDocument();
            expect(response).toBe(false);
        });
        it('queueUpload should assign the files', function () {
            scope.errorMessage = null;
            scope.addFileData = { files: [] };

            scope.queueUpload([ {} ]);

            expect(typeof scope.errorMessage).toBe("string");
            expect(scope.addFileData.files.length).toBe(1);
        });
        it('resetAddCommentModal should close a modal', function () {
            spyOn(scope, "closeModal");

            scope.resetAddCommentModal();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('resetAddFile should close a modal', function () {
            scope.errorMessage = null;
            spyOn(scope, "closeModal");

            scope.resetAddFile();

            expect(typeof scope.errorMessage).toBe("string");
            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('removeEmailAddressee should remove an email address', function () {
            var destination = { indexOf: jasmine.createSpy(), splice: jasmine.createSpy() };

            scope.removeEmailAddressee(true, destination);

            expect(destination.indexOf).toHaveBeenCalled();
            expect(destination.splice).toHaveBeenCalled();
        });
        it('removeFiles should remove files', function () {
            scope.errorMessage = null;
            scope.addFileData = { files: [] };

            scope.removeFiles();

            expect(typeof scope.errorMessage).toBe("string");
            expect(scope.addFileData.files).not.toBeDefined();
        });
        it('resetCommentModal should close a modal', function () {
            spyOn(scope, "closeModal");

            scope.resetCommentModal({});

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('saveDocumentFieldValue should close a modal', function () {
            var fieldValue = mockFieldValue(q);
            fieldValue.updating = null;

            // TODO: add test case for when scope.submission.saveFieldValue() response is INVALID.
            spyOn(scope, "closeModal");

            scope.saveDocumentFieldValue(fieldValue);
            scope.$digest();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(fieldValue.updating).toBe(false);
        });
        it('saveReviewerNotes should save the reviewer notes', function () {
            scope.submission = mockSubmission(q);
            scope.savingReviewerNotes = null;
            scope.editingReviewerNotes = null;

            scope.saveReviewerNotes();
            scope.$digest();

            expect(typeof scope.savingReviewerNotes).toBe("boolean");
            expect(scope.editingReviewerNotes).toBe(false);
        });
        it('showTab should return a boolean', function () {
            var response;
            var workflowStep = new mockWorkflowStep(q);

            response = scope.showTab(workflowStep);
            expect(response).toBe(false);

            workflowStep.aggregateFieldProfiles = [ new mockFieldProfile(q) ];
            workflowStep.aggregateFieldProfiles[0].mock(dataFieldProfile2);

            response = scope.showTab(workflowStep);
            expect(response).toBe(true);
        });
        it('submitAddFile should submit a file', function () {
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
                var response = {
                    data: {
                        meta: {
                            status: "INVALID",
                        },
                        payload: {},
                        status: 200
                    }
                };
                return valuePromise(q.defer(), response);
            };

            scope.submitAddFile();
            scope.$digest();

            scope.addFileData.needsCorrection = true;
            scope.addFileData.files = [ {} ];
            FileUploadService.uploadFile = function (submission, fieldValue) {
                var defer = q.defer();
                var response = {
                    meta: {
                        status: "INVALID",
                    },
                    status: 200
                };
                defer.reject(response);
                return defer.promise;
            };

            scope.submitAddFile();
            scope.$digest();
        });
        it('toggleConfirm should toggle a boolean', function () {
            scope.confirm = false;

            scope.toggleConfirm();
            expect(scope.confirm).toBe(true);

            scope.toggleConfirm();
            expect(scope.confirm).toBe(false);
        });
        it('updateActionLogLimit should assign the action log limit', function () {
            scope.actionLogCurrentLimit = null;
            scope.actionLogLimit = 1;

            scope.updateActionLogLimit();
            expect(scope.actionLogCurrentLimit).toBe(1);

            scope.submission = { actionLogs: { length: 100 } };

            scope.updateActionLogLimit();
            expect(scope.actionLogCurrentLimit).toBe(100);
        });
        it('disableSubmitAddFile should return a boolean', function () {
            var response;
            scope.addFileData = {};
            scope.addFileData.addFileSelection = 'replace';
            scope.addFileData.sendEmailToRecipient = true;
            scope.addFileData.sendEmailToCCRecipient = true;
            scope.addFileData.files = [ {} ];
            scope.recipientEmails = [ "a" ];
            scope.ccRecipientEmails = [ "b" ];
            scope.addFileData.uploading = false;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToCCRecipient = false;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToRecipient = false;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.addFileSelection = 'different';

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToRecipient = true;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");

            scope.addFileData.sendEmailToCCRecipient = true;

            response = scope.disableSubmitAddFile();
            expect(typeof response).toBe("boolean");
        });
    });

});
