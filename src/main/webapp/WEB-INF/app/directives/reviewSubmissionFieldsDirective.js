vireo.directive('reviewsubmissionsields', function(InputTypes, FieldValue, AdvisorSubmissionRepo) {
    return {
    	templateUrl: 'views/directives/reviewSubmissionFields.html',
        restrict: 'E',
        scope: {
        	submission: "=",
            filterOptional: "=?"
        },
        link: function($scope){

            $scope.InputTypes = InputTypes;

        	$scope.required = function(aggregateFieldProfile) {
				return !$scope.filterOptional || !aggregateFieldProfile.optional;
			};

			$scope.predicateMatch = function(fv) {
				return function(aggregateFieldProfile) {
			        return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
			    }
			};

            $scope.requiredViolation = function(aggregateFieldProfile) {
                
                var violation = true;

                for(var i in $scope.submission.fieldValues) {
                    var fv = $scope.submission.fieldValues[i];

                    if(aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id && fv.value !== "") violation=false; 
                }

                return violation;
            };

            $scope.getFile = function(fieldValue) {
                 $scope.submission.fileInfo(fieldValue.value).then(function(data) {
                    fieldValue.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
                    
                    $scope.submission.file(fieldValue.value).then(function(data) {
                        saveAs(new Blob([data], { type:fieldValue.fileInfo.type }), fieldValue.fileInfo.name);
                    });

                });
            };

        }
    };
});