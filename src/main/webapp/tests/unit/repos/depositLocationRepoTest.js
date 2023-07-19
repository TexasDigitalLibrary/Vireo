describe("service: depositLocationRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _DepositLocation_, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, DepositLocationRepo) {
            scope = rootScope.$new();

            repo = $injector.get('DepositLocationRepo');
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
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

    describe('Are the repo methods defined', function () {
        it('testConnection should be defined', function () {
            expect(repo.testConnection).toBeDefined();
            expect(typeof repo.testConnection).toEqual("function");
        });
    });

    describe('Are the repo methods working as expected', function () {
        it('testConnection should call WsApi', function () {
            spyOn(WsApi, 'fetch');

            repo.testConnection();
            scope.$digest();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
    });
});
