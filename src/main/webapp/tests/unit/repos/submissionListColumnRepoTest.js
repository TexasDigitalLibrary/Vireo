describe('service: submissionListColumnRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, SubmissionListColumnRepo) {
            scope = rootScope.$new();

            repo = SubmissionListColumnRepo;
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
        it('findByTitle should be defined', function () {
            expect(repo.findByTitle).toBeDefined();
            expect(typeof repo.findByTitle).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('findByTitle should return a submission', function () {
            repo.findByTitle("mock title");

            // TODO
        });
    });
});
