describe('directive: keyboardClickable', function () {
    var compile, rootScope, scope, MockedUser;

    var ENTER = 13;
    var SPACE = 32;

    var build = function (markup) {
        scope = rootScope.$new();
        scope.activated = 0;
        scope.activate = function () {
            scope.activated++;
        };

        var element = compile(markup)(scope);

        scope.$digest();

        return element;
    };

    var press = function (element, which) {
        var event = angular.element.Event('keydown');
        event.which = which;
        element.triggerHandler(event);
        return event;
    };

    beforeEach(function () {
        module('core');
        module('vireo');
        module('mock.user', function ($provide) {
            var User = function () {
                return MockedUser;
            };
            $provide.value('User', User);
        });
        module('mock.userService');

        inject(function ($compile, $q, $rootScope) {
            compile = $compile;
            rootScope = $rootScope;
            MockedUser = new mockUser($q);
        });
    });

    it('should give the element a tab stop and a button role', function () {
        var element = build('<span ng-click="activate()" keyboard-clickable></span>');

        expect(element.attr('tabindex')).toEqual('0');
        expect(element.attr('role')).toEqual('button');
    });

    it('should leave an existing tabindex and role alone', function () {
        var element = build('<span ng-click="activate()" keyboard-clickable tabindex="-1" role="link"></span>');

        expect(element.attr('tabindex')).toEqual('-1');
        expect(element.attr('role')).toEqual('link');
    });

    it('should activate the element on Enter and on Space', function () {
        var element = build('<span ng-click="activate()" keyboard-clickable></span>');

        press(element, ENTER);
        expect(scope.activated).toBe(1);

        press(element, SPACE);
        expect(scope.activated).toBe(2);
    });

    it('should ignore other keys', function () {
        var element = build('<span ng-click="activate()" keyboard-clickable></span>');

        press(element, 65);

        expect(scope.activated).toBe(0);
    });

    it('should not activate a disabled element', function () {
        var element = build('<span ng-click="activate()" keyboard-clickable class="disabled"></span>');

        press(element, ENTER);

        expect(scope.activated).toBe(0);
    });
});
