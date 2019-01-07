describe('controller: SettingsController', function () {

    var controller, q, scope, timeout, StudentSubmissionRepo, SubmissionStates;

    var initializeController = function(settings) {
        inject(function ($controller, $injector, $q, $rootScope, $timeout, $window, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _SubmissionStates_, _UserService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();
            timeout = $timeout;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            StudentSubmissionRepo = _StudentSubmissionRepo_;
            SubmissionStates = _SubmissionStates_;

            controller = $controller('SettingsController', {
                $scope: scope,
                $injector: $injector,
                $timeout: $timeout,
                $window: $window,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: settings && settings.StudentSubmissionRepo ? settings.StudentSubmissionRepo :  _StudentSubmissionRepo_,
                User: mockUser,
                UserService: _UserService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.degree');
        module('mock.modalService');
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmission');
        module('mock.studentSubmissionRepo');
        module('mock.user');
        module('mock.userService');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined for admin', function () {
            expect(controller).toBeDefined();
        });
        it('should be defined for manager', function () {
            initializeController({role: "ROLE_MANAGER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for reviewer', function () {
            initializeController({role: "ROLE_REVIEWER"});
            expect(controller).toBeDefined();
        });
        it('should be defined for student', function () {
            initializeController({role: "ROLE_STUDENT"});
            expect(controller).toBeDefined();
        });
        it('should be defined for anonymous', function () {
            initializeController({role: "ROLE_ANONYMOUS"});
            expect(controller).toBeDefined();
        });

    });

    describe('Are the scope methods defined', function () {
        it('confirmEdit should be defined', function () {
            expect(scope.confirmEdit).toBeDefined();
            expect(typeof scope.confirmEdit).toEqual("function");
        });
        it('delayedUpdateConfiguration should be defined', function () {
            expect(scope.delayedUpdateConfiguration).toBeDefined();
            expect(typeof scope.delayedUpdateConfiguration).toEqual("function");
        });
        it('editMode should be defined', function () {
            expect(scope.editMode).toBeDefined();
            expect(typeof scope.editMode).toEqual("function");
        });
        it('getFirstSubmissionId should be defined', function () {
            expect(scope.getFirstSubmissionId).toBeDefined();
            expect(typeof scope.getFirstSubmissionId).toEqual("function");
        });
        it('getUserSettingsValidations should be defined', function () {
            expect(scope.getUserSettingsValidations).toBeDefined();
            expect(typeof scope.getUserSettingsValidations).toEqual("function");
        });
        it('hasError should be defined', function () {
            expect(scope.hasError).toBeDefined();
            expect(typeof scope.hasError).toEqual("function");
        });
        it('hasSubmissions should be defined', function () {
            expect(scope.hasSubmissions).toBeDefined();
            expect(typeof scope.hasSubmissions).toEqual("function");
        });
        it('multipleSubmissions should be defined', function () {
            expect(scope.multipleSubmissions).toBeDefined();
            expect(typeof scope.multipleSubmissions).toEqual("function");
        });
        it('resetConfiguration should be defined', function () {
            expect(scope.resetConfiguration).toBeDefined();
            expect(typeof scope.resetConfiguration).toEqual("function");
        });
        it('saveDegree should be defined', function () {
            expect(scope.saveDegree).toBeDefined();
            expect(typeof scope.saveDegree).toEqual("function");
        });
        it('submissionInProgress should be defined', function () {
            expect(scope.submissionInProgress).toBeDefined();
            expect(typeof scope.submissionInProgress).toEqual("function");
        });
        it('submissionNeedsCorrections should be defined', function () {
            expect(scope.submissionNeedsCorrections).toBeDefined();
            expect(typeof scope.submissionNeedsCorrections).toEqual("function");
        });
        it('submissionsOpen should be defined', function () {
            expect(scope.submissionsOpen).toBeDefined();
            expect(typeof scope.submissionsOpen).toEqual("function");
        });
        it('updateConfiguration should be defined', function () {
            expect(scope.updateConfiguration).toBeDefined();
            expect(typeof scope.updateConfiguration).toEqual("function");
        });
        it('updateConfigurationPlainText should be defined', function () {
            expect(scope.updateConfigurationPlainText).toBeDefined();
            expect(typeof scope.updateConfigurationPlainText).toEqual("function");
        });
        it('updateUserSetting should be defined', function () {
            expect(scope.updateUserSetting).toBeDefined();
            expect(typeof scope.updateUserSetting).toEqual("function");
        });
        it('viewMode should be defined', function () {
            expect(scope.viewMode).toBeDefined();
            expect(typeof scope.viewMode).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('confirmEdit should process the event', function () {
            var event = {
                which: 13,
                target: {
                    blur: function() {}
                }
            };
            var property = "test";

            spyOn(event.target, 'blur');

            scope.confirmEdit(event, property);
            expect(event.target.blur).toHaveBeenCalled();
            expect(scope["edit" + property]).toBe(false);

            event.which = 0;
            event.target.blur = function() {};

            spyOn(event.target, 'blur');

            scope.confirmEdit(event);
            expect(event.target.blur).not.toHaveBeenCalled();
        });
        it('delayedUpdateConfiguration should update the configuration', function () {
            scope.pendingUpdate = false;
            scope.updateConfiguration = function(a, b) {};

            spyOn(timeout, "cancel").and.callThrough();

            // use synchronous timeout tests to confirm that the scope.pendingUpdate test properly cancels pending operations.
            jasmine.clock().install();
            setTimeout(function() {
                spyOn(scope, "updateConfiguration");

                expect(scope.pendingUpdate).toBe(true);

                scope.delayedUpdateConfiguration("type2", "name2");
            }, 100);

            scope.delayedUpdateConfiguration("type1", "name1");
            jasmine.clock().tick(101);
            jasmine.clock().uninstall();

            expect(timeout.cancel).toHaveBeenCalled();
        });
        it('editMode should update the edit mode', function () {
            var property = "test";
            scope["edit" + property] = null;

            scope.editMode(property);

            expect(scope["edit" + property]).toBe(true);
        });
        it('getFirstSubmissionId should return a submission id', function () {
            var result = scope.getFirstSubmissionId();
            expect(typeof result).toBe("number");
        });
        it('getUserSettingsValidations should return the validations array', function () {
            var result = scope.getUserSettingsValidations();
            expect(typeof result).toBe("object");
        });
        it('hasError should ', function () {
            var result = scope.hasError();
            expect(result).toBe(false);

            result = scope.hasError({a: null});
            expect(result).toBe(true);
        });
        it('hasSubmissions should return a boolean', function () {
            var result = scope.hasSubmissions();
            expect(result).toBe(true);
        });
        it('multipleSubmissions should return a boolean', function () {
            var result;
            scope.settings = {
                configurable: {
                    application: {
                        allow_multiple_submissions: {
                            value: "true"
                        }
                    }
                }
            };

            result = scope.multipleSubmissions();
            expect(result).toBe(true);

            scope.settings.configurable.application.allow_multiple_submissions.value = "FALSE";

            result = scope.multipleSubmissions();
            expect(result).toBe(false);
        });
        it('resetConfiguration should reset the configuration', function () {
            scope.settings = {
                configurable: {
                    a: {
                        b: {
                            reset: function() {}
                        }
                    }
                }
            };

            spyOn(scope.settings.configurable.a.b, "reset");

            scope.resetConfiguration("a", "b");

            expect(scope.settings.configurable.a.b.reset).toHaveBeenCalled();
        });
        it('saveDegree should save a degree', function () {
            var degree = new mockDegree(q);
            degree.mock(mockDegree1);

            scope.inProgress[degree.id] = null;

            spyOn(degree, "save").and.callThrough();

            scope.saveDegree(degree);
            scope.$digest();

            expect(scope.inProgress[degree.id]).toBe(false);
            expect(degree.save).toHaveBeenCalled();
        });
        it('submissionInProgress should return a boolean', function () {
            var result = scope.submissionInProgress();
            expect(result).toBe(true);

            // re-initialize the controller using a student submission repo stack without any IN_PROGRESS assigned.
            StudentSubmissionRepo.mock(mockStudentSubmissionRepo3);
            initializeController({StudentSubmissionRepo: StudentSubmissionRepo});

            result = scope.submissionInProgress();
            expect(result).toBe(false);
        });
        it('submissionNeedsCorrections should return a boolean', function () {
            var result = scope.submissionNeedsCorrections();
            expect(result).toBe(false);

            // re-initialize the controller using a student submission repo stack without any NEEDS_CORRECTIONS assigned.
            mockStudentSubmissionRepo3[0].submissionStatus.submissionState = SubmissionStates.NEEDS_CORRECTIONS;
            StudentSubmissionRepo.mock(mockStudentSubmissionRepo3);
            initializeController({StudentSubmissionRepo: StudentSubmissionRepo});

            result = scope.submissionNeedsCorrections();
            expect(result).toBe(true);
        });
        it('submissionsOpen should return a boolean', function () {
            var result;
            scope.settings = {
                configurable: {
                    application: {
                        submissions_open: {
                            value: "true"
                        }
                    }
                }
            };

            result = scope.submissionsOpen();
            expect(result).toBe(true);

            scope.settings.configurable.application.submissions_open.value = "FALSE";

            result = scope.submissionsOpen();
            expect(result).toBe(false);
        });
        it('updateConfiguration should update the configuration', function () {
            scope.settings = {
                configurable: {
                    a: {
                        b: {
                            save: function() {}
                        }
                    }
                }
            };

            spyOn(scope.settings.configurable.a.b, "save");

            scope.updateConfiguration("a", "b");

            expect(scope.settings.configurable.a.b.save).toHaveBeenCalled();
        });
        it('updateConfigurationPlainText should update the configuration', function () {
            scope.settings = {
                configurable: {
                    a: {
                        b: {
                            value: "<strong>Example Text</strong>",
                            save: function() {}
                        }
                    }
                }
            };

            spyOn(scope.settings.configurable.a.b, "save");

            scope.updateConfigurationPlainText("a", "b");

            expect(scope.settings.configurable.a.b.value).toBe("Example Text");
            expect(scope.settings.configurable.a.b.save).toHaveBeenCalled();
        });
        it('updateUserSetting should update the user settings', function () {
            spyOn(scope.settings.user, "save");

            scope.updateUserSetting();

            expect(scope.settings.user.save).toHaveBeenCalled();
            expect(scope.settings.user.UserSettings).not.toBeDefined();
        });
        it('viewMode should update the edit mode', function () {
            var property = "test";
            scope["edit" + property] = null;

            scope.viewMode(property);

            expect(scope["edit" + property]).toBe(false);
        });
    });

});
