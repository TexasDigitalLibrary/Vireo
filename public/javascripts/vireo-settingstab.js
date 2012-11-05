

/**********************************************************
 * Generic Sortable tools (used on several of the tabs)
 **********************************************************/

/**
 * Display a sortable item. This method will look at the type field being passed
 * and do different displayed based upon the value presented. It will either
 * display a simple text field-based value, or a pair of a text field and degree
 * level dropdown.
 * 
 * Here are the supported types: action, college, department, major,
 * graduationMonth, degree, documentType, and emailTemplate.
 * 
 * @param type
 *            The type of sortable being displayed.
 * @param editable
 *            Weather the field shouldbe displayed as editable or static.
 * @param $element
 *            The current element in the list to be replaced.
 * @param id
 *            The unique id of the object.
 * @param name
 *            The object's name.
 * @param level
 *            The object's degree level (as an integere, and optional)
 */
function displaySortableItem(type, editable, $element, id, name, level) {
		
	if (
	    type == "action" ||
	    type == "college" ||
	    type == "department" ||
	    type == "major" ||
	    type == "graduationMonth"
	   ) {
	
		if (editable) {
			$element.replaceWith("<li id='" + id + "'><span class='editing'><input type='text' value='"+name+"' placeholder='"+name+"'/><i class='icon-remove'></i><i class='icon-ok'></i></span></li>");
		} else {
			$element.replaceWith("<li id='" + id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> " + name + "</a></li>");
		}
	
	} else if (
		type == "degree" ||
		type == "documentType"
		) {
		
		if (editable) {
			
			// Note: the values for degree levels are hard coded. They should come from the 
			// Degree level enum.
			
			$element.replaceWith(
					"<li id='" + id + "'>"+
					"<span class='editing'>"+
					"<input type='text' value='"+name+"' placeholder='"+name+"'/>"+
					"<select placeholder='"+level+"'>"+
                    "<option value='2'>UNDERGRADUATE</option>"+
                    "<option value='3'>MASTERS</option>"+
                    "<option value='4'>DOCTORAL</option>"+
                    "</select>"+
					"<i class='icon-remove'></i>"+
					"<i class='icon-ok'></i>"+
					"</span>"+
					"</li>");
			jQuery("#"+id+" select").val(level);
		} else {
			
			var levelText = "NONE";
			if (level == "2" || level == 2)
				levelText = "UNDERGRADUATE";
			if (level == "3" || level == 3)
				levelText = "MASTERS";
			if (level == "4" || level == 4)
				levelText = "DOCTORAL";
			
			
			$element.replaceWith("<li id='" + id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> <span class='name'>" + name + "</span> <span class='level info'>("+levelText+")</span></a></li>");
		}
		
	} else if (
		type == "emailTemplate"
		) {
		
		if (editable) {
			
			$element.attr('id',id);
			$element.html(
					 "<div class='editing'>"+
					 "   <span>" + name + "</span>" +
					 "   <form class='form-horizontal' style='display: none;'>"+
					 "       <fieldset>"+
					 "           <div class='control-group'>"+
					 "               <label class='control-label' for='edit-emailTemplate-name'><strong>Name</strong></label>"+
					 "               <div class='controls'>"+
					 "                   <input type='text' class='input-xxlarge' id='edit-emailTemplate-name'/>"+
					 "               </div>"+
					 "           </div>"+
					 "           <div class='control-group'>"+
					 "               <label class='control-label' for='edit-emailTemplate-subject'><strong>Subject</strong></label>"+
					 "               <div class='controls'>"+
					 "                   <input type='text' class='input-xxlarge' id='edit-emailTemplate-subject'/>"+
					 "               </div>"+
					 "           </div>"+
					 "           <div class='control-group'>"+
					 "               <label class='control-label' for='edit-emailTemplate-message'><strong>Message</strong></label>"+
					 "               <div class='controls'>"+
					 "                   <textarea id='edit-emailTemplate-message' class='input-xxlarge'></textarea>"+
					 "               </div>"+
					 "           </div>"+
					 "           <div class='control-group'>"+
					 "               <div class='controls'>"+
					 "                   <button id='edit-emailTemplate-save' class='btn btn-primary'>Save</button>"+
					 "                   <button id='edit-emailTemplate-delete' class='btn'>Delete</button>"+
					 "                   <button id='edit-emailTemplate-cancel' class='btn'>Cancel</button>"+
					 "               </div>"+
					 "           </div>"+
					 "       </fieldset>"+
					 "   </form>"+
					 "</div>"
					 );
			
			// Animate the from's appearance
			$element.find("form").slideToggle();
			
			// Trigger loading the data from the server for this template
			$element.addClass("retrieve");
			$element.click();
			$element.removeClass("retrieve");
		} else {
			
			if ($element.attr('id')) {
				$element.find("form").slideToggle(null,function() {
					$element.replaceWith("<li id='" + id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> " + name + "</a></li>");				
				});
			} else {
				$element.replaceWith("<li id='" + id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> " + name + "</a></li>");
			}
		}
		
	}

}


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

	var id = $element.attr("id");
	var type = id.substring(0,id.indexOf("_"));
	
	if (
		type == "action" ||
		type == "college" ||
		type == "department" ||
		type == "major" ||
	    type == "graduationMonth"
	   ) {
		// Make the field editable
		var name = jQuery.trim($element.find("a."+type+"-editable").text());
		
		while (name.indexOf("'") > -1) {
			name = name.replace("'","&#39;");
		}
		
		displaySortableItem(type, true, $element, id, name);
	} else if (
		type == "degree" ||
		type == "documentType"
		) {
		// Make the field editable
		var name = jQuery.trim($element.find("a."+type+"-editable .name").text());
		var levelText = jQuery.trim($element.find("a."+type+"-editable .level").text());
		
		var level = 1;
		if (levelText.indexOf("UNDERGRADUATE") >= 0)
			level = 2;
		if (levelText.indexOf("MASTERS") >= 0)
			level = 3;
		if (levelText.indexOf("DOCTORAL") >= 0)
			level = 4;
		
		
		while (name.indexOf("'") > -1) {
			name = name.replace("'","&#39;");
		}
		
		displaySortableItem(type, true, $element, id, name, level);
	} else if (
		type == "emailTemplate"
	  ) {
		// Make the field editable
		var name = jQuery.trim($element.find("a."+type+"-editable").text());
		
		displaySortableItem(type, true, $element, id, name);		
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
	var type = id.substring(0,id.indexOf("_"));

	
	if (
		type == "action" ||
		type == "college" ||
		type == "department" ||
		type == "major" ||
	    type == "graduationMonth"
	   ) {

		var name = $element.find("input").attr("placeholder");
		
		displaySortableItem(type, false, $element, id, name);
	} else if (
	    type == "degree" ||
	    type == "documentType"
	   ) {

		var name = $element.find("input").attr("placeholder");
		var level = $element.find("select").attr("placeholder");
		
		displaySortableItem(type, false, $element, id, name, level);
	} else if (
			type == "emailTemplate"
	  ) {
		// Make the field editable
		var name = jQuery.trim($element.find("div.editing span").text());
		
		displaySortableItem(type, false, $element, id, name);		
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
		var name = jQuery(this).closest("li").find("input").val();
		var level = jQuery(this).closest("li").find("select").val(); 
		jQuery("#"+type+"-list").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			clearAlert(type + "-edit-" + id);
			jQuery("#"+type+"-list").removeClass("waiting");
			jQuery("#"+id).removeClass("settings-sortable-error");


			displaySortableItem(type,false,jQuery("#"+type+"_" + data.id), type+"_"+data.id, data.name, data.level);

		}

		var failureCallback = function(message) {
			jQuery("#"+type+"-list").removeClass("waiting");
			jQuery("#"+id).addClass("settings-sortable-error");
			displayAlert(type + "-edit-" + id, "Unable to edit "+type, message);
		}

		var data = {};
		data[type+'Id'] = id;
		data["name"] = name;
		if (level)
			data["level"] = level;
				
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
		
		event.stopPropagation();
	};
}


/**
 * Handler for adding a new action to the list. The method will handle
 * everything needed to update the dialog form, ajax request to add the new
 * action, and error handling.
 * 
 * @param type
 * 			  The type of objects: action, major, degree, etc...
 * @param jsonURL
 *            The JSON url to add new custom actions.
 * @returns A Callback function
 */
function saveAddActionHandler(type, jsonURL) {
	return function() {
		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			jQuery("#add-"+type+"-name").closest('.control-group').removeClass("error");
			if (jQuery("#add-"+type+"-level").length > 0)
				jQuery("#add-"+type+"-level").closest('.control-group').removeClass("error");
			if (jQuery("#add-"+type+"-subject").length > 0)
				jQuery("#add-"+type+"-subject").closest('.control-group').removeClass("error");
			if (jQuery("#add-"+type+"-message").length > 0)
				jQuery("#add-"+type+"-message").closest('.control-group').removeClass("error");
			clearAlert(type+"-add");
			
			var $newElement = jQuery("<li/>").appendTo(jQuery("#"+type+"-list"));
			displaySortableItem(type,false,$newElement, type+"_"+data.id, data.name, data.level);

			jQuery("#add-"+type+"-dialog").slideToggle();
			if (jQuery("#"+type+"-remove").length > 0)
				jQuery("#"+type+"-remove").fadeIn();
			jQuery("#add-"+type+"-name").val("");
			if (jQuery("#add-"+type+"-level").length > 0)
				jQuery("#add-"+type+"-level").val("-1");
			jQuery("#add-"+type+"-dialog .control-group").removeClass("error");
		}

		var failureCallback = function(message) {
			jQuery("#add-"+type+"-name").closest('.control-group').addClass("error");
			
			if (jQuery("#add-"+type+"-level").length > 0)
				jQuery("#add-"+type+"-level").closest('.control-group').addClass("error");

			if (jQuery("#add-"+type+"-subject").length > 0)
				jQuery("#add-"+type+"-subject").closest('.control-group').addClass("error");
			
			if (jQuery("#add-"+type+"-message").length > 0)
				jQuery("#add-"+type+"-message").closest('.control-group').addClass("error");
			
			displayAlert(type+"-add","Unable to add "+type, message);
		}

		var data = {};
		
		data.name = jQuery("#add-"+type+"-name").val();
		if (jQuery("#add-"+type+"-level").length > 0)
			data.level = jQuery("#add-"+type+"-level").val();
		
		if (jQuery("#add-"+type+"-subject").length > 0)
			data.subject = jQuery("#add-"+type+"-subject").val();
		
		if (jQuery("#add-"+type+"-message").length > 0)
			data.message = jQuery("#add-"+type+"-message").val();

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
 * @param type
 * 			  The type of objects: action, major, degree, etc..
 * @returns A Callback function
 */
function cancelAddActionHandler(type) {

	return function() {
		jQuery("#add-"+type+"-dialog").slideToggle();
		jQuery("#add-"+type+"-name").val("");
		if (jQuery("#add-"+type+"-level").length > 0)
			jQuery("#add-"+type+"-level").val("-1");
		
		if (jQuery("#add-"+type+"-subject").length > 0)
			jQuery("#add-"+type+"-subject").val("");
		
		if (jQuery("#add-"+type+"-message").length > 0)
			jQuery("#add-"+type+"-message").val("");
		
		jQuery("#add-"+type+"-dialog .control-group").removeClass("error");
		return false;
	};

}


/**
 * Retrieve an email template from the server and fill in it's editable fields.
 * If the email template is a system template then the name field and delete
 * field are disabled.
 * 
 * @param jsonURL
 *            The url where templates may be retrieved from.
 * @returns A Callback function
 */
function retrieveEmailTemplateHandler(jsonURL) {
	
	return function() {
		jQuery(this).addClass("waiting");
		var id = jQuery(this).attr("id");
		
		var successCallback = function(data) {
			// Remove loading indicator and alerts
			jQuery("#"+id).removeClass("waiting");
			clearAlert("emailTemplate-retrieve-"+id);
			
			jQuery("#"+id+" #edit-emailTemplate-name").val(data.name);
			jQuery("#"+id+" #edit-emailTemplate-subject").val(data.subject);
			jQuery("#"+id+" #edit-emailTemplate-message").val(data.message);

			if (data.system) {
				jQuery("#"+id+" #edit-emailTemplate-name").attr("disabled","true");
				jQuery("#"+id+" #edit-emailTemplate-name").attr("title","System defined templates may not be renamed or deleted.");
				
				jQuery("#"+id+" form fieldset").prepend("<div class='control-group warning'><label class='control-label'><p><strong>System Template</strong></p></label><div class='controls'><p id='system-warning' class='help-inline'></p></div></div>");
				if (data.name == "SYSTEM Initial Submission") {
					jQuery("#"+id+" #system-warning").html("This system defined template is sent to the student immediatly after completing their submission.");
				} else if (data.name == "SYSTEM Advisor Review Request") {
					jQuery("#"+id+" #system-warning").html("This system defined template is sent to the student's advisor requesting their approval of the submission and embargo options.");					
				} else if (data.name == "SYSTEM Verify Email Address") {
					jQuery("#"+id+" #system-warning").html("This system defined template is sent to the any user who has forgotten their password, and is attempting to recover it. It is very important that the template contains the {REGISTRATION_URL} variable otherwise the user would not be able to recover their account. If Vireo is configured to use a Single-Sign-On system this option may not be available.");										
				} else if (data.name == "SYSTEM New User Registration") {
					jQuery("#"+id+" #system-warning").html("This system defined template is sent to any new user when they register their account. It is very important that the template contains the {REGISTRATION_URL} variable otherwise the user would not be able to complete the registration process. If Vireo is configured to use a Single-Sign-On system this option may not be available.");										
				} else {
					jQuery("#"+id+" #system-warning").html("This system defined template.");
				}
				jQuery("#"+id+" #system-warning").append(" The template may not be renamed or deleted.");
				
				
				jQuery("#"+id+" #edit-emailTemplate-delete").addClass("disabled","true");
				jQuery("#"+id+" #edit-emailTemplate-delete").attr("title","System defined templates may not be renamed or deleted.");

			}

		}

		var failureCallback = function(message) {
			displayAlert("emailTemplate-retrieve-"+id,"Unable to load email template", message);
		}
		
		jQuery.ajax({
			url : jsonURL,
			data : {
				emailTemplateId: id
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
 * Delete an email template.
 * 
 * @param jsonURL
 *            The url where templates may be deleted.
 * @returns A Callback function
 */
function removeEmailTemplateHandler(jsonURL) {
	
	return function() {
		// Don't allow deleting of system email templates
		if (jQuery(this).hasClass("disabled"))
			return false;
		
		var $element = jQuery(this).closest("li");
		$element.addClass("waiting");
		var id = $element.attr("id");
		
		var successCallback = function(data) {
			// Remove loading indicator and alerts
			jQuery("#"+id).removeClass("waiting");
			clearAlert("emailTemplate-delete-"+id);
			
			jQuery("#"+id).slideToggle(null, function () {
				jQuery("#"+id).remove();
			});
		}

		var failureCallback = function(message) {
			displayAlert("emailTemplate-delete-"+id,"Unable to remove email template", message);
			
			jQuery("#"+id+" .control-group").each( function() {
				jQuery(this).addClass("error");
			});
		}
		
		jQuery.ajax({
			url : jsonURL,
			data : {
				emailTemplateId: id
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
 * Save an email template.
 * 
 * @param jsonURL
 *            The url where templates may be saved.
 * @returns A Callback function
 */
function editEmailTemplateHandler(jsonURL) {
	
	return function() {
		var $element = jQuery(this).closest("li");
		$element.addClass("waiting");
		var id = $element.attr("id");

		var name = $element.find("#edit-emailTemplate-name").val();
		var subject = $element.find("#edit-emailTemplate-subject").val();
		var message = $element.find("#edit-emailTemplate-message").val();

		
		var successCallback = function(data) {
			// Remove loading indicator and alerts
			jQuery("#"+id).removeClass("waiting");
			clearAlert("emailTemplate-delete-"+id);
			
			jQuery("#"+id+" .control-group").each( function() {
				jQuery(this).removeClass("error");
			});
			
			displaySortableItem('emailTemplate',false,$element, "emailTemplate_"+data.id, data.name);
		}

		var failureCallback = function(message) {
			$element.removeClass("waiting");
			displayAlert("emailTemplate-edit-"+id,"Unable to save email template", message);
			
			jQuery("#"+id+" .control-group").each( function() {
				jQuery(this).addClass("error");
			});
		}
		
		jQuery.ajax({
			url : jsonURL,
			data : {
				emailTemplateId: id,
				name: name,
				subject: subject,
				message: message
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
			$this.removeClass("settings-error");
			clearAlert("application-setting-"+field);
		}

		var failureCallback = function (message) {
			$this.parent().removeClass("waiting");
			$this.addClass("settings-error");
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
 * Callback handler to unlock a text area field under the application settings
 * tab. These fields have the "readonly" attribute set to prevent accedential
 * editing. Clicking a link with this call back will remove the readonly
 * attribute, and change the mesage to indicate that the field may now be
 * edited.
 */
function applicationUnlockField() {
	 var selector = jQuery(this).attr("href");
	 jQuery(selector).removeAttr("readonly");
	 jQuery(this).replaceWith("<em class='icon-pencil'></em> This field is unlocked and may be edited. Leaving the page will re-lock the field.");
	 return false;
}


/**
 * Handler to search for new members within the "Add Member" dialog box. This
 * will perform the search and replace the dialog box's contents which new HTML
 * returned from the server. Note this same handler is used for both pagination
 * and the search button.
 * 
 * @returns A Callback function
 */
function memberSearchHandler() {
	return function () {

		jQuery("#add-member-modal").addClass("waiting");

		// Follow pagination links
		var url = "";
		var data = {};
		if (jQuery(this).is("a")) {
			url = jQuery(this).attr("href");
		} else {
			url = jQuery("#add-member-modal form").attr("action");
			var bb = jQuery("#members-search-query");
			data["query"] = bb.val();
			data["offset"] = 0;
		}

		jQuery.ajax({
			url : url,
			data : data,
			dataType : 'html',
			type : 'POST',
			success : function(data) {
				jQuery("#add-member-modal .modal-body").replaceWith(data).remove();
				jQuery("#add-member-modal").removeClass("waiting"); 
			},
			error:function(){
				alert("Error unable to communicate with server.");
			}
		});


		return false;
	};
}

/**
 * Handler to add a new member found using the "add member" dialog box. This
 * call back handles clicking on the add button to add them as a reviewer.
 * 
 * @param htmlURL
 *            The url to update a person's role. (returns html NOT json)
 * @returns A Callback function
 */
function memberAddHandler(htmlURL) {
	return function () {

		jQuery("#add-member-modal").addClass("waiting");

		// Follow pagination links
		var id = jQuery(this).closest("tr").attr("id");
		var role = "2" // We always add people at the reviewer level

			var $this = jQuery(this);


		jQuery.ajax({
			url : htmlURL,
			data : {
				personId : id,
				role : role
			},
			dataType : 'html',
			type : 'POST',
			success : function(data) {
				jQuery("#members-table tbody").replaceWith(data).remove();
				$this.replaceWith("<span>Added</span>").remove();
				jQuery("#add-member-modal").removeClass("waiting"); 
			},
			error:function(){
				alert("Error unable to communicate with server.");
			}
		});


		return false;

	};
}

/**
 * Handler to update a current reviewer's role. This will switch the input to an
 * editable select list and attach the nessesary handlers to modify or cancel
 * the entry.
 * 
 * @param htmlURL
 *            The url to update a person's role. (returns html NOT json)
 * @returns A Callback function
 */
function memberUpdateHandler(htmlURL) {
	return function () {

		jQuery(this).hide();
		jQuery(this).closest("tr").find(".member-editing").show();


		// Attache a cancel handler
		var cancelHandler = function (event) {
			if (jQuery(event.target).closest(".member-editing").length == 0) {

				jQuery(".member-editing").each(function () {
					jQuery(this).hide();
					jQuery(this).find("select").unbind("click");
				})
				jQuery(".member-editable").each(function () {
					jQuery(this).show();
				})

				// Unregister this event
				jQuery(document).unbind("click",cancelHandler);
			}
		};

		// Attach a change handler
		var changeHandler = function () {
			var id = jQuery(this).closest("tr").attr("id");
			var role = jQuery(this).val();
			jQuery("#members-table").addClass("waiting");

			if (jQuery(this).hasClass("this-member")) {
				if (!confirm("You are about to change your current role! It may mean that you will no longer be able to access these settings tabs.")) {
					jQuery("#members-table").removeClass("waiting");
					jQuery(document).click();
					jQuery(this).unbind("change");
					return false; 
				}
			}

			jQuery(document).unbind("click",cancelHandler);
			jQuery(this).unbind("change");

			jQuery.ajax({
				url : htmlURL,
				data : {
					personId : id,
					role : role
				},
				dataType : 'html',
				type : 'POST',
				success : function(data) {
					jQuery("#members-table tbody").replaceWith(data).remove();
					jQuery("#members-table").removeClass("waiting"); 
				},
				error:function(){
					alert("Error unable to communicate with server.");
				}
			});
		};

		jQuery(this).closest("tr").find(".member-editing select").change(changeHandler);
		jQuery(document).click(cancelHandler);


		return false;

	};
}


/**********************************************************
 * Email Settings Tab
 **********************************************************/


/**
 * Handler for the email settings fields to save their state via ajax. This
 * method only supports the three toggleable settings at the top of the page,
 * not the templates further down, those are sortables.
 * 
 * 
 * @param jsonURL
 *            The JSON url to submit updates too.
 * @returns A Callback function
 */
function emailSettingsHandler(jsonURL) {

	return function () {
		var $this = jQuery(this);
		var field = $this.attr('name');
		var value = $this.attr('checked');

		$this.parent().addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			$this.parent().removeClass("waiting");
			clearAlert("email-setting-"+field);
		}

		var failureCallback = function (message) {
			$this.parent().removeClass("waiting");
			displayAlert("email-setting-"+field,"Unable to update setting",message);
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
 * Configurable Settings Tab (Embargos)
 **********************************************************/

/**
 * Event handler to toggle the display of embargo type's duration between
 * indeterminate and determinate.
 * 
 * @returns A Callback function
 */
function embargoDurationToggleHandler() {
	return function() {

		if (jQuery("#timeframe-indeterminate").attr("checked")) {
			// Indeterminate
			jQuery("#embargoType-months").attr("disabled", "true");
			jQuery("#duration-group").slideUp();

		} else {
			// Determinate
			jQuery("#embargoType-months").attr("disabled", null);
			jQuery("#duration-group").slideDown();
		}

	}
}

/**
 * Open the embargo type dialog box. This will work for opening an existing
 * embargo, or adding a new embargo.
 * 
 * @returns A Callback function
 */
function embargoOpenDialogHandler() {
	return function () {

		if (jQuery(this).closest("tr").length > 0) {
			// Loading an existing type
			var $row = jQuery(this).closest("tr"); 
			jQuery("#embargoType-id").val($row.attr("id"));
			jQuery("#embargoType-name").val(jQuery.trim($row.find(".embargoType-name-cell").text()));
			jQuery("#embargoType-description").val(jQuery.trim($row.find(".embargoType-description-cell").text()));

			if ($row.find(".embargoType-active-cell").text().indexOf("Yes") > -1)
				jQuery("#embargoType-active").attr("checked","true");
			else
				jQuery("#embargoType-active").attr("checked");

			if ($row.find(".embargoType-duration-cell").text().indexOf("Indefinite") > -1) {
				jQuery("#timeframe-indeterminate").attr("checked","true");
				jQuery("#embargoType-months").val("");
				jQuery("#embargoType-months").attr("disabled","true");
				jQuery("#duration-group").hide();
			} else {
				jQuery("#timeframe-determinate").attr("checked","true");
				jQuery("#embargoType-months").val(jQuery.trim($row.find(".embargoType-duration-cell").text()));
				jQuery("#embargoType-months").attr("disabled",null);
				jQuery("#duration-group").show();
			}
			
			jQuery("#embargo-type-modal .modal-header h3").text("Edit Embargo Type");
			jQuery("#embargo-type-modal .modal-footer #embargoType-save").val("Save Embargo");
			jQuery("#embargoType-remove").show();


		} else {
			// Adding a new embargo type
			jQuery("#embargoType-id").val("");
			jQuery("#embargoType-name").val("");
			jQuery("#embargoType-description").val("");
			jQuery("#embargoType-active").attr("checked","true");
			jQuery("#timeframe-determinate").attr("checked","true");
			jQuery("#embargoType-months").val("");
			jQuery("#embargoType-months").attr("disabled",null);
			jQuery("#duration-group").show();
			
			jQuery("#embargo-type-modal .modal-header h3").text("Add Embargo Type");
			jQuery("#embargo-type-modal .modal-footer #embargoType-save").val("Add Embargo");
			jQuery("#embargoType-remove").hide();

		}

		// Clear out any previous errors
		jQuery("#embargoType-errors").html("");
		jQuery("#embargo-type-modal .control-group").each(function () {
			jQuery(this).removeClass("error"); 
		});

		jQuery('#embargo-type-modal').modal('show');


	}
}

/**
 * Create or Edit an existing embargo type. This callback function handles
 * saving the embargo dialog box.
 * 
 * @param jsonURL
 *            The url where embargos should be updated.
 * @returns A Callback Function
 */
function embargoSaveDialogHandler(jsonURL) {
	return function () {

		var embargoTypeId = jQuery("#embargoType-id").val();
		var name = jQuery("#embargoType-name").val();
		var description = jQuery("#embargoType-description").val();
		var active = null;
		if (jQuery("#embargoType-active:checked").length > 0)
			active = "true";

		var months = null
		if (jQuery("#timeframe-determinate:checked").length > 0)
			months = jQuery("#embargoType-months").val();
		jQuery("#embargo-type-modal").addClass("waiting");

		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#embargo-type-modal").removeClass("waiting");
			jQuery("#embargoType-errors").html("");
			jQuery("#embargo-type-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});

			var $row
			if (jQuery("#embargoType_"+data.id).length > 0) {
				// Look up the old row
				$row = jQuery("#embargoType_"+data.id);
			} else {
				// Add a new row to the end of the list.
				$row = jQuery( 
						"<tr id='embargoType_"+data.id+"'>"+
						"    <td class='embargoType-name-cell'></td>"+
						"    <td class='embargoType-description-cell'></td>"+
						"    <td class='embargoType-active-cell'></td>"+
						"    <td class='embargoType-duration-cell'></td>"+
						"    <td class='embargoType-edit-cell'><a href='#'>Edit</a></td>" +
						"</tr>"
				).appendTo(jQuery("#embargoType-list"));
			}

			$row.find(".embargoType-name-cell").text(data.name);
			$row.find(".embargoType-description-cell").text(data.description);
			if (data.active == "true")
				$row.find(".embargoType-active-cell").text("Yes");
			else
				$row.find(".embargoType-active-cell").text("No");

			if (data.months == "null")
				$row.find(".embargoType-duration-cell").text("Indefinite");
			else
				$row.find(".embargoType-duration-cell").text(data.months);

			jQuery('#embargo-type-modal').modal('hide');

		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#embargo-type-modal").removeClass("waiting");
			jQuery("#embargo-type-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#embargoType-errors").html("<li><strong>Unable to save embargo</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'embargoTypeId': embargoTypeId,
				'name': name,
				'description': description,
				'months': months,
				'active': active
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

		return false;
	}
}


/**
 * Delete an existing embargo type. This will confirm that this is what the user wants to do.
 * 
 * @param jsonURL
 *            The url where embargos are deleted.
 * @returns A Callback Function
 */
function embargoRemoveDialogHandler(jsonURL) {
	return function () {

		// Confirm before deleting
		var really = confirm("Alert! All submissions which use this embargo type will have their embargo settings deleted with no way to recover. Are you REALLY sure you want to delete this embargo type?");
		if (!really)
			return false;
		
		var embargoTypeId = jQuery("#embargoType-id").val();
		jQuery("#embargo-type-modal").addClass("waiting");
		
		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#embargo-type-modal").removeClass("waiting");
			jQuery("#embargoType-errors").html("");
			jQuery("#embargo-type-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});
			jQuery("#"+embargoTypeId).remove();
			
			// Go back to the list
			jQuery('#embargo-type-modal').modal('hide');

		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#embargo-type-modal").removeClass("waiting");
			jQuery("#embargo-type-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#embargoType-errors").html("<li><strong>Unable to save embargo</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'embargoTypeId': embargoTypeId
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

		return false;
	}
}





/**
 * Sortable update handler for embargo types. This callback is called whenever
 * re-sorts the list of embargos, saving the new order in the system.
 * 
 * @param jsonURL
 *            The json url to update embargo order.
 * @returns A Callback function
 */
var embargoSortableUpdateHandler = function(jsonURL) {
	return function(event, ui) {

		var list = jQuery("#embargoType-list").sortable('toArray').toString();
		jQuery("#embargoType-list").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			clearAlert("embargoType-reorder");
			jQuery("#embargoType-list").removeClass("waiting");
		}

		var failureCallback = function(message) {
			displayAlert("embargoType-reorder", "Unable to reorder " + type,
					message);
			jQuery("#embargoType-list").removeClass("waiting");
		}

		var data = {};
		data['embargoTypeIds'] = list;

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
			error : function() {
				failureCallback("Unable to communicate with the server.");
			}

		});
	};
}

/**********************************************************
 * Deposit Settings
 **********************************************************/


/**
 * Handler for loading the deposit location modal dialog box. The entire body of
 * the modal dialog is loaded from the server to replace the contents of the old
 * dialog.
 * 
 * @param url
 *            The url to lead the dialog box from.
 * @returns A call back function.
 */
function depositLoadModalHandler(url) {
	return function() {

		jQuery("#deposit-location-modal").modal('show');
		jQuery("#deposit-location-modal form").addClass("waiting");

		var id = jQuery(this).closest("li").attr("id");

		jQuery.ajax({
			url : url,
			data : {
				depositLocationId : id
			},
			dataType : 'html',
			type : 'POST',
			success : function(data) {
				jQuery("#deposit-location-modal form").replaceWith(data)
						.remove();
			},
			error : function() {
				alert("Error unable to communicate with server.");
			}
		});

		return false;
	};
}


/**
 * Handler for adding a new deposit location. This will display the modal dialog
 * box with empty data. The dialog is loaded from the server.
 * 
 * @param url
 *            The url to lead the dialog box from.
 * @returns A call back function.
 */
function depositAddModalHandler(url) {
	return function() {

		jQuery("#deposit-location-modal").modal('show');
		jQuery("#deposit-location-modal form").addClass("waiting");

		jQuery.ajax({
			url : url,
			dataType : 'html',
			type : 'POST',
			success : function(data) {
				jQuery("#deposit-location-modal form").replaceWith(data)
						.remove();
			},
			error : function() {
				alert("Error unable to communicate with server.");
			}
		});

		return false;
	};
}

/**
 * This is not a handler. This is expected to be called by other handlers. It
 * function will update the list of depositLocations. The url where to get the
 * snipit of HTML is expected to be passed. Basically each time a location is
 * added, modified, or deleted this function is called to update the list.
 * 
 * The sortable handler works on the
 * <ul>
 * tag for this list. Since it is hard to re-adjust the sortable handler this
 * function goes out of it's way to just replace the children of the
 * <ul>
 * tag. This allows the individual
 * <li>'s to be updated without effecting the events registered on the
 * <ul>.
 * 
 * @param url
 *            The url where a snipit of HTML for the deposit list is located.
 */
function depositUpdateLocationList(url) {
	
	jQuery("#depositLocation-list").addClass("waiting");
	
	jQuery.ajax({
        url : url,
        dataType : 'html',
        type : 'POST',
        success : function(data) {
        	
        	jQuery("#depositLocation-list").empty();
        	jQuery("#depositLocation-list").append(jQuery(data).children())
        	jQuery("#depositLocation-list").removeClass("waiting");
        },
        error:function(){
            jQuery("#depositLocation-list").removeClass("waiting");
        }
    });
}

/**
 * Save a deposit location handler. This method is for multiple buttons on the
 * deposit location modal dialog box. Basicaly for everything but the delete &
 * cancel buttons. This works for adding new locations, or editing existing
 * ones.
 * 
 * @param closeOnSave
 *            A boolean flag to determine if the dialog box should be saved if
 *            this update is successfull.
 * @param saveURL
 *            The url where to send the updates too, an HTML snipit of a new
 *            form is expected.
 * @param updateURL
 *            The url where to update the list of deposit locations (in-case the
 *            name changed).
 * @returns A call back function.
 */
function depositSaveHandler(closeOnSave, saveURL, updateURL) {

	return function () {

		jQuery("#deposit-location-modal form").addClass("waiting");

		var action = jQuery(this).attr("id");
		var id = jQuery("#depositLocation-id").val()
		var name = jQuery("#depositLocation-name").val();
		var depositor = jQuery("#depositLocation-depositor").val();
		var packager = jQuery("#depositLocation-packager").val();
		var repository = jQuery("#depositLocation-repository").val();
		var username = jQuery("#depositLocation-username").val();
		var password = jQuery("#depositLocation-password").val();
		var onBehalfOf = jQuery("#depositLocation-onBehalfOf").val();
		var collection = jQuery("#depositLocation-collection").val();

		if (collection == null)
			collection = "";

		jQuery.ajax({
			url : saveURL,
			data : {
				action: action,
				depositLocationId: id,
				name: name,
				depositor: depositor,
				packager: packager,
				repository: repository,
				username: username,
				password: password,
				onBehalfOf: onBehalfOf,
				collection: collection
			},
			dataType : 'html',
			type : 'POST',
			success : function(data) {
				jQuery("#deposit-location-modal form").replaceWith(data).remove();

				// If this was the save button close the dialog if there are no errors
				if (closeOnSave && jQuery("#deposit-location-errors").children().length == 0)
					jQuery("#deposit-location-modal").modal('hide');

				depositUpdateLocationList(updateURL);

			},
			error:function(){
				alert("Error unable to communicate with server.");
			}
		});

		return false;
	};
};

/**
 * Delete deposit location handler. This method will send the ajax query to
 * delete the deposit location, and after that has returned succesfully it will
 * update the list of deposit locations.
 * 
 * @param deleteURL
 *            The JSON url where deletes should be posted.
 * @param updateURL
 *            The URL to update the list of deposit locations after the delete
 *            has occured.
 * @returns A call back function.
 */
function depositDeleteHandler(deleteURL, updateURL) {
	return function() {
		jQuery("#deposit-location-modal form").addClass("waiting");

		var id = jQuery("#depositLocation-id").val()

		jQuery.ajax({
			url : deleteURL,
			data : {
				depositLocationId : id,
			},
			dataType : 'json',
			type : 'POST',
			success : function(data) {
				depositUpdateLocationList(updateURL);
				jQuery("#deposit-location-modal").modal('hide');

				if (!data.success)
					alert("Error unable to delete deposit location because: "
							+ data.message);
			},
			error : function() {
				alert("Error unable to communicate with server.");
			}
		});

		return false;
	}
}

/**********************************************************
 * Submissions Settings Tab
 **********************************************************/


/**
 * Handler for the submission settings to save their state via ajax. This
 * method supports all the various settings related to submission fields.
 * 
 * @param jsonURL The JSON url to submit updates too.
 * @returns A Callback function
 */
function submissionSettingsHandler(jsonURL) {

	return function (event) {
		var $this = jQuery(this);
		var field = $this.attr('name');
		var value = $this.val();

		$this.addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			$this.removeClass("waiting");
			$this.removeClass("settings-error");
			clearAlert("submission-setting-"+field);
			
			// Update the label if required
			if (data.field.indexOf("_enabled") > -1) {
				var $element = jQuery("[name="+data.field+"]").closest("li");
				$element.removeClass("disabled");
				$element.removeClass("optional");
				$element.removeClass("required");
				$element.addClass(data.value);
			}
			
		}

		var failureCallback = function (message) {
			$this.removeClass("waiting");
			$this.addClass("settings-error");
			displayAlert("submission-setting-"+field,"Unable to update setting",message);
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
		
	    event.preventDefault(); // cancel default behavior
	};
}

/**
 * Handle saving updates to submission sticky notes.
 * 
 * @param jsonURL The URL where updates should be sent.
 */
function stickySettingsHandler(jsonURL) {

	return function (event) {
		var $this = jQuery(this);
		var field = $this.attr('name');
		var index = $this.attr('data-index');
		var value = $this.val();

		$this.addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			$this.removeClass("waiting");
			$this.removeClass("settings-error");
			clearAlert("submission-setting-"+field);
			
			if (!data.value) {
				$this.closest('li').slideUp(500, function() {
					// Remove the element and re-index.
					
					var $sticky = $this.closest(".sticky");
					$this.closest('li').remove();
					$sticky.find('li textarea').each(function (index) {
						jQuery(this).attr("data-index",index);
					});
				});
			}
			
		}

		var failureCallback = function (message) {
			$this.removeClass("waiting");
			$this.addClass("settings-error");
			displayAlert("submission-setting-"+field,"Unable to update setting",message);
		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'field': field,
				'index': index,
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
		
	    event.preventDefault(); // cancel default behavior
	};
}

function addStickySettingsHandler() {
	return function(event) {
		
		var $this = jQuery(this).closest('.sticky').find('ul');
		
		var name = $this.attr('data-name');
		var $newSticky = jQuery("<li class='hidden'><div class='sticky-top'></div><div class='sticky-bottom'><a class='remove-sticky-note' href='javascript: void(0);'><em class='icon-remove-sign'></em></a><textarea name='"+name+"'>New Sticky Note.</textarea></div></li>");
		$this.append($newSticky);
		$newSticky.slideToggle(500, function() {
			$newSticky.find('textarea').trigger('change');
		});
		
		
		// re-index
		$this.find('textarea').each(function (index) {
			jQuery(this).attr("data-index",index);
		});
		
	};
}



