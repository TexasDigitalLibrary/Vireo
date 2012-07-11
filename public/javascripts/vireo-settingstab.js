

/**********************************************************
 * My Profile (shown on all setting tabs)
 **********************************************************/

/**
 * Handle updating the my-profile sidebox on all settings tab. This is things
 * like display name, preferred email address, and weather you want to be CCed
 * all the time.
 * 
 * @param jsonURL
 *            The json url to submit profile updates too
 */
function myProfileHandler(jsonURL) {
	return function () {
		var $this = jQuery(this);
		var field = $this.attr('name');
		var value = $this.val();

		if ("ccEmail" == field) {
			value = $this.attr('checked');
		}

		jQuery("#my-profile").addClass("waiting");
		jQuery("#my-preferences").addClass("waiting");


		var successCallback = function(data) {

			// Remove the ajax loading indicators
			jQuery("#my-profile").removeClass("waiting");
			jQuery("#my-preferences").removeClass("waiting");

			// Clear any previous errors
			$this.parent("fieldset").removeClass("error");
			clearAlert("profile-alert-"+field);

			// Username at the upper left hand corner
			jQuery("#personal-bar a:first-of-type b").text(data.displayName);

			// Profile box
			jQuery("#my-profile ul:first-of-type li").text(data.displayName);
			jQuery("#my-profile ul:last-of-type li").text(data.currentEmailAddress);

			// The input field
			jQuery("#displayName").val(data.displayName);
		}

		var failureCallback = function (message) {
			jQuery("#my-profile").removeClass("waiting");
			jQuery("#my-preferences").removeClass("waiting");

			$this.parent("fieldset").addClass("error");
			displayAlert("profile-alert-"+field,"Unable to update profile",message);
		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'field': field,
				'value': value
			},
			dataType:'json',
			type:'POST',
			success:function(data){
				if (data.success) {
					successCallback(data);
				} else {
					failureCallback(data.message)
				}
			},
			error:function(){
				failureCallback("Unable to communicate with the server.");
			}

		});  
	}
}


/**********************************************************
 * User Preference Tab
 **********************************************************/

/**
 * Handle updating user preferences, like what the default options should be
 * under the view tab. All of these are simple checkbox toggles.
 * 
 * @param jsonURL
 *            The JSON url to submit updates to
 * @returns A Callback function
 */
function userPreferenceHandler(jsonURL) {

	return function () {
		var $this = jQuery(this);
		var field = $this.attr('name');
		value = $this.attr('checked');

		$this.closest("fieldset").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			$this.closest("fieldset").removeClass("waiting");
			$this.closest("fieldset").removeClass("error");
			clearAlert("user-preference-"+field);
		}

		var failureCallback = function (message) {
			$this.closest("fieldset").removeClass("waiting");
			$this.closest("fieldset").addClass("error");
			displayAlert("user-preference-"+field,"Unable to update preference",message);
		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'field': field,
				'value': value
			},
			dataType:'json',
			type:'POST',
			success:function(data){
				if (data.success) {
					successCallback(data);
				} else {
					failureCallback(data.message)
				}
			},
			error:function(){
				failureCallback("Unable to communicate with the server.");
			}

		});  
	};
}


/**********************************************************
 * Application Settings Tab
 **********************************************************/


/**
 * Handler for the application settings field to save their state via ajax. This
 * method supports all the toggleable fields on this page, as well as submision
 * semester and submission instructions.
 * 
 * @param jsonURL The JSON url to submit updates too.
 * @returns A Callback function
 */
function applicationSettingsHandler(jsonURL) {

	return function () {
		var $this = jQuery(this);
		var field = $this.attr('name');
		var value = $this.val();

		$this.parent().addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			$this.parent().removeClass("waiting");
			clearAlert("application-setting-"+field);
		}

		var failureCallback = function (message) {
			$this.parent().removeClass("waiting");
			displayAlert("application-setting-"+field,"Unable to update setting",message);
		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'field': field,
				'value': value
			},
			dataType:'json',
			type:'POST',
			success:function(data){
				if (data.success) {
					successCallback(data);
				} else {
					failureCallback(data.message)
				}
			},
			error:function(){
				failureCallback("Unable to communicate with the server.");
			}

		});  
	};
}


/**
 * Handler for the update event from a JQuery sortable element. The returned
 * callback function will do two things, either remove the element if it is
 * being hovered over the trashcan, or reorder the element based upon the new
 * position.
 * 
 * @param reorderURL
 *            The JSON url for reordering an element.
 * @param removeURL
 *            The JSON url for removing an element
 * @returns A Callback function
 */
function updateSortableHandler(reorderURL, removeURL) {

	return function(event,ui) {

		if (this.id == 'action-remove') {
			var actionId = ui.item.attr('id');

			jQuery("#"+actionId).remove();

			var successCallback = function(data) {
				// Remove the ajax loading indicators & alerts
				clearAlert("application-setting-actions-remove");

				// Check to see if we need to hide the trashcan
				if (jQuery("#action-list li").length == 0) {
					jQuery("#action-remove").fadeOut();
				}
			}

			var failureCallback = function (message) {
				displayAlert("application-setting-actions-remove","Unable to remove actions",message);
			}

			jQuery.ajax({
				url:removeURL,
				data:{
					'actionId': actionId
				},
				dataType:'json',
				type:'POST',
				success:function(data){
					if (data.success) {
						successCallback(data);
					} else {
						failureCallback(data.message)
					}
				},
				error:function(){
					failureCallback("Unable to communicate with the server.");
				}

			});  



		} else {

			var list = jQuery("#action-list").sortable('toArray').toString();
			
			var successCallback = function(data) {
				// Remove the ajax loading indicators & alerts
				clearAlert("application-setting-actions-reorder");
			}

			var failureCallback = function (message) {
				displayAlert("application-setting-actions-reorder","Unable to reorder actions",message);
			}

			jQuery.ajax({
				url:reorderURL,
				data:{
					'actionIds': list
				},
				dataType:'json',
				type:'POST',
				success:function(data){
					if (data.success) {
						successCallback(data);
					} else {
						failureCallback(data.message)
					}
				},
				error:function(){
					failureCallback("Unable to communicate with the server.");
				}

			});  
		};
	};
}

/**
 * Handler for adding a new action to the list. The method will handle
 * everything needed to update the dialog form, ajax request to add the new
 * action, and error handling.
 * 
 * @param jsonURL
 *            The JSON url to add new custom actions.
 * @returns A Callback function
 */
function saveAddActionHandler(jsonURL) {
	return function() {
		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			jQuery("#add-action-label").closest('.control-group').removeClass("error");
			clearAlert("application-setting-actions");

			jQuery("#action-list").append("<li id='action_" + data.id + "'>" + data.label + "</li>");

			jQuery("#add-action-dialog").fadeOut();
			jQuery("#action-remove").fadeIn();
			jQuery("#add-action-label").val("");
			jQuery("#add-action-dialog .control-group").removeClass("error");
		}

		var failureCallback = function(message) {
			jQuery("#add-action-label").closest('.control-group').addClass("error");
			displayAlert("application-setting-actions","Unable to update setting", message);
		}

		label = jQuery("#add-action-label").val();

		jQuery.ajax({
			url : jsonURL,
			data : {
				'label' : label
			},
			dataType : 'json',
			type : 'POST',
			success : function(data) {
				if (data.success) {
					successCallback(data);
				} else {
					failureCallback(data.message)
				}
			},
			error : function() {
				failureCallback("Unable to communicate with the server.");
			}

		});

		return false;
	};
}

/**
 * Handler for the cancel button when adding an action. This will clear out the
 * form and fadeout dialog form.
 * 
 * @returns A Callbackfunction
 */
function cancelAddActionHandler() {

	return function() {
		jQuery("#add-action-dialog").fadeOut();
		jQuery("#add-action-label").val("");
		jQuery("#add-action-dialog .control-group").removeClass("error");
		return false;
	};

}

