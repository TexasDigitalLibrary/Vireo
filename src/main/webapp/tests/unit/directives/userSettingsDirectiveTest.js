describe('directive: usersettings', function () {
    var compile, directive, httpBackend, rootScope, scope, templateCache, window, UserSettings, MockedUser;

    var initializeVariables = function () {
        inject(function ($q, $compile, $httpBackend, $rootScope, $templateCache, $window) {
            compile = $compile;
            httpBackend = $httpBackend;
            rootScope = $rootScope;
            templateCache = $templateCache;
            window = $window;
            MockedUser = new mockUser($q);
        });
    };

    var initializeDirective = function () {
        inject(function (AbstractAppModel, _UserSettings_) {
            scope = rootScope.$new();

            var element = '<usersettings';
            var directiveProperties = {

            };

            angular.forEach(directiveProperties, function(value, key) {
                element += " " + key + "=\"" + value + "\"";
            });

            element += '></usersettings>';

            directive = compile(element)(scope);

            scope.$digest();
        });
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

        installPromiseMatchers();
        initializeVariables();
    });

    describe('Does the directive compile', function () {
        it('should be defined', function () {
            // initializeDirective();
            // expect(directive).toBeDefined();
        });
    });

});
