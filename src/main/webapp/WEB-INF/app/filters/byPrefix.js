vireo.filter('byPrefix', function() {
    return function(initialValues, options) {
        var newValues = [];
        angular.forEach(initialValues, function(value) {
            if (value[options[1]].substring(0,options[0].length) !== options[0]) {
                newValues.push(value);
            }
        });
        return newValues;
    };
});
