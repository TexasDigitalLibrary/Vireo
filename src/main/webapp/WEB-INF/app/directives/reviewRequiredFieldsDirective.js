vireo.directive('reviewrequiredfields', function() {
    return {
    	templateUrl: 'views/directives/reviewRequiredFields.html',
        restrict: 'E',
        scope: {
        	"submission": "="
        },
        link: function($scope){
           
        	$scope.required = function(aggregateFieldProfile) {
				return !aggregateFieldProfile.optional;
			};

			$scope.predicateMatch = function(fv) {
				return function(aggregateFieldProfile) {
			        return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
			    }
			};

        }
    };
});