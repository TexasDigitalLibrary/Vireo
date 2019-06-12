describe('service: controlledVocabularyRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, FileService, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _FileService_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            FileService = _FileService_;
            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, ControlledVocabularyRepo) {
            scope = rootScope.$new();

            repo = ControlledVocabularyRepo;
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.controlledVocabulary');
        module('mock.fileService', function($provide) {
            FileService = {};
            $provide.value("FileService", FileService);
        });
        module('mock.vocabularyWord');
        module('mock.wsApi');

        initializeVariables();
        initializeRepo();
    });

    describe('Is the repo defined', function () {
        it('should be defined', function () {
            expect(repo).toBeDefined();
        });
    });

    describe('Are the repo methods defined', function () {
        it('addVocabularyWord should be defined', function () {
            expect(repo.addVocabularyWord).toBeDefined();
            expect(typeof repo.addVocabularyWord).toEqual("function");
        });
        it('cancel should be defined', function () {
            expect(repo.cancel).toBeDefined();
            expect(typeof repo.cancel).toEqual("function");
        });
        it('confirmCSV should be defined', function () {
            expect(repo.confirmCSV).toBeDefined();
            expect(typeof repo.confirmCSV).toEqual("function");
        });
        it('downloadCSV should be defined', function () {
            expect(repo.downloadCSV).toBeDefined();
            expect(typeof repo.downloadCSV).toEqual("function");
        });
        it('removeVocabularyWord should be defined', function () {
            expect(repo.removeVocabularyWord).toBeDefined();
            expect(typeof repo.removeVocabularyWord).toEqual("function");
        });
        it('status should be defined', function () {
            expect(repo.status).toBeDefined();
            expect(typeof repo.status).toEqual("function");
        });
        it('updateVocabularyWord should be defined', function () {
            expect(repo.updateVocabularyWord).toBeDefined();
            expect(typeof repo.updateVocabularyWord).toEqual("function");
        });
        it('uploadCSV should be defined', function () {
            expect(repo.uploadCSV).toBeDefined();
            expect(typeof repo.uploadCSV).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('addVocabularyWord should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);
            var vocabularyWord = new mockVocabularyWord(q);

            repo.addVocabularyWord(controlledVocabulary, vocabularyWord);
            scope.$digest();

            // TODO
        });
        it('cancel should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);

            repo.cancel(controlledVocabulary);
            scope.$digest();

            // TODO
        });
        it('confirmCSV should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);
            var file = {};

            FileService.upload = function() {
                return dataPromise(q.defer());
            };

            repo.confirmCSV(file, controlledVocabulary);
            scope.$digest();

            // TODO
        });
        it('downloadCSV should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);

            repo.downloadCSV(controlledVocabulary);
            scope.$digest();

            // TODO
        });
        it('removeVocabularyWord should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);
            var vocabularyWord = new mockVocabularyWord(q);

            repo.removeVocabularyWord(controlledVocabulary, vocabularyWord);
            scope.$digest();

            // TODO
        });
        it('status should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);

            repo.status(controlledVocabulary);
            scope.$digest();

            // TODO
        });
        it('updateVocabularyWord should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);
            var vocabularyWord = new mockVocabularyWord(q);

            repo.updateVocabularyWord(controlledVocabulary, vocabularyWord);
            scope.$digest();

            // TODO
        });
        it('uploadCSV should return a submission', function () {
            var controlledVocabulary = new mockControlledVocabulary(q);

            repo.uploadCSV(controlledVocabulary);
            scope.$digest();

            // TODO
        });
    });
});
