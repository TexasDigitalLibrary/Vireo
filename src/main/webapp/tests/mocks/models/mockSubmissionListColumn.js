var dataSubmissionListColumn1 = {
    id: 1,
    exactMatch: true,
    filters: [],
    inputType: {},
    predicate: "predicate 1",
    sort: "ASC",
    sortOrder: 1,
    status: "status",
    title: "submission list column 1",
    valuePath: [
        "path1"
    ],
    visible: true
};

var dataSubmissionListColumn2 = {
    id: 2,
    exactMatch: true,
    filters: [],
    inputType: {},
    predicate: "predicate 2",
    sort: "DESC",
    sortOrder: 2,
    status: "status",
    title: "submission list column 2",
    valuePath: [
        "path2"
    ],
    visible: false
};

var dataSubmissionListColumn3 = {
    id: 3,
    exactMatch: false,
    filters: [],
    inputType: {},
    predicate: "predicate 3",
    sort: "NONE",
    sortOrder: 3,
    status: "status",
    title: "submission list column 3",
    valuePath: [
        "path3",
        "3path"
    ],
    visible: true
};

var dataSubmissionListColumn4 = {
    id: 4,
    exactMatch: false,
    filters: [],
    inputType: {},
    predicate: "predicate 1",
    sort: "DESC",
    sortOrder: 1,
    status: "status",
    title: "submission list column 4",
    valuePath: [
        "path2"
    ],
    visible: false
};

var dataSubmissionListColumn5 = {
    id: 5,
    exactMatch: false,
    filters: [],
    inputType: {},
    predicate: "predicate 2",
    sort: "ASC",
    sortOrder: 2,
    status: "status",
    title: "submission list column 5",
    valuePath: [
        "path3",
        "3path"
    ],
    visible: true
};

var dataSubmissionListColumn6 = {
    id: 6,
    exactMatch: true,
    filters: [],
    inputType: {},
    predicate: "predicate 3",
    sort: "NONE",
    sortOrder: 3,
    status: "status",
    title: "submission list column 6",
    valuePath: [
        "path1"
    ],
    visible: false
};

var mockSubmissionListColumn = function($q) {
    var model = mockModel("SubmissionListColumn", $q, dataSubmissionListColumn1);

    return model;
};

angular.module("mock.submissionListColumn", []).service("SubmissionListColumn", mockSubmissionListColumn);

