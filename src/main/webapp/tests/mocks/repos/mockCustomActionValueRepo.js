var dataCustomActionValueRepo1 = [
    dataCustomActionValue1,
    dataCustomActionValue2,
    dataCustomActionValue3
];

var dataCustomActionValueRepo2 = [
    dataCustomActionValue3,
    dataCustomActionValue2,
    dataCustomActionValue1
];

var dataCustomActionValueRepo3 = [
    dataCustomActionValue4,
    dataCustomActionValue5,
    dataCustomActionValue6
];

angular.module('mock.customActionValueRepo', []).service('CustomActionValueRepo', function($q) {
    var repo = mockRepo('CustomActionValueRepo', $q, mockCustomActionValue, dataCustomActionValueRepo1);

    return repo;
});
