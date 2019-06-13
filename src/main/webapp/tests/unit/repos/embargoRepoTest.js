describe('service: embargoRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, EmbargoRepo) {
            scope = rootScope.$new();

            repo = EmbargoRepo;
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
        it('reorder should be defined', function () {
            expect(repo.reorder).toBeDefined();
            expect(typeof repo.reorder).toEqual("function");
        });
        it('sort should be defined', function () {
            expect(repo.sort).toBeDefined();
            expect(typeof repo.sort).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('reorder should re-order the repo', function () {
            repo.reorder("guarantor", "src", "dst");
            scope.$digest();

            // TODO
        });
        it('sort should sort the repo', function () {
            repo.sort("guarantor", "facet");
            scope.$digest();

            // TODO
        });
    });
});
