var dataLanguageRepo1 = [
    dataLanguage1,
    dataLanguage2,
    dataLanguage3
];

var dataLanguageRepo2 = [
    dataLanguage3,
    dataLanguage2,
    dataLanguage1
];

var dataLanguageRepo3 = [
    dataLanguage4,
    dataLanguage5,
    dataLanguage6
];

angular.module("mock.languageRepo", []).service("LanguageRepo", function($q) {
    var repo = mockRepo("LanguageRepo", $q, mockLanguage, dataLanguageRepo1);

    repo.getProquestLanguageCodes = function () {
        var payload = {};
        return payloadPromise($q.defer(), payload);
    };

    return repo;
});
