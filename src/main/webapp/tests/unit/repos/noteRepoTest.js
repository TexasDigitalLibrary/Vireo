describe('service: noteRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, NoteRepo) {
            scope = rootScope.$new();

            repo = NoteRepo;
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
