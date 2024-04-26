vireo.filter('uniqueSubmissionTypeList', function() {
  return function(initialValues, isActive) {
    var matched = [];
    var newValues = [];
    if (typeof initialValues === 'object') {
      angular.forEach(initialValues, function(fv) {
        if (matched.includes(fv.value)) return;

        matched.push(fv.value);
        newValues.push(fv);
      });
    }
    return newValues;
  };
});
