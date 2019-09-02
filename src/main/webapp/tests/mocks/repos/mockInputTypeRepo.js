var dataInputTypeRepo1 = [
    dataInputType1,
    dataInputType2,
    dataInputType3
];

var dataInputTypeRepo2 = [
    dataInputType3,
    dataInputType2,
    dataInputType1
];

var dataInputTypeRepo3 = [
    dataInputType4,
    dataInputType5,
    dataInputType6
];

angular.module("mock.inputTypeRepo", []).service("InputTypeRepo", function($q) {
    var repo = mockRepo("InputTypeRepo", $q, mockInputType, dataInputTypeRepo1);

    return repo;
});
