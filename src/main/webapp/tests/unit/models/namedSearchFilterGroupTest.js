describe('model: NamedSearchFilterGroup', function () {
    var rootScope, scope, WsApi, NamedSearchFilterGroup;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _NamedSearchFilterGroup_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            NamedSearchFilterGroup = _NamedSearchFilterGroup_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(NamedSearchFilterGroup).toBeDefined();
        });
    });
});
