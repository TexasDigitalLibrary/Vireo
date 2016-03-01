vireo.directive("lockingtextarea", function($timeout) {
	return {
		templateUrl: 'views/directives/lockingTextArea.html',
		restrict: 'E',
		scope: {
			"label": "@",
			"scopeValue": "=",
            "toolTip": "@",
            "hint": "@",
            "onBlur": "&",
            "keyDown": "&",
            "name": "@",
            "timer": "@"
		},
		controller: function($scope) {

			$scope.locked = true;

			$scope.toggleLock = function() {
				$scope.locked = !$scope.locked;
			};
			
			if($scope.timer == undefined) {
				$scope.timer = 5;
			}

			var timer;

			var save = function() {
				$scope.onBlur();
    			$scope.toggleLock();
    			delete timer;
			};

			$scope.tinymceOptions = {
				name: $scope.name,
				setup: function(editor) {
                    editor.on('KeyUp', function(e) {
                    	if(timer == undefined) {
                    		timer = $timeout(function() {
	                            save();
	                        }, $scope.timer * 1000);
                    	}
                    });
                    editor.on('Blur', function(e) {
                    	save();
                    });			        
                },
		        theme: "modern",
		        plugins: $scope.plugins,
		        menubar: false,
		        statusbar: false,
		        image_advtab: true,
		        height: "100%",
		        width: "100%"
		    };

		},
	};
});
