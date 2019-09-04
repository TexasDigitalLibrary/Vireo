describe('directive: displayname', function () {
    var compile, defaults, httpBackend, rootScope, scope, templateCache, window, UserSettings;

    var initializeVariables = function(settings) {
        inject(function ($compile, $httpBackend, $rootScope, $templateCache, $window) {
            compile = $compile;
            httpBackend = $httpBackend;
            rootScope = $rootScope;
            templateCache = $templateCache;
            window = $window;
        });
    };

    var initializeDirective = function(settings) {
        inject(function (AbstractAppModel) {
            scope = rootScope.$new();

            defaults = {
            };
        });
    };

    var createDirective = function(properties) {
        var directive = '<displayname';
        var directiveProperties = defaults;

        if (properties) {
            angular.forEach(properties, function(value, key) {
                directiveProperties[key] = value;
            });
        }

        angular.forEach(directiveProperties, function(value, key) {
            directive += " " + key + "=\"" + value + "\"";
        });

        directive += '></displayname>';

        return angular.element(directive);
    };

    beforeEach(function() {
        module('core');
        module('vireo');

        // TODO: implement a settings mock, such that module('mock.UserSettings') can instead be called.
        module(function($provide) {
            UserSettings = function() {
                var object = {};
                object.fetch = function() {};
                object.ready = function(callback) { return callback(); };

                return object;
            }
            $provide.value('UserSettings', UserSettings);
        });

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
