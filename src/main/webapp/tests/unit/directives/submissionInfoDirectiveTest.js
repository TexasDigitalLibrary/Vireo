describe('directive: submissionInfo', function () {
    var compile, defaults, httpBackend, rootScope, scope, templateCache, window;

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
                submission: "",
                fieldProfile: "",
                fields: "",
                label: "",
                showVocabularyWord: "",
                stacked: "",
                type: ""
            };

            httpBackend.whenGET('views/directives/submissionInfo.html').respond('<div></div>');
        });
    };

    var createDirective = function(properties) {
        var directive = '<submission-info';
        var directiveProperties = defaults;

        if (properties) {
            angular.forEach(properties, function(value, key) {
                directiveProperties[key] = value;
            });
        }

        angular.forEach(directiveProperties, function(value, key) {
            directive += " " + key + "=\"" + value + "\"";
        });

        directive += '></submission-info>';

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
