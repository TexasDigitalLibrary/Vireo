var mockSubmissionStatus1 = {
    'id': 1
};

var mockSubmissionStatus2 = {
    'id': 2
};

var mockSubmissionStatus3 = {
    'id': 3
};

var mockSubmissionStatus = function($q) {
    var model = mockModel($q, mockSubmissionStatus1);

    return model;
};

angular.module('mock.submissionStatus', []).service('SubmissionStatus', mockSubmissionStatus);

