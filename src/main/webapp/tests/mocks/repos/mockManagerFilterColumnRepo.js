angular.module("mock.managerFilterColumnRepo", []).service("ManagerFilterColumnRepo", function($q) {
    var repo = mockRepo("ManagerFilterColumnRepo", $q, mockSubmissionListColumn, dataSubmissionListColumnRepo1);

    repo.updateFilterColumns = function (filterColumns) {
        return payloadPromise($q.defer(), angular.copy(repo.mockedList));
    };

    repo.reset = function () {
        return payloadPromise($q.defer());
    };

    return repo;
});
