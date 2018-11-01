describe('model: Note', function () {
    var rootScope, scope, WsApi, Note;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _Note_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            Note = _Note_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Note).toBeDefined();
        });
    });
});
