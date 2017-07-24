vireo.controller("OrganizationSideBarController", function ($controller, $scope, $q, OrganizationCategoryRepo, OrganizationRepo) {

    angular.extend(this, $controller('AbstractController', {
        $scope: $scope
    }));

    $scope.organizations = OrganizationRepo.getAll();

    $scope.organizationRepo = OrganizationRepo;

    var organizationCategories = OrganizationCategoryRepo.getAll();

    $scope.ready = $q.all([
        OrganizationRepo.ready(),
        OrganizationCategoryRepo.ready()
    ]);

    $scope.forms = {};

    $scope.ready.then(function () {

        $scope.organizationCategories = organizationCategories.filter(function (category) {
            return category.name !== 'System';
        });

        $scope.reset = function () {
            $scope.organizationRepo.clearValidationResults();

            for (var key in $scope.forms) {
                if (!$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                }
            }

            $scope.newOrganization = OrganizationRepo.resetNewOrganization();

            if ($scope.newOrganization.category === undefined) {
                $scope.newOrganization.category = $scope.organizationCategories[0];
            }

            if ($scope.newOrganization.parent === undefined) {
                $scope.newOrganization.parent = $scope.organizations[0];
            }
        };

        $scope.reset();

        $scope.createNewOrganization = function (hierarchical) {
            var parentOrganization = hierarchical === 'true' ? OrganizationRepo.newOrganization.parent : $scope.organizations[0];
            OrganizationRepo.create({
                "name": OrganizationRepo.newOrganization.name,
                "category": OrganizationRepo.newOrganization.category,
                "parentOrganization": {
                    "id": parentOrganization.id,
                    "name": parentOrganization.name,
                    "category": parentOrganization.category
                }
            }, parentOrganization).then(function () {
                $scope.reset();
            });
        };

    });

});
