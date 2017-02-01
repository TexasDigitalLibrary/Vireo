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
	
	<title>Vireo :: Texas Digital Library</title>
	
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<meta name="description" content="Electronic Thesis &amp; Dissertation Submission Workflow Tool">
	
	<link rel="stylesheet" href="bower_components/html5-boilerplate/css/normalize.css" />
	<link rel="stylesheet" href="bower_components/html5-boilerplate/css/main.css" />
	
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
			      			<a href ng-click="openModal('#loginModal')">Login</a>
			      		</li>
						<li class="dropdown">
			      			<a href ng-click="openModal('#verifyEmailModal')">Register</a>
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
								<li><a role="menuitem" href="submission/history">Submission History</a></li>
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
			      		<li ng-class="{'active': activeTab('/admin/list')}">
			      			<a href="admin/list">List</a>
			      		</li>
						<li ng-class="{'active': activeTab('/admin/view')}">
			      			<a href ng-click="viewSelect()">View</a>
			      		</li>
			      		<li ng-class="{'active': activeTab('/admin/log')}">
			      			<a href="admin/log">Log</a>
			      		</li>
			      		<li ng-class="{'active': activeTab('/admin/settings')}" class="settings-tab">
			      			<a href="admin/settings">Settings</a>
			      		</li>						
					</ul>
				</div>
			</div>
		</header>

		<alerts types="WARNING, ERROR"></alerts>
		<alerts seconds="45" channels="auth/register" types="SUCCESS"></alerts>
		<alerts seconds="45" channels="organization/delete" types="SUCCESS"></alerts>
				
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
	<script src="bower_components/FileSaver/FileSaver.min.js"></script>
	
	<script src="bower_components/sockjs-client/dist/sockjs.min.js"></script>
	<script src="bower_components/stomp-websocket/lib/stomp.min.js"></script>
	
	<script src="bower_components/angular/angular.js"></script>
	<script src="bower_components/angular-sanitize/angular-sanitize.min.js"></script>
	<script src="bower_components/angular-route/angular-route.js"></script>
	<script src="bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
	<script src="bower_components/angular-loader/angular-loader.js"></script>
	<script src="bower_components/angular-mocks/angular-mocks.js"></script>
	<script src="bower_components/angular-messages/angular-messages.js"></script>

	<script src="bower_components/tinymce-dist/tinymce.min.js"></script>
	<script src="bower_components/angular-ui-tinymce/dist/tinymce.min.js"></script>
	<script src="bower_components/ng-sortable/dist/ng-sortable.js"></script>
	<script src="bower_components/ng-csv/build/ng-csv.min.js"></script>

	<script src="bower_components/ng-file-upload/ng-file-upload-shim.min.js"></script>
	<script src="bower_components/ng-file-upload/ng-file-upload.min.js"></script>
	<script src="bower_components/ng-table/dist/ng-table.min.js"></script>

	
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
	    <script src="config/apiMapping.js"></script>

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
	    <script src="bower_components/core/app/directives/validationMessageDirective.js"></script>
	    <script src="bower_components/core/app/directives/validatedInputDirective.js"></script>
	    <script src="bower_components/core/app/directives/validatedSelectDirective.js"></script>
	    <script src="bower_components/core/app/directives/validatedTextAreaDirective.js"></script>

	    <!-- Services -->
	    <script src="bower_components/core/app/services/accesscontrollservice.js"></script>
	    <script src="bower_components/core/app/services/wsservice.js"></script>
	    <script src="bower_components/core/app/services/wsapi.js"></script>
	    <script src="bower_components/core/app/services/restapi.js"></script>
	    <script src="bower_components/core/app/services/fileapi.js"></script>
	  	<script src="bower_components/core/app/services/authserviceapi.js"></script>
	  	<script src="bower_components/core/app/services/modalservice.js"></script>
	  	<script src="bower_components/core/app/services/storageservice.js"></script>
	  	<script src="bower_components/core/app/services/utilityservice.js"></script>
	  	<script src="bower_components/core/app/services/alertservice.js"></script>
	  	<script src="bower_components/core/app/services/validationstore.js"></script>
	  	<script src="bower_components/core/app/services/userservice.js"></script>
	  	
	  	<!-- Repo -->
	  	<script src="bower_components/core/app/repo/abstractRepo.js"></script>

	  	<!-- Models -->
	    <script src="bower_components/core/app/model/abstractModel.js"></script>
	    <script src="bower_components/core/app/model/assumedControl.js"></script>
		<script src="bower_components/core/app/model/user.js"></script>

	    <!-- Controllers -->
	    <script src="bower_components/core/app/controllers/authenticationController.js"></script>
	    <script src="bower_components/core/app/controllers/registrationController.js"></script>
	    <script src="bower_components/core/app/controllers/loginController.js"></script>
	    <script src="bower_components/core/app/controllers/userController.js"></script>
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
	    <script src="directives/userSettingsDirective.js"></script>
	    <script src="directives/legendDirective.js"></script>
	    <script src="directives/checkBoxDirective.js"></script>
	    <script src="directives/tamuFocusDirective.js"></script>
	    <script src="directives/tooltipDirective.js"></script>
	    <script src="directives/dragAndDropListDirective.js"></script>
	    <script src="directives/lockingTextAreaDirective.js"></script>
	    <script src="directives/trashCanDirective.js"></script>
	    <script src="directives/selectedDirective.js"></script>
	    <script src="directives/dropZoneDirective.js"></script>
	    <script src="directives/fieldProfileDirective.js"></script>
	    <script src="directives/fieldProfileDisplayDirective.js"></script>
	    <script src="directives/reviewSubmissionFieldsDirective.js"></script>
	    <script src="directives/submissionNoteDirective.js"></script>
	    <script src="directives/stringToDateDirective.js"></script>
	    <script src="directives/triptychDirective.js"></script>
	    <script src="directives/infoDirective.js"></script>

	    <!-- Services -->
	    <script src="services/sidebarService.js"></script>
	    <script src="services/itemViewService.js"></script>

	    <!-- Repos -->	    
	    <script src="repo/abstractAppRepo.js"></script>
	    <script src="repo/advisorSubmissionRepo.js"></script>
	    <script src="repo/languageRepo.js"></script>
	    <script src="repo/attachmentTypeRepo.js"></script>
	    <script src="repo/customActionDefinitionRepo.js"></script>
	    <script src="repo/depositLocationRepo.js"></script>
	    <script src="repo/emailTemplateRepo.js"></script>
	    <script src="repo/graduationMonthRepo.js"></script>
	    <script src="repo/configurationRepo.js"></script>
	    <script src="repo/controlledVocabularyRepo.js"></script>
	    <script src="repo/workflowStepRepo.js"></script>
	    <script src="repo/embargoRepo.js"></script>
	    <script src="repo/userRepo.js"></script>
	    <script src="repo/organizationRepo.js"></script>
	    <script src="repo/organizationCategoryRepo.js"></script>
	    <script src="repo/inputTypeRepo.js"></script>
	    <script src="repo/fieldPredicateRepo.js"></script>
	    <script src="repo/fieldGlossRepo.js"></script>	    
	    <script src="repo/studentSubmissionRepo.js"></script>
	    <script src="repo/savedFilterRepo.js"></script>
	    <script src="repo/submissionRepo.js"></script>
	    <script src="repo/submissionStateRepo.js"></script>
	    <script src="repo/noteRepo.js"></script>
	    <script src="repo/fieldProfileRepo.js"></script>
	    <script src="repo/submissionListColumnRepo.js"></script>
	    <script src="repo/managerSubmissionListColumnRepo.js"></script>
	    <script src="repo/managerFilterColumnRepo.js"></script>
	    <script src="repo/customActionValueRepo.js"></script>

	    <!-- Models --> 
		<script src="model/abstractAppModel.js"></script>
	    <script src="model/namedSearchFilterGroup.js"></script>
		<script src="model/language.js"></script>
		<script src="model/attachmentType.js"></script>
		<script src="model/customActionDefinition.js"></script>
		<script src="model/depositLocation.js"></script>
		<script src="model/emailTemplate.js"></script>
		<script src="model/graduationMonth.js"></script>
		<script src="model/userSettings.js"></script>
		<script src="model/configuration.js"></script>
		<script src="model/controlledVocabulary.js"></script>
		<script src="model/workflowStep.js"></script>
		<script src="model/embargo.js"></script>
		<script src="model/organization.js"></script>
		<script src="model/organizationCategory.js"></script>
	    <script src="model/inputType.js"></script>
	    <script src="model/fieldPredicate.js"></script>
	    <script src="model/fieldGloss.js"></script>	    
	    <script src="model/savedFilter.js"></script>
	    <script src="model/submission.js"></script>
	    <script src="model/submissionState.js"></script>
	    <script src="model/fieldProfile.js"></script>
	    <script src="model/fieldValue.js"></script>
	    <script src="model/note.js"></script>
	    <script src="model/submissionListColumn.js"></script>
	    <script src="model/customActionValue.js"></script>

	    <!-- Constants -->
		<script src="constants/emailRecipientType.js"></script>
		<script src="constants/inputType.js"></script>		

	    <!-- Factories -->
		<script src="factories/dragAndDropListenerFactory.js"></script>


	    <!-- Controllers -->
	    <script src="controllers/abstractController.js"></script>
	    <script src="controllers/adminController.js"></script>
	    <script src="controllers/submission/advisorReviewController.js"></script>
	    <script src="controllers/applicationSettingsController.js"></script>	    
	    <script src="controllers/settings/emailWorkflowRulesController.js"></script>	    
	    <script src="controllers/headerController.js"></script>
	    <script src="controllers/settings/lookAndFeelController.js"></script>
        <script src="controllers/organizationSettingsController.js"></script>
        <script src="controllers/settings/organizationManagementController.js"></script>
	    <script src="controllers/settingsController.js"></script>
	    <script src="controllers/sidebarController.js"></script>  
	    <script src="controllers/userRepoController.js"></script>
	    <script src="controllers/settings/customActionSettingsController.js"></script>
	    <script src="controllers/settings/depositLocationRepoController.js"></script> 
	    <script src="controllers/settings/embargoRepoController.js"></script>
	    <script src="controllers/settings/graduationMonthRepoController.js"></script> 
	    <script src="controllers/settings/emailTemplateRepoController.js"></script> 
	    <script src="controllers/settings/controlledVocabularyRepoController.js"></script>
	    <script src="controllers/settings/languagesController.js"></script>
	    <script src="controllers/settings/attachmentTypesController.js"></script>
	    <script src="controllers/sideBars/organizationSideBarController.js"></script>
	    <script src="controllers/settings/organizationCategoriesController.js"></script>
	    <script src="controllers/settings/fieldProfileManagementController.js"></script>
	    <script src="controllers/settings/noteManagementController.js"></script>
	    <script src="controllers/submission/newSubmissionController.js"></script>
	    <script src="controllers/submission/studentSubmissionController.js"></script>
	    <script src="controllers/submission/submissionListController.js"></script>
	    <script src="controllers/submission/submissionHistoryController.js"></script>
	    <script src="controllers/submission/submissionCompleteController.js"></script>
	    <script src="controllers/submission/itemViewController.js"></script>
	    
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
