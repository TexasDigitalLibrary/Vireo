describe("service: managedConfigurationRepo", function () {
    var q, repo, rootScope, mockedRepo, mockedUser, scope, User, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            mockedUser = mockParameterModel(q, mockUser);

            WsApi = _WsApi_;
        });
    };

    var initializeRepo = function(settings) {
        inject(function ($injector, ManagedConfigurationRepo, _ManagedConfiguration_) {
            scope = rootScope.$new();

            repo = $injector.get('ManagedConfigurationRepo');

            // FIXME: find a way to get something like `angular.extend(new mockRepo("ManagedConfigurationRepo", q), $injector.get("ManagedConfigurationRepo")())` or `repo = ManagedConfigurationRepo` to work.
            /*
            mockedRepo = new mockRepo("ManagedConfigurationRepo", q);
            repo = $injector.get("ManagedConfigurationRepo");

            repo.get = mockedRepo.get;
            repo.getAll = mockedRepo.getAll;
            repo.listen = mockedRepo.listen;
            repo.ready = mockedRepo.ready;
            */
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.managedConfiguration");
        module("mock.user", function($provide) {
            User = function() {
                return mockedUser;
            };
            $provide.value("User", User);
        });
        module("mock.wsApi");

        // TODO: find a way to get this to work with current design.
        // see: repo.ready() tests.
        /*
        module("mock.wsApi", function($provide) {
            WsApi = {};
            $provide.value("WsApi", WsApi);
        });
        */


        initializeVariables();
        initializeRepo();
    });

    describe("Is the repo defined", function () {
        it("should be defined", function () {
            expect(repo).toBeDefined();
        });
    });

    describe("Are the repo methods defined", function () {
        it("findByTypeAndName should be defined", function () {
            expect(repo.findByTypeAndName).toBeDefined();
            expect(typeof repo.findByTypeAndName).toEqual("function");
        });
        it("getAllShibbolethConfigurations should be defined", function () {
            expect(repo.getAllShibbolethConfigurations).toBeDefined();
            expect(typeof repo.getAllShibbolethConfigurations).toEqual("function");
        });
        it("ready should be defined", function () {
            expect(repo.ready).toBeDefined();
            expect(typeof repo.ready).toEqual("function");
        });
        it("reset should be defined", function () {
            expect(repo.reset).toBeDefined();
            expect(typeof repo.reset).toEqual("function");
        });
    });

    describe("Do the repo methods work as expected", function () {
        it("findByTypeAndName should return a configuration", function () {
            var response;

            response = repo.findByTypeAndName("type", "name");

            // TODO
        });
        it("getAllShibbolethConfigurations should return a configuration", function () {
            var response;

            response = repo.getAllShibbolethConfigurations();

            // TODO
        });
        it("ready should perform additional actions", function () {
            WsApi.listen = function() {
                // FIXME: this is not called because the mocked WsApi is not atually used.
                // `repo = ManagedConfigurationRepo;` above is the culprit and some sort of instantiation needs to take place.
                return payloadPromise(q.defer());
            };

            repo.ready();
            scope.$digest();

            // TODO
        });
        it("reset should reset the repo", function () {
            var model = new mockManagedConfiguration(q);

            repo.reset(model);
            scope.$digest();

            // TODO
        });
    });
});
