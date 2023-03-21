describe("service: abstractAppRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedRepo = new mockRepo("AbstractAppRepo", q);
            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector) {
            scope = rootScope.$new();

            repo = $injector.get("AbstractAppRepo")();

            // FIXME: find a way to get something like `angular.extend(new mockRepo("AbstractAppRepo", q), $injector.get("AbstractAppRepo")())` or `repo = AbstractAppRepo` to work.
            repo.getAll = mockedRepo.getAll;
            repo.listen = mockedRepo.listen;
            repo.ready = mockedRepo.ready;
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.fieldPredicate");
        module("mock.user", function($provide) {
            User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
        module('mock.userService');
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
        it("getAllFiltered should be defined", function () {
            expect(repo.getAllFiltered).toBeDefined();
            expect(typeof repo.getAllFiltered).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("getAllFiltered should return filtered data", function () {
            var fieldPredicate = new mockFieldPredicate(q);

            repo.getAllFiltered();
            scope.$digest();

            // TODO
        });
    });

});
