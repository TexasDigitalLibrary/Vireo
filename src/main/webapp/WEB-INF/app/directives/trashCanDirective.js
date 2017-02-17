vireo.directive("trashcan", function() {
  return {
    templateUrl: 'views/directives/trashCan.html',
    restrict: 'E',
    scope: {
      'id': '@',
      'dragging': '=',
      'listeners': '='
    }
  };
});
