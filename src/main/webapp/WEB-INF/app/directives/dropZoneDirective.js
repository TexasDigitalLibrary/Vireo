vireo.directive("dropzone", function($timeout) {
	return {
		templateUrl: 'views/directives/dropZone.html',
		restrict: 'E',
		scope: {
			'id': '@',
			'text': '@',
			'patterns': '@',
			'dropMethod': '&',
			'fileModel': '='
		},
		link: function($scope) {

			$scope.fileValidationError = false;

			$scope.dropMethodWrapper = function(file) {
				$scope.fileValidationError = file.file === null;
				if($scope.fileValidationError) {
					$timeout(function() {
						$scope.fileValidationError = false;
					}, 3000);
				}
				$scope.dropMethod(file);
			};

			$scope.dragging = function() {
				return "dragging-accept";
			};
		}
	};
});