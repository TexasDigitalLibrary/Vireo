var dataEmbargoRepo1 = [
    dataEmbargo1,
    dataEmbargo2,
    dataEmbargo3
];

var dataEmbargoRepo2 = [
    dataEmbargo3,
    dataEmbargo2,
    dataEmbargo1
];

var dataEmbargoRepo3 = [
    dataEmbargo4,
    dataEmbargo5,
    dataEmbargo6
];

angular.module("mock.embargoRepo", []).service("EmbargoRepo", function($q) {
    var repo = mockRepo("EmbargoRepo", $q, mockEmbargo, dataEmbargoRepo1);

    repo.activate = function (model) {
        model.systemRequired = true;
        return payloadPromise($q.defer(), model);
    };

    repo.deactivate = function (id) {
        model.systemRequired = false;
        return payloadPromise($q.defer(), model);
    };

    return repo;
});
