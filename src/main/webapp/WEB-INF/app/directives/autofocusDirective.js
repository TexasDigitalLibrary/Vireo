vireo.directive('vireoAutofocus', function($timeout) {
   return {
    restrict: "A",
    scope: {
      vireoAutofocus: "=?",
      vireoAutofocusRefresh: "=?"
    },
    link : function($scope, $element) {

      $scope.$watch("vireoAutofocusRefresh", function() {
        $timeout(function() {
          if($scope.vireoAutofocus === undefined || $scope.vireoAutofocus === true) {
            $element[0].focus();
          }
        });
      });
  
    }
   }
});