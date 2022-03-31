describe("service: managerFilterColumnRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, ManagerFilterColumnRepo) {
            scope = rootScope.$new();

            repo = $injector.get('ManagerFilterColumnRepo');
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
        it("updateFilterColumns should be defined", function () {
            expect(repo.updateFilterColumns).toBeDefined();
            expect(typeof repo.updateFilterColumns).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("updateFilterColumns should update the columns", function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.updateFilterColumns([]);
            scope.$digest();

            // TODO
        });
    });
});
