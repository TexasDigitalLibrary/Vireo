

/**********************************************************
 * Generic Sortable tools (used on several of the tabs)
 **********************************************************/

/**
 * Swap out the current element and make it editable. This is used for all
 * single value sortables, like custom actions, majors, departments, etc...
 * 
 * @param element
 *            The element being swapped.
 */
function swapToEditable(element) {
	var $element = jQuery(element);
	if (!$element.is("li"))
		$element = $element.closest("li");

	// Make sure other fields have been closed.
	if (jQuery(".editing").length > 0) {
		swapFromEditable(jQuery(".editing"));
	}

	if ($element.attr("id").indexOf("action_") == 0) {
		// Make the field editable
		var actionId = $element.attr('id');
		var label = jQuery.trim($element.find("a.action-editable").text());
		
		while (label.indexOf("'") > -1) {
			label = label.replace("'","&#39;");
		}
		
		$element.replaceWith("<li id='" + actionId + "'><span class='editing'><input type='text' value='"+label+"' placeholder='"+label+"'/><i class='icon-remove'></i><i class='icon-ok'></i></span></li>");
	}
}

/**
 * Swap out the current element to be a display version. This is used for all
 * single value sortables, like custom actions, majors, departments, etc...
 * 
 * @param element
 *            The element being swapped.
 */
function swapFromEditable(element) {

	// Find the parent element
	var $element = jQuery(element);
	if (!$element.is("li"))
		$element = $element.closest("li");

	var id = $element.attr("id");
	if (id.indexOf("action_") == 0) {

		var actionId = $element.attr('id');
		var label = $element.find("input").attr("placeholder");
		
		$element.replaceWith("<li id='" + actionId + "'><a class='action-editable' href='#'><em class='icon-pencil'></em> " + label + "</a></li>");

	}

}

/**
 * This is a global click handler for sortables being edited. If the click event
 * is outside an ".editing" element them swapFromEditable() is called to cancel
 * the editing of whatever the current field is.
 * 
 * @returns A Callback function.
 */
function sortableGlobalCancelEditHandler() {
	return function(event) {
		if (jQuery(event.target).closest(".editing").length == 0) {
			if (jQuery(".editing").length > 0) {
				swapFromEditable(jQuery(".editing"));
			}
		}
		// ignore
	}
}

/**
 * Handler to cancel the editing of the current item.
 * 
 * @returns A Callback function.
 */
function sortableCancelEditHandler() {
	return function(event) {

		swapFromEditable(this);
		event.stopPropagation();
		return false;
	}
}

/**
 * Handler to start editing of the current item.
 * 
 * @returns A Callback function
 */
function sortableStartEditHandler() {
	return function(event) {

		swapToEditable(this);

		event.stopPropagation();
		return false;
	}
}

/**
 * A handler to save the current item.
 * 
 * There are a few assumptions being made by this handler. First all items
 * should be in a list with an id of the form "type-list". And all element
 * should have the id: "#type_id" as well.
 * 
 * @param type
 *            The type of objects: action, major, degree, etc...
 * @param jsonURL
 *            The url where re-order events should be sent.
 * @returns A Callback function
 */
function sortableSaveEditHandler(type,jsonURL) {
	
	return function(event) {

		// If this is a key press only do something when the user hits enter.
		if (event.type == "keypress" && event.which != 13)
			return;

		var id = jQuery(this).closest("li").attr('id');
		var label = jQuery(this).closest("li").find("input").val();
		jQuery("#"+type+"-list").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			clearAlert(type + "-edit-" + id);
			jQuery("#"+type+"-list").removeClass("waiting");

			jQuery("#"+type+"_" + data.id).replaceWith("<li id='"+type+"_" + data.id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> " + data.label + "</a></li>");
		}

		var failureCallback = function(message) {
			jQuery("#action-list").removeClass("waiting");
			displayAlert(type + "-edit-" + id, "Unable to edit "+type, message);
		}

		
		var data = {};
		data[type+'Id'] = id;
		data["label"] = label;
		
		console.log("%o",data);
		
		jQuery.ajax({
			url : jsonURL,
			data : data,
			dataType : 'json',
			type : 'POST',
			success : function(data) {
				if (data.success) {
					successCallback(data);
				} else {
					failureCallback(data.message)
				}
			},
			error : function(one,two,three) {
				failureCallback("Unable to communicate with the server.");
			}

		});
		event.stopPropagation();
		return false;
	};
}


/**
 * Handler for the update event from a JQuery sortable element. The returned
 * callback function will do two things, either remove the element if it is
 * being hovered over the trashcan, or reorder the element based upon the new
 * position.
 * 
 * @param type
 *            The type of objects: action, major, degree, etc...
 * @param reorderURL
 *            The JSON url for reordering an element.
 * @param removeURL
 *            The JSON url for removing an element
 * @returns A Callback function
 */
function sortableUpdateHandler(type, reorderURL, removeURL) {

	return function(event,ui) {

		if (this.id == type+'-remove') {
			var id = ui.item.attr('id');

			jQuery("#"+id).remove();

			var successCallback = function(data) {
				// Remove the ajax loading indicators & alerts
				clearAlert(type + "-remove-" + id);

				// Check to see if we need to hide the trashcan
				if (jQuery("#"+type+"-list li").length == 0) {
					jQuery("#"+type+"-remove").fadeOut();
				}
			}

			var failureCallback = function (message) {
				displayAlert(type + "-remove-" + id,"Unable to remove "+type,message);
			}

			var data = {};
			data[type+'Id'] = id;
			
			jQuery.ajax({
				url : removeURL,
				data : data,
				dataType : 'json',
				type : 'POST',
				success: function(data){
					if (data.success) {
						successCallback(data);
					} else {
						failureCallback(data.message)
					}
				},
				error: function(){
					failureCallback("Unable to communicate with the server.");
				}

			});



		} else {

			var list = jQuery("#"+type+"-list").sortable('toArray').toString();

			var successCallback = function(data) {
				// Remove the ajax loading indicators & alerts
				clearAlert(type + "-reorder");
			}

			var failureCallback = function (message) {
				displayAlert(type + "-reorder","Unable to reorder "+type,message);
			}

			var data = {};
			data[type+'Ids'] = list;
			
			jQuery.ajax({
				url : reorderURL,
				data : data,
				dataType : 'json',
				type : 'POST',
				success : function(data){
					if (data.success) {
						successCallback(data);
					} else {
						failureCallback(data.message)
					}
				},
				error : function(){
					failureCallback("Unable to communicate with the server.");
				}

			});
		};
	};
}


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

			jQuery("#action-list").append("<li id='action_" + data.id + "'><a class='action-editable' href='#'><em class='icon-pencil'></em> " + data.label + "</a></li>");
			
			
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

