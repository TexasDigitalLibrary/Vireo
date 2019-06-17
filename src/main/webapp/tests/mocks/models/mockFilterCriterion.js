var dataFilterCriterion1 = {
    id: 1,
    criterionName: "criterion name 1",
    exactMatch: true,
    filterGloss: "filter gloss 1",
    filterValue: "filter value 1"
};

var dataFilterCriterion2 = {
    id: 2,
    criterionName: "criterion name 2",
    exactMatch: false,
    filterGloss: "filter gloss 2",
    filterValue: "filter value 2"
};

var dataFilterCriterion3 = {
    id: 3,
    criterionName: "criterion name 3",
    exactMatch: true,
    filterGloss: "filter gloss 3",
    filterValue: "filter value 3"
};

var dataFilterCriterion4 = {
    id: 4,
    criterionName: "criterion name 4",
    exactMatch: true,
    filterGloss: "filter gloss 4",
    filterValue: "filter value 4"
};

var dataFilterCriterion5 = {
    id: 5,
    criterionName: "criterion name 5",
    exactMatch: false,
    filterGloss: "filter gloss 5",
    filterValue: "filter value 5"
};

var dataFilterCriterion6 = {
    id: 6,
    criterionName: "criterion name 6",
    exactMatch: false,
    filterGloss: "filter gloss 6",
    filterValue: "filter value 6"
};

var mockFilterCriterion = function($q) {
    var model = mockModel("FilterCriterion", $q, dataFilterCriterion1);

    return model;
};

angular.module("mock.filterCriterion", []).service("FilterCriterion", mockFilterCriterion);

