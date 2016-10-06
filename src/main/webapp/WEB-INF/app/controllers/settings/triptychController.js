vireo.controller("TriptychController", function ($controller, $scope, $q, $timeout, OrganizationRepo) {
    angular.extend(this, $controller('AbstractController', {$scope: $scope}));

    OrganizationRepo.listen(function(data) {
        $timeout(function(){
            $scope.refreshPanels();
        }, 250);
    });

    $scope.navigation = {
        expanded: true,
        backward: false,
        defer: undefined,
        panels: []
    };

    var create = function(organization) {
        var panel = {
            organization: organization,
            categories: [],
            visible: false,
            opening: false,
            closing: false,
            showing: false,
            active: false,
            previouslyActive: false,
            filter: ''
        };
        setCategories(panel);
        return panel;
    };

    var setOrganzization = function(panel, organization) {
        panel.organization = organization;
        setCategories(panel);
    };

    var setCategories = function(panel) {
        for(var i in panel.organization.childrenOrganizations) {
            if(panel.categories.indexOf(panel.organization.childrenOrganizations[i].category.name) == -1) {
                panel.categories.push(panel.organization.childrenOrganizations[i].category.name);
            }
        }
    };

    var add = function(panel) {
        for(var i in $scope.navigation.panels) {
            if($scope.navigation.panels[i].organization.id == panel.organization.id) {
                angular.extend($scope.navigation.panels[i], panel);
                return $scope.navigation.panels[i];
            }
        }
        $scope.navigation.panels.push(panel);
        return panel;
    };

    var remove = function(panel) {
        for(var i in $scope.navigation.panels) {
            if($scope.navigation.panels[i].organization.id == panel.organization.id) {
                $scope.navigation.panels.splice(i, 1);
                return;
            }
        }
    };

    var clear = function(panel) {
        if(panel.parent !== undefined) {
            delete panel.parent.selected;
        }
        panel.visible = false;
        panel.showing = false;
    };

    var open = function(panel, promise) {
        var action = function(panel) {
            panel.visible = true;
            $timeout(function() {
                panel.opening = true;
                panel.showing = true;
                $timeout(function() {
                    panel.opening = false;
                    $scope.navigation.backward = false;
                }, 355);
            });
        };
        if(promise !== undefined) {
            promise.then(function() {
                action(panel);
            });
        }
        else {
            action(panel);
        }
    };

    var close = function(panel) {
        var defer = $q.defer();
        $timeout(function() {
            panel.closing = true;
            $timeout(function() {
                panel.closing = false;
                panel.showing = false;
                panel.visible = false;

                if(panel.organization.childrenOrganizations.length === 0) {
                    remove(panel);
                }

                defer.resolve();
            }, 355);
        });
        return defer.promise;
    };

    var getPanel = function(organization) {
        return add(create(organization));
    };

    var setVisibility = function(panel) {
        var closingPromise;
        var visible = panel.organization.childrenOrganizations.length > 0;
        if(panel.visible && !visible) {
            closingPromise = close(panel);
        }
        if(panel.parent != undefined && panel.parent.parent != undefined && panel.parent.parent.parent != undefined) {
            if(panel.parent.parent.parent.visible) {
                if(visible) {
                    closingPromise = close(panel.parent.parent.parent);
                }
            }
            else {
                if(!visible) {
                    open(panel.parent.parent.parent, closingPromise);
                }
            }
        }
        if(panel.parent != undefined && panel.parent.parent != undefined) {
            if(!panel.parent.parent.visible) {
                open(panel.parent.parent, closingPromise);
            }
        }
        if(panel.parent != undefined) {
            if(!panel.parent.visible) {
                open(panel.parent, closingPromise);
            }
        }
        if(!panel.visible && visible) {
            open(panel, closingPromise);
        }
    };
    
    $scope.selectOrganization = function(organization) {
        if(organization.id != $scope.getSelectedOrganization().id) {
            var parent;
            for(var i = $scope.navigation.panels.length - 1; i >= 0; i--) {
                var panel = $scope.navigation.panels[i];
                if(parent === undefined) {
                    if(panel.organization.id == organization.parentOrganizations[0]) {
                        parent = panel;
                    }
                    else {
                        if(panel.visible) {
                            panel.active = false;
                            panel.previouslyActive = true;
                            delete panel.selected;
                        }
                        panel.visible = false;
                        panel.showing = false;
                    }
                }
                else {
                    panel.active = false;
                    panel.previouslyActive = true;
                }
            }

            var panel = getPanel(organization);
            if(parent !== undefined) {
                if(parent.previouslyActive) {
                    $scope.navigation.backward = true;
                }
                parent.active = true;
                parent.previouslyActive = false;
                parent.selected = panel;
                panel.parent = parent;
            }
            setVisibility(panel);
        }
        $scope.setSelectedOrganization(organization);
    };

    $scope.getBreadcrumbs = function() {
        var breadcrumbs = [];
        for(var i in $scope.navigation.panels) {
            var panel = $scope.navigation.panels[i];
            if(panel.visible) {
                return breadcrumbs;
            }
            if(panel.previouslyActive && panel.selected !== undefined) {
                breadcrumbs.push(panel);
            }
        }
    };
    
    $scope.refreshPanels = function() {
        var selectedOrganization = $scope.getSelectedOrganization();
        var newVisiblePanel;
        for(var i in $scope.navigation.panels) {
            var panel = $scope.navigation.panels[i];
            var updatedOrganization = OrganizationRepo.findById(panel.organization.id);
            var previousPanelChildrenCount = panel.organization.childrenOrganizations.length;
            if(updatedOrganization !== undefined) {
                setOrganzization(panel, updatedOrganization);
                if(previousPanelChildrenCount === 0) {
                    newVisiblePanel = panel;
                }
                else {
                    if(panel.organization.childrenOrganizations.length === 0) {
                        clear(panel);
                    }
                }
            }
            else {
                if(selectedOrganization.id == panel.organization.id) {
                    if(panel.parent !== undefined) {
                        selectedOrganization = panel.parent.organization;
                    }
                    else {
                        selectedOrganization = $scope.organizations[0];
                    }
                }
                clear(panel);
            }
        }
        if(newVisiblePanel) {
            setVisibility(newVisiblePanel);
        }
        $scope.selectOrganization(selectedOrganization);
    };

    $scope.ready = $q.all([OrganizationRepo.ready()]);

    $scope.ready.then(function() {
        $scope.selectOrganization($scope.organizations[0]);
    });

});
