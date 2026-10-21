vireo.factory('DragAndDropListenerFactory', function($q, ModalService) {

    this.buildDragControls = function(drag) {

        var listener = {

            'trash': {
                hover: false,
                element: null,
                id: ''
            },

            'select': null,

            'dragging': null,

            'confirm': {
                'remove': {
                    'modal': ''
                }
            },

            'reorder': function(src, dest) {
                return $q(function(resolve) {
                    resolve();
                });
            }
        };

        if(typeof drag == 'object') {
            listener.trash.id = drag.trashId;
            listener.dragging = drag.dragging;
            listener.select = drag.select;
            listener.model = drag.model;
            listener.confirm.remove.modal = drag.confirm;
            listener.reorder = drag.reorder;
        }
        else {
            console.log('ensure configured');
        }

        var startingObj;

        var inbounds;

        var original;

        var updateRequest = function() {
            angular.forEach(listener.model, function(model) {
                model.updateRequested = true;
            });
        };

        var relocate = function(list, from, to) {
            list.splice(to, 0, list.splice(from, 1)[0]);
        };

        var dragControls = {
            getListener: function () {
                return listener;
            },
            dragStart: function(event) {
                inbounds = true;
                original = angular.copy(listener.model);
                startingObj = event.source.sortableScope.modelValue[0];
                listener.dragging = true;
                listener.select(event.source.index);
                angular.element('.as-sortable-drag').css('display', 'none');
            },
            dragMove: function(movement, containment, event) {
                inbounds = angular.element(event.target).parents('.list-wrapper').length > 0;
                var dragging = angular.element('.as-sortable-drag');
                dragging.css('display', 'block');
                dragging.css('margin-top', angular.element(drag.container).offset().top - angular.element('html').scrollTop());
                dragging.css('margin-left', '250px');
                if(listener.trash.hover) {
                    listener.trash.hover = false;
                    listener.trash.element.removeClass('dragging');
                }
            },
            dragEnd: function(event) {
                if(listener.dragging) {
                    if(!inbounds) {
                        angular.extend(listener.model, original);
                    }
                    if(listener.trash.hover) {
                        updateRequest();
                        ModalService.openModal(listener.confirm.remove.modal);
                        listener.trash.element.removeClass('dragging');
                    } else {
                        listener.select(-1);
                    }
                }
                listener.dragging = false;
            },
            accept: function (sourceItemHandleScope, destSortableScope) {
                var currentElement = destSortableScope.element;
                if(listener.dragging && currentElement[0].id === listener.trash.id) {
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
                if(inbounds) {
                    var isSingleSorted = (listener.model.length === event.source.sortableScope.modelValue.length);
                    var src = event.source.index + 1;
                    var dest = event.dest.index + 1;
                    updateRequest();
                    listener.reorder(src, dest).then(function(res) {
                        var message = angular.fromJson(res.body);
                        if (message.meta.status !== "SUCCESS") {
                            angular.extend(listener.model, original);
                        }
                    });
                }
            },

            // Keyboard accessible equivalents of the drag gestures above. These drive the
            // per item controls rendered by the draganddroplist directive so that reordering
            // and removal never require a pointer. See issue #1674.

            canReorder: function() {
                return angular.isFunction(listener.reorder);
            },

            canRemove: function() {
                return angular.isFunction(listener.select) && !!listener.confirm.remove.modal;
            },

            // Mirrors dropping an item on the trash can: select it, then hand off to the
            // same confirmation modal the drag gesture opens.
            removeAt: function(index) {
                if(!dragControls.canRemove()) {
                    return;
                }
                original = angular.copy(listener.model);
                listener.select(index);
                updateRequest();
                ModalService.openModal(listener.confirm.remove.modal);
            },

            // Mirrors dragging an item to a new slot. The list passed in is the rendered
            // list, which is what the sortable mutates and what the reorder end points
            // expect positions against.
            moveItem: function(list, from, to) {
                if(!dragControls.canReorder() || !angular.isArray(list)) {
                    return $q.when();
                }
                if(from === to || to < 0 || to >= list.length) {
                    return $q.when();
                }
                original = angular.copy(listener.model);
                relocate(list, from, to);
                updateRequest();
                return listener.reorder(from + 1, to + 1).then(function(res) {
                    var message = angular.fromJson(res.body);
                    if (message.meta.status !== "SUCCESS") {
                        relocate(list, to, from);
                        angular.extend(listener.model, original);
                    }
                });
            },

            containerPositioning: 'relative',
            containment: drag.container
        };

        return dragControls;
    };

    return this;
});
