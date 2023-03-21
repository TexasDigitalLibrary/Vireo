describe('directive: submissionInfo', function () {
    var compile, directive, httpBackend, rootScope, scope, templateCache, window, MockedUser;

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
        inject(function () {
            scope = rootScope.$new();

            var element = '<submission-info';
            var directiveProperties = {
                submission: "",
                fieldProfile: "",
                fields: "",
                label: "",
                showVocabularyWord: "",
                stacked: "",
                type: ""
            };

            angular.forEach(directiveProperties, function(value, key) {
                element += " " + key + "=\"" + value + "\"";
            });

            element += '></submission-info>';

            httpBackend.whenGET('views/directives/submissionInfo.html').respond('<div></div>');

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

        installPromiseMatchers();
        initializeVariables();
    });

    describe('Does the directive compile', function () {
        it('should be defined', function () {
            initializeDirective();
            expect(directive).toBeDefined();
        });
    });
});
