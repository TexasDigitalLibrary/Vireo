vireo.filter('displayFieldValue', function($filter, InputTypes) {
    return function(value, inputType) {
        if (angular.isUndefined(inputType)) {
          return value;
        }

        var date = null;
        var type = null;

        if (typeof inputType == 'string') {
            type = inputType;
        } else if (angular.isDefined(inputType) && angular.isDefined(inputType.name)) {
            type = inputType.name;
        }

        if (type != null) {
            if (inputType.name == InputTypes.INPUT_LICENSE || inputType.name == InputTypes.INPUT_PROQUEST) {
              return value == 'true' ? 'yes' : 'no';
            }

            if (angular.isDefined(appConfig.dateColumns) && angular.isDefined(inputType.name)) {
                for (var i = 0; i < appConfig.dateColumns.length; i++) {
                    if (appConfig.dateColumns[i].how === 'exact') {
                        if (inputType.name === appConfig.dateColumns[i].name) {
                            date = appConfig.dateColumns[i];
                            break;
                        }
                    } else if (appConfig.dateColumns[i].how === 'start') {
                        if (inputType.name.startsWith(appConfig.dateColumns[i].name)) {
                            date = appConfig.dateColumns[i];
                            break;
                        }
                    }
                }
            }
        }

        if (date == null || angular.isUndefined(value) || value == null) {
          return value;
        }

        return $filter('date')(new Date(value).toISOString(), date.format, 'utc');
    };
});
