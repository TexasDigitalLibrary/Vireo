var mockEmailTemplateRepo1 = [
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

var mockEmailTemplateRepo2 = [
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

var mockEmailTemplateRepo3 = [
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

angular.module('mock.emailTemplateRepo', []).service('EmailTemplateRepo', function($q) {
    var repo = mockRepo('EmailTemplateRepo', $q, mockEmailTemplate, mockEmailTemplateRepo1);

    return repo;
});
