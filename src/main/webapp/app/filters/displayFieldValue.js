vireo.filter('displayFieldValue', function($filter, InputTypes) {
    return function(value, inputType) {
        if (angular.isUndefined(inputType)) {
            return value;
        }

        var dateColumn = null;
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
                            dateColumn = appConfig.dateColumns[i];
                            break;
                        }
                    } else if (appConfig.dateColumns[i].how === 'start') {
                        if (inputType.name.startsWith(appConfig.dateColumns[i].name)) {
                            dateColumn = appConfig.dateColumns[i];
                            break;
                        }
                    }
                }
            }
        }

        if (dateColumn === null || angular.isUndefined(value) || value === null) {
            return value;
        }

        // Some browsers, like Firefox, do not support 'MMMM yyyy' formats for Date.parse().
        var stamp = Date.parse(value);
        if (isNaN(stamp) && dateColumn.format == 'MMMM yyyy') {
            var split = value.match(/^(\S+) (\d+)$/);

            if (split === null || split.length < 3) {
                return value;
            }

            var date = new Date(split[1] + ' 01, ' + split[2]);

            if (Number.isNaN(date) || Number.isNaN(date.getTime())) {
                return value;
            }

            return $filter('date')(date.toISOString(), dateColumn.format, 'utc');
        }

        var date = new Date(value);

        if (isNaN(date) || Number.isNaN(date.getTime())) {
            return value;
        }

        return $filter('date')(date.toISOString(), dateColumn.format, 'utc');
    };
});
