vireo.filter('uniqueEmbargoType', function() {
  return function(initialValues, isActive) {
    var matched = [];
    var newValues = [];
    if (typeof initialValues === "object") {
      angular.forEach(initialValues, function(embargo) {
        if (matched.includes(embargo.name)) return;
        if (embargo.isActive !== isActive) return;

        matched.push(embargo.name);
        newValues.push(embargo);
      });
    }
    return newValues;
  };
});
