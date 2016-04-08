vireo.controller("TrypticController", function ($controller, $scope, $q, OrganizationRepo) {
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

        $scope.filterPanelByParent = function(parentPanelIndex, organization) {
            if(!$scope.openPanels[parentPanelIndex]) return false;
            return organization.parentOrganizations.indexOf($scope.openPanels[parentPanelIndex].parentOrganization.id) != -1;
        }

        $scope.setActivePanel = function(panel) {
        	$scope.activePanel = panel;
        }

        $scope.panelIsActive = function(panel) {
        	return panel == $scope.activePanel;
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
    this.active = false;
    return this;
}