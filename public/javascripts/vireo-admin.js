/**
 * Common javascript routines that are shared between pages. 
 * 
 * Everything in this file *must* have qunit tests for verification.
 */


/**
 * Register general handles that appear across several admin pages.
 * 
 * Please keep these to a minimum. We do not want too many callbacks registered
 * globally, try and only include handlers on individual pages. Then only if
 * they are used often should they be considered to be included here.
 */
jQuery(document).ready(function(){

	// Expand / Collapse sidebar boxes
	jQuery(".box-head").click(toggleSideBoxHandler());

	// Tab navigation with-in page
	jQuery(".edit-holder .nav a").click(tabNavigationHandler());

	// Show more elements within group
	jQuery('.more').click(moreHandler());

	// Confirm links
	jQuery('.confirm').live("click", confirmHandler());	
});


/**********************************************************
 * General Handlers (which are used above)
 **********************************************************/

/**
 * Handle the expand and collapse of the sidebar boxes which are very prevalent
 * throughout the entire administrative interface.
 * 
 * <div class="side-box">
 * 	  <div class="box-head"></div> (Trigger)
 *    <div class="box-body"></div> (Add or remove the expanded class)
 * </div>
 * 
 * @returns A Callback function
 */
function toggleSideBoxHandler() {
	return function(){        			
		var box = jQuery(this).parent(".side-box");
		box.find(".box-body").toggle();
		if(jQuery(this).hasClass("expanded")){
			jQuery(this).removeClass("expanded");
			jQuery(this).find(".expand").html("[+]");
		} else {
			jQuery(this).addClass("expanded");
			jQuery(this).find(".expand").html("[-]");
		}
	};
}

/**
 * Handle dynamic tab navigation within a page. At the time of writting this is
 * only used on the view page to switch between Personal Info, Document Info,
 * and Degree Info tabs.
 * 
 * @returns A Callback function
 */
function tabNavigationHandler() {
	return function(){
		if(jQuery(this).parent("li").hasClass("active")){
			//Do Nothing
		} else {
			jQuery(".edit-holder .nav li").each(function(){
				jQuery(this).removeClass("active");
			});
			jQuery(this).parent("li").addClass("active");
			var name = jQuery(this).attr("class");
			jQuery(".edit-box-holder").each(function(){
				jQuery(this).addClass("hidden");
			});
			jQuery(".edit-box-holder[id="+name+"]").removeClass("hidden");
		}
	};
}

/**
 * Handle expanding lists.
 * 
 * Remove the hidden class from all sibling elements and then remove this
 * element. This is used on both the list and log tabs to handle long lists of
 * filter options.
 * 
 * @returns A Callback function
 */
function moreHandler() {
	return function(){
		jQuery(this).parent().find(".hidden").each(function(){
			jQuery(this).removeClass("hidden");
		});
		jQuery(this).remove();
	};
}

/**
 * Handle confirming links.
 * 
 * Sometimes a link is more than a link. This handler will require that the link
 * be clicked twice, the first click will replace the link text with "(Are you
 * sure?)". Then only upon the second link will the link be followed.
 * 
 * @returns A Callback function
 */
function confirmHandler() {	
	return function() {
		 if (jQuery(this).text().indexOf("Are you sure?") >= 0) {
			 return true;
		 } else {
			 jQuery(this).text("(Are you sure?)");
			 return false;
		 }
	 };
}

/**********************************************************
 * General Handlers (not used in this file)
 **********************************************************/

/**
 * Handler to fadeOut the selected element.
 * 
 * @param selector
 *            The element to fade out.
 * @returns A Callback function
 */
function fadeOutHandler(selector) {
	return function() {
		jQuery(selector).fadeOut();
		return false;
	}
}

/**
 * Handler to fodeIn the selected element
 * 
 * @param selector
 *            The element to fade in.
 * @returns A Callback function.
 */
function fadeInHandler(selector) {
	return function() {
		jQuery(selector).fadeIn();
		return false;
	}
}

/**
 * Handle to slide in and slide out selected elements.
 * 
 * @param selector
 *            The element to be slide
 * @returns A Callback function
 */
function slideToggleHandler(selector) {
	return function() {
		jQuery(selector).slideToggle();
		return false;
	}
}


/**********************************************************
 * Miscellaneous Functions
 **********************************************************/

/**
 * General function to display an alert message.
 * 
 * If this alert has never been displayed before then a new alert box will be
 * appended to the alert-area. If the alert has been displayed before then it's
 * contents is replaced with the new error message.
 * 
 * id: Unique identifier for this error message. It will be used later for
 * clearing the error. heading: The text that should be bolded in the error
 * message. message: A detailed description of the error.
 */
function displayAlert(id, heading, message) {

	var alert = jQuery("<div id='"
			+ id
			+ "' class='alert alert-error'><button data-dismiss='alert' class='close' type='button'>×</button><p><strong>"
			+ heading + "</strong>: " + message + "</p></div>");

	if (jQuery("#alert-area #" + id).length == 0) {
		// This is a new alert, that has never been seen before
		alert.appendTo(jQuery("#alert-area")).fadeIn();

	} else {
		// An allert of this id allready, exists. Replace it.
		jQuery("#alert-area #" + id).replaceWith(alert);
	}
}

/**
 * General function to clear out stale alert messages.
 */
function clearAlert(id) {
	jQuery("#alert-area #" + id).fadeOut().remove();
}

/**
 * Function to convert new lines in <br /> tags.
 */
function nl2br (str, is_xhtml) {   
	var breakTag = (is_xhtml || typeof is_xhtml === 'undefined') ? '<br />' : '<br>';    
	return (str + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1'+ breakTag +'$2');
}

/**
 * Function to escape double quotes.
 */
function escapeQuotes (str) {
	return(str).replace('"', '&#34;');
}