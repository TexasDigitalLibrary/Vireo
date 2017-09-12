module.exports = function (grunt) {

    // Configurable paths
    var build = {
        app: 'src/main/webapp/WEB-INF/app'
    };


    grunt.initConfig({

        // Project settings
        build: build,

        symlink: {
            options: {
                overwrite: true,
                force: true
            },
            explicit: {
                src: 'node_modules',
                dest: 'src/main/webapp/WEB-INF/app/node_modules'
            }
        },

        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: [
                'Gruntfile.js',
                '<%= build.app %>/**/*.js',
                'node_modules/weaver-ui-core/**/*',
                'node_modules/weaver-ui-core/components/**/*',
                'node_modules/weaver-ui-core/resources/**/*',
                '!node_modules/**/*',
                '!<%= build.app %>/node_modules/**/*',
                '!<%= build.app %>/components/**/*',
                '!<%= build.app %>/resources/**/*'
            ]
        },

        concat: {
            options: {
                separator: ';',
                sourceMap: false
            },
            vendor: {
                src: [
                    'node_modules/jquery/dist/jquery.min.js',
                    'node_modules/bootstrap/dist/js/bootstrap.min.js',

                    'node_modules/sockjs-client/dist/sockjs.min.js',
                    'node_modules/stompjs/lib/stomp.min.js',

                    'node_modules/angular/angular.min.js',

                    'node_modules/angular-sanitize/angular-sanitize.min.js',
                    'node_modules/angular-route/angular-route.min.js',
                    'node_modules/angular-loader/angular-loader.min.js',
                    'node_modules/angular-messages/angular-messages.min.js',
                    'node_modules/angular-mocks/angular-mocks.js',

                    'node_modules/ng-csv/build/ng-csv.min.js',

                    'node_modules/ng-sortable/dist/ng-sortable.min.js',

                    'node_modules/ng-table/bundles/ng-table.min.js',

                    'node_modules/ng-file-upload/dist/ng-file-upload-shim.min.js',
                    'node_modules/ng-file-upload/dist/ng-file-upload.min.js',

                    'node_modules/tinymce/tinymce.min.js',
                    'node_modules/angular-ui-tinymce/dist/tinymce.min.js',

                    'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',

                    'node_modules/file-saver/FileSaver.min.js'
                ],
                dest: '<%= build.app %>/resources/scripts/vendor_concat.js'
            },
            core: {
                src: [
                    'node_modules/weaver-ui-core/app/config/coreConfig.js',

                    'node_modules/weaver-ui-core/app/components/version/version.js',
                    'node_modules/weaver-ui-core/app/components/version/version-directive.js',
                    'node_modules/weaver-ui-core/app/components/version/interpolate-filter.js',

                    '<%= build.app %>/config/appConfig.js',
                    '<%= build.app %>/config/apiMapping.js',

                    '<%= build.app %>/components/version/version.js',
                    '<%= build.app %>/components/version/version-directive.js',
                    '<%= build.app %>/components/version/interpolate-filter.js',

                    'node_modules/weaver-ui-core/app/core.js',
                    'node_modules/weaver-ui-core/app/setup.js',
                    'node_modules/weaver-ui-core/app/config/coreRuntime.js',
                    'node_modules/weaver-ui-core/app/config/coreAngularConfig.js',
                    'node_modules/weaver-ui-core/app/config/logging.js',

                    'node_modules/weaver-ui-core/app/directives/headerDirective.js',
                    'node_modules/weaver-ui-core/app/directives/footerDirective.js',
                    'node_modules/weaver-ui-core/app/directives/userDirective.js',
                    'node_modules/weaver-ui-core/app/directives/modalDirective.js',
                    'node_modules/weaver-ui-core/app/directives/alertDirective.js',
                    'node_modules/weaver-ui-core/app/directives/validationMessageDirective.js',
                    'node_modules/weaver-ui-core/app/directives/validatedSelectDirective.js',
                    'node_modules/weaver-ui-core/app/directives/validatedTextAreaDirective.js',

                    'node_modules/weaver-ui-core/app/services/accesscontrollservice.js',
                    'node_modules/weaver-ui-core/app/services/wsservice.js',
                    'node_modules/weaver-ui-core/app/services/wsapi.js',
                    'node_modules/weaver-ui-core/app/services/restapi.js',
                    'node_modules/weaver-ui-core/app/services/fileapi.js',
                    'node_modules/weaver-ui-core/app/services/authserviceapi.js',
                    'node_modules/weaver-ui-core/app/services/storageservice.js',
                    'node_modules/weaver-ui-core/app/services/utilityservice.js',
                    'node_modules/weaver-ui-core/app/services/alertservice.js',
                    'node_modules/weaver-ui-core/app/services/validationstore.js',
                    'node_modules/weaver-ui-core/app/services/userservice.js',
                    'node_modules/weaver-ui-core/app/services/modalservice.js',
                    'node_modules/weaver-ui-core/app/services/modelcache.js',
                    'node_modules/weaver-ui-core/app/services/modelupdateservice.js',

                    'node_modules/weaver-ui-core/app/repo/abstractRepo.js',

                    'node_modules/weaver-ui-core/app/model/abstractModel.js',
                    'node_modules/weaver-ui-core/app/model/assumedControl.js',
                    'node_modules/weaver-ui-core/app/model/user.js',

                    'node_modules/weaver-ui-core/app/controllers/abstractController.js',
                    'node_modules/weaver-ui-core/app/controllers/coreAdminController.js',
                    'node_modules/weaver-ui-core/app/controllers/authenticationController.js',
                    'node_modules/weaver-ui-core/app/controllers/loginController.js',
                    'node_modules/weaver-ui-core/app/controllers/registrationController.js',
                    'node_modules/weaver-ui-core/app/controllers/userController.js',
                    'node_modules/weaver-ui-core/app/controllers/errorpagecontroller.js',
                ],
                dest: '<%= build.app %>/resources/scripts/core_concat.js'
            },
            app: {
                src: [
                    '<%= build.app %>/**/*.js',
                    '!<%= build.app %>/config/appConfig.js',
                    '!<%= build.app %>/config/apiMapping.js',
                    '!<%= build.app %>/resources/**/*',
                    '!<%= build.app %>/components/**/*',
                    '!<%= build.app %>/node_modules/**/*'
                ],
                dest: '<%= build.app %>/resources/scripts/app_concat.js'
            },
            bundle: {
                src: [
                    '<%= build.app %>/resources/scripts/vendor_concat.js',
                    '<%= build.app %>/resources/scripts/core_concat.js',
                    '<%= build.app %>/resources/scripts/app_concat.js'
                ],
                dest: '<%= build.app %>/resources/scripts/bundle.js'
            }
        },

        uglify: {
            options: {
                mangle: false
            },
            bundle: {
                src: '<%= build.app %>/resources/scripts/bundle.js',
                dest: '<%= build.app %>/resources/scripts/bundle.js'
            }
        },

        usemin: {
            html: '<%= build.app %>/index.html',
            options: {
                assetsDirs: ['<%= build.app %>/resources/scripts']
            }
        },

        sass: {
            options: {
                sourceMap: false
            },
            dist: {
                files: [{
                    expand: true,
                    cwd: 'src/main/webapp/WEB-INF/app/resources/styles/sass',
                    src: ['*.scss'],
                    dest: 'src/main/webapp/WEB-INF/app/resources/styles',
                    ext: '.css'
                }]
            }
        },

        clean: {
            development: [
                '<%= build.app %>/node_modules'
            ],
            production: {
                folder: [
                    '<%= build.app %>/node_modules'
                ]
            }
        },

        copy: {
            styles: {
                files: [{
                    cwd: 'node_modules/ng-sortable/dist/',
                    src: 'ng-sortable.min.css',
                    dest: '<%= build.app %>/resources/styles/',
                    expand: true
                }]
            },
            tinymce: {
                files: [{
                    cwd: 'node_modules/tinymce/',
                    src: [
                        'plugins/**/*',
                        'themes/**/*',
                        'skins/**/*'
                    ],
                    dest: '<%= build.app %>/resources/scripts/',
                    expand: true
                }]
            },
            weaver: {
                files: [{
                    src: [
                        'node_modules/weaver-ui-core/**/*.html',
                        '!node_modules/weaver-ui-core/docs/'
                    ],
                    dest: '<%= build.app %>',
                    expand: true
                }]
            },
            fonts: {
                files: [{
                    src: [
                        'node_modules/bootstrap-sass/assets/fonts/bootstrap/*'
                    ],
                    dest: '<%= build.app %>',
                    expand: true
                }],
            }
        },

        watch: {
            css: {
                files: '**/*.scss',
                tasks: ['sass']
            }
        }

    });

    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-usemin');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-symlink');

    grunt.registerTask('default', ['jshint', 'sass', 'copy:styles', 'clean', 'symlink']);

    grunt.registerTask('watch', ['watch']);

    grunt.registerTask('develop', ['jshint', 'concat', 'usemin', 'sass', 'copy:styles', 'clean', 'symlink', 'watch']);

    grunt.registerTask('deploy', ['jshint', 'concat', 'uglify', 'usemin', 'sass', 'clean', 'copy']);

};
