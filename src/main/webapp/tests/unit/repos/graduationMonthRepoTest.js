describe("service: graduationMonthRepo", function () {
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
        inject(function ($injector, GraduationMonthRepo) {
            scope = rootScope.$new();

            repo = $injector.get('GraduationMonthRepo');
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
});
