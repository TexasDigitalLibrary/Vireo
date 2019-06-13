var dataPackagerRepo1 = [
    dataPackager1,
    dataPackager2,
    dataPackager3
];

var dataPackagerRepo2 = [
    dataPackager3,
    dataPackager2,
    dataPackager1
];

var dataPackagerRepo3 = [
    dataPackager4,
    dataPackager5,
    dataPackager6
];

angular.module("mock.packagerRepo", []).service("PackagerRepo", function($q) {
    var repo = mockRepo("PackagerRepo", $q, mockPackager, dataPackagerRepo1);

    return repo;
});
