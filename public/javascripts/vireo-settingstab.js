

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
	    type == "program" ||
	    type == "department" ||
	    type == "major" ||
	    type == "graduationMonth" ||
	    type == "language"
	   ) {
	
		if (editable) {
			$element.replaceWith("<li id='" + id + "'><span class='editing'><input type='text' value='"+name+"' placeholder='"+name+"'/><i class='icon-remove'></i><i class='icon-ok'></i></span></li>");
		} else {
			$element.replaceWith("<li id='" + id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> " + name + "</a></li>");
		}
	
	} else if(
		type == "college" 
	) {

		if (editable) {
			$element.replaceWith("<li id='" + id + "'><span class='editing'><input type='text' value='"+name+"' placeholder='"+name+"'/><i class='icon-remove'></i><i class='icon-ok'></i></span></li>");
		} else {
			$element.replaceWith("<li id='" + id + "'><a class='"+type+"-editable' href='#'><em class='icon-pencil'></em> " + name + "</a></li>");
		}

	} else if (
		type == "degree" ||
		type == "documentType" ||
		type == "committeeMemberRoleType"
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
			
			if (type == "committeeMemberRoleType") {
				// Copy down the typeahead attributes to the new field.
				jQuery("#"+id+" input").attr("data-provide",jQuery("#add-committeeMemberRoleType-name").attr("data-provide"));
				jQuery("#"+id+" input").attr("data-items",jQuery("#add-committeeMemberRoleType-name").attr("data-items"));
				jQuery("#"+id+" input").attr("data-min-length",jQuery("#add-committeeMemberRoleType-name").attr("data-min-length"));
				jQuery("#"+id+" input").attr("data-source",jQuery("#add-committeeMemberRoleType-name").attr("data-source"));
			}
			
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
		type == "program" ||
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
		type == "documentType" ||
		type == "committeeMemberRoleType"
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
		type == "program" ||
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
			Alert(type + "-edit-" + id);
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
				Alert(type + "-remove-" + id);

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
				Alert(type + "-reorder");
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
			Alert(type+"-add");
			
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

		if (jQuery("#add-"+type+"-emails").length > 0)
			data.emails = jQuery("#add-"+type+"-emails").val();
		
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
 * Handler for the cancel button when adding an action. This will  out the
 * form and fadeout dialog form.
 * 
 * @param type
 * 			  The type of objects: action, major, degree, etc..
 * @returns A Callback function
 */
function cancelAddActionHandler(type) {

	return function() {
		
		if (jQuery("#add-"+type+"-dialog").is(":visible")) 
			jQuery("#add-"+type+"-dialog").slideToggle();
		
		if (jQuery("#bulk-add-"+type+"-dialog").is(":visible")) 
			jQuery("#bulk-add-"+type+"-dialog").slideToggle();
		
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
			Alert("emailTemplate-retrieve-"+id);
			
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
			Alert("emailTemplate-delete-"+id);
			
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
 * Handles the ui interactions for the workflow email rules
 * 
 * @param jsonURL
 *            The url where templates may be saved.
 * @returns A Callback function
 */
function workflowEmailRuleHandler($this, $select, options) {

	$select.html("");	
	
	var message = options.length > 0 ? "Choose a "+$this.children("option:selected").text() : "No " +$this.children("option:selected").text()+ " added"
	$select.html("")
	$select.html("<option value=''>"+message+"</option>");

	jQuery(options).each(function(key, val) {
		var option = "<option value="+val.id+">"+val.name+"</option>"
		$select.append(option);
	});
}

/**
 * Create a workflow email rule
 * 
 * @param jsonURL
 *            The url where templates may be saved.
 * @returns A Callback function
 */
function createWorkflowEmailRuleHandler(jsonURL) {
	return function() {
		var state = $(this).attr("data-state");
		console.log(state);
		var id = "#"+ state + "-workflow-add";
		var $element = $(id);
		var $targetElem = $("#"+state+"-workflow-list");

		var reqData = new Object;
		reqData.state = state;
		reqData.conditionCategory = ""
		reqData.conditionIDString = ""
		reqData.recipientType = ""
		reqData.templateString = ""
		$targetElem.addClass("waiting");
		
		var successCallback = function(data) {
			$targetElem.removeClass("waiting");

			var newRow = "<tr id='"+data.state+"-"+data.id+"'>" 
+	"<td><span class='"+data.state+"-"+data.id+"-condition' style='display: none;'>if</span></td>" 
+	"<td class='edit-box'>" 
+		"<ul class='unstyled'>" 
+			"<li class='edit'>" 
+				"<span id='"+data.state+"-"+data.id+"-conditionCategory' class='select' data-state='"+data.state+"' data-id='"+data.id+"' data-rulefieldname='conditionCategory'><i class='icon-pencil'></i> none</span>"
+			"</li>"
+		"</ul>" 
+	"</td>"
+	"<td>"
+	"<span class='"+data.state+"-"+data.id+"-condition' style='display: none;'>=</span>"
+	"</td>"
+	"<td class='edit-box'>" 
+		"<ul class='unstyled "+data.state+"-"+data.id+"-condition' style='display: none;'>"
+			"<li class='edit'>" 
+				"<span id='"+data.state+"-"+data.id+"-condition' class='empty autocomplete' data-id='"+data.id+"' data-state='"+data.state+"' data-rulefieldname='condition'>" 
+					"<i class='icon-pencil'></i> none" 											
+				"</span>" 
+			"</li>" 
+		"</ul>" 
+	"</td>" 
+	"<td><span class='"+data.state+"-"+data.id+"-condition' style='display: none;''>then </span>email</td>" 
+	"<td class='edit-box'>" 
+		"<ul class='unstyled'>" 
+			"<li class='edit'>" 
+				"<span id='"+data.state+"-"+data.id+"-template' class='empty select' data-id='"+data.id+"' data-state='"+data.state+"' data-rulefieldname='templateString'>" 
+					"<i class='icon-pencil'></i>   none"											
+				"</span>"
+			"</li>" 
+		"</ul>" 
+	"</td>" 
+	"<td class='edit-box'>" 
+		"<ul class='unstyled'>" 
+			"<li class='edit'>" 
+				"<span id='"+data.state+"-"+data.id+"-recipientType' class='empty select' data-id='"+data.id+"' data-state='"+data.state+"' data-rulefieldname='recipientType'>" 
+					"<i class='icon-pencil'></i>   none"											
+				"</span>"
+			"</li>" 
+		"</ul>" 
+	"</td>"
+	"<td>"
+		"<a href='#' class='removeRule' data-id='"+data.id+"'><em class='icon-trash'></em>"
+	"</a></td>"
+ "</tr>"

			$targetElem.append(newRow);


			return false;
		}

		var failureCallback = function(message) {
			$targetElem.removeClass("waiting");
			return false;
		}

		
		jQuery.ajax({
			url : jsonURL,
			data : {
				id: null,
				stateString: state,
				conditionCategory: reqData.conditionCategory,
				conditionIDString: reqData.conditionIDString,
				recipientString: reqData.recipientString,
				templateString: reqData.templateString
			},
			dataType : 'json',
			type : 'POST',
			success : function(data) {
				console.log(data);
				if (data.success) {
					successCallback(data);
				} else {
					failureCallback(data.message)
				}
			},
			error : function(e) {
				console.log(e);
				failureCallback("Unable to communicate with the server.");
			}

		});

		return false;
	};
}

/**
 * Remove a workflow email rule
 * 
 * @param jsonURL
 *            The url where templates may be saved.
 * @returns A Callback function
 */
function removeWorkflowEmailRuleHandler(jsonURL) {
	return function() {
		
		$this = $(this);

		$this.html("confirm remove?");

		
		$this.click(function() {
			
			var backupHTML = $this.html();
			$this.html("");	
			$this.parents("tr").addClass("waiting");

			successCallback = function() {
				$this.parents("tr").fadeOut(300);
			}

			var failureCallback = function(message) {
				$this.html(backupHTML);	
				$this.parents("tr").removeClass("waiting");
				return false;
			}
			console.log($this.attr('data-id'));
			jQuery.ajax({
				url : jsonURL,
				data : {
					ruleID: $this.attr('data-id')
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
				error : function(e) {
					console.log(e);
					failureCallback("Unable to communicate with the server.");
				}
			});

		});
		

		return false;
	}
}

/**
 * Swap to input field function
 */
function swapToInputHandler(){	
	return function(event) {
		if(jQuery(this).closest(".editing").length == 0) {
			jQuery(".icon-remove").click();

			//Clean up
			jQuery(".tooltip").remove();
			jQuery(this).find(".tooltip-icon").remove();
			jQuery("#backup").remove()

			var editItem = jQuery(this);
			
			var value = jQuery.trim(escapeQuotes(editItem.text()));
						
			var checkValue = value.replace(/\t/g,"");
			checkValue = checkValue.replace(/\n/g,"");
			checkValue = checkValue.replace(/\r/g,"");
			checkValue = checkValue.replace(" ","");
			
			if(checkValue=="none" || checkValue=="null"){
				value="";
				jQuery("body").append('<div id="backup"></div>')
			} else {
				//Make back up info			
				jQuery("body").append('<div id="backup">'+editItem.html()+'</div>')
				
			} 

			if(editItem.hasClass("select")) {

				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" select").attr("data-id", editItem.attr("data-id"));
				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" select").attr("data-state", editItem.attr("data-state"));
				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" select").attr("data-ruleFieldName", editItem.attr("data-ruleFieldName"));

				//Select Drop Downs
				var selectCode = '<div id="'+editItem.attr("data-state")+'-workflowRule-'+editItem.attr("data-ruleFieldName")+'" class="editing select" >';
				selectCode += jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")).html();
				selectCode += '<br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>';
				editItem.replaceWith(selectCode);
				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" .field option").each(function(){
					if(jQuery(this).text()==value){
						jQuery(this).attr("selected","selected");
					}
				})


				
			} else if(editItem.hasClass("autocomplete")) {

				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" input").attr("data-id", editItem.attr("data-id"));
				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" input").attr("data-state", editItem.attr("data-state"));
				jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")+" input").attr("data-ruleFieldName", editItem.attr("data-ruleFieldName"));



				// Autocomplete fields
				var selectCode = '<div id="'+editItem.attr("data-state")+'-workflowRule-'+editItem.attr("data-ruleFieldName")+'" class="editing autocomplete">';
				selectCode += jQuery("#"+editItem.attr("data-state")+"-workflowRule-"+editItem.attr("data-ruleFieldName")).html();
				selectCode += '<br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>';
				editItem.replaceWith(selectCode);
				
				jQuery("#"+editItem.attr("id")+" .field").val(value);

				var $categoryElem = $("#"+editItem.attr("id")+"Category");

				switch($categoryElem.text().trim()) {
				    case "College":
				        $("#Submitted-workflowRule-condition input").attr("data-source",$("#Submitted-workflowRule-condition input").attr("data-Colleges"));
				        break;
				    case "Department":
				        $("#Submitted-workflowRule-condition input").attr("data-source",$("#Submitted-workflowRule-condition input").attr("data-Departments"));
				        break;
				    case "Program":
				        $("#Submitted-workflowRule-condition input").attr("data-source",$("#Submitted-workflowRule-condition input").attr("data-Programs"));
				        break;
				    default:
				        break;
				}
				
			} else {
				//Make back up info			
				jQuery("body").append('<div id="backup">'+editItem.html()+'</div>')
				
				if (editItem.attr("data-primary"))
					jQuery("#backup").attr("data-primary",editItem.attr("data-primary"));
				if (editItem.attr("data-secondary"))
					jQuery("#backup").attr("data-secondary",editItem.attr("data-secondary"));
				if (editItem.attr("data-tertiary"))
					jQuery("#backup").attr("data-tertiary",editItem.attr("data-tertiary"));
			}

			if(editItem.hasClass("autocomplete")) { 
				// Autocomplete fields
				var selectCode = '<div id="'+editItem.attr("id")+'" class="editing autocomplete" data-id="'+editItem.attr("data-id")+'" data-state="'+editItem.attr("data-state")+'" data-ruleFieldName="'+editItem.attr("data-ruleFieldName")+' data-oldID="'+$(this).attr("id")+'">';
				selectCode += jQuery("#"+editItem.attr("id")+"Options").html();
				selectCode += '<br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>';
				editItem.replaceWith(selectCode);
				
				jQuery("#"+editItem.attr("id")+" .field").val(value);
				
			} else {
				//Input Fields				
				editItem.replaceWith('<div id="'+editItem.attr("id")+'" class="editing"><input class="field" type="text" value="'+value+'" data-id="'+editItem.attr("data-id")+'" data-state="'+editItem.attr("data-state")+'" data-ruleFieldName="'+editItem.attr("data-ruleFieldName")+'" /><br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>');
			}			

			event.stopPropagation();
		}
	}
}

/**
 * This function cancels the currently edited field
 * and replaces the content with a backup stored in 
 * a hidden div (#backup)
 */
function cancelEditingHandler(){
	return function() {
		$this = jQuery(".icon-remove");
		if($this.closest(".add").length){
			jQuery(".add").remove();
		} else {
			var classValue = '';
			var fieldItem;
			if(jQuery(".editing").hasClass("select")){
				classValue = classValue + 'select ';
				fieldItem = jQuery(".editing select");
			} else if(jQuery(".editing").hasClass("autocomplete")) { 
				classValue = classValue + ' autocomplete';
				fieldItem = jQuery(".editing input");
			} else {		
				fieldItem = jQuery(".editing input");
			}

			var id=fieldItem.attr(fieldItem.attr("data-state")+"-"+fieldItem.attr("data-id")+"-"+fieldItem.attr("data-ruleFieldName"));
			
			var currentValue = jQuery("#backup").html();
			
			if(!currentValue){
				currentValue = '<i class="icon-pencil"></i> none';
				classValue += " empty ";
			}
			
			
			jQuery(".editing").replaceWith('<span id="'+id+'" class="'+classValue+'" data-state="'+fieldItem.attr("data-state")+'" data-id="'+fieldItem.attr("data-id")+'" data-ruleFieldName="'+fieldItem.attr("data-ruleFieldName")+'">'+currentValue+'</span>');
			
		}
		jQuery("#backup").remove();
	}
}

/**
 * This function commits changes for the currently
 * edited field.
 * 
 * @param eventTarget (The reference element)
 * @param jsonURL (The method to update generic items)
 * @param graduationURL (The method to update graduation semester)
 * @param committeeURL (The method to update committee members)
 * @param subId (The submission id)
 */
function commitChangesHandler(eventTarget, jsonURL){
	var classValue = "";
	var ruleField;  
	if(jQuery(".editing").hasClass("select")){
		classValue = classValue + 'select ';
		$ruleField = jQuery(".editing select");
	} else if(jQuery(".editing").hasClass("autocomplete")){
		classValue = classValue + 'autocomplete ';
		$ruleField = jQuery(".editing input");
	} else {
		$ruleField = jQuery(".editing input");
	}
	
	var parent = eventTarget.parent();
	console.log(parent)
	
	var ruleFieldName = $ruleField.attr("data-ruleFieldName");
	var theValue = $ruleField.val();
	
	var id = $ruleField.attr("data-id");
	var attrID = $ruleField.attr("data-state") +"-"+ $ruleField.attr("data-id") +"-"+ $ruleField.attr("data-ruleFieldName");
	var stateString = $ruleField.attr("data-state");
	var conditionCategory = "";
	var conditionIDString = "";
	var recipientString = "";
	var templateString = "";


	var currentConditionCategory = $("#"+attrID+"Category").text().trim().toLowerCase()+"sArray";

	switch(ruleFieldName) {
	    case "conditionCategory":
	        conditionCategory = theValue;
	        break;
	    case "condition":

	        conditionString = theValue;

	        $(jsDataObjects[currentConditionCategory]).each(function(key, condition) {
	        	if(condition.name == theValue)
	        		conditionIDString = condition.id;
	        });

	        break;
	    case "recipientType":
	        recipientString = theValue;	        
	        break;
	    case "templateString":
	        templateString = theValue;
	        break;    
	    default:
	        break;
	}

	jQuery(".editing").replaceWith('<div class="'+attrID+' progress progress-striped active"><div class="bar" style="width:100%"></div></div>');
	
	var reqObj = {
		stateString: stateString,
		id: id,
		conditionCategory: conditionCategory,
		conditionIDString: conditionIDString,
		recipientString: recipientString,
		templateString: templateString
	}

	jQuery.ajax({
		url:jsonURL,
		data: reqObj,
		dataType:'json',
		type:'POST',
		
		success:function(data){
			
			if(data.success) {

				jQuery("div."+attrID).replaceWith('<span id="'+attrID+'" class="'+classValue+'" data-state="'+$ruleField.attr("data-state")+'" data-id="'+$ruleField.attr("data-id")+'" data-ruleFieldName="'+ruleFieldName+'"><i class="icon-pencil"></i> '+data[ruleFieldName]+'</span>');

				console.log(recipientString+templateString != "");

				if(data.conditionCategory != "Always" && data.conditionCategory != "none" && data.conditionCategory != "") { //this condition is satisfied when selcting college, department or program
					
					$(jsDataObjects[data.conditionCategory.trim().toLowerCase()+"sArray"]).each(function() {
						if(this.id == data[ruleFieldName]){
							$("#"+attrID).html("<i class='icon-pencil'></i> "+this.name);
						}
					});

					if(data.condition == "null") $("#"+attrID.replace("Category", "")).html("<i class='icon-pencil'></i> none");


					var $hiddenAutoComplete = jQuery("#"+$ruleField.attr("data-state")+"-workflowRule-"+ruleFieldName);

					$hiddenAutoComplete.attr("data-source", $hiddenAutoComplete.attr("data-"+data.conditionCategory));

					if(data.conditionId != "") {
						var $correspondingCodition = $(attrID.replace("Category", ""));
						$correspondingCodition.html("i class='icon-pencil'></i> none");
					}

					switch(data.conditionCategory) {
					    case "College":
					    	$("#Submitted-workflowRule-condition input").attr("data-source",$("#Submitted-workflowRule-condition input").attr("data-colleges"));
					        conditionCategory = theValue;
					        break;
					    case "Department":
					    	$("#Submitted-workflowRule-condition input").attr("data-source",$("#Submitted-workflowRule-condition input").attr("data-departments"));
					        conditionIDString = theValue;
					        break;
					    case "Program":
					    	$("#Submitted-workflowRule-condition input").attr("data-source",$("#Submitted-workflowRule-condition input").attr("data-programs"));
					        recipientString = theValue;	        
					        break;
					    default:
					        break;
					}

					$("."+data.state+"-"+data.id+"-condition").show();	
				} else {
					if(data.recipientType + data.templateString === "") //checks to make sure that template or recipient were not the fields being checked
						$("."+data.state+"-"+data.id+"-condition").hide();					
				}

			} else {
				jQuery("div."+attrID).replaceWith('<span id="'+attrID+'" class="error '+classValue+' data-state="'+$ruleField.attr("data-state")+'" data-id="'+$ruleField.attr("data-id")+'" data-ruleFieldName="'+$ruleField.attr("data-ruleFieldName")+'"><i class="icon-pencil"></i> '+currentValue+' <a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
				jQuery('.tooltip-icon').tooltip();
			}
			//refreshAll();
		},
		error:function(){
			jQuery("div."+attrID).replaceWith('<span id="'+attrID+'" class="error '+classValue+'" data-state="'+$ruleField.attr("data-state")+'" data-id="'+$ruleField.attr("data-id")+'" data-ruleFieldName="'+$ruleField.attr("data-ruleFieldName")+'">'+jQuery("#backup").html()+' <a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
			jQuery('.tooltip-icon').tooltip();
		}
		
	});

	jQuery("#backup").remove();
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
			Alert("emailTemplate-delete-"+id);
			
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

			//  any previous errors
			$this.parent("fieldset").removeClass("error");
			Alert("profile-alert-"+field);

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
			Alert("user-preference-"+field);
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
			Alert("application-setting-"+field);
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

		//  out any previous errors
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
			Alert("embargoType-reorder");
			jQuery("#embargoType-list").removeClass("waiting");
		}

		var failureCallback = function(message) {
			displayAlert("embargoType-reorder", "Unable to reorder embargo",
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

/**
 * Open the college dialog box. This will work for opening an existing
 * college, or adding a new college.
 * 
 * @returns A Callback function
 */
function collegeOpenDialogHandler(isNew, id) {
	if(isNew == null && typeof(isNew) == "undefined") {
		isNew = false;
	}

	if (!isNew) {
		// Loading an existing type
		var array_key = -1;
		for(college in jsDataObjects.collegesArray) {
			if(jsDataObjects.collegesArray[college].id == id) {
				array_key = college;
			}
		}
		if(array_key == -1) {
			return;
		}
		// get our college data from JS cache
		var college = jsDataObjects.collegesArray[array_key];
		jQuery("#college-id").val(id);
		jQuery("#college-name").val(college.name);
		var emails_string = "";
		var emails = college.emails;
		$.each(emails, function(key, val) {
			if (val.email != "") {
				emails_string += val.email + ", ";
			}
		});

		jQuery("#college-emails").val(emails_string.substring(0, emails_string.length-2));
		
		jQuery("#college-modal .modal-header h3").text("Edit College");
		jQuery("#college-modal .modal-footer #college-save").val("Save College");
		jQuery("#college-remove").show();
	} else {
		// Adding a new college
		jQuery("#college-id").val("");
		jQuery("#college-name").val("");
		jQuery("#college-emails").val("");
		
		jQuery("#college-modal .modal-header h3").text("Add College");
		jQuery("#college-modal .modal-footer #college-save").val("Add College");
		jQuery("#college-remove").hide();

	}

	//  out any previous errors
	jQuery("#college-errors").html("");
	jQuery("#college-modal .control-group").each(function () {
		jQuery(this).removeClass("error"); 
	});

	jQuery('#college-modal').modal('show');
}

/**
 * Create or Edit an existing college. This callback function handles
 * saving the college dialog box.
 * 
 * @param jsonURL
 *            The url where colleges should be updated.
 * @returns A Callback Function
 */
function collegeSaveDialogHandler(jsonURL) {
	return function () {
		var collegeId = jQuery("#college-id").val();
		var name = jQuery("#college-name").val();
		var emails = jQuery("#college-emails").val();
		
		jQuery("#college-modal").addClass("waiting");

		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#college-modal").removeClass("waiting");
			jQuery("#college-errors").html("");
			jQuery("#college-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});

			var $row;
			if (jQuery("#college_"+data.id).length > 0) {
				// Look up the old row
				$row = jQuery("#college_"+data.id);
			} else {
				// Add a new row to the end of the list.
				$row = jQuery( 
						"<tr id='college_"+data.id+"'>"+
						"    <td class='college-name-cell'></td>"+
						"    <td class='college-emails-cell'></td>"+
						"    <td class='college-edit-cell'><a data-id='"+data.id+"' href='javascript:void(0);'>Edit</a></td>" +
						"</tr>"
				).appendTo(jQuery("#colleges-list"));
			}

			$row.find(".college-name-cell").text(data.name);
			var emails_string = "";
			var emails = data.emails;
			$.each(emails, function(key, val) {
				emails_string += val.email + ", ";
			});
			$row.find(".college-emails-cell").text(emails_string.substring(0, emails_string.length-2));
			
			jQuery('#college-modal').modal('hide');
			// refresh local JS cache of collegesArray data
			var array_key = -1;
			// look for the array index of this college in the array
			for(college in jsDataObjects.collegesArray) {
				if(jsDataObjects.collegesArray[college].id == collegeId) {
					array_key = college;
				}
			}
			// if the index position was not found
			if(array_key == -1) {
				// add new data
				jsDataObjects.collegesArray.push(data);
			} else {
				// change existing data
				jsDataObjects.collegesArray[array_key] = data;
			}
		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#college-modal").removeClass("waiting");
			jQuery("#college-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#college-errors").html("<li><strong>Unable to save college</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'collegeId':collegeId,
				'name': name,
				'emails': emails
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
			error:function(e){
				console.log(e);
				failureCallback("Unable to communicate with the server."+e);
			}

		});

		return false;
	}
}

/**
 * Delete an existing college. This will confirm that this is what the user wants to do.
 * 
 * @param jsonURL
 *            The url where colleges are deleted.
 * @returns A Callback Function
 */
function collegeRemoveDialogHandler(jsonURL) {
	return function () {

		// Confirm before deleting
		var really = confirm("Alert! All submissions which use this college will have their college settings deleted with no way to recover. Are you REALLY sure you want to delete this college?");
		if (!really)
			return false;
		
		var collegeId = jQuery("#college-id").val();
		jQuery("#college-modal").addClass("waiting");
		
		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#college-modal").removeClass("waiting");
			jQuery("#college-errors").html("");
			jQuery("#college-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});
			jQuery("#college_"+collegeId).remove();
			
			// Go back to the list
			jQuery('#college-modal').modal('hide');
			
			// refresh local JS cache of collegesArray data
			var array_key = -1;
			// look for the array index of this college in the array
			for(college in jsDataObjects.collegesArray) {
				if(jsDataObjects.collegesArray[college].id == collegeId) {
					array_key = parseInt(college);
				}
			}
			// if the index position was found
			if(array_key != -1) {
				// remove old data
				var arrayBefore = jsDataObjects.collegesArray.slice(0, array_key);
				var arrayAfter = new Array();
				// avoid index out of bounds (in case we're removing last element in array)
				if(array_key+1 < jsDataObjects.collegesArray.length) {
					arrayAfter = jsDataObjects.collegesArray.slice(array_key+1, jsDataObjects.collegesArray.length);
				}
				var newArray = arrayBefore.concat(arrayAfter);
				jsDataObjects.collegesArray = newArray;
			}
		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#college-modal").removeClass("waiting");
			jQuery("#college-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#college-errors").html("<li><strong>Unable to remove college</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'collegeId': collegeId
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
 * Sortable update handler for colleges. This callback is called whenever
 * re-sorts the list of colleges, saving the new order in the system.
 * 
 * @param jsonURL
 *            The json url to update colleges order.
 * @returns A Callback function
 */
var collegeSortableUpdateHandler = function(jsonURL) {
	return function(event, ui) {

		var list = jQuery("#colleges-list").sortable('toArray').toString();
		jQuery("#colleges-list").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			Alert("college-reorder");
			jQuery("#colleges-list").removeClass("waiting");
		}

		var failureCallback = function(message) {
			displayAlert("college-reorder", "Unable to reorder college",
					message);
			jQuery("#colleges-list").removeClass("waiting");
		}

		var data = {};
		data['collegeIds'] = list;

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

/**
 * Open the program dialog box. This will work for opening an existing
 * program, or adding a new program.
 * 
 * @returns A Callback function
 */
function programOpenDialogHandler(isNew, id) {
	if(isNew == null && typeof(isNew) == "undefined") {
		isNew = false;
	}

	if (!isNew) {
		// Loading an existing type
		var array_key = -1;
		for(program in jsDataObjects.programsArray) {
			if(jsDataObjects.programsArray[program].id == id) {
				array_key = program;
			}
		}
		if(array_key == -1) {
			return;
		}
		// get our program data from JS cache
		var program = jsDataObjects.programsArray[array_key];
		jQuery("#program-id").val(id);
		jQuery("#program-name").val(program.name);
		var emails_string = "";
		var emails = program.emails;
		$.each(emails, function(key, val) {
			if (val.email != "") {
				emails_string += val.email + ", ";
			}
		});

		jQuery("#program-emails").val(emails_string.substring(0, emails_string.length-2));
		
		jQuery("#program-modal .modal-header h3").text("Edit Program");
		jQuery("#program-modal .modal-footer #program-save").val("Save Program");
		jQuery("#program-remove").show();
	} else {
		// Adding a new college
		jQuery("#program-id").val("");
		jQuery("#program-name").val("");
		jQuery("#program-emails").val("");
		
		jQuery("#program-modal .modal-header h3").text("Add Program");
		jQuery("#program-modal .modal-footer #program-save").val("Add Program");
		jQuery("#program-remove").hide();

	}

	//  out any previous errors
	jQuery("#program-errors").html("");
	jQuery("#program-modal .control-group").each(function () {
		jQuery(this).removeClass("error"); 
	});

	jQuery('#program-modal').modal('show');
}

/**
 * Create or Edit an existing program. This callback function handles
 * saving the program dialog box.
 * 
 * @param jsonURL
 *            The url where program should be updated.
 * @returns A Callback Function
 */
function programSaveDialogHandler(jsonURL) {
	return function () {
		var programId = jQuery("#program-id").val();
		var name = jQuery("#program-name").val();
		var emails = jQuery("#program-emails").val();
		
		jQuery("#program-modal").addClass("waiting");

		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#program-modal").removeClass("waiting");
			jQuery("#program-errors").html("");
			jQuery("#program-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});

			var $row;
			if (jQuery("#program_"+data.id).length > 0) {
				// Look up the old row
				$row = jQuery("#program_"+data.id);
			} else {
				// Add a new row to the end of the list.
				$row = jQuery( 
						"<tr id='program_"+data.id+"'>"+
						"    <td class='program-name-cell'></td>"+
						"    <td class='program-emails-cell'></td>"+
						"    <td class='program-edit-cell'><a data-id='"+data.id+"' href='javascript:void(0);'>Edit</a></td>" +
						"</tr>"
				).appendTo(jQuery("#programs-list"));
			}

			$row.find(".program-name-cell").text(data.name);
			var emails_string = "";
			var emails = data.emails;
			$.each(emails, function(key, val) {
				emails_string += val.email + ", ";
			});
			$row.find(".program-emails-cell").text(emails_string.substring(0, emails_string.length-2));
			
			jQuery('#program-modal').modal('hide');
			// refresh local JS cache of programsArray data
			var array_key = -1;
			// look for the array index of this program in the array
			for(program in jsDataObjects.programsArray) {
				if(jsDataObjects.programsArray[program].id == programId) {
					array_key = program;
				}
			}
			// if the index position was not found
			if(array_key == -1) {
				// add new data
				jsDataObjects.programsArray.push(data);
			} else {
				// change existing data
				jsDataObjects.programsArray[array_key] = data;
			}
		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#program-modal").removeClass("waiting");
			jQuery("#program-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#program-errors").html("<li><strong>Unable to save program</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'programId':programId,
				'name': name,
				'emails': emails
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
			error:function(e){
				console.log(e);
				failureCallback("Unable to communicate with the server."+e);
			}

		});

		return false;
	}
}

/**
 * Delete an existing program. This will confirm that this is what the user wants to do.
 * 
 * @param jsonURL
 *            The url where programs are deleted.
 * @returns A Callback Function
 */
function programRemoveDialogHandler(jsonURL) {
	return function () {

		// Confirm before deleting
		var really = confirm("Alert! All submissions which use this program will have their program settings deleted with no way to recover. Are you REALLY sure you want to delete this program?");
		if (!really)
			return false;
		
		var programId = jQuery("#program-id").val();
		jQuery("#program-modal").addClass("waiting");
		
		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#program-modal").removeClass("waiting");
			jQuery("#program-errors").html("");
			jQuery("#program-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});
			jQuery("#program_"+programId).remove();
			
			// Go back to the list
			jQuery('#program-modal').modal('hide');
			
			// refresh local JS cache of programsArray data
			var array_key = -1;
			// look for the array index of this program in the array
			for(program in jsDataObjects.programsArray) {
				if(jsDataObjects.programsArray[program].id == programId) {
					array_key = parseInt(program);
				}
			}
			// if the index position was found
			if(array_key != -1) {
				// remove old data
				var arrayBefore = jsDataObjects.programsArray.slice(0, array_key);
				var arrayAfter = new Array();
				// avoid index out of bounds (in case we're removing last element in array)
				if(array_key+1 < jsDataObjects.programsArray.length) {
					arrayAfter = jsDataObjects.programsArray.slice(array_key+1, jsDataObjects.programsArray.length);
				}
				var newArray = arrayBefore.concat(arrayAfter);
				jsDataObjects.programsArray = newArray;
			}
		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#program-modal").removeClass("waiting");
			jQuery("#program-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#program-errors").html("<li><strong>Unable to remove program</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'programId': programId
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
 * Sortable update handler for programs. This callback is called whenever
 * re-sorts the list of programs, saving the new order in the system.
 * 
 * @param jsonURL
 *            The json url to update programs order.
 * @returns A Callback function
 */
var programSortableUpdateHandler = function(jsonURL) {
	return function(event, ui) {

		var list = jQuery("#programs-list").sortable('toArray').toString();
		jQuery("#programs-list").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			Alert("program-reorder");
			jQuery("#programs-list").removeClass("waiting");
		}

		var failureCallback = function(message) {
			displayAlert("program-reorder", "Unable to reorder program",
					message);
			jQuery("#programs-list").removeClass("waiting");
		}

		var data = {};
		data['programIds'] = list;

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

/**
 * Open the department dialog box. This will work for opening an existing
 * department, or adding a new department.
 * 
 * @returns A Callback function
 */
function departmentOpenDialogHandler(isNew, id) {
	if(isNew == null && typeof(isNew) == "undefined") {
		isNew = false;
	}

	if (!isNew) {
		// Loading an existing type
		var array_key = -1;
		for(department in jsDataObjects.departmentsArray) {
			if(jsDataObjects.departmentsArray[department].id == id) {
				array_key = department;
			}
		}
		if(array_key == -1) {
			return;
		}
		// get our college data from JS cache
		var department = jsDataObjects.departmentsArray[array_key];
		jQuery("#department-id").val(id);
		jQuery("#department-name").val(department.name);
		var emails_string = "";
		var emails = department.emails;
		$.each(emails, function(key, val) {
			if (val.email != "") {
				emails_string += val.email + ", ";
			}
		});

		jQuery("#department-emails").val(emails_string.substring(0, emails_string.length-2));
		
		jQuery("#department-modal .modal-header h3").text("Edit Department");
		jQuery("#department-modal .modal-footer #department-save").val("Save Department");
		jQuery("#department-remove").show();
	} else {
		// Adding a new department
		jQuery("#department-id").val("");
		jQuery("#department-name").val("");
		jQuery("#department-emails").val("");
		
		jQuery("#department-modal .modal-header h3").text("Add Department");
		jQuery("#department-modal .modal-footer #department-save").val("Add Department");
		jQuery("#department-remove").hide();

	}

	//  out any previous errors
	jQuery("#department-errors").html("");
	jQuery("#department-modal .control-group").each(function () {
		jQuery(this).removeClass("error"); 
	});

	jQuery('#department-modal').modal('show');
}

/**
 * Create or Edit an existing department. This callback function handles
 * saving the department dialog box.
 * 
 * @param jsonURL
 *            The url where departments should be updated.
 * @returns A Callback Function
 */
function departmentSaveDialogHandler(jsonURL) {
	return function () {
		var departmentId = jQuery("#department-id").val();
		var name = jQuery("#department-name").val();
		var emails = jQuery("#department-emails").val();
		
		jQuery("#department-modal").addClass("waiting");

		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#department-modal").removeClass("waiting");
			jQuery("#department-errors").html("");
			jQuery("#department-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});

			var $row;
			if (jQuery("#department_"+data.id).length > 0) {
				// Look up the old row
				$row = jQuery("#department_"+data.id);
			} else {
				// Add a new row to the end of the list.
				$row = jQuery( 
						"<tr id='department_"+data.id+"'>"+
						"    <td class='department-name-cell'></td>"+
						"    <td class='department-emails-cell'></td>"+
						"    <td class='department-edit-cell'><a data-id='"+data.id+"' href='javascript:void(0);'>Edit</a></td>" +
						"</tr>"
				).appendTo(jQuery("#departments-list"));
			}

			$row.find(".department-name-cell").text(data.name);
			var emails_string = "";
			var emails = data.emails;
			$.each(emails, function(key, val) {
				emails_string += val.email + ", ";
			});
			$row.find(".department-emails-cell").text(emails_string.substring(0, emails_string.length-2));
			
			jQuery('#department-modal').modal('hide');
			// refresh local JS cache of departmentsArray data
			var array_key = -1;
			// look for the array index of this department in the array
			for(department in jsDataObjects.departmentsArray) {
				if(jsDataObjects.departmentsArray[department].id == departmentId) {
					array_key = department;
				}
			}
			// if the index position was not found
			if(array_key == -1) {
				// add new data
				jsDataObjects.departmentsArray.push(data);
			} else {
				// change existing data
				jsDataObjects.departmentsArray[array_key] = data;
			}
		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#department-modal").removeClass("waiting");
			jQuery("#department-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#department-errors").html("<li><strong>Unable to save department</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'departmentId':departmentId,
				'name': name,
				'emails': emails
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
			error:function(e){
				console.log(e);
				failureCallback("Unable to communicate with the server."+e);
			}

		});

		return false;
	}
}

/**
 * Delete an existing department. This will confirm that this is what the user wants to do.
 * 
 * @param jsonURL
 *            The url where departments are deleted.
 * @returns A Callback Function
 */
function departmentRemoveDialogHandler(jsonURL) {
	return function () {

		// Confirm before deleting
		var really = confirm("Alert! All submissions which use this department will have their department settings deleted with no way to recover. Are you REALLY sure you want to delete this department?");
		if (!really)
			return false;
		
		var departmentId = jQuery("#department-id").val();
		jQuery("#department-modal").addClass("waiting");
		
		var successCallback = function(data) {

			// Remove the ajax loading indicators & alerts
			jQuery("#department-modal").removeClass("waiting");
			jQuery("#department-errors").html("");
			jQuery("#department-modal .control-group").each(function () {
				jQuery(this).removeClass("error"); 
			});
			jQuery("#department_"+departmentId).remove();
			
			// Go back to the list
			jQuery('#department-modal').modal('hide');
			
			// refresh local JS cache of departmentsArray data
			var array_key = -1;
			// look for the array index of this department in the array
			for(department in jsDataObjects.departmentsArray) {
				if(jsDataObjects.departmentsArray[department].id == departmentId) {
					array_key = parseInt(department);
				}
			}
			// if the index position was found
			if(array_key != -1) {
				// remove old data
				var arrayBefore = jsDataObjects.departmentsArray.slice(0, array_key);
				var arrayAfter = new Array();
				// avoid index out of bounds (in case we're removing last element in array)
				if(array_key+1 < jsDataObjects.departmentsArray.length) {
					arrayAfter = jsDataObjects.departmentsArray.slice(array_key+1, jsDataObjects.departmentsArray.length);
				}
				var newArray = arrayBefore.concat(arrayAfter);
				jsDataObjects.departmentsArray = newArray;
			}
		}

		var failureCallback = function (message) {

			// Add failure indicators
			jQuery("#department-modal").removeClass("waiting");
			jQuery("#department-modal .control-group").each(function () {
				jQuery(this).addClass("error"); 
			});

			// Display the error
			jQuery("#department-errors").html("<li><strong>Unable to remove department</strong>: "+message);

		}

		jQuery.ajax({
			url:jsonURL,
			data:{
				'departmentId': departmentId
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
 * Sortable update handler for departments. This callback is called whenever
 * re-sorts the list of departments, saving the new order in the system.
 * 
 * @param jsonURL
 *            The json url to update departments order.
 * @returns A Callback function
 */
var departmentSortableUpdateHandler = function(jsonURL) {
	return function(event, ui) {

		var list = jQuery("#departments-list").sortable('toArray').toString();
		jQuery("#departments-list").addClass("waiting");

		var successCallback = function(data) {
			// Remove the ajax loading indicators & alerts
			Alert("department-reorder");
			jQuery("#departments-list").removeClass("waiting");
		}

		var failureCallback = function(message) {
			displayAlert("department-reorder", "Unable to reorder department",
					message);
			jQuery("#departments-list").removeClass("waiting");
		}

		var data = {};
		data['departmentIds'] = list;

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
			Alert("submission-setting-"+field);
			
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
			Alert("submission-setting-"+field);
			
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