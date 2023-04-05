describe("service: studentSubmissionRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, timeout, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, $timeout, _WsApi_) {
            q = $q;
            rootScope = $rootScope;
            timeout = $timeout;

            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, StudentSubmissionRepo) {
            scope = rootScope.$new();

            repo = $injector.get('StudentSubmissionRepo');
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.submission");
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
        it("fetchSubmissionById should be defined", function () {
            expect(repo.fetchSubmissionById).toBeDefined();
            expect(typeof repo.fetchSubmissionById).toEqual("function");
        });

        it("findPaginatedActionLogsById should be defined", function () {
            expect(repo.findPaginatedActionLogsById).toBeDefined();
            expect(typeof repo.findPaginatedActionLogsById).toEqual("function");
        });

        it("listenForChanges should be defined", function () {
            expect(repo.listenForChanges).toBeDefined();
            expect(typeof repo.listenForChanges).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("fetchSubmissionById should return a submission", function () {
            repo.fetchSubmissionById(1);
            scope.$digest();

            // TODO
        });

        it("findPaginatedActionLogsById should return action logs", function () {
            var mockOrderBy = { order: 'id' };
            var mockPayload = {
                PageImpl: {}
            };

            WsApi.mockFetchResponse({ type: "payload", payload: mockPayload });

            repo.findPaginatedActionLogsById(1, mockOrderBy, 0, 10);
            scope.$digest();

            // TODO
        });

        it("listenForChanges should return a promise", function () {
            var response;

            response = repo.listenForChanges();
            scope.$digest();

            // TODO
        });
    });

    describe("Does the scope initialize as expected", function () {
        it("Listen on '/private/queue/submissions' should work as expected", function () {
            var submission = new mockSubmission(q);

            // FIXME: find a way to inject WsApi.
            WsApi.listen = function(path) {
                var payload = {
                    Submission: submission
                };
                return notifyPromise(timeout, q.defer(), payload);
            };

            initializeRepo();
            scope.$digest();
            timeout.flush();

            // TODO
        });
    });
});
