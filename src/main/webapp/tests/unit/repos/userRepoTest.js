describe("service: userRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, UserRepo) {
            scope = rootScope.$new();

            mockedUser = mockParameterModel(q, mockUser);
            mockedRepo = new mockRepo("UserRepo", q);

            repo = $injector.get('UserRepo');
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
        it("getAssignableUsers should be defined", function () {
            expect(repo.getAssignableUsers).toBeDefined();
            expect(typeof repo.getAssignableUsers).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("getAssignableUsers should return an array", function () {
            var response;

            var data1 = mockedRepo.mockCopy(dataUser1);
            var data2 = mockedRepo.mockCopy(dataUser4);
            var data3 = mockedRepo.mockCopy(dataUser5);

            var payload = {
                "ArrayList<User>": [
                    data1,
                    data2,
                    data3
                ]
            };

            WsApi.mockFetchResponse({ type: "payload", payload: payload });

            response = repo.getAssignableUsers();
            scope.$digest();

            expect(response.length).toBe(3);
        });
        it("getUnassignableUsers should return an array", function () {
            var response;

            var data1 = mockedRepo.mockCopy(dataUser3);
            var data2 = mockedRepo.mockCopy(dataUser6);

            var payload = {
                "ArrayList<User>": [
                    data1,
                    data2
                ]
            };

            WsApi.mockFetchResponse({ type: "payload", payload: payload });

            response = repo.getUnassignableUsers();
            scope.$digest();

            expect(response.length).toBe(2);
        });
    });
});
