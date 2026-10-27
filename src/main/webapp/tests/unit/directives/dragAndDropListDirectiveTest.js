describe('directive: draganddroplist', function () {
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

            var element = '<draganddroplist';
            var directiveProperties = {
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

            angular.forEach(directiveProperties, function(value, key) {
                element += " " + key + "=\"" + value + "\"";
            });

            element += '></draganddroplist>';

            httpBackend.whenGET('views/directives/dragAndDropList.html').respond('<div></div>');

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

    describe('Keyboard alternatives to dragging', function () {
        var isolate, listeners;

        beforeEach(function () {
            inject(function () {
                scope = rootScope.$new();

                listeners = {
                    canReorder: function () {
                        return true;
                    },
                    canRemove: function () {
                        return true;
                    },
                    moveItem: jasmine.createSpy('moveItem'),
                    removeAt: jasmine.createSpy('removeAt')
                };

                scope.listeners = listeners;
                scope.items = [{ name: 'alpha' }, { name: 'beta' }];

                httpBackend.whenGET('views/directives/dragAndDropList.html').respond('<div></div>');

                directive = compile('<draganddroplist scope-value="items" listeners="listeners" properties=\'["name"]\'></draganddroplist>')(scope);

                scope.$digest();
                httpBackend.flush();

                isolate = directive.isolateScope();
            });
        });

        it('should report what the list supports', function () {
            expect(isolate.canReorder()).toBe(true);
            expect(isolate.canRemove()).toBe(true);

            listeners.canReorder = function () {
                return false;
            };
            listeners.canRemove = function () {
                return false;
            };

            expect(isolate.canReorder()).toBe(false);
            expect(isolate.canRemove()).toBe(false);
        });

        it('should report no keyboard support when the listeners are missing', function () {
            isolate.listeners = undefined;

            expect(isolate.canReorder()).toBe(false);
            expect(isolate.canRemove()).toBe(false);
        });

        it('should not offer removal when the list opts out', function () {
            isolate.allowRemove = false;

            expect(isolate.canRemove()).toBe(false);
        });

        it('moveUp and moveDown should hand the neighbouring position to the listeners', function () {
            isolate.moveUp(1);
            expect(listeners.moveItem).toHaveBeenCalledWith(isolate.scopeValue, 1, 0);

            isolate.moveDown(0);
            expect(listeners.moveItem).toHaveBeenCalledWith(isolate.scopeValue, 0, 1);
        });

        it('removeItem should hand the position to the listeners', function () {
            isolate.removeItem(1);

            expect(listeners.removeAt).toHaveBeenCalledWith(1);
        });

        it('itemLabel should name the item from its displayed properties', function () {
            expect(isolate.itemLabel(isolate.scopeValue[0])).toEqual('alpha');
        });

        it('itemLabel should fall back to the name of the item', function () {
            isolate.properties = ['System', 'Name'];

            expect(isolate.itemLabel({ name: 'Indefinite' })).toEqual('Indefinite');
        });

        it('itemLabel should describe an unnamed item', function () {
            isolate.properties = ['name'];

            expect(isolate.itemLabel({})).toEqual('this item');
            expect(isolate.itemLabel(undefined)).toEqual('this item');
        });
    });
});
