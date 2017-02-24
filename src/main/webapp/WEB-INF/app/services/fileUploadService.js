vireo.service("FileUploadService", function($q, FileApi) {

    var FileUploadService = this;

    FileUploadService.uploadFile = function(fieldValue) {
        return FileApi.upload({'endpoint': '', 'controller': 'submission', 'method': 'upload', 'file': fieldValue.file});
    };

    FileUploadService.removeFile = function(submission, fieldValue) {
        return $q(function(resolve) {
            submission.removeFile(fieldValue).then(function(response) {
                submission.removeFieldValue(fieldValue).then(function() {
                    resolve();
                });
            });
        });
    };

    FileUploadService.download = function(submission, fieldValue) {
        return submission.file(fieldValue);
    };

    FileUploadService.getFileType = function(fieldPredicate) {
        return fieldPredicate.value.substring(9).toUpperCase();
    };

    FileUploadService.isPrimaryDocument = function(fieldPredicate) {
        return FileUploadService.getFileType(fieldPredicate) == 'PRIMARY';
    };

    return FileUploadService;

});
