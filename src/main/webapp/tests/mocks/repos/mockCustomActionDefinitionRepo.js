var dataCustomActionDefinitionRepo1 = [
    dataCustomActionDefinition1,
    dataCustomActionDefinition2,
    dataCustomActionDefinition3
];

var dataCustomActionDefinitionRepo2 = [
    dataCustomActionDefinition3,
    dataCustomActionDefinition2,
    dataCustomActionDefinition1
];

var dataCustomActionDefinitionRepo3 = [
    dataCustomActionDefinition4,
    dataCustomActionDefinition5,
    dataCustomActionDefinition6
];

angular.module('mock.customActionDefinitionRepo', []).service('CustomActionDefinitionRepo', function($q) {
    var repo = mockRepo('CustomActionDefinitionRepo', $q, mockCustomActionDefinition, dataCustomActionDefinitionRepo1);

    return repo;
});
