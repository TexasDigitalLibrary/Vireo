describe("service: submissionRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, FileService, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _FileService_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedUser = mockParameterModel(q, mockUser);

            FileService = _FileService_;
            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, SubmissionRepo) {
            scope = rootScope.$new();

            repo = $injector.get('SubmissionRepo');
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.depositLocation");
        module("mock.fileService", function($provide) {
            FileService = {};
            $provide.value("FileService", FileService);
        });
        module("mock.packager");
        module("mock.submissionStatus");
        module("mock.user", function($provide) {
            var User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
        module("mock.userService");
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
        it("batchExport should be defined", function () {
            expect(repo.batchExport).toBeDefined();
            expect(typeof repo.batchExport).toEqual("function");
        });
        it("batchUpdateStatus should be defined", function () {
            expect(repo.batchUpdateStatus).toBeDefined();
            expect(typeof repo.batchUpdateStatus).toEqual("function");
        });
        it("batchPublish should be defined", function () {
            expect(repo.batchPublish).toBeDefined();
            expect(typeof repo.batchPublish).toEqual("function");
        });
        it("batchAssignTo should be defined", function () {
            expect(repo.batchAssignTo).toBeDefined();
            expect(typeof repo.batchAssignTo).toEqual("function");
        });
        it("fetchSubmissionById should be defined", function () {
            expect(repo.fetchSubmissionById).toBeDefined();
            expect(typeof repo.fetchSubmissionById).toEqual("function");
        });
        it("query should be defined", function () {
            expect(repo.query).toBeDefined();
            expect(typeof repo.query).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("batchExport should trigger a download", function () {
            var packager = new mockPackager(q);

            FileService.download = function() {
                return dataPromise(q.defer());
            };

            repo.batchExport(packager, 1);
            scope.$digest();

            // TODO
        });
        it("batchUpdateStatus should update", function () {
            var submissionStatus = new mockSubmissionStatus(q);

            repo.batchUpdateStatus(submissionStatus);
            scope.$digest();

            // TODO
        });
        it("batchPublish should publish", function () {
            var depositLocation = new mockDepositLocation(q);

            repo.batchPublish(depositLocation);
            scope.$digest();

            // TODO
        });
        it("batchAssignTo should assign", function () {
            var assignee = new mockUser(q);

            repo.batchAssignTo(assignee);
            scope.$digest();

            // TODO
        });
        it("fetchSubmissionById should return a submission", function () {
            repo.fetchSubmissionById(1);
            scope.$digest();

            // TODO
        });
        it("query should return a submission", function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.query([], "0", "1");
            scope.$digest();

            // TODO
        });
    });
});
