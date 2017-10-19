vireo.service("FileUploadService", function($q, FileService) {

    var FileUploadService = this;

    FileUploadService.uploadFile = function(submission, fieldValue) {
        return FileService.upload({
            'endpoint': '',
            'controller': 'submission',
            'method': submission.id + '/' + FileUploadService.getFileType(fieldValue.fieldPredicate) + '/upload',
            'file': fieldValue.file
        });
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

    FileUploadService.archiveFile = function(submission, fieldValue) {
        return $q(function(resolve) {
            submission.archiveFile(fieldValue).then(function(response) {
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
