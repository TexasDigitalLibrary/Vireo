vireo.directive("dropzone", function() {
	return {
		templateUrl: 'views/directives/dropZone.html',
		restrict: 'E',
		scope: {
			'id': '@',
			'text': '@',
			'patterns': '@',
			'dropMethod': '&'
		}
	};
});