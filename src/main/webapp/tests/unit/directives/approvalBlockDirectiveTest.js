describe('directive: approvalblock', function () {
    var compile, defaults, httpBackend, rootScope, scope, templateCache, window, MockedUser;

    var initializeVariables = function(settings) {
        inject(function ($q, $compile, $httpBackend, $rootScope, $templateCache, $window) {
            compile = $compile;
            httpBackend = $httpBackend;
            rootScope = $rootScope;
            templateCache = $templateCache;
            window = $window;
            MockedUser = new mockUser($q);
        });
    };

    var initializeDirective = function(settings) {
        inject(function (AbstractAppModel) {
            scope = rootScope.$new();

            defaults = {
                type: "",
                approvalProxy: "",
                status: "",
                statusDate: ""
            };

            httpBackend.whenGET('views/directives/approvalBlock.html').respond('<div></div>');
        });
    };

    var createDirective = function(properties) {
        var directive = '<approvalblock';
        var directiveProperties = defaults;

        if (properties) {
            angular.forEach(properties, function(value, key) {
                directiveProperties[key] = value;
            });
        }

        angular.forEach(directiveProperties, function(value, key) {
            directive += " " + key + "=\"" + value + "\"";
        });

        directive += '></approvalblock>';

        return angular.element(directive);
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module("mock.user", function ($provide) {
            var User = function () {
              return MockedUser;
            };
            $provide.value("User", User);
        });
        module("mock.userService");

        initializeVariables();
        initializeDirective();
    });

    describe('Does the directive compile', function () {
        it('should be defined', function () {
            var directive = createDirective();
            var compiled = compile(directive)(scope);
            scope.$digest();

            expect(compiled).toBeDefined();
        });
    });
});
