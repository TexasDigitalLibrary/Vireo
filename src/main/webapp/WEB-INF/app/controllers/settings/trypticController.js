vireo.controller("TrypticController", function ($controller, $scope, $q, OrganizationRepo, OrganizationCategoryRepo) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.ready = $q.all([OrganizationRepo.ready()]);

	$scope.ready.then(function() {
      
		$scope.resetPanels = function() {
        	$scope.activePanel;
	        $scope.panelHistory = [];
	        $scope.openPanels = [new PanelEntry($scope.organizations.list[0])];
        }

        $scope.shiftPanels = function(panelIndex, organization) {

            var nextPanelIndex = panelIndex+1;
            var hasHistory = $scope.panelHistory.length > 0;
            var orgHasChildren = organization.childrenOrganizations.length > 0;
            var isFirstPanel = panelIndex == 0;
            var isLastPanel = panelIndex == 2;

            $scope.setSelectedOrganization(organization);

            $scope.openPanels[panelIndex].selectedOrganization = organization;

    		for(var i in $scope.openPanels) {
    			if($scope.openPanels[i].active) $scope.openPanels[i].previouslyActive = true;
    			$scope.openPanels[i].active = false;
    		}

    		$scope.openPanels[panelIndex].previouslyActive = false;
            $scope.openPanels[panelIndex].active = true;

            if(orgHasChildren || !isLastPanel) {
            	$scope.openPanels[nextPanelIndex] = new PanelEntry(organization);  
            }

            if(orgHasChildren && isLastPanel) {
                $scope.panelHistory.push($scope.openPanels[0]);
                $scope.openPanels.shift();
            } 

            if(isFirstPanel) {
            	if(hasHistory) {
            		$scope.openPanels.unshift($scope.panelHistory.pop());
                	$scope.openPanels.pop();	
            	} else {
            		$scope.openPanels.splice(2, 1);	
            	}
            }

        } 

        $scope.rewindPanels = function(panelEntry) {
        	//todo: rewind the histroy to the selected panel
        	console.log($scope.panelHistory);
        	console.log($scope.panelHistory.indexOf(panelEntry));
        	
        	var indexOfPanelEntry = $scope.panelHistory.indexOf(panelEntry);
        	var numberToRemove = $scope.panelHistory.length - indexOfPanelEntry;
        	var removedEntries = $scope.panelHistory.splice(indexOfPanelEntry, numberToRemove);

        	for(var i in removedEntries.reverse()) {
        		var entryToAdd = removedEntries[i];
        		$scope.openPanels.unshift(entryToAdd);
        		$scope.openPanels.pop();
        	} 
        	
        }

        $scope.panelHasChildren = function(panel) {

        	if(!$scope.openPanels[panel]) return false;

            var parentOrganization = $scope.openPanels[panel].parentOrganization;
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
        	return  $scope.getPanel(panel).organizationCatagories.filter(function(item, pos) {
			    return $scope.openPanels[panel].organizationCatagories.indexOf(item) == pos;
			});
        }

        $scope.filterPanelByParent = function(parentPanelIndex, organization) {

        	var panel = $scope.openPanels[parentPanelIndex];

            if(!panel) return false;

            var panelParentOrganization = $scope.openPanels[parentPanelIndex].parentOrganization

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
            if(!$scope.openPanels[parentPanelIndex].selectedOrganization) return false;
            return $scope.openPanels[parentPanelIndex].selectedOrganization.id == organization.id;
        }

        $scope.resetPanels();

    });

});

var PanelEntry = function(parentOrganization) {
    this.parentOrganization = parentOrganization;
    this.organizationCatagories = [];
    this.selectedOrganization;
    this.previouslyActive = false;
    this.active = false;
    return this;
}