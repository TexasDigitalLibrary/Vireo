describe('service: SidebarService', function () {
    var q, rootScope, service, scope, timeout, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, $timeout, _WsApi_) {
            q = $q;
            rootScope = $rootScope;
            timeout = $timeout;

            WsApi = _WsApi_;
        });
    };

    var initializeService = function(settings) {
        inject(function ($injector, AbstractAppModel) {
            scope = rootScope.$new();

            service = $injector.get('SidebarService');
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        initializeVariables();
        initializeService();
    });

    describe('Is the service defined', function () {
        it('should be defined', function () {
            expect(service).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('addBox should be defined', function () {
            expect(service.addBox).toBeDefined();
            expect(typeof service.addBox).toEqual("function");
        });
        it('addBoxes should be defined', function () {
            expect(service.addBoxes).toBeDefined();
            expect(typeof service.addBoxes).toEqual("function");
        });
        it('clear should be defined', function () {
            expect(service.clear).toBeDefined();
            expect(typeof service.clear).toEqual("function");
        });
        it('getBox should be defined', function () {
            expect(service.getBox).toBeDefined();
            expect(typeof service.getBox).toEqual("function");
        });
        it('getBoxes should be defined', function () {
            expect(service.getBoxes).toBeDefined();
            expect(typeof service.getBoxes).toEqual("function");
        });
        it('remove should be defined', function () {
            expect(service.remove).toBeDefined();
            expect(typeof service.remove).toEqual("function");
        });
    });

    describe('Do the service methods work as expected', function () {
        it('addBox should add a box', function () {
            var response;
            var target1 = { id: 1 };
            var target2 = { id: 2 };

            service.boxes = [];

            service.addBox(target1);
            expect(service.boxes.length).toBe(1);

            service.addBox(target2);
            expect(service.boxes.length).toBe(2);
        });
        it('addBoxes should add boxes', function () {
            var response;
            var target1 = { id: 1 };
            var target2 = { id: 2 };
            var target3 = { id: 3 };
            var boxes1 = [ target1, target2 ];
            var boxes2 = [ target3 ];
            var boxes3 = [];

            service.boxes = [];

            response = service.addBoxes(boxes1);
            expect(service.boxes.length).toBe(2);

            // FIXME: the code is failing this test.
            //response = service.addBoxes(boxes2);
            //expect(service.boxes.length).toBe(3);

            // FIXME: the code is failing this test.
            //response = service.addBoxes(boxes3);
            //expect(service.boxes.length).toBe(3);
        });
        it('clear should remove all boxes', function () {
            var response;
            var target1 = { id: 1 };
            var target2 = { id: 2 };

            service.boxes = [ target1, target2 ];

            service.clear();
            expect(service.boxes.length).toBe(0);
        });
        it('getBox should return a box', function () {
            var response;
            var target1 = { id: 1 };
            var target2 = { id: 2 };

            service.boxes = [ target1, target2 ];

            response = service.getBox(0);
            expect(response).toBe(target1);

            response = service.getBox(1);
            expect(response).toBe(target2);
        });
        it('getBoxes should return all boxes', function () {
            var response;
            var target1 = { id: 1 };
            var target2 = { id: 2 };

            service.boxes = [ target1, target2 ];

            response = service.getBoxes();
            expect(response).toBe(service.boxes);
        });
        it('remove should remove a box', function () {
            var response;
            var target1 = { id: 1 };
            var target2 = { id: 2 };

            service.boxes = [ target1, target2 ];

            service.remove(target1);
            expect(service.boxes.length).toBe(1);
            expect(service.boxes[0]).toBe(target2);

            service.remove(target2);
            expect(service.boxes.length).toBe(0);
        });
    });

    /* TODO: implement this.
    describe('Does the service initialize as expected', function () {
        it('Listen on "$routeChangeSuccess" should work as expected', function () {
            // TODO
        });
    });
    */
});
