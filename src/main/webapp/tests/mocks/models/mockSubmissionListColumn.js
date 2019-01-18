var dataSubmissionListColumn1 = {
    id: 1
};

var dataSubmissionListColumn2 = {
    id: 2
};

var dataSubmissionListColumn3 = {
    id: 3
};

var dataSubmissionListColumn4 = {
    id: 4
};

var dataSubmissionListColumn5 = {
    id: 5
};

var dataSubmissionListColumn6 = {
    id: 6
};

var mockSubmissionListColumn = function($q) {
    var model = mockModel("SubmissionListColumn", $q, dataSubmissionListColumn1);

    return model;
};

angular.module('mock.submissionListColumn', []).service('SubmissionListColumn', mockSubmissionListColumn);

