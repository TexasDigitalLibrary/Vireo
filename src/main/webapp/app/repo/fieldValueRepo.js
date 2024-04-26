vireo.repo("FieldValueRepo", function FieldValueRepo(FieldValue, WsApi) {

  var fieldValueRepo = this;

  fieldValueRepo.findByPredicateValue = function(value) {
      var fieldValues = [];

      // FieldValue exists on the API Mapping but is under the Submission controller, which is not correct and so manually define the entire mapping inline here.
      var mapping = {
        'endpoint': '/private/queue',
        'controller': 'settings/field-values',
        'method': 'predicate/' + value
      };

      WsApi.fetch(mapping).then(function(res) {
          if (!!res && !!res.body) {
            var resObj = angular.fromJson(res.body);
            if (resObj.meta.status === 'SUCCESS') {
                var values = resObj.payload['ArrayList<FieldValue>'];
                for (var i in values) {
                    fieldValues.push(new FieldValue(values[i]));
                }
            }
          }
      });

      return fieldValues;
  };

  return fieldValueRepo;
});
