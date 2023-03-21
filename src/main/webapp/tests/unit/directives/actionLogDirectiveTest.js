describe('directive: actionLog', function () {
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
                submission: "",
                public: ""
            };
        });
    };

    var createDirective = function(properties) {
        var directive = '<action-log';
        var directiveProperties = defaults;

        if (properties) {
            angular.forEach(properties, function(value, key) {
                directiveProperties[key] = value;
            });
        }

        angular.forEach(directiveProperties, function(value, key) {
            directive += " " + key + "=\"" + value + "\"";
        });

        directive += '></action-log>';

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
