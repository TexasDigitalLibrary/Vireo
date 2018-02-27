vireo.repo("SubmissionListColumnRepo", function SubmissionListColumnRepo() {

    // additional repo methods and variables
    
    this.findByTitle = function(title) {
        for(var i in this.getAll()) {
            var slc = this.getAll()[i];
            if(slc.title == title) {
                return slc;
            }
        }
    };
    
    return this;

});