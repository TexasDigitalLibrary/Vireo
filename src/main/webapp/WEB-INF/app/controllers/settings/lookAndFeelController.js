vireo.controller("LookAndFeelController", function($scope, $controller, $q, WsApi, RestApi) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.modalData = {
		newLogo: {}
	};
	
	var setServerErrors = function(name, validationResponse){
  		// either put a ValidationResponse object into the array, or clear it since it'll be "undefined"
		$scope.serverErrors[name] = validationResponse;
  	};

	$scope.modalData.logoLeft = $scope.settings.configurable.lookAndFeel.left_logo; 
	$scope.modalData.logoRight = $scope.settings.configurable.lookAndFeel.right_logo;

	$scope.previewLogo = function(file) {
		previewLogo(file).then(function(result) {
			
			var fileType = result.substring(result.indexOf("/")+1,result.indexOf(";"));
			
			$scope.modalData.newLogo.fileType = fileType;
			$scope.modalData.newLogo.file = result;

			angular.element('#newLogoConfirmUploadModal').modal('show');

		});
	}

	$scope.modalData.confirmLogoUpload = function() {

		$scope.uplaodLogo = {};
		angular.copy($scope.modalData.newLogo, $scope.uplaodLogo);
		delete $scope.uplaodLogo.file;

		//TODO: This may be better if removed to a service
		var uploadPromise = RestApi.post({
			'endpoint': '', 
			'controller': 'settings/look-and-feel',  
			'method': 'logo/upload',
			'data': $scope.uplaodLogo,
			'file': $scope.modalData.newLogo.file
		});

		uploadPromise.then(
			function(data) {
				if(data.payload !== undefined) {
					setServerErrors($scope.uplaodLogo.setting, data.payload.ValidationResponse);
				
	                if(data.payload.Configuration !== undefined) {
	                	updateLogos(data);
	                }
				}
			}, 
			function(data) {
				if(data.payload !== undefined) {
					setServerErrors($scope.uplaodLogo.setting, data.payload.ValidationResponse);
				}
				console.log("Error");
			}
		);

		return uploadPromise;

	}

	$scope.modalData.cancelLogoUpload = function() {
		$scope.resetModalData();
		angular.element('#newLogoConfirmUploadModal').modal('hide');
	}

	$scope.resetLogo = function(setting) {

		//TODO: This may be better if removed to a service
		var resetPromise = WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/look-and-feel', 
			'method': 'logo/reset',
			'data': {setting: setting},
		});

		resetPromise.then(
			function(data) {
				data = angular.fromJson(data.body);
				if(data.payload !== undefined) {
					setServerErrors(setting, data.payload.ValidationResponse);
				
	                if(data.payload.Configuration !== undefined) {
	                	updateLogos(data);
					}
				}
			}, 
			function(data) {
				data = angular.fromJson(data.body);
				if(data.payload !== undefined) {
					setServerErrors(setting, data.payload.ValidationResponse);
				}
				console.log("error");
			}
		);

		return resetPromise;
	};

	var updateLogos = function(data) {

		var newLogoConfiguration = typeof data.body === "string" ? JSON.parse(data.body).payload.Configuration : data.payload.Configuration;

		if(newLogoConfiguration !== undefined) {
			if(newLogoConfiguration.name == "left_logo") {
				$scope.modalData.logoLeft = newLogoConfiguration.value;
				$scope.settings.configurable.lookAndFeel.left_logo = newLogoConfiguration.value
			}
			if(newLogoConfiguration.name == "right_logo") {
				$scope.modalData.logoRight = newLogoConfiguration.value;
				$scope.settings.configurable.lookAndFeel.right_logo = newLogoConfiguration.value
			}

			$scope.resetModalData();
		}
		
	}

	$scope.resetModalData = function() {
		$scope.modalData.newLogo = {};
		$scope.modalData.newLogo.setting = "left_logo";
	}

	var previewLogo = function(file) {

		var defer = $q.defer();
		var reader = new FileReader();

		reader.onload = function() {
			defer.resolve(reader.result);
		};

		reader.readAsDataURL(file);

		return defer.promise;

	};

	
});