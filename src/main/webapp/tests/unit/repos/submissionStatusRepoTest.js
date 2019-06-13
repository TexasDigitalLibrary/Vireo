describe('service: submissionStatusRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, SubmissionStatusRepo) {
            scope = rootScope.$new();

            repo = SubmissionStatusRepo;
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
        it('findById should be defined', function () {
            expect(repo.findById).toBeDefined();
            expect(typeof repo.findById).toEqual("function");
        });
        it('findByName should be defined', function () {
            expect(repo.findByName).toBeDefined();
            expect(typeof repo.findByName).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('findById should return a submission status', function () {
            var response;

            response = repo.findById();
            scope.$digest();

            // TODO
        });
        it('findByName should return a submission status', function () {
            var response;

            response = repo.findByName("mock");
            scope.$digest();

            // TODO
        });
    });
});
