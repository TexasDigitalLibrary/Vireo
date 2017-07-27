vireo.controller("SettingsController", function ($controller, $injector, $scope, $timeout, UserSettings, ConfigurationRepo, StudentSubmissionRepo, SubmissionStates) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.settings = {};

    $scope.settings.configurable = ConfigurationRepo.getAllMapByType();

    $scope.submissionsOpen = function () {
        return stringToBoolean($scope.settings.configurable.application ? $scope.settings.configurable.application.submissions_open ? $scope.settings.configurable.application.submissions_open.value : 'false' : 'false');
    };

    $scope.multipleSubmissions = function () {
        return stringToBoolean($scope.settings.configurable.application ? $scope.settings.configurable.application.allow_multiple_submissions ? $scope.settings.configurable.application.allow_multiple_submissions.value : 'false' : 'false');
    };

    if (!$scope.isAnonymous()) {

        var DegreeRepo = $injector.get("DegreeRepo");

        $scope.degrees = DegreeRepo.getAll();

        var proquestPromise = DegreeRepo.getProquestDegreeCodes().then(function (data) {
            $scope.proquestDegreeCodes = [];
            var codes = angular.fromJson(data.body).payload.HashMap;
            for (var key in codes) {
                $scope.proquestDegreeCodes.push({
                    code: key,
                    degree: codes[key]
                });
            }
        });

        $scope.settings.user = new UserSettings();

        $scope.settings.user.fetch();

        $scope.settings.user.ready().then(function () {


            $scope.updateUserSetting = function (name) {
                delete $scope.settings.user.UserSettings;
                return $scope.settings.user.save();
            };

            var userSettingsValidations = {
                "displayName": {
                    "minlength": {
                        "type": "minlength",
                        "message": "Display name must be at least 2 characters",
                        "property": "displayName",
                        "value": 2
                    },
                    "maxlength": {
                        "type": "maxlength",
                        "message": "Display name cannot be more than 255 characters",
                        "property": "displayName",
                        "value": 255
                    },
                    "required": {
                        "type": "required",
                        "message": "Display requires a name",
                        "property": "displayName",
                        "value": true
                    }
                },
                "preferedEmail": {
                    "minlength": {
                        "type": "minlength",
                        "message": "Preferred email must be at least 2 characters",
                        "property": "preferedEmail",
                        "value": 2
                    },
                    "maxlength": {
                        "type": "maxlength",
                        "message": "Preferred email cannot be more than 255 characters",
                        "property": "preferedEmail",
                        "value": 255
                    },
                    "required": {
                        "type": "required",
                        "message": "Preferred email requires a name",
                        "property": "preferedEmail",
                        "value": true
                    },
                    "pattern": {
                        "type": "pattern",
                        "message": "Invalid email address",
                        "property": "preferedEmail",
                        "value": "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
                    }
                }
            };

            $scope.getUserSettingsValidations = function () {
                return userSettingsValidations;
            };
        });

        var submissions = StudentSubmissionRepo.getAll();

        ConfigurationRepo.ready().then(function () {

            $scope.hasSubmissions = function () {
                return submissions.length > 0;
            };

            $scope.getFirstSubmissionId = function () {
                return submissions[0].id;
            };

            $scope.submissionInProgress = function () {
                var isInProgress = false;
                for (var i in submissions) {
                    var submission = submissions[i];
                    if (submission.submissionStatus.submissionState === SubmissionStates.IN_PROGRESS) {
                        isInProgress = true;
                        break;
                    }
                }

                return isInProgress;
            };

            $scope.submissionNeedsCorrections = function () {
                var isInProgress = false;
                for (var i in submissions) {
                    var submission = submissions[i];
                    if (submission.submissionStatus.submissionState === SubmissionStates.NEEDS_CORRECTIONS) {
                        isInProgress = true;
                        break;
                    }
                }

                return isInProgress;
            };

            //TODO: check these update config settings methods for redundancy and clean up.
            $scope.delayedUpdateConfiguration = function (type, name) {

                if ($scope.pendingUpdate) {
                    $timeout.cancel($scope.updateTimeout);
                }

                $scope.pendingUpdate = true;

                $scope.updateTimeout = $timeout(function () {
                    $scope.updateConfiguration(type, name);
                    $scope.pendingUpdate = false;
                }, 500);

            };

            $scope.updateConfigurationPlainText = function (type, name) {
                $scope.settings.configurable[type][name].value = filterHtml($scope.settings.configurable[type][name].value);
                return $scope.settings.configurable[type][name].save();
            };

            $scope.updateConfiguration = function (type, name) {
                return $scope.settings.configurable[type][name].save();
            };

            $scope.resetConfiguration = function (type, name) {
                return $scope.settings.configurable[type][name].reset();
            };

            $scope.saveDegree = function (degree) {
                $scope.inProgress = true;
                degree.save().then(function () {
                    $scope.inProgress = false;
                });
            };

        });

    }

    var filterHtml = function (html) {
        var temp = document.createElement("div");
        if (!html) {
            return "";
        }
        temp.innerHTML = html;
        return temp.textContent || temp.innerText || "";
    };

    var stringToBoolean = function (string) {
        switch (string.toLowerCase().trim()) {
        case "false":
        case "no":
        case "0":
        case "":
            return false;
        default:
            return true;
        }
    };

    $scope.editMode = function (prop) {
        $scope["edit" + prop] = true;
    };

    $scope.viewMode = function (prop) {
        $scope["edit" + prop] = false;
    };

    $scope.confirmEdit = function ($event, prop) {
        if ($event.which == 13) {
            if (prop)
                $scope["edit" + prop] = false;
            $event.target.blur();
        }
    };

    $scope.hasError = function (field) {
        if (!field)
            field = {};
        return Object.keys(field).length > 0;
    };

    /**
     * Toggle options
     *
     * {evaluation: gloss}
     *
     */

    // SUBMISSION AVAILABILITY
    $scope.submissionsOpenOptions = [{
        "true": "Open"
    }, {
        "false": "Closed"
    }];

    $scope.allowMultipleSubmissionsOptions = [{
        "true": "Yes"
    }, {
        "false": "No"
    }];

    // PROQUEST / UMI SETTINGS / DEGREE CODE
    $scope.proquestIndexingOptions = [{
        "true": "Yes"
    }, {
        "false": "No"
    }];

    // ORCID
    $scope.orcidValidationOptions = [{
        "true": "Yes"
    }, {
        "false": "No"
    }];

    $scope.orcidAuthenticationOptions = [{
        "true": "Yes"
    }, {
        "false": "No"
    }];

});
