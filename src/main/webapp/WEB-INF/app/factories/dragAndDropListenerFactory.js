vireo.factory('DragAndDropListenerFactory', function($window) {

	this.buildDragControls = function(drag) {

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

		if(typeof drag == 'object') {
			listener.trash.id = drag.trashId;
			listener.dragging = drag.dragging;
			listener.select = drag.select;
			listener.list = drag.list;
			listener.confirm.remove.modal = drag.confirm;
			listener.reorder = drag.reorder;
		}
		else {
			console.log('ensure configured')
		}
		
		var dragControls = {
			dragStart: function(event) {
				listener.dragging = true;								
				listener.select(event.source.index);
				angular.element('.as-sortable-drag').css('display', 'none');
			},
			dragMove: function(event) {

				var dragging = angular.element('.as-sortable-drag');
				dragging.css('margin-top', -angular.element('body').scrollTop());
				dragging.css('display', 'block');
				
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
		    },
		    containment: drag.container
		};
		
		return dragControls;
	}
	
	return this;
});
