

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
					jQuery("#"+id+" #system-warning").html("This system defined template is sent to the any user who has forgotten their password, and is attempting to recover it. It is very important that the template contain the {REGISTRATION_URL} variable otherwise the user would not be able to recover their account. If Vireo is configured to use a Single-Sign-On system this option may not be available.");										
				} else if (data.name == "SYSTEM New User Registration") {
					jQuery("#"+id+" #system-warning").html("This system defined template is sent to any new user when they register their account. It is very important that the template contain the {REGISTRATION_URL} variable otherwise the user would not be able to complete the registration process. If Vireo is configured to use a Single-Sign-On system this option may not be available.");										
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

