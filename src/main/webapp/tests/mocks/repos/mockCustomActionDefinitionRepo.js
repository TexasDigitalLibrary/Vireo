var mockCustomActionDefinitionRepo1 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockCustomActionDefinitionRepo2 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

var mockCustomActionDefinitionRepo3 = [
    {
        id: 1
    },
    {
        id: 2
    },
    {
        id: 3
    }
];

angular.module('mock.customActionDefinitionRepo', []).service('CustomActionDefinitionRepo', function($q) {
    var repo = mockRepo('CustomActionDefinitionRepo', $q, mockCustomActionDefinition, mockCustomActionDefinitionRepo1);

    return repo;
});
