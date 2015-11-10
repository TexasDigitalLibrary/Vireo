vireo.controller('ModalController', function ($controller, $scope) {
	
   	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
   
   	$scope.showModal = function(modal) {
   		angular.element("#" + modal).modal('toggle');
   	};

});
