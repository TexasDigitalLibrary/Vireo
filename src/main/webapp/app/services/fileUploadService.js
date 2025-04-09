vireo.service("FileUploadService", function ($q, FieldValue, FileService) {

    var FileUploadService = this;

    FileUploadService.getPattern = function (fieldProfile) {
        var pattern = '*';

        if (fieldProfile?.fieldPredicate?.value === '_doctype_primary') {
            return '.pdf'
        }

        if (angular.isDefined(fieldProfile?.controlledVocabulary)) {
            var cv = fieldProfile?.controlledVocabulary;
            pattern = '';
            for (var i in cv.dictionary) {
                var word = cv.dictionary[i];
                pattern += pattern.length > 0 ? (",." + word.name) : ("." + word.name);
            }
        }

        return pattern;
    };

    FileUploadService.uploadFile = function (submission, fieldValue) {
        return FileService.upload({
            'endpoint': '',
            'controller': 'submission',
            'method': submission.id + '/' + FileUploadService.getFileType(fieldValue.fieldPredicate) + '/upload-file',
            'file': fieldValue.file
        });
    };

    FileUploadService.removeFile = function (submission, fieldValue) {
        return $q(function (resolve) {
            submission.removeFile(fieldValue).then(function (response) {
                var apiRes = angular.fromJson(response.body);
                if(apiRes.meta.status === 'SUCCESS') {
                    submission.removeFieldValue(fieldValue).then(function () {
                        resolve(true);
                    });
                } else {
                    console.warn(response);
                    resolve(false);
                }
            });
        });
    };

    FileUploadService.archiveFile = function (submission, fieldValue, removeFieldValue) {
        return $q(function (resolve) {
            submission.archiveFile(fieldValue).then(function (response) {
                var apiRes = angular.fromJson(response.body);
                if(apiRes.meta.status === 'SUCCESS') {

                    var archivedDocumentFieldValue = new FieldValue();

                    archivedDocumentFieldValue.value = apiRes.meta.message;

                    var archivedDocumentFieldProfile = submission.getFieldProfileByPredicateName("_doctype_archived");

                    if (archivedDocumentFieldProfile !== undefined && archivedDocumentFieldProfile !== null) {
                        archivedDocumentFieldValue.fieldPredicate = archivedDocumentFieldProfile.fieldPredicate;

                        archivedDocumentFieldValue.updating = true;

                        submission.saveFieldValue(archivedDocumentFieldValue, archivedDocumentFieldProfile).then(function (response) {
                            archivedDocumentFieldValue.updating = false;
                        });
                    } else {
                        console.warn("No archived field profile exists on submission!");
                    }

                    if(removeFieldValue) {
                        submission.removeFieldValue(fieldValue).then(function () {
                            resolve(true);
                        });
                    }
                } else {
                    console.warn(response);
                    resolve(false);
                }
            });
        });
    };

    FileUploadService.download = function (submission, fieldValue) {
        return submission.file(fieldValue);
    };

    FileUploadService.getFileType = function (fieldPredicate) {
        return fieldPredicate.value.substring(9).toUpperCase();
    };

    FileUploadService.isPrimaryDocument = function (fieldPredicate) {
        return FileUploadService.getFileType(fieldPredicate) === 'PRIMARY';
    };

    return FileUploadService;

});
