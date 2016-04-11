vireo.controller("TrypticController", function ($controller, $scope, $q, $timeout, OrganizationRepo, OrganizationCategoryRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {
      
		$scope.resetPanels = function() {
        	$scope.activePanel;
	        $scope.panelHistory = [];
	        $scope.openPanels = [new Panel($scope.organizations.list[0])];
        }

        $scope.shiftPanels = function(panel, organization) {

            var panelIndex = $scope.openPanels.indexOf(panel);
            var nextPanelIndex = panelIndex + 1;
            var hasHistory = $scope.panelHistory.length > 0;
            var orgHasChildren = organization.childrenOrganizations.length > 0;
            var isFirstPanel = panelIndex == 0;
            var isLastPanel = panelIndex == 2;

            $scope.setSelectedOrganization(organization);

            panel.selectedOrganization = organization;

    		for(var i in $scope.openPanels) {
    			if($scope.openPanels[i].active) $scope.openPanels[i].previouslyActive = true;
    			$scope.openPanels[i].active = false;
    		}

    		panel.previouslyActive = false;
            panel.active = true;

            if(orgHasChildren || !isLastPanel) {
            	$scope.openPanels[nextPanelIndex] = new Panel(organization);  
            } 

            if(orgHasChildren && isLastPanel) {

                var panelToAdd = new Panel(organization);
                panelToAdd.visible = false;
                $scope.openPanels[nextPanelIndex] = panelToAdd; 

                animatePanelClose($scope.openPanels[0]).then(function() {
                    panelToAdd.visible = true;
                    $scope.panelHistory.push($scope.openPanels[0]);
                    $scope.openPanels.shift();
                });
                
            } 

            if(isFirstPanel) {
            	if(hasHistory) {

                    $scope.openPanels.pop();
                    var oldPanel = $scope.panelHistory.pop();
                    oldPanel.close = true;
                    $scope.openPanels.unshift(oldPanel);
                    $timeout(function() {
                        animatePanelOpen(oldPanel);
                    });

                } else {
            		$scope.openPanels.splice(2, 1);	
            	}
            }

        } 

        $scope.rewindPanels = function(panelEntry) {
        	
        	var indexOfPanel = $scope.panelHistory.indexOf(panelEntry);
        	var numberToRemove = $scope.panelHistory.length - indexOfPanel;
        	var removedEntries = $scope.panelHistory.splice(indexOfPanel, numberToRemove);

        	for(var i in removedEntries.reverse()) {
        		var panelToAdd = removedEntries[i];
        		$scope.openPanels.unshift(panelToAdd);
        		$scope.openPanels.pop();
        	} 
        	
        }

        $scope.panelHasChildren = function(panel) {

            var panelIndex = $scope.openPanels.indexOf(panel);
        	if(!panelIndex) return false;

            var parentOrganization = $scope.openPanels[panelIndex].parentOrganization;
            for(var i in $scope.organizations.list) {
            	var organization = $scope.organizations.list[i];
            	if(organization.id ==  parentOrganization.id) {
            		if(organization.childrenOrganizations.length > 0) {
	            		return true;
	            	}
	            	return false;
            	} 
            }
        	return false;
        }

        $scope.getPanel = function(panel) {
        	return  $scope.openPanels[panel];
        }

        $scope.getPanelCatagories = function(panel) {
        	return  panel.organizationCatagories.filter(function(item, pos) {
			    return panel.organizationCatagories.indexOf(item) == pos;
			});
        }

        $scope.filterPanelByParent = function(panel, organization) {

            if(!panel) return false;

            var panelParentOrganization = panel.parentOrganization

            if(organization.parentOrganizations.indexOf(panelParentOrganization.id) != -1) {
            	panel.organizationCatagories.push(OrganizationCategoryRepo.findById(organization.category))
            	return true;
            } 
            
            return false;
            
        }

        $scope.getCatagoryById = function(id) {
        	return OrganizationCategoryRepo.findById(id);
        }

        $scope.entryIsisSelected = function(parentPanelIndex, organization) {
            var parentPanelIndex = $scope.openPanels.indexOf(panel);
            if(!$scope.openPanels[parentPanelIndex].selectedOrganization) return false;
            return $scope.openPanels[parentPanelIndex].selectedOrganization.id == organization.id;
        }

        $scope.resetPanels();

    });

    
    var animatePanelClose = function(panel) {
        var defer = $q.defer();
        
        panel.close = true;

        setTimeout(function() {
            panel.close = false;
            defer.resolve();
        }, 255);
        
        return defer.promise;
    }

     var animatePanelOpen = function(panel) {
        var defer = $q.defer();
    
        panel.close = false;
        panel.open = true;

        setTimeout(function() {
            panel.open = false;
            defer.resolve();
        }, 255);
        
        return defer.promise;
    }

});

var Panel = function(parentOrganization) {
    this.parentOrganization = parentOrganization;
    this.organizationCatagories = [];
    this.selectedOrganization;
    this.previouslyActive = false;
    this.active = false;
    this.close = false;
    this.open = false;
    this.visible = true;
    return this;
}
