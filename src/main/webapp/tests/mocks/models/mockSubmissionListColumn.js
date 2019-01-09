var dataSubmissionListColumn1 = {
    id: 1
};

var dataSubmissionListColumn2 = {
    id: 2
};

var dataSubmissionListColumn3 = {
    id: 3
};

var mockSubmissionListColumn = function($q) {
    var model = mockModel($q, dataSubmissionListColumn1);

    return model;
};

angular.module('mock.submissionListColumn', []).service('SubmissionListColumn', mockSubmissionListColumn);

