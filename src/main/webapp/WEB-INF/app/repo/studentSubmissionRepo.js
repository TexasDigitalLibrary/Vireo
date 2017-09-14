vireo.repo("StudentSubmissionRepo", function StudentSubmissionRepo(WsApi) {

    var StudentSubmissionRepo = this;

    StudentSubmissionRepo.findSubmissionById = function (id) {
        if (StudentSubmissionRepo.mapping.useWebSockets) {
            angular.extend(StudentSubmissionRepo.mapping.one, {
                useWebSockets: true
            })
        }
        angular.extend(StudentSubmissionRepo.mapping.one, {
            'method': 'get-one/' + id
        });
        return WsApi.fetch(StudentSubmissionRepo.mapping.one);
    };

    return StudentSubmissionRepo;

});
