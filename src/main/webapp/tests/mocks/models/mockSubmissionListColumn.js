var mockSubmissionListColumn1 = {
    'id': 1
};

var mockSubmissionListColumn2 = {
    'id': 2
};

var mockSubmissionListColumn3 = {
    'id': 3
};

var mockSubmissionListColumn = function($q) {
    var model = mockModel($q, mockSubmissionListColumn1);

    return model;
};

angular.module('mock.submissionListColumn', []).service('SubmissionListColumn', mockSubmissionListColumn);

