vireo.controller("TriptychController", function ($controller, $scope, $q, $timeout, OrganizationRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {

        $scope.triptych = new Triptych($scope.organizations[0]);

        $scope.shiftPanels = function(panel, organization) {

            if($scope.getSelectedOrganization() == organization) {
                return;
            }

            var panelIndex = $scope.triptych.openPanels.indexOf(panel);
            var nextPanelIndex = panelIndex + 1;
            var hasHistory = $scope.triptych.panelHistory.length > 0;
            var orgHasChildren = organization.childrenOrganizations.length > 0;
            var isFirstPanel = panelIndex === 0;
            var isLastPanel = panelIndex == 2;

            if(organization.id !== 1) $scope.setSelectedOrganization(organization);

            panel.selectedOrganization = organization;

    		$scope.triptych.setActivePanel(panel);

            if(orgHasChildren || !isLastPanel) {
            	$scope.triptych.addPanel(organization, nextPanelIndex).show();  
            } 

            if(orgHasChildren && isLastPanel) {
                var newPanel = $scope.triptych.addPanel(organization, nextPanelIndex);

                $scope.triptych.openPanels[0].close().then(function() {
                    newPanel.show();
                });
            } 

            if(isFirstPanel) {
            	if(hasHistory) {
                    $scope.triptych.retrievePanel(); 
                } else {
                    $scope.triptych.removePanels();
            	}
            }

        }; 

        $scope.filterPanelByParent = function(panel, organization) {

            if(!panel) return false;

            var panelParentOrganization = panel.parentOrganization;

            if(organization.parentOrganizations.indexOf(panelParentOrganization.id) != -1) {
            	panel.organizationCategories[organization.id] = organization.category;
            	return true;
            } 
            
            return false;
            
        };

        $scope.resetPanels = function() {
            $scope.triptych.resetPanels();
            $scope.newOrganization.parent = $scope.organizations[0];
        };

        if($scope.organizations.length==1) $scope.add = true;

        $scope.hasOrganization = function() {
            
            var hasOrgs = $scope.organizations.length < 2;

            if($scope.organizations.length == 2 && $scope.add) {
                $scope.add = false;
                $timeout(function() {
                    $scope.shiftPanels($scope.triptych.rootPanel, $scope.organizations[0]);
                });
            }

            return hasOrgs;
        }; 

    });

    

    var Triptych = function(organization) {

        var Triptych = this;

        Triptych.activePanel;
        Triptych.panelHistory = [];
        Triptych.openPanels = [];
        Triptych.rootPanel = Triptych.addPanel(organization);
        Triptych.rootPanel.show();
        Triptych.expanded = true;

        return Triptych;
    }

    Triptych.prototype = {
        resetPanels: function() {
            var Triptych = this;
            Triptych.activePanel;
            Triptych.panelHistory = [];
            Triptych.openPanels = [];
            Triptych.openPanels.unshift(Triptych.rootPanel); 
            Triptych.rootPanel.show();
            Triptych.expanded = true;
        },
        addPanel: function(organization, index) {
            var Triptych = this;

            var panelToAdd = new Panel(organization);
            panelToAdd.triptych = Triptych;

            if(!index) {
                Triptych.openPanels.unshift(panelToAdd);    
            } else {
                Triptych.openPanels[index] = panelToAdd;
            }

            return panelToAdd;
            
        },
        removePanels: function() {
            var Triptych = this;
            Triptych.openPanels.splice(2, 1);  
        },
        storePanel: function(panel) {
            var Triptych = this;
            Triptych.panelHistory.push(panel);
        },
        retrievePanel: function(panel) {

            var Triptych = this;

            Triptych.openPanels.pop();
                    
            var oldPanel = !panel ? Triptych.panelHistory.pop() : panel;
            oldPanel.closing = true;
            Triptych.openPanels.unshift(oldPanel);
            
            $timeout(function() {
                oldPanel.open();
            });

        },
        rewindPanels: function(panel) {

            var Triptych = this;

            var indexOfPanel = Triptych.panelHistory.indexOf(panel);
            var numberToRemove = Triptych.panelHistory.length - indexOfPanel;
            var removedEntries = Triptych.panelHistory.splice(indexOfPanel, numberToRemove);

            for(var i in removedEntries.reverse()) {
                var panelToAdd = removedEntries[i];
                Triptych.openPanels.unshift(panelToAdd);
                Triptych.openPanels.pop();
            }

            $scope.setSelectedOrganization(panel.selectedOrganization);
            $scope.triptych.setActivePanel(panel);

        },
        setActivePanel: function(panel) {
            var Triptych = this;
            for(var i in $scope.triptych.openPanels) {
                if(Triptych.openPanels[i].active) Triptych.openPanels[i].previouslyActive = true;
                Triptych.openPanels[i].active = false;
            }

            panel.previouslyActive = false;
            panel.active = true;
        }
    };

    var Panel = function(parentOrganization) {
        var Panel = this;
        Panel.triptych;
        Panel.parentOrganization = parentOrganization;
        Panel.organizationCategories = {};
        Panel.selectedOrganization;
        Panel.filterOn;
        Panel.previouslyActive = false;
        Panel.active = false;
        Panel.closing = false;
        Panel.opening = false;
        Panel.visible = false;
        return this;
    };

    Panel.prototype = {
        open: function() {
            var Panel = this;
            var defer = $q.defer();
    
            Panel.closing = false;
            Panel.opening = true;

            setTimeout(function() {
                Panel.opening = false;
                defer.resolve();
            }, 355);
            
            return defer.promise;
        },
        close: function() {
            var Panel = this;
            var defer = $q.defer();
        
            Panel.closing = true;

            setTimeout(function() {
                Panel.closing = false;
                defer.resolve();
            }, 355);

            defer.promise.then(function() {
                Panel.triptych.storePanel(Panel.triptych.openPanels[0]);
                Panel.triptych.openPanels.shift();
            });
            
            return defer.promise;
        },
        show: function() {
            var Panel = this;
            if(!Panel.visible) {
                $timeout(function(){
                   Panel.visible = true; 
                });    
            }
        },
        hide: function() {
            var Panel = this;
            Panel.visible = false;
        },
        toggleFilter: function(category) {
            var Panel = this;
            Panel.filterOn = Panel.filterOn == category.id ? "" : category.id;
        },
        clearFilter: function() {
            var Panel = this;
            Panel.filterOn = "";
        },
        getUniqueCategories: function() {
            var Panel = this;
            var uniqueCategories = [];

            for(var key in Panel.organizationCategories) {
                var category = Panel.organizationCategories[key];
                var unique = true; 
                for(var i in uniqueCategories) {
                	 if(uniqueCategories[i].id === category.id) { 
                		 unique = false;
                		 break;
                	}
                }
                if(unique) { 
                	uniqueCategories.push(category); 
                }
            }

            return uniqueCategories;
        }
    };

});
