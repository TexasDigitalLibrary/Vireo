vireo.controller("LookAndFeelController", function($scope, $controller, $q, RestApi) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.logoLeft = "resources/images/left-logo.png";
	$scope.logoRight = "resources/images/right-logo.gif";
	$scope.uploadState = "LOADED";
	
	$scope.modalData = {
		newLogo: {}
	};

	$scope.previewLeftLogoUpload = function(file) {
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

		$scope.uploadState = "LOADING"

		var uploadPromise = RestApi.post({
			'endpoint': '', 
			'controller': 'settings/look-and-feel', 
			'method': 'logo/upload/',
			'data': {"path": $scope.modalData.newLogo.path},
			'file': $scope.modalData.newLogo.file
		});

		uploadPromise.then(function(result) {
			$scope.logoLeft = $scope.logoLeft;
			$scope.logoRight = $scope.logoRight;
			$scope.uploadState = "LOADED";
			$scope.modalData.newLogo = {};
		});

		return uploadPromise;

	}

	$scope.cancelLogoUpload = function() {
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