describe('service: userRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

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

            repo = UserRepo;
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.user');
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
        it('getAllByRole should be defined', function () {
            expect(repo.getAllByRole).toBeDefined();
            expect(typeof repo.getAllByRole).toEqual("function");
        });
        it('getAssignableUsers should be defined', function () {
            expect(repo.getAssignableUsers).toBeDefined();
            expect(typeof repo.getAssignableUsers).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('getAllByRole should return an array', function () {
            var response;
            var roles = [ "mock" ];

            response = repo.getAllByRole(roles);

            // TODO
        });
        it('getAssignableUsers should return an array', function () {
            var response;
            var roles = [ "mock" ];

            response = repo.getAssignableUsers(roles);
            scope.$digest();

            // TODO
        });
    });
});
