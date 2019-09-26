describe('directive: draganddroplist', function () {
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
                dragging: "",
                scopeValue: "",
                listeners: "",
                edit: "",
                toString: "",
                itemView: "",
                sortLabel: "",
                sortColumn: "",
                sortAction: "",
                sortActionSort: "",
                sortMethod: "",
                textFilter: "",
                selectedFilter: "",
                textFilterValue: "",
                passiveFilter: "",
                isEditable: ""
            };

            httpBackend.whenGET('views/directives/dragAndDropList.html').respond('<div></div>');
        });
    };

    var createDirective = function(properties) {
        var directive = '<draganddroplist';
        var directiveProperties = defaults;

        if (properties) {
            angular.forEach(properties, function(value, key) {
                directiveProperties[key] = value;
            });
        }

        angular.forEach(directiveProperties, function(value, key) {
            directive += " " + key + "=\"" + value + "\"";
        });

        directive += '></draganddroplist>';

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
