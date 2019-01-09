var dataSubmissionStatus1 = {
    id: 1,
    submissionState: "IN_PROGRESS"
};

var dataSubmissionStatus2 = {
    id: 2,
    submissionState: "SUBMITTED"
};

var dataSubmissionStatus3 = {
    id: 3,
    submissionState: "WITHDRAWN"
};

var mockSubmissionStatus = function($q) {
    var model = mockModel($q, dataSubmissionStatus1);

    return model;
};

angular.module('mock.submissionStatus', []).service('SubmissionStatus', mockSubmissionStatus);

