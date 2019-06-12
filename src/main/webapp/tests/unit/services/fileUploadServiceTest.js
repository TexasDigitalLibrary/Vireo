describe('service: FileUploadService', function () {
    var q, rootScope, service, scope, FieldValue, FileService, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _FieldValue_, _FileService_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            FieldValue = _FieldValue_;
            FileService = _FileService_;
            WsApi = _WsApi_;
        });
    };

    var initializeService = function(settings) {
        inject(function ($injector) {
            scope = rootScope.$new();

            service = $injector.get('FileUploadService');
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.fieldValue', function($provide) {
            FieldValue = {};
            $provide.value("FieldValue", mockParameterConstructor(FieldValue));
        });
        module('mock.fileService', function($provide) {
            FileService = {};
            $provide.value("FileService", FileService);
        });
        module('mock.fieldPredicate');
        module('mock.submission');
        module('mock.wsApi');

        initializeVariables();
        initializeService();
    });

    describe('Is the service defined', function () {
        it('should be defined', function () {
            expect(service).toBeDefined();
        });
    });

    describe('Are the service methods defined', function () {
        it('archiveFile should be defined', function () {
            expect(service.archiveFile).toBeDefined();
            expect(typeof service.archiveFile).toEqual("function");
        });
        it('download should be defined', function () {
            expect(service.download).toBeDefined();
            expect(typeof service.download).toEqual("function");
        });
        it('getFileType should be defined', function () {
            expect(service.getFileType).toBeDefined();
            expect(typeof service.getFileType).toEqual("function");
        });
        it('isPrimaryDocument should be defined', function () {
            expect(service.isPrimaryDocument).toBeDefined();
            expect(typeof service.isPrimaryDocument).toEqual("function");
        });
        it('removeFile should be defined', function () {
            expect(service.removeFile).toBeDefined();
            expect(typeof service.removeFile).toEqual("function");
        });
        it('uploadFile should be defined', function () {
            expect(service.uploadFile).toBeDefined();
            expect(typeof service.uploadFile).toEqual("function");
        });
    });

    describe('Do the service methods work as expected', function () {
        it('archiveFile should archive a file', function () {
            var fieldValue = new mockFieldValue(q);
            var fieldProfile = new mockFieldProfile(q);
            var submission = new mockSubmission(q);
            var removeFieldValue = function() {
                return payloadPromise(q.defer());
            };

            service.archiveFile(submission, fieldValue, undefined);
            scope.$digest();

            service.archiveFile(submission, fieldValue, removeFieldValue);
            scope.$digest();

            submission.getFieldProfileByPredicateName = function() {
                return fieldProfile;
            };

            service.archiveFile(submission, fieldValue, removeFieldValue);
            scope.$digest();

            submission.archiveFile = function() {
                return messagePromise(q.defer(), {}, "FAILURE");
            };

            service.archiveFile(submission, fieldValue, removeFieldValue);
            scope.$digest();
        });
        it('download should download a file', function () {
            var fieldValue = new mockFieldValue(q);
            var submission = new mockSubmission(q);

            spyOn(submission, "file");

            service.download(submission, fieldValue);
            expect(submission.file).toHaveBeenCalled();
        });
        it('getFileType should return a string', function () {
            var response;
            var fieldPredicate1 = new mockFieldPredicate(q);
            var fieldPredicate2 = new mockFieldPredicate(q);

            fieldPredicate2.mock(dataFieldPredicate2);

            response = service.getFileType(fieldPredicate1);
            expect(response).toEqual("PRIMARY");

            response = service.getFileType(fieldPredicate2);
            expect(response).toEqual("ARCHIVED");
        });
        it('isPrimaryDocument should return a boolean', function () {
            var response;
            var fieldPredicate1 = new mockFieldPredicate(q);
            var fieldPredicate2 = new mockFieldPredicate(q);

            fieldPredicate2.mock(dataFieldPredicate2);

            response = service.isPrimaryDocument(fieldPredicate1);
            expect(response).toBe(true);

            response = service.isPrimaryDocument(fieldPredicate2);
            expect(response).toBe(false);
        });
        it('removeFile should remove a file', function () {
            var fieldValue = new mockFieldValue(q);
            var submission = new mockSubmission(q);

            service.removeFile(submission, fieldValue);
            scope.$digest();

            submission.removeFile = function() {
                return payloadPromise(q.defer(), {}, "FAILURE");
            };

            service.removeFile(submission, fieldValue);
            scope.$digest();
        });
        it('uploadFile should upload a file', function () {
            var fieldValue = new mockFieldValue(q);
            var submission = new mockSubmission(q);

            FileService.upload = function() {
                return {};
            };

            service.uploadFile(submission, fieldValue);
            scope.$digest();
        });
    });
});
