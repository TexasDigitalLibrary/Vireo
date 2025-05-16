const config = {
  path: './dist',

  // webpack dev server public path
  publicPath: '/vireo',

  terserOptions: {
    ecma: 5,
    format: {},
    parse: {},
    compress: {
      unused: false,
    },
    mangle: false,
    module: false
  },

  stats: {},

  client: {
    overlay: false,
  },

  copy: [
    {
      from: './node_modules/@wvr/core/app/resources/images',
      to: './node_modules/@wvr/core/app/resources/images'
    },
    {
      from: './node_modules/@wvr/core/app/views',
      to: './node_modules/@wvr/core/app/views'
    },
    {
      from: './node_modules/tinymce/skins',
      to: './skins'
    },
    {
      from: './build/appConfig.js.template',
      to: './appConfig.js',
      transform(content) {
        return content
          .toString()
          .replace('${AUTH_SERVICE_URL}', 'window.location.protocol + \'//\' + window.location.host + window.location.base + \'/mock/auth\'')
          .replace('${STOMP_DEBUG}', 'false')
          .replace('${LOCAL_AUTHENTICATION}', 'true');
      },
    },
  ],
  entry: {
    app: [
      './node_modules/jquery/dist/jquery.js',
      './node_modules/bootstrap/dist/js/bootstrap.js',
      './node_modules/sockjs-client/dist/sockjs.js',
      './node_modules/stompjs/lib/stomp.js',
      './node_modules/angular/angular.js',
      './node_modules/angular-sanitize/angular-sanitize.js',
      './node_modules/angular-route/angular-route.js',
      './node_modules/angular-loader/angular-loader.js',
      './node_modules/angular-messages/angular-messages.js',
      './node_modules/ng-csv/build/ng-csv.js',
      './node_modules/ng-sortable/dist/ng-sortable.js',
      './node_modules/ng-table/bundles/ng-table.js',
      './node_modules/ng-file-upload/dist/ng-file-upload-shim.js',
      './node_modules/ng-file-upload/dist/ng-file-upload.js',
      './node_modules/tinymce/tinymce.js',
      './node_modules/tinymce/themes/**/theme.js',
      './node_modules/tinymce/plugins/**/plugin.js',
      './node_modules/tinymce/icons/**/icons.js',
      './node_modules/angular-ui-tinymce/src/tinymce.js',
      './node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
      './node_modules/file-saver/src/FileSaver.js',
      './node_modules/@wvr/core/app/config/coreConfig.js',
      './node_modules/@wvr/core/app/components/version/version.js',
      './node_modules/@wvr/core/app/components/version/version-directive.js',
      './node_modules/@wvr/core/app/components/version/interpolate-filter.js',
      './src/main/webapp/app/config/apiMapping.js',
      './node_modules/@wvr/core/app/core.js',
      './node_modules/@wvr/core/app/setup.js',
      './node_modules/@wvr/core/app/config/coreRuntime.js',
      './node_modules/@wvr/core/app/config/coreAngularConfig.js',
      './node_modules/@wvr/core/app/config/logging.js',
      './node_modules/@wvr/core/app/constants/apiResponseActions.js',
      './node_modules/@wvr/core/app/constants/httpMethodVerbs.js',
      './node_modules/@wvr/core/app/directives/headerDirective.js',
      './node_modules/@wvr/core/app/directives/footerDirective.js',
      './node_modules/@wvr/core/app/directives/userDirective.js',
      './node_modules/@wvr/core/app/directives/modalDirective.js',
      './node_modules/@wvr/core/app/directives/alertDirective.js',
      './node_modules/@wvr/core/app/directives/validationMessageDirective.js',
      './node_modules/@wvr/core/app/directives/validatedSelectDirective.js',
      './node_modules/@wvr/core/app/directives/validatedTextAreaDirective.js',
      './node_modules/@wvr/core/app/services/accessControlService.js',
      './node_modules/@wvr/core/app/services/wsService.js',
      './node_modules/@wvr/core/app/services/wsApi.js',
      './node_modules/@wvr/core/app/services/restApi.js',
      './node_modules/@wvr/core/app/services/fileService.js',
      './node_modules/@wvr/core/app/services/authService.js',
      './node_modules/@wvr/core/app/services/storageService.js',
      './node_modules/@wvr/core/app/services/utilityService.js',
      './node_modules/@wvr/core/app/services/alertService.js',
      './node_modules/@wvr/core/app/services/validationStore.js',
      './node_modules/@wvr/core/app/services/userService.js',
      './node_modules/@wvr/core/app/services/modalService.js',
      './node_modules/@wvr/core/app/services/modelCache.js',
      './node_modules/@wvr/core/app/services/modelUpdateService.js',
      './node_modules/@wvr/core/app/repo/abstractRepo.js',
      './node_modules/@wvr/core/app/model/abstractModel.js',
      './node_modules/@wvr/core/app/model/assumedControl.js',
      './node_modules/@wvr/core/app/model/user.js',
      './node_modules/@wvr/core/app/controllers/abstractController.js',
      './node_modules/@wvr/core/app/controllers/coreAdminController.js',
      './node_modules/@wvr/core/app/controllers/authenticationController.js',
      './node_modules/@wvr/core/app/controllers/loginController.js',
      './node_modules/@wvr/core/app/controllers/registrationController.js',
      './node_modules/@wvr/core/app/controllers/userController.js',
      './node_modules/@wvr/core/app/controllers/errorPageController.js',
      './src/main/webapp/app/**/*(*.js)'
    ],
  }
}

module.exports.config = config;
