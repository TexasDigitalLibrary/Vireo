vireo.filter('documentFieldValuePerProfile', function() {
    return function(fieldValues) {
        var documentFieldValuePerProfileValues = [];
        angular.forEach(fieldValues, function(fieldValue) {
            if (fieldValue.fieldPredicate.documentTypePredicate) {
                documentFieldValuePerProfileValues.push(fieldValue);
            }
        });
        return documentFieldValuePerProfileValues;
    }
});
