/**
 * @ngdoc directive
 * @name  core.directive:modal
 * @restrict 'E'
 * @requires $controller
 * @scope
 *
 * @example
 * <pre>
 * 	<modal  
 * 		modal-id="example" 
 * 		modal-view="views/my.html" 
 * 		modal-header-class="modal-header-primary">
 * 	</modal>
 * </pre>
 * 
 * @description 
 *	The modal element directive is used to include a bootstrap style modal in your application.
 * 
 */
vireo.directive('vireomodal', function ($controller) {
	return {
		templateUrl: 'bower_components/core/app/views/modalWrapper.html',
		restrict: 'E',
		replace: false,
		transclude: true,
		scope: {
			"modalData": "="
		},
		link: function ($scope, element, attr) {

			/**
			 * @ngdoc property
			 * @name core.directive:modal#$scope.attr
			 * @propertyOf core.directive:modal
			 *
			 * @description
			 * 	The modal element attributes are stored on the $scope.attr variable. 	 
			 **/	    	
			$scope.attr = attr;

			if($scope.attr.modalController) {
				angular.extend(this, $controller($scope.attr.modalController, {$scope: $scope}));
			}

			/**
			 * @ngdoc method
			 * @name core.directive:modal#$scope.click
			 * @methodOf core.directive:modal
			 * @returns {void} returns void
			 * 
			 * @description
			 * 	This click method will parse the string '$scope.attr.modalNgClickParam' as JSON and provide it to $scope object
			 */						
	    	$scope.click = function() {
	    		if($scope.attr.modalNgClickFunction && $scope.attr.modalNgClickParam) {
	    			$scope[$scope.attr.modalNgClickFunction](JSON.parse($scope.attr.modalNgClickParam));
	    		}
	    	}
	    }
	};
});