vireo.filter('fieldValuePerProfile', function(FieldValue) {
    return function(fieldValues, fieldPredicate) {
        var fieldProfileValues = [];
        angular.forEach(fieldValues, function(fieldValue) {
            if (fieldValue.fieldPredicate.id === fieldPredicate.id) {
                fieldProfileValues.push(fieldValue);
            }
        });

        if(fieldProfileValues.length===0) {
          var fv = new FieldValue({
            fieldPredicate: fieldPredicate
          });
          fieldValues.push(fv);
          fieldProfileValues.push(fv);
        }

        return fieldProfileValues;
    };
});
