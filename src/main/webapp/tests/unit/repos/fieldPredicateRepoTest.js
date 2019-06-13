describe('service: fieldPredicateRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, FieldPredicateRepo) {
            scope = rootScope.$new();

            repo = FieldPredicateRepo;
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
        it('findByValue should be defined', function () {
            expect(repo.findByValue).toBeDefined();
            expect(typeof repo.findByValue).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('findByValue should return a field predicate', function () {
            repo.findByValue("mock value");
            scope.$digest();

            // TODO
        });
    });
});
