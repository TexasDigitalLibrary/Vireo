vireo.directive("triptych", function () {
	return {
		templateUrl: 'views/directives/triptych.html',
		restrict: 'E',
		replace: true,
		transclude: true,
		scope: true,
		controller: function($controller, $q, $scope, $timeout, OrganizationRepo)  {

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
				angular.extend(panel.organization, organization);
				setCategories(panel);
			};

			var setCategories = function(panel) {
				panel.categories = [];
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
						break;
					}
				}
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
						defer.resolve();
					}, 355);
				});
				return defer.promise;
			};

			var getPanel = function(organization) {
				return add(create(organization));
			};

			var clear = function(panel) {
				panel.visible = false;
				panel.showing = false;
				delete panel.selected;
			};
			
			$scope.selectOrganization = function(organization) {
				var selectedOrganization = $scope.getSelectedOrganization();
				if(organization.id != selectedOrganization.id || selectedOrganization.id == $scope.organizations[0].id) {
					var parent;
					for(var i = $scope.navigation.panels.length - 1; i >= 0; i--) {
						var panel = $scope.navigation.panels[i];
						if(parent === undefined) {
							if(panel.organization.id == organization.parentOrganizations[0]) {
								parent = panel;
							}
							else {
								clear(panel);
								panel.active = false;
								panel.previouslyActive = false;
							}
						}
						else {
							panel.active = false;
							panel.previouslyActive = true;
						}
					}
					var panel = getPanel(organization);
					if(parent !== undefined) {
						if(parent.previouslyActive !== undefined) {
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
			
			$scope.refreshPanels = function() {
				var selectedOrganization;
				var newVisiblePanel;
				for(var i in $scope.navigation.panels) {
					var panel = $scope.navigation.panels[i];
					var updatedOrganization = OrganizationRepo.findById(panel.organization.id);
					var previousPanelChildrenCount = panel.organization.childrenOrganizations.length;
					if(updatedOrganization !== undefined) {
						setOrganzization(panel, updatedOrganization);
						if(panel.organization.childrenOrganizations.length === 0) {
							clear(panel);
						}
						else {
							if(previousPanelChildrenCount === 0) {
								newVisiblePanel = panel;
							}
						}
					}
					else {
						if(panel.parent !== undefined) {
							selectedOrganization = panel.parent.organization;
						}
						else {
							selectedOrganization = $scope.organizations[0];
						}
						remove(panel);
					}
				}
				if(newVisiblePanel != undefined) {
					setVisibility(newVisiblePanel);
				}
				if(selectedOrganization != undefined) {
					$scope.selectOrganization(selectedOrganization);
				}
			};

			var setVisibility = function(panel) {
				var closingPromise;
				var visible = panel.organization.childrenOrganizations.length > 0;
				if(panel.visible && !visible) {
					closingPromise = close(panel);
				}
				if(panel.parent != undefined) {
					if(panel.parent.parent != undefined) {
						if(panel.parent.parent.parent != undefined) {
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
						if(!panel.parent.parent.visible) {
							open(panel.parent.parent, closingPromise);
						}
					}
					if(!panel.parent.visible) {
						open(panel.parent, closingPromise);
					}
				}
				if(!panel.visible && visible) {
					open(panel, closingPromise);
				}
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

			$scope.ready = $q.all([OrganizationRepo.ready()]);

			$scope.ready.then(function() {
				$scope.selectOrganization($scope.organizations[0]);
			});

		},
		link: function($scope, element, attr) {
			$scope.attr = attr;
		}
	};
});