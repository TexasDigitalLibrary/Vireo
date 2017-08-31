vireo.run(function($route, $rootScope, $location, ModalService) {

	angular.element("body").fadeIn(1000);

  // Add runtime tasks here
  
  $rootScope.$on("$routeChangeStart", function (evt, next, current) {
      ModalService.closeModal();
      //this should be incorporated with the clodeModal method in weaver-ui-core
      angular.element('.modal-backdrop').remove();
  });

	// Allow the passing of an additional parameter which 
	// Will allow the path to be changed with out route reload
	var original = $location.path;
    $location.path = function (path, reload) {
        if (reload === false) {
            var lastRoute = $route.current;
            var un = $rootScope.$on('$locationChangeSuccess', function () {
                $route.current = lastRoute;
                un();
            });
        }
        return original.apply($location, [path]);
    };
	
});
