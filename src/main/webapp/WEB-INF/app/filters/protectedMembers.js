vireo.filter('protectedMembers', function() {
    return function(initialValues, property) {
        var newValues = [];
        angular.forEach(initialValues, function(value) {

            console.log(value, property);

            if (value[property][0] !== "_") {
                newValues.push(value);
            }
        });
        return newValues;
    };
});
