var dataDegreeLevelRepo1 = [
    dataDegreeLevel1,
    dataDegreeLevel2,
    dataDegreeLevel3
];

var dataDegreeLevelRepo2 = [
    dataDegreeLevel3,
    dataDegreeLevel2,
    dataDegreeLevel1
];

var dataDegreeLevelRepo3 = [
    dataDegreeLevel4,
    dataDegreeLevel5,
    dataDegreeLevel6
];

angular.module('mock.degreeLevelRepo', []).service('DegreeLevelRepo', function($q) {
    var repo = mockRepo('DegreeLevelRepo', $q, mockDegreeLevel, dataDegreeLevelRepo1);

    return repo;
});
