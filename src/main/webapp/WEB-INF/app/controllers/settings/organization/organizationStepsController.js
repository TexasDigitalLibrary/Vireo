vireo.controller("OrganizationStepsController", function ($controller, $scope, $q) { // TODO inject model

  angular.extend(this, $controller("AbstractController", {$scope: $scope}));



  //TODO grab the currently selected organization off the parent scope. (from organizationSettingsController);
  //For now we mock with the following JSON:
  $scope.stepOrder = [0,2,1];
  $scope.steps = [{
    id: 0,
    name:'step name foo',
    originatingOrganization:1,
    containedByOrganizations:[0,1],
    optional:true,
    fieldProfiles:[
      {name:'fp name 1'},
      {name:'fp name 2'},
      {name:'fp name 3'}
    ],
    fieldProfileOrder:[1,0,2],
    notes:[{name:'note name', text: 'note text'}]
  }, {
    id: 1,
    name:'step name bar',
    originatingOrganization:1,
    containedByOrganizations:[0,1],
    optional:false,
    fieldProfiles:[
      {name:'fp name foo'},
      {name:'fp name bar'},
      {name:'fp name qux'}
    ],
    fieldProfileOrder:[1,0,2],
    notes:[{name:'note name', text: 'note text'}]
  },{
    id: 2,
    name:'step name qux',
    originatingOrganization:1,
    containedByOrganizations:[0,1],
    optional:false,
    fieldProfiles:[
      {name:'fp name foo'},
      {name:'fp name bar'},
      {name:'fp name qux'}
    ],
    fieldProfileOrder:[1,0,2],
    notes:[{name:'note name', text: 'note text'}]
  }];
  //End temporary mock JSON



  var reorder = function(originIdx, destinationIdx){
    if (!(originIdx == 0 && destinationIdx == -1) && !(originIdx == $scope.steps.length-1 && destinationIdx == $scope.steps.length)) {
      var tmp = $scope.stepOrder[originIdx];
      $scope.stepOrder[originIdx] = $scope.stepOrder[destinationIdx];
      $scope.stepOrder[destinationIdx] = tmp;
      tempRefreshStepOrder();
    }
  };

  var stepForId = function(id){
    for(var i = 0; i < $scope.steps.length; i++) {
      if ($scope.steps[i].id == id) {
        return $scope.steps[i];
      }
    }
  }

  var tempRefreshStepOrder = function(){
    var tmp = ['a', 'b', 'c'];
    for(var i=0; i<$scope.steps.length; ++i) {
      tmp[i] = stepForId($scope.stepOrder[i]);
    }
    $scope.steps = tmp;
    //TODO would save the organization (and thus the ordering) here; refresh from the broadcast.
  };

  $scope.ready.then(function() {

    $scope.reorderUp = function(originIdx) {
      reorder(originIdx, originIdx-1); //TODO handle originIdx = 0
    };

    $scope.reorderDown = function(originIdx) {
      reorder(originIdx, originIdx+1); //TODO handle originIdx = array.length-1
    };

    $scope.resetOrganizationSteps = function() {
      $scope.modalData = {'name':''};
    };

    $scope.resetOrganizationSteps();
    
    $scope.selectOrganizationSteps = function(index) {
      $scope.resetOrganizationSteps();
      $scope.modalData = $scope.organizationSteps.list[index];
    };

    $scope.createOrganizationSteps = function() {
      OrganizationStepsRepo.add($scope.modalData).then(function(){
      });
        $scope.resetOrganizationSteps();
    };

    $scope.updateOrganizationSteps = function() {
        OrganizationStepsRepo.update($scope.modalData);
        $scope.resetOrganizationSteps();
    };

    $scope.launchEditModal = function(organizationSteps) {
        $scope.modalData = organizationSteps;
        angular.element('#organizationStepsEditModal').modal('show');
    };

    $scope.removeOrganizationSteps = function(index) {
      OrganizationStepsRepo.remove($scope.modalData).then(function(){
        $scope.resetOrganizationSteps();
        console.info($scope.organizationSteps);
      });
    };

    $scope.printState = function() {
      tempRefreshStepOrder();
    };
    tempRefreshStepOrder();
    console.info($scope.stepOrder);

  });
});
