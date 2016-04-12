<!DOCTYPE html>

<!--[if lt IE 7]>      <html lang="en" class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html lang="en" class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html lang="en" class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
<head>
	
	<script type="text/javascript">
		window.location.base = "${base}";
	</script>
	
	<base href="${base}/">
	
	<title>Vireo :: Texas Digital Libraries</title>
	
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<meta name="description" content="Electronic Thesis &amp; Dissertation Submission Workflow Tool">
	
	<link rel="shortcut icon" href="resources/images/favicon.ico" type="image/x-icon" />

	<link rel="stylesheet" href="bower_components/html5-boilerplate/css/normalize.css" />
	<link rel="stylesheet" href="bower_components/html5-boilerplate/css/main.css" />
	
	<link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.min.css" />
	<link rel="stylesheet" href="bower_components/ng-sortable/dist/ng-sortable.min.css">

	<link rel="stylesheet" href="resources/styles/app.css" />	

	<!-- <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300' rel='stylesheet' type='text/css'> -->

	<script src="bower_components/html5-boilerplate/js/vendor/modernizr-2.6.2.min.js"></script>
	
</head>
<body>
	<!--[if lt IE 7]>
    	<p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
	<![endif]-->

	<!-- Content placed here will appear on every page -->
	<main>
		<nav class="navbar navbar-default">
  			<div class="container-fluid" ng-controller="AuthenticationController">
  			
	    		<div class="navbar-header">
	      			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
				        <span class="sr-only">Toggle navigation</span>
				        <span class="icon-bar"></span>
				        <span class="icon-bar"></span>
				        <span class="icon-bar"></span>
			      	</button>
			    </div>
			    
			    <modal modal-id="verifyEmailModal" modal-view="views/modals/verifyEmailModal.html" modal-header-class="modal-header-primary"></modal>					
				<modal modal-id="loginModal" modal-view="views/modals/loginModal.html" modal-header-class="modal-header-primary"></modal>

	    		<div ng-if="isAnonymous()" class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">	    					      	
			      	<ul class="nav navbar-nav navbar-right">
			      		<li class="dropdown">
			      			<a href data-toggle="modal" data-target="#loginModal">Login</a>
			      		</li>
						<li class="dropdown">
			      			<a href data-toggle="modal" data-target="#verifyEmailModal">Register</a>
			      		</li>						
					</ul>
	    		</div>
	    			    		
	    		<div ng-if="!isAnonymous()" class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			      	<ul class="nav navbar-nav navbar-right">
						<li class="dropdown">
							<a class="dropdown-toggle toggle-href" data-toggle="dropdown" aria-expanded="false"><displayname></displayname> <span class="caret"></span></a>
							<ul class="dropdown-menu" role="menu">
								<li role="presentation" class="dropdown-header">Profile</li>
								<li><a role="menuitem" href="myprofile">Profile</a></li>
								<li ng-if="isAdmin() || isManager()" role="presentation" class="divider"></li>
								<li ng-if="isAdmin() || isManager()" role="presentation" class="dropdown-header">Manager Actions</li>
								<li ng-if="isAdmin() || isManager()">
									<a role="menuitem" href="users">Manage Users</a>
								</li>

								<li ng-if="isAdmin()" role="presentation" class="divider"></li>
								<li ng-if="isAdmin()" role="presentation" class="dropdown-header">Admin Actions</li>
								<li ng-if="isAdmin()">
									<a role="menuitem" href="admin/list">List</a>
								</li>
								<li ng-if="isAdmin()">
									<a role="menuitem" href="admin/log">Log</a>
								</li>
								<li ng-if="isAdmin()">
									<a role="menuitem" href="admin/settings">Settings</a>
								</li>
								<li role="presentation" class="divider"></li>
								<li><a role="menuitem" href ng-click="logout()">Logout</a></li>
							</ul>
						</li>
					</ul>
	    		</div>
	    		
	    		
	  		</div>
		</nav>
		
		<header class="container-fluid site-title" ng-controller="HeaderController">
			<div class="container">
				<div class="row">
					<a class="pull-left" href="home"><img style="max-height: 57px;" ng-src="{{logoImage()}}"></img></a>
					<ul ng-if="activeAdminSection()" class="tab-nav nav navbar-nav navbar-right hidden-xs">
			      		<li ng-class="{'active': activeTab('list')}">
			      			<a href="admin/list">List</a>
			      		</li>
						<li ng-class="{'active': activeTab('view')}">
			      			<a href="admin/view">View</a>
			      		</li>
			      		<li ng-class="{'active': activeTab('log')}">
			      			<a href="admin/log">Log</a>
			      		</li>
			      		<li ng-class="{'active': activeTab('settings')}" class="settings-tab">
			      			<a href="admin/settings">Settings</a>
			      		</li>						
					</ul>
				</div>
			</div>
		</header>

		<alerts types="WARNING, ERROR"></alerts>
		<alerts seconds="45" channels="auth/register" types="SUCCESS"></alerts>
				
		<div class="container-fluid main">			
			<div ng-view class="view"></div>					
		</div>


	</main>


	<footer class="footer">
      <div class="container">
        <p class="text-muted">
	        <ul class="inline-list">
				<li>&copy; Vireo <span app-version></span></li>
				<li>
					<a href="#">Webmaster</a>
				</li>
				<li>
					<a href="#">Legal</a>
				</li>
				<li>
					<a href="#">Comments</a>
				</li>
				<li>
					<a href="#">Accessibility</a>
				</li>
			</ul>
		</p>
      </div>
    </footer>

	<!-- In production use: <script src="//ajax.googleapis.com/ajax/libs/angularjs/x.x.x/angular.min.js"></script> -->

	
	<!-- Bower component -->
	<script src="bower_components/jquery/dist/jquery.js"></script>
	<script src="bower_components/bootstrap/dist/js/bootstrap.js"></script>
	
	<script src="bower_components/sockjs-client/dist/sockjs.min.js"></script>
	<script src="bower_components/stomp-websocket/lib/stomp.min.js"></script>
	
	<script src="bower_components/angular/angular.js"></script>
	<script src="bower_components/angular-sanitize/angular-sanitize.min.js"></script>
	<script src="bower_components/angular-route/angular-route.js"></script>
	<script src="bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
	<script src="bower_components/angular-loader/angular-loader.js"></script>
	<script src="bower_components/angular-mocks/angular-mocks.js"></script>

	<script src="bower_components/tinymce-dist/tinymce.min.js"></script>
	<script src="bower_components/angular-ui-tinymce/dist/tinymce.min.js"></script>
	<script src="bower_components/ng-sortable/dist/ng-sortable.js"></script>
	<script src="bower_components/ng-csv/build/ng-csv.min.js"></script>

	<script src="bower_components/ng-file-upload/ng-file-upload-shim.min.js"></script>
	<script src="bower_components/ng-file-upload/ng-file-upload.min.js"></script>

	
	<!--  Core libraries -->
	
	<!-- build:js src/main/resources/static/ui/app/resources/scripts/core_concat.js -->

		<!-- TODO: concat core js -->
	
	<!-- endbuild -->

		<!-- Core Configuration -->
	    <script src="bower_components/core/app/config/coreConfig.js"></script>

		<!-- Core Modules -->
		<script src="bower_components/core/app/components/version/version.js"></script>
		<script src="bower_components/core/app/components/version/version-directive.js"></script>
		<script src="bower_components/core/app/components/version/interpolate-filter.js"></script>


		<!-- App Configuration -->
	    <script src="config/appConfig.js"></script>

		<!-- App Modules -->
		<script src="components/version/version.js"></script>
		<script src="components/version/version-directive.js"></script>
		<script src="components/version/interpolate-filter.js"></script>

		<!-- Application Start -->
	    <script src="bower_components/core/app/core.js"></script>
	    <script src="bower_components/core/app/config/coreRuntime.js"></script>
	    <script src="bower_components/core/app/setup.js"></script>
	    <script src="bower_components/core/app/config/logging.js"></script>

	    <!-- Directives -->
	    <script src="bower_components/core/app/directives/headerDirective.js"></script>
	    <script src="bower_components/core/app/directives/footerDirective.js"></script>
	    <script src="bower_components/core/app/directives/userDirective.js"></script>
	    <script src="bower_components/core/app/directives/modalDirective.js"></script>
	    <script src="bower_components/core/app/directives/alertDirective.js"></script>

	    <!-- Services -->
	    <script src="bower_components/core/app/services/accesscontrollservice.js"></script>
	    <script src="bower_components/core/app/services/wsservice.js"></script>
	    <script src="bower_components/core/app/services/wsapi.js"></script>
	    <script src="bower_components/core/app/services/restapi.js"></script>
	  	<script src="bower_components/core/app/services/authserviceapi.js"></script>
	  	<script src="bower_components/core/app/services/storageservice.js"></script>
	  	<script src="bower_components/core/app/services/utilityservice.js"></script>
	  	<script src="bower_components/core/app/services/alertservice.js"></script>

	  	<!-- Models -->
	    <script src="bower_components/core/app/model/abstractModel.js"></script>
	    <script src="bower_components/core/app/model/assumedControlModel.js"></script>
	    <script src="bower_components/core/app/model/userModel.js"></script>

	    <!-- Controllers -->
	    <script src="bower_components/core/app/controllers/abstractController.js"></script>
	    <script src="bower_components/core/app/controllers/userController.js"></script>
	    <script src="bower_components/core/app/controllers/registrationController.js"></script>
	    <script src="bower_components/core/app/controllers/loginController.js"></script>
	    <script src="bower_components/core/app/controllers/authenticationController.js"></script>
	    <script src="bower_components/core/app/controllers/errorpagecontroller.js"></script>


	<!-- build:js src/main/resources/static/ui/app/resources/scripts/app_concat.js -->

	    <!-- Application Start -->
	    <script src="app.js"></script>
	    <script src="config/runTime.js"></script>
	    <script src="config/routes.js"></script>

	    <!-- Directives -->
	    <script src="directives/accordionDirective.js"></script>
	    <script src="directives/deHashColorDirective.js"></script>
	    <script src="directives/tabsDirective.js"></script>
	    <script src="directives/sideBoxDirective.js"></script>
        <script src="directives/toggleButtonDirective.js"></script>
        <script src="directives/textFieldDirective.js"></script>
	    <script src="directives/userSettingsDirective.js"></script>
	    <script src="directives/legendDirective.js"></script>
	    <script src="directives/shadowDirective.js"></script>
	    <script src="directives/checkBoxDirective.js"></script>
	    <script src="directives/shadowDirective.js"></script>
	    <script src="directives/tamuFocusDirective.js"></script>
	    <script src="directives/tooltipDirective.js"></script>
	    <script src="directives/dragAndDropListDirective.js"></script>
	    <script src="directives/lockingTextAreaDirective.js"></script>
	    <script src="directives/trashCanDirective.js"></script>
	    <script src="directives/selectedDirective.js"></script>
	    <script src="directives/dropZoneDirective.js"></script>

	    <!-- Services -->
	    <script src="services/sidebarService.js"></script>

	    <!-- Factories -->
		<script src="factories/dragAndDropListenerFactory.js"></script>
		
	    <!-- Models -->
	    <script src="model/configurableSettingsModel.js"></script>
	    <script src="model/customActionSettingsModel.js"></script>
	    <script src="model/organizationRepoModel.js"></script>
	    <script src="model/organizationCategoryRepo.js"></script>
	    <script src="model/userSettingsModel.js"></script>
	    <script src="model/userRepoModel.js"></script>
	    <script src="model/depositLocationRepoModel.js"></script>
	    <script src="model/graduationMonthRepoModel.js"></script>
	    <script src="model/emailTemplateRepo.js"></script>
	    <script src="model/embargoRepoModel.js"></script>
	    <script src="model/languageRepoModel.js"></script>
	    <script src="model/controlledVocabularyRepoModel.js"></script>
	    <script src="model/availableDocumentTypesRepoModel.js"></script>


	    <!-- Controllers -->
	    <script src="controllers/adminController.js"></script>
	    <script src="controllers/applicationSettingsController.js"></script>	    
	    <script src="controllers/headerController.js"></script>
	    <script src="controllers/settings/lookAndFeelController.js"></script>
        <script src="controllers/organizationSettingsController.js"></script>
        <script src="controllers/settings/triptychController.js"></script>
        <script src="controllers/settings/organizationManagementController.js"></script>
	    <script src="controllers/settingsController.js"></script>
	    <script src="controllers/sidebarController.js"></script>  
	    <script src="controllers/userRepoController.js"></script>  
	    <script src="controllers/workflowSettingsController.js"></script>  
	    <script src="controllers/whoHasAccessController.js"></script>
	    <script src="controllers/settings/customActionSettingsController.js"></script>
	    <script src="controllers/settings/depositLocationRepoController.js"></script> 
	    <script src="controllers/settings/embargoRepoController.js"></script>
	    <script src="controllers/settings/graduationMonthRepoController.js"></script> 
	    <script src="controllers/settings/emailTemplateRepoController.js"></script> 
	    <script src="controllers/settings/controlledVocabularyRepoController.js"></script>
	    <script src="controllers/settings/languageRepoController.js"></script> 
	    <script src="controllers/settings/availableDocumentTypesController.js"></script>
	    <script src="controllers/sideBars/organizationSideBarController.js"></script>
	    
	<!-- endbuild -->

	<!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
	<script>
		(function(b, o, i, l, e, r) {
			b.GoogleAnalyticsObject = l;
			b[l] || (b[l] = function() {
				(b[l].q = b[l].q || []).push(arguments)
			});
			b[l].l = +new Date;
			e = o.createElement(i);
			r = o.getElementsByTagName(i)[0];
			e.src = '//www.google-analytics.com/analytics.js';
			r.parentNode.insertBefore(e, r)
		}(window, document, 'script', 'ga'));
		ga('create', 'UA-XXXXX-X');
		ga('send', 'pageview');
	</script>

</body>
</html>
