describe('model: FieldProfile', function () {
    var rootScope, scope, WsApi, FieldProfile;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.wsApi');

        inject(function ($rootScope, _WsApi_, _FieldProfile_) {
            rootScope = $rootScope;
            scope = $rootScope.$new();

            WsApi = _WsApi_;

            FieldProfile = _FieldProfile_;
        });
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(FieldProfile).toBeDefined();
        });
    });
});
