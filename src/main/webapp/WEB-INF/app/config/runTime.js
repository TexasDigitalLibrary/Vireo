vireo.run(function($route, $rootScope, $location) {
	
	angular.element("body").fadeIn(1000);
	
	
	// Add runtime tasks here

	var originalLocation = $location.path;
    $location.path = function (path, reload) {
        if (reload === false) {
            var lastRoute = $route.current;
            var un = $rootScope.$on('$locationChangeSuccess', function () {
                $route.current = lastRoute;
                un();
            });
        }
        return originalLocation.apply($location, [path]);
    };

    var originalHash = $location.hash;
    $location.hash = function (hash, reload) {
	    if (reload === false) {
	        var lastRoute = $route.current;
	        var un = $rootScope.$on('$locationChangeSuccess', function(event) {
    			$route.current = lastRoute;
    			un();
			});
	    }
        return originalHash.apply($location, [hash]);
    };
	
});