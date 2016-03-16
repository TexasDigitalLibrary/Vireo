vireo.controller("LookAndFeelController", function($scope, $controller, $q, RestApi) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.modalData = {
		newLogo: {}
	};

	$scope.logoLeftPath = "resources/images/left-logo.png";
	$scope.logoRightPath = "resources/images/right-logo.gif";
	$scope.logoLoading = "resources/images/ajax-loader.gif"
	
	$scope.modalData.logoLeft = $scope.logoLeftPath; 
	$scope.modalData.logoRight = $scope.logoRightPath;
	
	

	$scope.previewLeftLogoUpload = function(file) {
		$scope.modalData.logoLeft = $scope.logoLoading;
		previewLogo(file).then(function(result) {
			$scope.modalData.newLogo = {
				file: result,
				path: $scope.logoLeft,
				gloss: "Left Logo"
			}
			angular.element('#newLogoConfirmUploadModal').modal('show');
		});
	}

	$scope.previewRightLogoUpload = function(file) {
		$scope.modalData.logoRight = $scope.logoLoading;
		previewLogo(file).then(function(result) {
			$scope.modalData.newLogo = {
				file: result,
				path: $scope.logoRight,
				gloss: "Right Logo"
			}
			angular.element('#newLogoConfirmUploadModal').modal('show');
		});
	}

	$scope.confirmLogoUpload = function() {


		var uploadPromise = RestApi.post({
			'endpoint': '', 
			'controller': 'settings/look-and-feel', 
			'method': 'logo/upload'
			// ,
			// 'data': {"path": $scope.modalData.newLogo.path},
			// 'file': $scope.modalData.newLogo.file
		});

		uploadPromise.then(
			function(result) {
				resetLogos();
			}, 
			function(result) {
				resetLogos();
			}
		);

		return uploadPromise;

	}

	$scope.cancelLogoUpload = function() {
		resetLogos();
		angular.element('#newLogoConfirmUploadModal').modal('hide');
	}

	var resetLogos = function() {
		$scope.modalData.logoLeft = $scope.logoLeftPath; 
		$scope.modalData.logoRight = $scope.logoRightPath;
		$scope.modalData.newLogo = {};
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