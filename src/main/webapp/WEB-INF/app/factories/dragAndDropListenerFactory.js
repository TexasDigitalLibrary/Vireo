vireo.factory('DragAndDropListenerFactory', function(ModalService) {

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

			'reorder': function(src, dest) {}
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

		var dragControls = {
			getListener: function () {
				return listener;
			},
			dragStart: function(event) {
				startingObj = event.source.sortableScope.modelValue[0];
				listener.dragging = true;
				listener.select(event.source.index);
				angular.element('.as-sortable-drag').css('display', 'none');
			},
			dragMove: function(event) {
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
					if(listener.trash.hover) {
						ModalService.openModal(listener.confirm.remove.modal);
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
		    	if(listener.dragging && currentElement[0].id == listener.trash.id) {
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
		    		var isSingleSorted = (listener.model.length == event.source.sortableScope.modelValue.length);
		    		var src = event.source.index + 1;
		    		var dest = event.dest.index + 1;
		    		if(!isSingleSorted) {
		    			var offset = 0;
			    		for(var i in listener.model) {
			    			if(listener.model[i].id == startingObj.id) {
			    				offset = i;
			    				break;
			    			}
			    		}
			    		src = listener.model[parseInt(event.source.index) + parseInt(offset)].position;
			    		dest = listener.model[parseInt(event.dest.index) + parseInt(offset)].position;
		    		}
		    		listener.reorder(src, dest);
		    	}
		    },
            containerPositioning: 'relative',
		    containment: drag.container
		};

		return dragControls;
	};

	return this;
});
