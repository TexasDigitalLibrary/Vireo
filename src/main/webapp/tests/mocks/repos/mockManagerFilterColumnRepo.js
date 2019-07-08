angular.module("mock.managerFilterColumnRepo", []).service("ManagerFilterColumnRepo", function($q) {
    var repo = mockRepo("ManagerFilterColumnRepo", $q, mockSavedFilter, dataSavedFilter1);

    repo.updateFilterColumns = function (filterColumns) {
        return payloadPromise($q.defer(), angular.copy(repo.mockedList));
    };

    return repo;
});
