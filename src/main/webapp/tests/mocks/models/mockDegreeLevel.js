var dataDegreeLevel1 = {
    id: 1
};

var dataDegreeLevel2 = {
    id: 2
};

var dataDegreeLevel3 = {
    id: 3
};

var dataDegreeLevel4 = {
    id: 4
};

var dataDegreeLevel5 = {
    id: 5
};

var dataDegreeLevel6 = {
    id: 6
};

var mockDegreeLevel = function($q) {
    var model = mockModel("DegreeLevel", $q, dataDegreeLevel1);

    return model;
};

angular.module("mock.degreeLevel", []).service("DegreeLevel", mockDegreeLevel);

