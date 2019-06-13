describe('service: managerSubmissionListColumnRepo', function () {
    var q, repo, rootScope, mockedRepo, scope, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, ManagerSubmissionListColumnRepo) {
            scope = rootScope.$new();

            repo = ManagerSubmissionListColumnRepo;
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
        it('resetSubmissionListColumns should be defined', function () {
            expect(repo.resetSubmissionListColumns).toBeDefined();
            expect(typeof repo.resetSubmissionListColumns).toEqual("function");
        });
        it('submissionListPageSize should be defined', function () {
            expect(repo.submissionListPageSize).toBeDefined();
            expect(typeof repo.submissionListPageSize).toEqual("function");
        });
        it('updateSubmissionListColumns should be defined', function () {
            expect(repo.updateSubmissionListColumns).toBeDefined();
            expect(typeof repo.updateSubmissionListColumns).toEqual("function");
        });
    });

    describe('Do the repo methods work as expected', function () {
        it('resetSubmissionListColumns should reset the columns', function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.resetSubmissionListColumns([]);
            scope.$digest();

            // TODO
        });
        it('submissionListPageSize should return a number', function () {
            var response;

            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            response = repo.submissionListPageSize([]);
            scope.$digest();

            // TODO
        });
        it('updateSubmissionListColumns should update the columns', function () {
            WsApi.fetch = function() {
                return payloadPromise(q.defer());
            };

            repo.updateSubmissionListColumns([]);
            scope.$digest();

            // TODO
        });
    });
});
