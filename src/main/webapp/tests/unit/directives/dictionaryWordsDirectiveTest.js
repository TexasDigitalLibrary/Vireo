describe('directive: dictionaryWords', function () {
    var compile, directive, httpBackend, rootScope, scope, templateCache, window, MockedUser;

    var initializeVariables = function () {
        inject(function ($q, $compile, $httpBackend, $rootScope, $templateCache, $window) {
            compile = $compile;
            httpBackend = $httpBackend;
            rootScope = $rootScope;
            templateCache = $templateCache;
            window = $window;
            MockedUser = new mockUser($q);

            // TODO: implement a stompClient mock object.
            window.stompClient = {
                subscribe: function() {}
            };
        });
    };

    var initializeDirective = function () {
        inject(function () {
            scope = rootScope.$new();

            var element = '<dictionary-words';
            var directiveProperties = {
                fieldValues: "",
                fieldProfile: "",
                showVocabularyWord: ""
            };

            angular.forEach(directiveProperties, function(value, key) {
                element += " " + key + "=\"" + value + "\"";
            });

            element += '></dictionary-words>';

            httpBackend.whenGET('views/directives/dictionaryWords.html').respond('<div></div>');

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
