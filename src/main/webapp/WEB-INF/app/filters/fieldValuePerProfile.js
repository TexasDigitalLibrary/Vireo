vireo.filter('fieldValuePerProfile', function() {
    return function(fieldValues, fieldPredicate) {
        var fieldProfileValues = [];
        angular.forEach(fieldValues, function(fieldValue) {
            if (fieldValue.fieldPredicate.id === fieldPredicate.id) {
                fieldProfileValues.push(fieldValue);
            }
        });
        return fieldProfileValues;
    }
});

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
