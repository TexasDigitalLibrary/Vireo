describe('model: Packager', function () {
    var rootScope, scope, WsApi, Packager;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _Packager_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            Packager = _Packager_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(Packager).toBeDefined();
        });
    });
});
