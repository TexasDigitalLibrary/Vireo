describe('directive: keyboardActivate', function () {
    var compile, directive, httpBackend, rootScope, scope, templateCache, window, MockedUser;

    var ENTER = 13;
    var SPACE = 32;

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

    var initializeDirective = function (markup) {
        inject(function () {
            scope = rootScope.$new();
            scope.activate = jasmine.createSpy('activate');

            directive = compile(markup || '<span role="button" tabindex="0" keyboard-activate ng-click="activate()"></span>')(scope);

            scope.$digest();
        });
    };

    // The event object is extended onto whatever event the element library
    // hands the handler, so a spy here is called in place of the real
    // preventDefault whether jqLite or jQuery is in play.
    var keydown = function (element, which) {
        var event = {
            type: 'keydown',
            which: which,
            preventDefault: jasmine.createSpy('preventDefault')
        };

        element.triggerHandler(event);

        return event;
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

    describe('Is the element activated by the keyboard', function () {
        it('should activate on enter', function () {
            initializeDirective();

            keydown(directive, ENTER);

            expect(scope.activate).toHaveBeenCalled();
        });

        it('should activate on space', function () {
            initializeDirective();

            keydown(directive, SPACE);

            expect(scope.activate).toHaveBeenCalled();
        });

        it('should not activate on any other key', function () {
            initializeDirective();

            keydown(directive, 65);

            expect(scope.activate).not.toHaveBeenCalled();
        });

        it('should prevent the default action when activating', function () {
            initializeDirective();

            var event = keydown(directive, SPACE);

            expect(event.preventDefault).toHaveBeenCalled();
        });

        it('should leave the default action alone for any other key', function () {
            initializeDirective();

            var event = keydown(directive, 65);

            expect(event.preventDefault).not.toHaveBeenCalled();
        });
    });

    describe('Is an ancestor handler reached', function () {
        it('should activate a handler on a parent element', function () {
            initializeDirective('<div ng-click="activate()"><span role="button" tabindex="0" keyboard-activate></span></div>');

            keydown(angular.element(directive[0].querySelector('span')), ENTER);

            expect(scope.activate).toHaveBeenCalled();
        });
    });

    describe('Are natively activated elements left alone', function () {
        it('should not bind to a button, which the browser already activates', function () {
            initializeDirective('<button keyboard-activate ng-click="activate()"></button>');

            keydown(directive, ENTER);

            expect(scope.activate).not.toHaveBeenCalled();
        });

        it('should not bind to an anchor, which the browser already activates', function () {
            initializeDirective('<a href="" keyboard-activate ng-click="activate()"></a>');

            keydown(directive, ENTER);

            expect(scope.activate).not.toHaveBeenCalled();
        });
    });
});
