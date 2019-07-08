var dataEmailTemplateRepo1 = [
    dataEmailTemplate1,
    dataEmailTemplate2,
    dataEmailTemplate3
];

var dataEmailTemplateRepo2 = [
    dataEmailTemplate3,
    dataEmailTemplate2,
    dataEmailTemplate1
];

var dataEmailTemplateRepo3 = [
    dataEmailTemplate4,
    dataEmailTemplate5,
    dataEmailTemplate6
];

angular.module("mock.emailTemplateRepo", []).service("EmailTemplateRepo", function($q) {
    var repo = mockRepo("EmailTemplateRepo", $q, mockEmailTemplate, dataEmailTemplateRepo1);

    return repo;
});
