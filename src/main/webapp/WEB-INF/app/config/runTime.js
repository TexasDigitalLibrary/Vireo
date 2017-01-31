vireo.run(function($route, $rootScope, $location) {

	angular.element("body").fadeIn(1000);

	// Add runtime tasks here

	// Allow the passing of an additional parameter which 
	// Will allow the path to be changed with out route reload
	var original = $location.path;
    $location.path = function (path, reload) {
        if (reload === false) {
            var lastRoute = $route.current;
            var un = $rootScope.$on('$locationChangeSuccess', function () {
                console.log("unbind");
                $route.current = lastRoute;
                un();
            });
        }
        return original.apply($location, [path]);
    };
	
});
