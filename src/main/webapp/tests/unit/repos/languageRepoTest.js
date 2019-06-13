describe('service: languageRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, LanguageRepo) {
            scope = rootScope.$new();

            repo = LanguageRepo;
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

    describe('Are the repo methods defined', function () {
        it('getProquestLanguageCodes should be defined', function () {
            expect(repo.getProquestLanguageCodes).toBeDefined();
            expect(typeof repo.getProquestLanguageCodes).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('getProquestLanguageCodes should return a field predicate', function () {
            var response;

            response = repo.getProquestLanguageCodes();
            scope.$digest();

            // TODO
        });
    });
});
