describe('model: Submission', function () {
    var model, q, rootScope, scope, ActionLog, FieldValue, FileService, Organization, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, $rootScope, _ActionLog_, _FieldValue_, _FileService_ /*, _Organization_*/, _WsApi_) {
            q = $q;
            rootScope = $rootScope;

            ActionLog = _ActionLog_;
            FieldValue = _FieldValue_;
            FileService = _FileService_;
            // Organization = _Organization_;
            WsApi = _WsApi_;
        });
    };

    var initializeModel = function(settings) {
        inject(function (Submission) {
            scope = rootScope.$new();

            model = angular.extend(new Submission(), dataSubmission1);
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.actionLog');
        module('mock.fieldPredicate');
        module('mock.fieldValue');
        module('mock.fileService');
        //module('mock.organization');
        module('mock.user');
        module('mock.userService');
        module('mock.wsApi');

        initializeVariables();
        initializeModel();
    });

    describe('Is the model defined', function () {
        it('should be defined', function () {
            expect(model).toBeDefined();
        });
    });

    describe('Are the model methods defined', function () {

        it('addComment should be defined', function () {
            expect(model.addComment).toBeDefined();
            expect(typeof model.addComment).toEqual("function");
        });
        it('addFieldValue should be defined', function () {
            expect(model.addFieldValue).toBeDefined();
            expect(typeof model.addFieldValue).toEqual("function");
        });
        it('addMessage should be defined', function () {
            expect(model.addMessage).toBeDefined();
            expect(typeof model.addMessage).toEqual("function");
        });
        it('archiveFile should be defined', function () {
            expect(model.archiveFile).toBeDefined();
            expect(typeof model.archiveFile).toEqual("function");
        });
        it('assign should be defined', function () {
            expect(model.assign).toBeDefined();
            expect(typeof model.assign).toEqual("function");
        });
        it('changeStatus should be defined', function () {
            expect(model.changeStatus).toBeDefined();
            expect(typeof model.changeStatus).toEqual("function");
        });
        it('delete should be defined', function () {
            expect(model.delete).toBeDefined();
            expect(typeof model.delete).toEqual("function");
        });
        it('fetchDocumentTypeFileInfo should be defined', function () {
            expect(model.fetchDocumentTypeFileInfo).toBeDefined();
            expect(typeof model.fetchDocumentTypeFileInfo).toEqual("function");
        });
        it('file should be defined', function () {
            expect(model.file).toBeDefined();
            expect(typeof model.file).toEqual("function");
        });
        it('fileInfo should be defined', function () {
            expect(model.fileInfo).toBeDefined();
            expect(typeof model.fileInfo).toEqual("function");
        });
        it('findFieldValueById should be defined', function () {
            expect(model.findFieldValueById).toBeDefined();
            expect(typeof model.findFieldValueById).toEqual("function");
        });
        it('getContactEmails should be defined', function () {
            expect(model.getContactEmails).toBeDefined();
            expect(typeof model.getContactEmails).toEqual("function");
        });
        it('getFieldProfileByPredicate should be defined', function () {
            expect(model.getFieldProfileByPredicate).toBeDefined();
            expect(typeof model.getFieldProfileByPredicate).toEqual("function");
        });
        it('getFieldProfileByPredicateName should be defined', function () {
            expect(model.getFieldProfileByPredicateName).toBeDefined();
            expect(typeof model.getFieldProfileByPredicateName).toEqual("function");
        });
        it('getFieldValuesByFieldPredicate should be defined', function () {
            expect(model.getFieldValuesByFieldPredicate).toBeDefined();
            expect(typeof model.getFieldValuesByFieldPredicate).toEqual("function");
        });
        it('getFieldValuesByInputType should be defined', function () {
            expect(model.getFieldValuesByInputType).toBeDefined();
            expect(typeof model.getFieldValuesByInputType).toEqual("function");
        });
        it('getFileType should be defined', function () {
            expect(model.getFileType).toBeDefined();
            expect(typeof model.getFileType).toEqual("function");
        });
        it('getFlaggedFieldProfiles should be defined', function () {
            expect(model.getFlaggedFieldProfiles).toBeDefined();
            expect(typeof model.getFlaggedFieldProfiles).toEqual("function");
        });
        it('getPrimaryDocumentFieldProfile should be defined', function () {
            expect(model.getPrimaryDocumentFieldProfile).toBeDefined();
            expect(typeof model.getPrimaryDocumentFieldProfile).toEqual("function");
        });
        it('needsCorrection should be defined', function () {
            expect(model.needsCorrection).toBeDefined();
            expect(typeof model.needsCorrection).toEqual("function");
        });
        it('publish should be defined', function () {
            expect(model.publish).toBeDefined();
            expect(typeof model.publish).toEqual("function");
        });
        it('removeAllUnsavedFieldValuesByPredicate should be defined', function () {
            expect(model.removeAllUnsavedFieldValuesByPredicate).toBeDefined();
            expect(typeof model.removeAllUnsavedFieldValuesByPredicate).toEqual("function");
        });
        it('removeFieldValue should be defined', function () {
            expect(model.removeFieldValue).toBeDefined();
            expect(typeof model.removeFieldValue).toEqual("function");
        });
        it('removeFile should be defined', function () {
            expect(model.removeFile).toBeDefined();
            expect(typeof model.removeFile).toEqual("function");
        });
        it('removeUnsavedFieldValue should be defined', function () {
            expect(model.removeUnsavedFieldValue).toBeDefined();
            expect(typeof model.removeUnsavedFieldValue).toEqual("function");
        });
        it('renameFile should be defined', function () {
            expect(model.renameFile).toBeDefined();
            expect(typeof model.renameFile).toEqual("function");
        });
        it('saveFieldValue should be defined', function () {
            expect(model.saveFieldValue).toBeDefined();
            expect(typeof model.saveFieldValue).toEqual("function");
        });
        it('saveReviewerNotes should be defined', function () {
            expect(model.saveReviewerNotes).toBeDefined();
            expect(typeof model.saveReviewerNotes).toEqual("function");
        });
        it('sendAdvisorEmail should be defined', function () {
            expect(model.sendAdvisorEmail).toBeDefined();
            expect(typeof model.sendAdvisorEmail).toEqual("function");
        });
        it('sendEmail should be defined', function () {
            expect(model.sendEmail).toBeDefined();
            expect(typeof model.sendEmail).toEqual("function");
        });
        it('setSubmissionDate should be defined', function () {
            expect(model.setSubmissionDate).toBeDefined();
            expect(typeof model.setSubmissionDate).toEqual("function");
        });
        it('submit should be defined', function () {
            expect(model.submit).toBeDefined();
            expect(typeof model.submit).toEqual("function");
        });
        it('submitCorrections should be defined', function () {
            expect(model.submitCorrections).toBeDefined();
            expect(typeof model.submitCorrections).toEqual("function");
        });
        it('updateAdvisorApproval should be defined', function () {
            expect(model.updateAdvisorApproval).toBeDefined();
            expect(typeof model.updateAdvisorApproval).toEqual("function");
        });
        it('updateCustomActionValue should be defined', function () {
            expect(model.updateCustomActionValue).toBeDefined();
            expect(typeof model.updateCustomActionValue).toEqual("function");
        });
        it('validate should be defined', function () {
            expect(model.validate).toBeDefined();
            expect(typeof model.validate).toEqual("function");
        });
        it('validateFieldValue should be defined', function () {
            expect(model.validateFieldValue).toBeDefined();
            expect(typeof model.validateFieldValue).toEqual("function");
        });
    });

    describe('Are the model methods working as expected', function () {
        it('addComment should call WsApi', function () {
            spyOn(WsApi, "fetch").and.callThrough();

            model.addComment("test");
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        // FIXME: find a way to inject FieldValue to prevent "FieldValue is not a constructor in app/model/submission.js"
        /*it('addFieldValue should return a field value', function () {
            var response = model.addFieldValue(new mockFieldPredicate(q));
            expect (typeof response).toBe("object");
        });
        // FIXME: dst is undefined?
        /*it('addMessage should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.addMessage("test");
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });*/
        it('archiveFile should call WsApi', function () {
            var fieldValue = new mockFieldValue(q);
            fieldValue.mock(dataFieldValue2);

            spyOn(WsApi, "fetch").and.callThrough();

            model.archiveFile(fieldValue);
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('assign should call WsApi', function () {
            var response;

            spyOn(WsApi, "fetch");

            model.assign(new mockUser(q));
            scope.$apply();
            expect(WsApi.fetch).toHaveBeenCalled();

            response = model.assign();
            scope.$apply();
        });
        it('changeStatus should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.changeStatus("test");
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('delete should call WsApi', function () {
            var originalFetch = WsApi.fetch;
            var defer;

            spyOn(WsApi, "fetch").and.callThrough();

            model.delete();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();

            defer = q.defer();
            defer.resolve({
                meta: {
                    status: "INVALID",
                },
                payload: { ValidationResults: {} },
                status: 500
            });

            WsApi.fetch = originalFetch;
            spyOn(WsApi, "fetch").and.returnValue(defer.promise);

            model.delete();
            scope.$apply();
        });
        it('fetchDocumentTypeFileInfo should call WsApi', function () {
            model.fieldValues = [ new mockFieldValue(q), new mockFieldValue(q), new mockFieldValue(q) ];

            spyOn(model, "fileInfo").and.returnValue(payloadPromise(q.defer(), { ObjectNode: { size: 0 } } ));

            model.fieldValues[1].mock(dataFieldValue4);
            model.fieldValues[1].value = "test";

            model.fieldValues[2].mock(dataFieldValue6);
            model.fieldValues[2].value = "test";
            model.fieldValues[2].fieldPredicate.value = "_doctype_primary";

            model.fetchDocumentTypeFileInfo();
            scope.$apply();
        });
        it('file should call FileService', function () {
            spyOn(FileService, "anonymousDownload");

            model.file("uri");
            scope.$apply();

            expect(FileService.anonymousDownload).toHaveBeenCalled();
        });
        it('fileInfo should call WsApi', function () {
            var fieldValue = new mockFieldValue(q);
            fieldValue.mock(dataFieldValue2);

            spyOn(WsApi, "fetch");

            model.fileInfo(fieldValue);
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('findFieldValueById should return a FieldValue', function () {
            var response;
            model.fieldValues = [ new mockFieldValue(q), new mockFieldValue(q) ];
            model.fieldValues[1].mock(dataFieldValue2);

            response = model.findFieldValueById(2);
            expect(response.id).toBe(model.fieldValues[1].id);

            response = model.findFieldValueById(3);
            expect(response).toBe(null);
        });
        it('getContactEmails should return an array', function () {
            var response;
            var fieldValues = [ new mockFieldValue(q), new mockFieldValue(q) ];
            fieldValues[1].mock(dataFieldValue2);
            model.organization = new mockOrganization(q);

            spyOn(model, "getFieldValuesByInputType").and.returnValue(fieldValues);

            response = model.getContactEmails();
            expect(response.length).toBe(4);
        });
        it('getFieldProfileByPredicate should return a Field Predicate', function () {
            var response;
            var fieldPredicate = new mockFieldPredicate(q);

            response = model.getFieldProfileByPredicate(fieldPredicate);
            expect(response).toBe(null);

            model.submissionWorkflowSteps = [ new mockWorkflowStep(q), new mockWorkflowStep(q) ];
            model.submissionWorkflowSteps[1].mock(dataWorkflowStep2);

            response = model.getFieldProfileByPredicate(fieldPredicate);
            expect(response.id).toBe(1);
        });
        it('getFieldProfileByPredicateName should return a Field Profile', function () {
            var response;
            var fieldPredicate = new mockFieldPredicate(q);

            response = model.getFieldProfileByPredicateName(fieldPredicate.value);
            expect(response).toBe(null);

            model.submissionWorkflowSteps = [ new mockWorkflowStep(q), new mockWorkflowStep(q) ];
            model.submissionWorkflowSteps[1].mock(dataWorkflowStep2);

            // FIXME: asynchronous issues.
            /*response = model.getFieldProfileByPredicateName(fieldPredicate.value);
            expect(response.id).toBe(1);*/
        });
        it('getFieldValuesByFieldPredicate should return a Field Profile', function () {
            var response = null;
            var fieldPredicate = new mockFieldPredicate(q);

            response = model.getFieldValuesByFieldPredicate(fieldPredicate);
            expect(response.length).toBe(0);

            model.fieldValues = [ new mockFieldValue(q), new mockFieldValue(q) ];
            model.fieldValues[1].mock(dataFieldValue2);

            // FIXME: asynchronous issues.
            /*response = model.getFieldValuesByFieldPredicate(fieldPredicate);
            expect(response.length).toBe(1);*/
        });
        it('getFieldValuesByInputType should return an array', function () {
            var response;
            var inputType = new mockInputType(q);
            model.fieldValues = [ new mockFieldValue(q), new mockFieldValue(q) ];
            model.fieldValues[1].mock(dataFieldValue2);

            response = model.getFieldValuesByInputType("INPUT_TEXT");
            expect(response.length).toBe(0);

            model.submissionWorkflowSteps = [ new mockWorkflowStep(q), new mockWorkflowStep(q) ];
            model.submissionWorkflowSteps[1].mock(dataWorkflowStep2);

            // FIXME: asynchronous issues.
            /*response = model.getFieldValuesByInputType("INPUT_TEXT");
            expect(response.length).toBe(1);

            response = model.getFieldValuesByInputType("should not exist");
            expect(response.length).toBe(0);*/
        });
        it('getFileType should return an uppercase string', function () {
            var response;
            var fieldPredicate = new mockFieldPredicate(q);
            fieldPredicate.value = "123456789uppercase";

            response = model.getFileType(fieldPredicate);

            expect(response).toEqual("UPPERCASE");
        });
        it('getFlaggedFieldProfiles should return an array', function () {
            var response;
            var inputType = new mockInputType(q);

            response = model.getFlaggedFieldProfiles();
            scope.$apply();
            expect(response.length).toBe(0);

            model.submissionWorkflowSteps = [ new mockWorkflowStep(q), new mockWorkflowStep(q) ];
            model.submissionWorkflowSteps[1].mock(dataWorkflowStep2);

            response = model.getFlaggedFieldProfiles();
            scope.$apply();
            expect(response.length).toBe(0);

            model.submissionWorkflowSteps[1].aggregateFieldProfiles[0].flagged = true;

            response = model.getFlaggedFieldProfiles();
            scope.$apply();
            expect(response.length).toBe(1);
        });
        it('getPrimaryDocumentFieldProfile should return a Field Profile', function () {
            var response;

            response = model.getPrimaryDocumentFieldProfile();
            expect(response).toBe(null);

            model.submissionWorkflowSteps = [ new mockWorkflowStep(q), new mockWorkflowStep(q) ];
            model.submissionWorkflowSteps[1].mock(dataWorkflowStep2);

            response = model.getPrimaryDocumentFieldProfile();
            scope.$apply();
            expect(response.id).toBe(1);

            model.submissionWorkflowSteps[1].aggregateFieldProfiles[0].fieldPredicate.value = "test";

            response = model.getPrimaryDocumentFieldProfile();
            expect(response).toBe(null);
        });
        it('publish should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.publish(new mockDepositLocation(q));
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('needsCorrection should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.needsCorrection();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        // FIXME: find a way to inject FieldValue to prevent "FieldValue is not a constructor in app/model/submission.js"
        /*it('removeAllUnsavedFieldValuesByPredicate should call WsApi', function () {
            model.fieldValues = [ new mockFieldValue(q), new mockFieldValue(q) ];
            model.fieldValues[0].fieldPredicate = new mockFieldPredicate(q);
            model.fieldValues[1].fieldPredicate = new mockFieldPredicate(q);
            model.fieldValues[1].mock(dataFieldValue2);

            model.removeAllUnsavedFieldValuesByPredicate(model.fieldValues[0].fieldPredicate);
        });*/
        it('removeFieldValue should call WsApi', function () {
            var mfv = new mockFieldValue(q);

            spyOn(WsApi, "fetch").and.returnValue(Promise.resolve({
                body: JSON.stringify({
                    meta: { status: 'SUCCESS' },
                    payload: mfv
                })
            }));

            model.removeFieldValue(mfv);
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('removeFile should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.removeFile(new mockFieldValue(q));
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('renameFile should call WsApi', function () {
            var fieldValue = new mockFieldValue(q);
            fieldValue.mock(dataFieldValue2);

            spyOn(WsApi, "fetch");

            model.renameFile(fieldValue);
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('removeUnsavedFieldValue should call WsApi', function () {
            model.fieldValues = [ new mockFieldValue(q) ];

            model.removeUnsavedFieldValue(model.fieldValues[0]);
            expect(model.fieldValues.length).toBe(1);

            delete model.fieldValues[0].id;

            model.removeUnsavedFieldValue(model.fieldValues[0]);
            expect(model.fieldValues.length).toBe(0);
        });
        // FIXME: find a way to inject FieldValue to prevent "FieldValue is not a constructor in app/model/submission.js"
        /*
        it('saveFieldValue should call WsApi', function () {
            var fieldValue = new mockFieldValue(q);
            var fieldProfile = new mockFieldProfile(q);
            var originalFetch = WsApi.fetch;
            var payload = { FieldValue: fieldValue };

            spyOn(WsApi, "fetch").and.returnValue(payloadPromise(q.defer(), payload));

            model.saveFieldValue(fieldValue, fieldProfile);
            scope.$apply();

            fieldValue.value = "";

            model.saveFieldValue(fieldValue, fieldProfile);
            scope.$apply();

            WsApi.fetch = originalFetch;
            payload = { HashMap: [ "test message" ] };
            spyOn(WsApi, "fetch").and.returnValue(payloadPromise(q.defer(), payload, "INVALID", 500));

            model.saveFieldValue(fieldValue, fieldProfile);
            scope.$apply();
        });*/
        it('saveReviewerNotes should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.saveReviewerNotes([ "note" ]);
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('sendAdvisorEmail should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.sendAdvisorEmail();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('sendEmail should call WsApi', function () {
            var originalFetch = WsApi.fetch;
            var defer;

            spyOn(WsApi, "fetch").and.callThrough();

            model.sendEmail({});
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();

            defer = q.defer();
            defer.resolve({
                meta: {
                    status: "INVALID",
                },
                payload: { ValidationResults: {} },
                status: 500
            });

            WsApi.fetch = originalFetch;
            spyOn(WsApi, "fetch").and.returnValue(defer.promise);

            model.sendEmail({});
            scope.$apply();

        });
        it('setSubmissionDate should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.setSubmissionDate();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('submit should change the status', function () {
            spyOn(model, "changeStatus");

            model.submit();

            expect(model.changeStatus).toHaveBeenCalled();
        });
        // FIXME: asynchronous issues.
        /*it('submitCorrections should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.submitCorrections();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });*/
        it('updateAdvisorApproval should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.updateAdvisorApproval();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('updateCustomActionValue should call WsApi', function () {
            spyOn(WsApi, "fetch");

            model.updateCustomActionValue();
            scope.$apply();

            expect(WsApi.fetch).toHaveBeenCalled();
        });
        it('validate should set the is valid boolean', function () {
            var fieldProfile = new mockFieldProfile(q);
            fieldProfile.optional = false;
            fieldProfile.enabled = true;

            model.isValid = null;
            model.getMapping = function () {
                return {
                    validateFieldValue: {
                        endpoint: "/private/queue",
                        controller: "fake",
                        method: "fake"
                    }
                };
            };

            model.validate();
            scope.$apply();
            expect(model.isValid).toBe(true);

            model.fieldValues = [ new mockFieldValue(q), new mockFieldValue(q) ];
            model.fieldValues[0].fieldPredicate = new mockFieldPredicate(q);
            model.fieldValues[1].fieldPredicate = new mockFieldPredicate(q);
            model.fieldValues[1].mock(dataFieldValue2);
            model.fieldValues[1].value = "";
            model.submissionWorkflowSteps = [ new mockWorkflowStep(q), new mockWorkflowStep(q) ];
            model.submissionWorkflowSteps[1].mock(dataWorkflowStep2);
            model.submissionWorkflowSteps[1].aggregateFieldProfiles[0] = fieldProfile;
            fieldProfile.fieldPredicate = model.fieldValues[0].fieldPredicate;

            spyOn(model, "getFieldProfileByPredicate").and.returnValue(fieldProfile);

            model.validate();
            scope.$apply();
            expect(model.isValid).toBe(false);
        });
        // FIXME: find a way to inject WsApi to use mockMapping().
        /*it('validateFieldValue should call WsApi', function () {
            var fieldValue = new mockFieldValue(q);
            var fieldProfile = new mockFieldProfile(q);

            WsApi.mockMapping({
                validateFieldValue: {
                    endpoint: "/private/queue",
                    controller: "fake",
                    method: "fake"
                }
            });

            spyOn(WsApi, "fetch").and.callThrough();

            model.validateFieldValue(fieldValue, fieldProfile);
            scope.$apply();
            expect(WsApi.fetch).toHaveBeenCalled();

            fieldValue.value = "test";
            fieldProfile.optional = true;

            model.validateFieldValue(fieldValue, fieldProfile);
            scope.$apply();
        });*/
    });
});
