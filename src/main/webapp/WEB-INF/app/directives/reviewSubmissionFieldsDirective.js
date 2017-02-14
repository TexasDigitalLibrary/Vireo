vireo.directive('reviewsubmissionsields', function($location, InputTypes, FieldValue, AdvisorSubmissionRepo) {
    return {
    	templateUrl: 'views/directives/reviewSubmissionFields.html',
        restrict: 'E',
        scope: {
        	submission: "=",
            filterOptional: "=?",
            hideLinks: "=?",
            setActiveStep: "&"
        },
        controller: function($scope) {
            $scope.submission.ready().then(function() {
                $scope.submission.validate();
            });
        },
        link: function($scope){

            $scope.InputTypes = InputTypes;

        	$scope.required = function(aggregateFieldProfile) {
				return !$scope.filterOptional || !aggregateFieldProfile.optional;
			};

			$scope.predicateMatch = function(fv) {
				return function(aggregateFieldProfile) {
			        return aggregateFieldProfile.fieldPredicate.id == fv.fieldPredicate.id;
			    };
			};

             $scope.hasValidationViolation = function(predicate) {
        
                var fieldValues = $scope.submission.getFieldValuesByFieldPredicate(predicate);

                for(var i in fieldValues) {
                    var fieldValue = fieldValues[i];
                    if (fieldValue.isValid && !fieldValue.isValid()) {
                        return true;
                    }
                }

                return false;
            };

            $scope.getFile = function(fieldValue) {
                 $scope.submission.fileInfo(fieldValue.value).then(function(data) {
                    fieldValue.fileInfo = angular.fromJson(data.body).payload.ObjectNode;
                    
                    $scope.submission.file(fieldValue.value).then(function(data) {
                        saveAs(new Blob([data], { type:fieldValue.fileInfo.type }), fieldValue.fileInfo.name);
                    });

                });
            };

            $scope.jumpToStep = function(wfs,hash) {
                $scope.$parent.setActiveStep(wfs, hash);
            };

        }
    };
});