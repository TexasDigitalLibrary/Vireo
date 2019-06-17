var dataFieldProfileRepo1 = [
    dataFieldProfile1,
    dataFieldProfile2,
    dataFieldProfile3
];

var dataFieldProfileRepo2 = [
    dataFieldProfile3,
    dataFieldProfile2,
    dataFieldProfile1
];

var dataFieldProfileRepo3 = [
    dataFieldProfile4,
    dataFieldProfile5,
    dataFieldProfile6
];

angular.module("mock.fieldProfileRepo", []).service("FieldProfileRepo", function($q) {
    var repo = mockRepo("FieldProfileRepo", $q, mockFieldProfile, dataFieldProfileRepo1);

    return repo;
});
