/**
 * Handle confirming batch changes on the list page.
 * 
 * Sometimes a link is more than a link. This handler will require that the link
 * be clicked twice, the first click will replace the link text with "This will 
 * affect XXX records. Are you sure?". Then only upon the second link will 
 * the link be followed.
 * 
 * @returns A Callback function
 */
function batchConfirmHandler(numRecords) {	
	return function() {
		 if (jQuery(this).text().indexOf("Are you sure?") >= 0) {
			 return true;
		 } else {
			 jQuery(this).text("This will affect "+numRecords+" record(s). Are you sure?");
			 jQuery(this).addClass("btn-danger");
			 return false;
		 }
	 };
}