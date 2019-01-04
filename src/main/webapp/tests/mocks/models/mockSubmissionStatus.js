var mockSubmissionStatus1 = {
    id: 1,
    submissionState: "IN_PROGRESS"
};

var mockSubmissionStatus2 = {
    id: 2,
    submissionState: "SUBMITTED"
};

var mockSubmissionStatus3 = {
    id: 3,
    submissionState: "WITHDRAWN"
};

var mockSubmissionStatus = function($q) {
    var model = mockModel($q, mockSubmissionStatus1);

    return model;
};

angular.module('mock.submissionStatus', []).service('SubmissionStatus', mockSubmissionStatus);

