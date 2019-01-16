describe('controller: SubmissionHistoryController', function () {

    var controller, location, q, scope, timeout, NgTableParams;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $q, $rootScope, $timeout, $window, SubmissionStates, _ModalService_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _WsApi_) {
            installPromiseMatchers();

            location = $location;
            q = $q;
            scope = $rootScope.$new();
            timeout = $timeout;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('SubmissionHistoryController', {
                $location: $location,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                SubmissionStates: SubmissionStates,
                ModalService: _ModalService_,
                NgTableParams: mockNgTableParams,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
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
        module('mock.modalService');
        module('mock.ngTableParams');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmission');
        module('mock.studentSubmissionRepo');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('confirmDelete should be defined', function () {
            expect(scope.confirmDelete).toBeDefined();
            expect(typeof scope.confirmDelete).toEqual("function");
        });
        it('deleteSubmission should be defined', function () {
            expect(scope.deleteSubmission).toBeDefined();
            expect(typeof scope.deleteSubmission).toEqual("function");
        });
        it('getDocumentTitle should be defined', function () {
            expect(scope.getDocumentTitle).toBeDefined();
            expect(typeof scope.getDocumentTitle).toEqual("function");
        });
        it('getManuscriptFileName should be defined', function () {
            expect(scope.getManuscriptFileName).toBeDefined();
            expect(typeof scope.getManuscriptFileName).toEqual("function");
        });
        it('startNewSubmission should be defined', function () {
            expect(scope.startNewSubmission).toBeDefined();
            expect(typeof scope.startNewSubmission).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('confirmDelete should open a modal', function () {
            var submission = mockSubmission(q);

            spyOn(scope, "openModal");

            scope.confirmDelete(submission);

            expect(scope.openModal).toHaveBeenCalled();
            expect(scope.submissionToDelete.id).toBe(submission.id);
        });
        it('deleteSubmission should delete a submission', function () {
            scope.submissionToDelete = mockSubmission(q);

            spyOn(scope, "closeModal");

            scope.deleteSubmission();
            scope.$digest();

            expect(scope.closeModal).toHaveBeenCalled();
        });
        it('getDocumentTitle should return the document title', function () {
            var result;
            var row = { fieldValues: [ new mockFieldValue(q) ] };

            result = scope.getDocumentTitle(row);
            expect(result).toBe(null);

            row.fieldValues[0].fieldPredicate.value = "dc.title";

            result = scope.getDocumentTitle(row);
            expect(result).toBe(row.fieldValues[0].value);
        });
        it('getManuscriptFileName should return a manuscript file name', function () {
            var result;
            var row = { fieldValues: [ new mockFieldValue(q) ] };

            result = scope.getManuscriptFileName(row);
            expect(result).toBe(null);

            row.fieldValues[0].fieldPredicate.value = "_doctype_primary";
            row.fieldValues[0].fileInfo = { name: "test" };

            result = scope.getManuscriptFileName(row);
            expect(result).toBe(row.fieldValues[0].fileInfo.name);
        });
        it('startNewSubmission should close a modal', function () {
            scope.submissionToDelete = mockSubmission(q);

            spyOn(scope, "closeModal");
            spyOn(location, "path");

            scope.startNewSubmission();
            scope.$digest();
            timeout.flush();

            expect(scope.closeModal).toHaveBeenCalled();
            expect(location.path).toHaveBeenCalled();
        });
    });
});
