angular.module('mock.dragAndDropListenerFactory', []).factory('DragAndDropListenerFactory', function($q, ModalService) {
    var factory = this;
    var defer;

    factory.listener = {
        'trash': {
            hover: false,
            element: null,
            id: ''
        },
        'select': null,
        'dragging': null,
        'list': [],
        'confirm': {
            'remove': {
                'modal': ''
            }
        },
        'reorder': function(src, dest) {}
    };

    factory.mock = function(toMock) {
        if (typeof toMock === "object") {
            var keys = Object.keys(toMock);
            for (var i in keys) {
                factory[keys[i]] = toMock[keys[i]];
            }
        } else if (toMock === undefined || toMock === null) {
            factory = null;
        }
    };

    factory.listener.setConfirmRemoveModal = function(confirmRemoveModal) {
        factory.listener.confirm.remove.modal = confirmRemoveModal;
    };

    factory.listener.setScopeDragging = function(dragging) {
        factory.listener.dragging = dragging;
    };

    factory.listener.setScopeList = function(list) {
        factory.listener.list = list;
    };

    factory.listener.setScopeReorderFunction = function(reorder) {
        factory.listener.reorder = reorder;
    };

    factory.listener.setScopeSelect = function(select) {
        factory.listener.select = select;
    };

    factory.listener.setScopeTrashId = function(id) {
        factory.listener.trash.id = id;
    };

    factory.buildDragControls = function(drag) {
        if (typeof drag === 'object') {
            factory.listener.setScopeTrashId(drag.trashId);
            factory.listener.setScopeDragging(drag.dragging);
            factory.listener.setScopeSelect(drag.select);
            factory.listener.setScopeList(drag.list);
            factory.listener.setConfirmRemoveModal(drag.confirm);
            factory.listener.setScopeReorderFunction(drag.reorder);
        }
        else {
            console.log('ensure configured');
        }

        var dragControls = {
            dragStart: function(event) {
                factory.listener.dragging = true;
                factory.listener.select(event.source.index);
            },
            dragMove: function(event) {
                if (factory.listener.trash.hover) {
                    factory.listener.trash.hover = false;
                    factory.listener.trash.element.removeClass('dragging');
                }
            },
            dragEnd: function(event) {
                if (factory.listener.dragging) {
                    if (factory.listener.trash.hover) {
                        angular.element(factory.listener.confirm.remove.modal).modal('show');
                        factory.listener.trash.element.removeClass('dragging');
                    } else {
                        // do nothing
                    }
                }
                factory.listener.dragging = false;
            },
            accept: function (sourceItemHandleScope, destSortableScope) {
                var currentElement = destSortableScope.element;
                if(currentElement[0].id == factory.listener.trash.id) {
                    factory.listener.trash.hover = true;
                    factory.listener.trash.element = currentElement;
                    factory.listener.trash.element.addClass('dragging');
                } else {
                    factory.listener.trash.hover = false;
                }
                return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
            },
            orderChanged: function(event) {
                if (!factory.listener.trash.hover) {
                    var src = event.source.index + 1;
                    var dest = event.dest.index + 1;
                    factory.listener.reorder(src, dest);
                }
            }
        };

        dragControls.getListener = function() {
            return factory.listener;
        };

        return dragControls;
    };

    return factory;
});
