angular.module('mock.dragAndDropListenerFactory', []).factory('DragAndDropListenerFactory', function($q, ModalService) {
    var factory = this;
    var defer;
    var payloadResponse = function (payload) {
        return defer.resolve({
            body: angular.toJson({
                meta: {
                    status: 'SUCCESS'
                },
                payload: payload
            })
        });
    };

    var listener = {

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

    listener.setConfirmRemoveModal = function(confirmRemoveModal) {
        listener.confirm.remove.modal = confirmRemoveModal;
    };

    listener.setScopeDragging = function(dragging) {
        listener.dragging = dragging;
    };

    listener.setScopeList = function(list) {
        listener.list = list;
    };

    listener.setScopeReorderFunction = function(reorder) {
        listener.reorder = reorder;
    };

    listener.setScopeSelect = function(select) {
        listener.select = select;
    };

    listener.setScopeTrashId = function(id) {
        listener.trash.id = id;
    };

    factory.buildDragControls = function(drag) {

        if(typeof drag == 'object') {
            listener.setScopeTrashId(drag.trashId);
            listener.setScopeDragging(drag.dragging);
            listener.setScopeSelect(drag.select);
            listener.setScopeList(drag.list);
            listener.setConfirmRemoveModal(drag.confirm);
            listener.setScopeReorderFunction(drag.reorder);
        }
        else {
            console.log('ensure configured')
        }

        var dragControls = {
            dragStart: function(event) {
                listener.dragging = true;
                listener.select(event.source.index);
            },
            dragMove: function(event) {
                if(listener.trash.hover) {
                    listener.trash.hover = false;
                    listener.trash.element.removeClass('dragging');
                }
            },
            dragEnd: function(event) {
                if(listener.dragging) {
                    if(listener.trash.hover) {
                        angular.element(listener.confirm.remove.modal).modal('show');
                        listener.trash.element.removeClass('dragging');
                    }
                    else {
                        // do nothing
                    }
                }
                listener.dragging = false;
            },
            accept: function (sourceItemHandleScope, destSortableScope) {
                var currentElement = destSortableScope.element;
                if(currentElement[0].id == listener.trash.id) {
                    listener.trash.hover = true;
                    listener.trash.element = currentElement;
                    listener.trash.element.addClass('dragging');
                }
                else {
                    listener.trash.hover = false;
                }
                return sourceItemHandleScope.itemScope.sortableScope.$id === destSortableScope.$id;
            },
            orderChanged: function(event) {
                if(!listener.trash.hover) {
                    var src = event.source.index + 1;
                    var dest = event.dest.index + 1;
                    listener.reorder(src, dest);
                }
            }
        };

        dragControls.getListener = function() {
            return listener;
        };

        return dragControls;
    }

    return factory;
});
