vireo.model("FieldValue", function FieldValue(WsApi) {

  return function FieldValue() {

    // additional model methods and variables
    var fieldValue = this;
    var isValid = true;
    var validationMessages = [];

    fieldValue.setIsValid = function(valid) {
      isValid = valid;
    };

    fieldValue.isValid = function() {
      return isValid;
    };

    fieldValue.setValidationMessages = function(messages) {
      validationMessages.length = 0;
      angular.extend(validationMessages, messages);
    };

    fieldValue.addValidationMessage = function(message) {
      validationMessages.push(message);
    };

    fieldValue.getValidationMessages = function() {
      return validationMessages;
    };

    fieldValue.save = function(submissionId) {
      var fieldProfile = $scope.submission.getFieldProfileByPredicate(fieldValue.fieldPredicate);
      return $scope.submission.saveFieldValue(fieldValue, fieldProfile);;
    };

    return fieldValue;
  }

});
