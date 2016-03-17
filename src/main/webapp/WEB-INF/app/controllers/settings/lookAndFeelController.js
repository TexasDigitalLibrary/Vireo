vireo.controller("LookAndFeelController", function($scope, $controller, $q, WsApi) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.modalData = {
		newLogo: {}
	};

	$scope.logoLoading = "resources/images/ajax-loader.gif"
	$scope.modalData.logoLeft = $scope.settings.configurable.lookAndFeel.left_logo; 
	$scope.modalData.logoRight = $scope.settings.configurable.lookAndFeel.right_logo;

	$scope.previewLogo = function(file) {
		previewLogo(file).then(function(result) {
			
			var fileType = result.substring(result.indexOf("/")+1,result.indexOf(";")) 
			
			$scope.modalData.newLogo.type = fileType;
			$scope.modalData.newLogo.file = result;

			angular.element('#newLogoConfirmUploadModal').modal('show');

		});
	}

	$scope.modalData.confirmLogoUpload = function() {

		var uploadPromise = WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/look-and-feel', 
			'method': 'logo/upload',
			'data': $scope.modalData.newLogo,
		});

		uploadPromise.then(
			function(data) {
				revertPreviewLogos(data);
			}, 
			function(data) {
				console.log("Error");
			}
		);

		return uploadPromise;

	}

	$scope.modalData.cancelLogoUpload = function() {
		revertPreviewLogos();
		angular.element('#newLogoConfirmUploadModal').modal('hide');
	}

	$scope.resetLogo = function(setting) {
		var uploadPromise = WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'settings/look-and-feel', 
			'method': 'logo/reset',
			'data': {setting: setting},
		});

		uploadPromise.then(
			function(data) {
				revertPreviewLogos(data);
			}, 
			function(data) {
				console.log("error");
			}
		);

		return uploadPromise;
	};

	var revertPreviewLogos = function(data) {

		var newLogoConfiguration = JSON.parse(data.body).payload.Configuration;

		console.log(newLogoConfiguration.name);

		if(newLogoConfiguration.name == "left_logo") $scope.modalData.logoLeft = newLogoConfiguration.value;
		
		if(newLogoConfiguration.name == "right_logo") $scope.modalData.logoRight = newLogoConfiguration.value;
		
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