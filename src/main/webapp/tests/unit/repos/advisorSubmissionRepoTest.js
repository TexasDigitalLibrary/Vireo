describe("service: advisorSubmissionRepo", function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, AdvisorSubmissionRepo) {
            scope = rootScope.$new();

            repo = AdvisorSubmissionRepo;
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
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
        it("fetchSubmissionByHash should be defined", function () {
            expect(repo.fetchSubmissionByHash).toBeDefined();
            expect(typeof repo.fetchSubmissionByHash).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("fetchSubmissionByHash should return a submission", function () {
            repo.fetchSubmissionByHash("mock hash");
            scope.$digest();

            // TODO
        });
    });
});
