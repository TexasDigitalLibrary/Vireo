vireo.directive('vireoAutofocus', function($timeout) {
   return {
    restrict: "A",
    scope: {
      vireoAutofocus: "=?"
    },
    link : function($scope, $element) {
      $timeout(function() {
        if($scope.vireoAutofocus === undefined || $scope.vireoAutofocus === true) {
          $element[0].focus();
        }
      });
    }
   }
});