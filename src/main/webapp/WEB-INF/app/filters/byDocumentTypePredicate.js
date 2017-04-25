vireo.filter('byDocumentTypePredicate', function() {
    return function(initialValues, options) {
        var newValues = [];
        angular.forEach(initialValues, function(value) {
            if (!value[options[0]]) {
                newValues.push(value);
            }
        });
        return newValues;
    };
});
