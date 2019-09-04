describe('directive: reviewsubmissionsfields', function () {
    var compile, defaults, httpBackend, rootScope, scope, templateCache, window;

    var initializeVariables = function(settings) {
        inject(function ($compile, $httpBackend, $rootScope, $templateCache, $window) {
            compile = $compile;
            httpBackend = $httpBackend;
            rootScope = $rootScope;
            templateCache = $templateCache;
            window = $window;

            // TODO: implement a stompClient mock object.
            window.stompClient = {
                subscribe: function() {}
            };
        });
    };

    var initializeDirective = function(settings) {
        inject(function (AbstractAppModel) {
            scope = rootScope.$new();

            defaults = {
                submission: "",
                filterOptional: "",
                hideLinks: "",
                setActiveStep: "",
                showVocabularyWord: "",
                validate: ""
            };

            httpBackend.whenGET('views/directives/reviewSubmissionFields.html').respond('<div></div>');
        });
    };

    var createDirective = function(properties) {
        var directive = '<reviewsubmissionsfields';
        var directiveProperties = defaults;

        if (properties) {
            angular.forEach(properties, function(value, key) {
                directiveProperties[key] = value;
            });
        }

        angular.forEach(directiveProperties, function(value, key) {
            directive += " " + key + "=\"" + value + "\"";
        });

        directive += '></reviewsubmissionsfields>';

        return angular.element(directive);
    };

    beforeEach(function() {
        module('core');
        module('vireo');

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
