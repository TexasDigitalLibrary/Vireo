angular.module('mock.abstractRepo', []).service('AbstractRepo', function($q) {
    var repo = mockRepo('AbstractRepo', $q);

    return repo;
});
