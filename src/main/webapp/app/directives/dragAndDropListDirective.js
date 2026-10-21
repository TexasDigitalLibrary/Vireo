vireo.directive("draganddroplist", function($filter) {
	return {
		templateUrl: function(elem, attr) {
			if(attr.listView !== undefined) {
				return attr.listView;
			} else {
				return 'views/directives/dragAndDropList.html';
			}
		},
		restrict: 'E',
		scope: {
			'dragging': '=',
			'scopeValue': '=',
			'listeners': '=',
			'edit': '&',
			'toString': '&',
			'itemView': '@',
			'sortLabel': '@',
			'sortColumn': '@',
			'sortAction': '=',
			'sortActionSort': '=',
			'sortMethod': '&',
            'textFilter': '=?',
            'selectedFilter': '=?',
            'textFilterValue': '=?',
            'passiveFilter': '=?',
            'allowRemove': '=?',
			'isEditable': '&',
		},
		controller: function($scope) {

            if(!$scope.textFilterValue) $scope.textFilterValue = {};

            $scope.setSelectedFilter = function(filter) {
                $scope.selectedFilter = filter;
            };

			if(typeof $scope.itemView == 'undefined') {
				$scope.itemView = 'views/directives/dragAndDropItem.html';
			}

            // Keyboard accessible alternatives to the drag gestures. Drag and drop cannot be
            // the only way to reorder or remove an item. See issue #1674.

            $scope.itemControlsView = 'views/directives/dragAndDropItemControls.html';

            $scope.canReorder = function () {
                return !!($scope.listeners && $scope.listeners.canReorder && $scope.listeners.canReorder());
            };

            // Lists whose items are deleted somewhere other than the trash can (the embargo
            // tables delete from their edit modal) opt out with allow-remove="false".
            $scope.canRemove = function () {
                if ($scope.allowRemove === false) {
                    return false;
                }
                return !!($scope.listeners && $scope.listeners.canRemove && $scope.listeners.canRemove());
            };

            $scope.moveUp = function (index) {
                $scope.listeners.moveItem($scope.scopeValue, index, index - 1);
            };

            $scope.moveDown = function (index) {
                $scope.listeners.moveItem($scope.scopeValue, index, index + 1);
            };

            $scope.removeItem = function (index) {
                $scope.listeners.removeAt(index);
            };

            // Used to give each control an accessible name that identifies the row it acts on,
            // so "Remove" is announced as "Remove Masters of Science" rather than just "Remove".
            $scope.itemLabel = function (item) {
                var parts = [];

                if (item) {
                    angular.forEach($scope.properties, function (property) {
                        var value = item[property];
                        if (angular.isString(value) || angular.isNumber(value)) {
                            parts.push(value);
                        } else if (value && angular.isString(value.name)) {
                            parts.push(value.name);
                        }
                    });
                }

                if (parts.length > 0) {
                    return parts.join(' ');
                }

                // The table list view passes column headings rather than item keys as
                // properties, so fall back to whatever the item calls itself.
                return (item && (item.name || item.label || item.gloss)) || 'this item';
            };
		},
		link: function($scope, elem, attr) {
			$scope.properties = angular.fromJson(attr.properties);
            $scope.selectedFilter = $scope.properties.length === 1 ? $scope.properties[0] : "";
		}
	};
});
