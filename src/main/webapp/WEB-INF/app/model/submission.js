var submissionModel = function () {

	return function Submission() {
		
		// additional model methods and variables

		return this;
	}

}

vireo.model("Submission", submissionModel);
vireo.model("StudentSubmission", submissionModel);