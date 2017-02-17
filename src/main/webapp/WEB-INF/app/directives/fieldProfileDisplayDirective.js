vireo.directive("fieldProfileDisplay",  function() {
  return {
    templateUrl: 'views/directives/fieldProfileDisplay.html',
    restrict: 'E',
    replace: 'false',
    scope: {
      profile: "="
    },
    link: function($scope) {
      $scope.submission = $scope.$parent.submission;
    }
  };
});
