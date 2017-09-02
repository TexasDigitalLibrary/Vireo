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

        useminPrepare: {
            html: '<%= build.app %>/index.html',
            options: {
                dest: '<%= build.app %>'
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
                separator: ';'
            },
            vendor: {
                src: [
					'node_modules/jquery/dist/jquery.min.js',
					'node_modules/bootstrap/dist/js/bootstrap.min.js',
					
					'node_modules/sockjs-client/dist/sockjs.min.js',
					'node_modules/stomp-websocket/lib/stomp.min.js',
					
					'node_modules/file-saver/FileSaver.min.js',
					
					'node_modules/angular/angular.min.js',
					
					'node_modules/angular-sanitize/angular-sanitize.min.js',
					'node_modules/angular-route/angular-route.min.js',
					'node_modules/angular-loader/angular-loader.min.js',
					'node_modules/angular-mocks/angular-mocks.js',
					'node_modules/angular-messages/angular-messages.js',
					
					'node_modules/ng-csv/build/ng-csv.min.js',
					
					'node_modules/ng-sortable/dist/ng-sortable.min.js',
					
					'node_modules/ng-table/dist/ng-table.min.js',
					
					'node_modules/ng-file-upload/ng-file-upload-shim.min.js',
					'node_modules/ng-file-upload/ng-file-upload.min.js',
					
					'node_modules/tinymce/tinymce.min.js',
					'node_modules/angular-ui-tinymce/dist/tinymce.min.js',
					
					'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js'
                ],
                dest: '<%= build.app %>/resources/scripts/vendor_concat.js'
            },
            core: {
                src: [
                    'node_modules/weaver-ui-core/**/*.js'
                ],
                dest: '<%= build.app %>/resources/scripts/core_concat.js'
            },
            angular: {
                src: [
                    '<%= build.app %>/**/*.js',
                    '<%= build.app %>/config/appConfig.js',
                    '!<%= build.app %>/config/appConfig_sample.js',
                    '!<%= build.app %>/node_modules/**/*',
                    '!<%= build.app %>/components/**/*',
                    '!<%= build.app %>/resources/**/*',
                    '!<%= build.app %>/resources/scripts/app_concat.js'
                ],
                dest: '<%= build.app %>/resources/scripts/app_concat.js'
            }
        },

        uglify: {
            options: {
                mangle: false
            },
            vendor: {
                src: '<%= build.app %>/resources/scripts/vendor_concat.js',
                dest: '<%= build.app %>/resources/scripts/vendor_concat.js'
            },
            core: {
                src: '<%= build.app %>/resources/scripts/core_concat.js',
                dest: '<%= build.app %>/resources/scripts/core_concat.js'
            },
            angular: {
                src: '<%= build.app %>/resources/scripts/app_concat.js',
                dest: '<%= build.app %>/resources/scripts/app_concat.js'
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

        watch: {
            css: {
                files: '**/*.scss',
                tasks: ['sass']
            }
        }

    });

    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-usemin');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-symlink');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['jshint', 'sass', 'symlink']);

    grunt.registerTask('watch', ['watch']);

    grunt.registerTask('develop', ['jshint', 'useminPrepare', 'concat', 'usemin', 'watch']);

    grunt.registerTask('deploy', ['jshint', 'useminPrepare', 'concat', 'uglify', 'usemin', 'sass']);

};
