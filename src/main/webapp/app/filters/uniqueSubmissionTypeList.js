vireo.filter('uniqueSubmissionTypeList', function() {
  return function(initialValues, isActive) {
    var matched = [];
    var newValues = [];
    if (typeof initialValues === 'object') {
      angular.forEach(initialValues, function(value) {
        if (matched.includes(value)) return;

        matched.push(value);
        newValues.push(value);
      });
    }

    return newValues;
  };
});
