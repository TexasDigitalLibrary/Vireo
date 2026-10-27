describe('factory: DragAndDropListenerFactory', function () {
    var q, rootScope, DragAndDropListenerFactory, MockedUser, ModalService;

    var reorderResponse = function (status) {
        return {
            body: angular.toJson({
                meta: {
                    status: status
                }
            })
        };
    };

    var buildControls = function (overrides) {
        var config = {
            trashId: 'test-trash',
            dragging: false,
            select: function (index) {
                config.selected = index;
            },
            model: [],
            confirm: '#testConfirmRemoveModal',
            reorder: function (src, dest) {
                config.reordered = {
                    src: src,
                    dest: dest
                };
                return q.when(reorderResponse('SUCCESS'));
            },
            container: '#test-container'
        };

        angular.extend(config, overrides || {});

        return {
            config: config,
            controls: DragAndDropListenerFactory.buildDragControls(config)
        };
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
        module('mock.modalService');

        inject(function ($q, $rootScope, _DragAndDropListenerFactory_, _ModalService_) {
            q = $q;
            rootScope = $rootScope;
            MockedUser = new mockUser($q);
            DragAndDropListenerFactory = _DragAndDropListenerFactory_;
            ModalService = _ModalService_;
        });
    });

    describe('Keyboard alternatives to dragging', function () {

        it('canReorder should report whether the list has a reorder end point', function () {
            expect(buildControls().controls.canReorder()).toBe(true);
            expect(buildControls({ reorder: undefined }).controls.canReorder()).toBe(false);
        });

        it('canRemove should report whether the list has a confirmation modal', function () {
            expect(buildControls().controls.canRemove()).toBe(true);
            expect(buildControls({ confirm: undefined }).controls.canRemove()).toBe(false);
            expect(buildControls({ select: undefined }).controls.canRemove()).toBe(false);
        });

        it('removeAt should select the item and open the same modal the trash can opens', function () {
            var built = buildControls();

            spyOn(ModalService, 'openModal');

            built.controls.removeAt(2);

            expect(built.config.selected).toBe(2);
            expect(ModalService.openModal).toHaveBeenCalledWith('#testConfirmRemoveModal');
        });

        it('removeAt should do nothing when the list cannot be removed from', function () {
            var built = buildControls({ confirm: undefined });

            spyOn(ModalService, 'openModal');

            built.controls.removeAt(0);

            expect(ModalService.openModal).not.toHaveBeenCalled();
        });

        it('moveItem should reposition the item and send one based positions', function () {
            var list = ['a', 'b', 'c'];
            var built = buildControls({ model: list });

            built.controls.moveItem(list, 2, 1);
            rootScope.$digest();

            expect(list).toEqual(['a', 'c', 'b']);
            expect(built.config.reordered).toEqual({ src: 3, dest: 2 });
        });

        it('moveItem should put the item back when the server rejects the reorder', function () {
            var list = ['a', 'b', 'c'];
            var built = buildControls({
                model: list,
                reorder: function () {
                    return q.when(reorderResponse('ERROR'));
                }
            });

            built.controls.moveItem(list, 0, 1);
            rootScope.$digest();

            expect(list).toEqual(['a', 'b', 'c']);
        });

        it('moveItem should ignore moves that fall outside the list', function () {
            var list = ['a', 'b'];
            var built = buildControls({ model: list });

            built.controls.moveItem(list, 0, -1);
            built.controls.moveItem(list, 1, 2);
            rootScope.$digest();

            expect(list).toEqual(['a', 'b']);
            expect(built.config.reordered).toBeUndefined();
        });

        it('moveItem should do nothing when the list cannot be reordered', function () {
            var list = ['a', 'b'];
            var built = buildControls({ model: list, reorder: undefined });

            built.controls.moveItem(list, 0, 1);
            rootScope.$digest();

            expect(list).toEqual(['a', 'b']);
        });
    });
});
