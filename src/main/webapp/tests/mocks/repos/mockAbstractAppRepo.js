angular.module('mock.abstractAppRepo', []).service('AbstractAppRepo', function($q) {
    var repo = mockRepo('AbstractAppRepo', $q);

    return repo;
});
