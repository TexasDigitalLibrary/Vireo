describe('service: degreeRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, DegreeRepo) {
            scope = rootScope.$new();

            repo = DegreeRepo;
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        initializeVariables();
        initializeRepo();
    });

    describe('Is the repo defined', function () {
        it('should be defined', function () {
            expect(repo).toBeDefined();
        });
    });
});
