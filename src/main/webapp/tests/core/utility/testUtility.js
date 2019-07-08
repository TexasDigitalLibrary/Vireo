// provide tests or parts of tests that are commonly performed.

var testUtility = {};

testUtility.repoSorting = function(scope, repo, method) {
    spyOn(repo, "sort");

    scope.sortAction = "confirm";
    method("column");

    expect(scope.sortAction).toEqual("sort");
    expect(repo.sort).not.toHaveBeenCalled();

    scope.sortAction = "sort";
    method("column");

    expect(scope.sortAction).toEqual("confirm");
    expect(repo.sort).toHaveBeenCalled();

    scope.sortAction = "unknown";
    method("column");
    expect(scope.sortAction).toEqual("unknown");
};
