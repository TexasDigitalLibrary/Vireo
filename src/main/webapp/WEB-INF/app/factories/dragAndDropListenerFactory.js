vireo.factory('DragAndDropListenerFactory', function() {
	
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
	
	listener.setScopeTrashId = function(id) {
		listener.trash.id = id;
	};
	
	listener.setScopeDragging = function(dragging) {
		listener.dragging = dragging;
	};
	
	listener.setScopeSelect = function(select) {
		listener.select = select;
	};
		
	listener.setScopeList = function(list) {
		listener.list = list;
	};
	
	listener.setConfirmRemoveModal = function(confirmRemoveModal) {		
		listener.confirm.remove.modal = confirmRemoveModal;
	};
	
	listener.setScopeReorderFunction = function(reorder) {
		listener.reorder = reorder;
	};
		
	listener.buildDragControls = function(drag) {
		
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
		
		return dragControls;
	}
	
	return listener;
});