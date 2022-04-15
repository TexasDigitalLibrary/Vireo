describe("service: embargoRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope

            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, EmbargoRepo) {
            scope = rootScope.$new();

            repo = $injector.get('EmbargoRepo');
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.user", function($provide) {
            User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
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
        it("activate should be defined", function () {
            expect(repo.activate).toBeDefined();
            expect(typeof repo.activate).toEqual("function");
        });
        it("deactivate should be defined", function () {
            expect(repo.deactivate).toBeDefined();
            expect(typeof repo.deactivate).toEqual("function");
        });
        it("reorder should be defined", function () {
            expect(repo.reorder).toBeDefined();
            expect(typeof repo.reorder).toEqual("function");
        });
        it("sort should be defined", function () {
            expect(repo.sort).toBeDefined();
            expect(typeof repo.sort).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("activate should activate an embargo", function () {
            var embargo = new mockEmbargo(q);
            var embargoData = dataEmbargo1;
            embargo.isActive = false;
            embargoData.isActive = true;

            WsApi.mockFetchResponse({ type: "payload", payload: embargoData });
            repo.activate(embargo);
            scope.$digest();

            WsApi.mockFetchResponse({ type: "payload", messageStatus: "INVALID" });
            repo.activate(embargo);
            scope.$digest();
        });
        it("deactivate should deactivate an embargo", function () {
            var embargo = new mockEmbargo(q);
            var embargoData = dataEmbargo1;
            embargo.isActive = false;
            embargoData.isActive = true;

            WsApi.mockFetchResponse({ type: "payload", payload: embargoData });
            repo.deactivate(embargo);
            scope.$digest();

            WsApi.mockFetchResponse({ type: "payload", messageStatus: "INVALID" });
            repo.deactivate(embargo);
            scope.$digest();
        });
        it("reorder should re-order the repo", function () {
            repo.reorder("guarantor", "src", "dst");
            scope.$digest();

            WsApi.mockFetchResponse({ type: "payload", messageStatus: "INVALID" });
            repo.reorder("guarantor", "src", "dst")
            scope.$digest();
        });
        it("sort should sort the repo", function () {
            repo.sort("guarantor", "facet");
            scope.$digest();

            WsApi.mockFetchResponse({ type: "payload", messageStatus: "INVALID" });
            repo.sort("guarantor", "facet");
            scope.$digest();
        });
    });
});
