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
			
			editItem.find("br").replaceWith("\n");
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
				
				if (editItem.attr("data-primary"))
					jQuery("#backup").attr("data-primary",editItem.attr("data-primary"));
				if (editItem.attr("data-secondary"))
					jQuery("#backup").attr("data-secondary",editItem.attr("data-secondary"));
				if (editItem.attr("data-tertiary"))
					jQuery("#backup").attr("data-tertiary",editItem.attr("data-tertiary"));
			}

			if(editItem.hasClass("textarea")) {
				//Text Areas
				if (editItem.is("#publishedMaterial")) {
					if (value.indexOf("Yes -") == 0)
						value = value.substring(6);
					if (value.indexOf("No -") == 0) 
						value = value.substring(5);
				}
				
				editItem.replaceWith('<div id="'+editItem.attr("id")+'" class="editing textarea"><textarea class="field">'+value+'</textarea><br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>');
				
			} else if(editItem.hasClass("select")) {
				//Select Drop Downs
				var selectCode = '<div id="'+editItem.attr("id")+'" class="editing select">';
				selectCode += jQuery("#"+editItem.attr("id")+"Options").html();
				selectCode += '<br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>';
				editItem.replaceWith(selectCode);
				jQuery("#"+editItem.attr("id")+" .field option").each(function(){
					if(jQuery(this).text()==value){
						jQuery(this).attr("selected","selected");
					}
				})
				
			} else if(editItem.hasClass("autocomplete")) { 
				// Autocomplete fields
				var selectCode = '<div id="'+editItem.attr("id")+'" class="editing autocomplete">';
				selectCode += jQuery("#"+editItem.attr("id")+"Options").html();
				selectCode += '<br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>';
				editItem.replaceWith(selectCode);
				
				jQuery("#"+editItem.attr("id")+" .field").val(value);
				
			} else if(editItem.hasClass("subject")) {
				// The three subject fields;
				
				var primary = editItem.attr("data-primary");
				var secondary = editItem.attr("data-secondary");
				var tertiary = editItem.attr("data-tertiary");
				
				var selectCode = '<div id="'+editItem.attr("id")+'" class="editing subject">';
				selectCode += jQuery("#"+editItem.attr("id")+"Options").html();
				selectCode += '<br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>';
				editItem.replaceWith(selectCode);

				
				jQuery("#"+editItem.attr("id")+" .primary option").each(function(){
					if(jQuery(this).text()==primary){
						jQuery(this).attr("selected","selected");
					}
				})

				jQuery("#"+editItem.attr("id")+" .secondary option").each(function(){
					if(jQuery(this).text()==secondary){
						jQuery(this).attr("selected","selected");
					}
				})
				
				jQuery("#"+editItem.attr("id")+" .tertiary option").each(function(){
					if(jQuery(this).text()==tertiary){
						jQuery(this).attr("selected","selected");
					}
				})
			} else if(editItem.hasClass("date")) {
				editItem.replaceWith('<div id="'+editItem.attr("id")+'" class="editing date"><input class="field datepickerDefense" type="text" value="'+value+'" /><br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>');
				jQuery(".datepickerDefense").datepicker({
					"autoclose": true
				});
				jQuery(".editing .datepickerDefense").datepicker("show");				
			} else {
			
				//Input Fields				
				editItem.replaceWith('<div id="'+editItem.attr("id")+'" class="editing"><input class="field" type="text" value="'+value+'" /><br /><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div>');
			}			

			event.stopPropagation();
		}
	}
}

/**
 * This function swaps the committee member content
 * into editable fields.
 */
function editCommitteeMemberHandler(){	
	return function(event){
		if(jQuery(this).closest(".editing").length == 0) {
			jQuery(".icon-remove").click();

			//Clean up
			jQuery(".tooltip").remove();
			jQuery(this).find(".tooltip-icon").remove();
			jQuery("#backup").remove();

			//Backup
			jQuery("body").append('<div id="backup">'+jQuery(this).html()+'</div>');

			var memberId = jQuery(this).parent("li").attr("class");		
			var firstName = jQuery.trim(escapeQuotes(jQuery(this).find(".firstName").text()));
			var lastName = jQuery.trim(escapeQuotes(jQuery(this).find(".lastName").text()));
			var middleName = jQuery.trim(escapeQuotes(jQuery(this).find(".middleName").text()));
			var currentRoles = new Array();
			jQuery(this).find(".role").each(function () {
				currentRoles.push(jQuery.trim(escapeQuotes(jQuery(this).text())));
			});
			
			// get available roles
			var availableRoles = jQuery.parseJSON(jQuery("#committeeMembers").attr("data-roles"));

			var markup = '<div class="editing"><table>';
			markup += '<tr><td><b>Last Name</b></td><td><b>First Name</b></td><td><b>Middle Name</b></td><td></td></tr>'
			markup += '<tr>'
			markup += '<td><input id="memberId" class="hidden" type="hidden" value="'+memberId+'" />';
			markup += '<input id="cmLastName" class="span2" type="text" value="'+lastName+'" /></td>';
			markup += '<td><input id="cmFirstName" class="span2" type="text" value="'+firstName+'" /></td>';
			markup += '<td><input id="cmMiddleName" class="span2" type="text" value="'+middleName+'" /></td>';
			
			if (availableRoles.length == 1 && ( currentRoles.length == 0 || currentRoles[0] == availableRoles[0] )) {
				// Only one role, so just show a checkbox
				
				var checked = "";
				if (currentRoles[0] == availableRoles[0]) 
					checked = 'checked="checked"'
				
				markup += '</tr>';
				markup += '<tr><td style="text-align:right;"><b>Role:</b> </td>';
				markup += '<td colspan="2"> ';
				markup += '<input class="single-role" id="cmRoles" type="checkbox" value="'+availableRoles[0]+'" '+checked+'> '+availableRoles[0];
				markup += '</td>';
				markup += '</tr>';
				
			} else {
				// Otherwise show a full drop down list
				markup += '</tr>';
				markup += '<tr><td style="text-align:right;"><b>Roles:</b></td>';
				markup += '<td colspan="2"><select id="cmRoles" multiple="multiple">';	
				
				// List all of the normal roles
				jQuery.each(availableRoles, function(index, value) {
					var selected = '';				
					if (currentRoles.indexOf(value) > -1)
						var selected = 'selected="selected"';
					
					markup += '<option value="'+value+'" '+selected+'>'+value+'</option>'
				});
				// If a current role is not in the list of available roles, add it to the end of the list.
				jQuery.each(currentRoles, function(index, value) {
					if (availableRoles.indexOf(value) == -1)
						markup += '<option value="'+value+'" selected="selected">'+value+'</option>'
				});
	
				markup += '</select></td>';
				markup += '</tr>';
			}
			
			markup += '</table><div style="padding:5px;"><a class="btn btn-danger btn-mini remove-committee-member" style="margin-right:10px;">delete</a>&nbsp;<i class="icon-remove" title="cancel"></i>&nbsp;<i class="icon-ok" title="commit"></i></div></div>';

			jQuery(this).replaceWith(markup);

			event.stopPropagation();
			
			jQuery("select#cmRoles").multiselect({
				header: false,
				selectedList: 1,
				noneSelectedText: "No roles selected"
			});
		}
	}
}

/**
 * This function adds fields to add a new committee member.
 */
function addCommitteeMemberHandler(){
	return function(event) {
		if(jQuery(this).closest(".editing").length == 0) {
			jQuery(".icon-remove").click();

			//Clean up
			jQuery(".tooltip").remove();
			jQuery(this).find(".tooltip-icon").remove();
			jQuery("#backup").remove();

			// get available roles
			var availableRoles = jQuery.parseJSON(jQuery("#committeeMembers").attr("data-roles"));
			
			var markup = '<li class="add"><div class="editing"><table>';
			markup += '<tr><td><b>Last Name</b></td><td><b>First Name</b></td><td><b>Middle Name</b></td><td></td></tr>'
			markup += '<tr>'
			markup += '<td><input id="cmLastName" class="span2" type="text" /></td>';
			markup += '<td><input id="cmFirstName" class="span2" type="text" /></td>';
			markup += '<td><input id="cmMiddleName" class="span2" type="text" /></td>';
			if (availableRoles.length == 1 ) {
				// Only one role, so just show a checkbox
				markup += '</tr>';
				markup += '<tr><td style="text-align:right;"><b>Role:</b> </td>';
				markup += '<td colspan="2"> ';
				markup += '<input class="single-role" id="cmRoles" type="checkbox" value="'+availableRoles[0]+'"> '+availableRoles[0];
				markup += '</td>';
				markup += '</tr>';
				
			} else {
				// Otherwise show a full drop down list
				markup += '</tr>';
				markup += '<tr><td style="text-align:right;"><b>Roles:</b></td>';
				markup += '<td colspan="2"><select id="cmRoles" multiple="multiple">';	
				
				// List all of the normal roles
				jQuery.each(availableRoles, function(index, value) {
					markup += '<option value="'+value+'">'+value+'</option>'
				});
				markup += '</select></td>';
				markup += '</tr>';
			}
			markup += '</table><i class="icon-remove" title="cancel"></i>&nbsp<i class="icon-ok" title="commit"></i></div></li>';

			jQuery(markup).insertBefore('#add_new_member');

			event.stopPropagation();
			
			jQuery("select#cmRoles").multiselect({
				header: false,
				selectedList: 1,
				noneSelectedText: "No roles selected"
			});
		}
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
function commitChangesHandler(eventTarget, jsonURL, committeeURL, subId){
	var classValue = '';
	var fieldItem;
	var parent = eventTarget.parent();
	
	var subjectsField = false;
	var primary;
	var secondary;
	var tertiary;
	
	if(jQuery(".editing").hasClass("textarea")){
		classValue = classValue + 'textarea ';
		fieldItem = jQuery(".editing textarea");
	} else if(jQuery(".editing").hasClass("select")){
		classValue = classValue + 'select ';
		fieldItem = jQuery(".editing select");
	} else if(jQuery(".editing").hasClass("autocomplete")){
		classValue = classValue + 'autocomplete ';
		fieldItem = jQuery(".editing input");
	} else if(jQuery(".editing").hasClass("subject")){
		classValue = classValue + 'subject ';
		
		subjectsField = true;
		primary = jQuery(".editing .primary").val();
		secondary = jQuery(".editing .secondary").val(); 
		tertiary = jQuery(".editing .tertiary").val();
	} else if(jQuery(".editing").hasClass("date")) {
		classValue = classValue + 'date ';
		fieldItem = jQuery(".editing input");
	} else {
		fieldItem = jQuery(".editing input");
	}
	var id=jQuery(".editing").attr("id");
	var theValue;
	if(fieldItem && fieldItem.val()){
		theValue = fieldItem.val();
	}
	
	var committeeMember;
	var committeeFirstName;
	var committeeLastName;
	var committeeMiddleName;
	var committeeRoles;
	var committeeId;
	
	if(eventTarget.closest("#committeeMembers").length){
		committeeMember = true;
		committeeFirstName = jQuery("#cmFirstName").val();
		committeeLastName = jQuery("#cmLastName").val();
		committeeMiddleName = jQuery("#cmMiddleName").val();
		
		if (jQuery("#cmRoles").is("select")) {
			committeeRoles = jQuery("#cmRoles").val();
		} else {
			if (jQuery("#cmRoles").is(":checked"))
				committeeRoles = jQuery("#cmRoles").val();
		}
		
	    if (committeeRoles == null)
	    	committeeRoles = new Array();
		committeeId = jQuery("#memberId").val();
	}
	
	jQuery(".editing").replaceWith('<div class="'+id+' progress progress-striped active"><div class="bar" style="width:100%"></div></div>');
	
	if(committeeMember) {
		jQuery.ajax({
			url:committeeURL,
			data:{
				id:committeeId,
				firstName:committeeFirstName,
				lastName:committeeLastName,
				middleName:committeeMiddleName,
				roles:committeeRoles
			},
			dataType:'json',
			type:'POST',
			success:function(data){	
				if(data.success){
					markup = '<div class="editCommitteeMember">';
					markup += '<span class="lastName">'+data.lastName+'</span><span class="seperator">,&nbsp;</span>';
					markup += '<span class="firstName">'+data.firstName+'&nbsp;</span>';
					markup += '<span class="middleName">'+data.middleName+'&nbsp;</span>';	
					jQuery.each(data.roles,function(index,value) {
						markup += ' <span class="role label label-info">'+value+'</span> '
					
					});
					markup += '</div>';
					
					jQuery("li."+data.id).html(markup);
				} else {
					markup = '<div class="editCommitteeMember">';
					markup += '<span class="lastName">'+data.lastName+'</span><span class="seperator">,&nbsp;</span>';
					markup += '<span class="firstName">'+data.firstName+'&nbsp;</span>';
					markup += '<span class="middleName">'+data.middleName+'&nbsp;</span>';						
					jQuery.each(data.roles,function(index,value) {
						markup += ' <span class="role label label-info">'+value+'</span> '
					
					});
					markup += '<span><a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><span class="badge badge-important"><i class="icon-warning-sign icon-white"></i></span></a></span>';
					markup += '</div>';
					
					jQuery("li."+data.id).html(markup);
					jQuery('.tooltip-icon').tooltip();
				}
				refreshAll();
			},
			error:function(){
				jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'">'+jQuery("#backup").html()+' <a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
				jQuery('.tooltip-icon').tooltip();
			}
			
		});
	} else if (subjectsField) { 
		jQuery.ajax({
			url:jsonURL,
			data:{
				subId:subId,
				field:id,
				primary:primary,
				secondary:secondary,
				tertiary:tertiary
			},
			dataType:'json',
			type:'POST',
			success:function(data) {
				
				
				currentValue = "<ul>";
				if (primary)
					currentValue += "<li>"+primary + "</li>";
				if (secondary)
					currentValue += "<li>"+secondary + "</li>";
				if (tertiary)
					currentValue += "<li>"+tertiary + "</li>";
				currentValue += "</ul>"
				
				if (!primary && !secondary && !tertiary) {
					classValue = classValue + 'empty';
					currentValue = "none";
				}
				
				if(data.success){
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="'+classValue+'" data-primary="'+primary+'" data-secondary="'+secondary+'" data-tertiary="'+tertiary+'"><i class="icon-pencil"></i>'+currentValue+'</span>');
				} else {
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'"><i class="icon-pencil"></i> '+currentValue+' <a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
					jQuery('.tooltip-icon').tooltip();
				}
				refreshAll();
			},
			error:function(){
				jQuery("div."+id).replaceWith('<span id="'+id+'" class="error subjects">'+jQuery("#backup").html()+' <a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
				jQuery('.tooltip-icon').tooltip();
			}
			
		});
	} else {
		jQuery.ajax({
			url:jsonURL,
			data:{
				subId:subId,
				field:id,
				value:theValue
			},
			dataType:'json',
			type:'POST',
			success:function(data){
				var currentValue;
				if(data.value){
					currentValue = nl2br(data.value);
				} else {
					currentValue = "none";
					classValue = classValue + 'empty ';
				}
				
				if (id == "publishedMaterial" ) {
					if (currentValue == "none") 
						currentValue = " No -";
					else 
						currentValue = " Yes - "+currentValue;
				}
				
				
				if(data.success){
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="'+classValue+'"><i class="icon-pencil"></i> '+currentValue+'</span>');
					if(data.degreeLevel != null){
						jQuery("#degreeLevel").text(data.degreeLevel);
						jQuery("#degreeLevel").removeClass("empty");
					}
				} else {
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'"><i class="icon-pencil"></i> '+currentValue+' <a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
					jQuery('.tooltip-icon').tooltip();
				}
				refreshAll();
			},
			error:function(){
				jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'">'+jQuery("#backup").html()+' <a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
				jQuery('.tooltip-icon').tooltip();
			}
			
		});
	}	
	jQuery("#backup").remove();
}

/**
 * This function commits adding a new committee member.
 */
function commitNewCommitteeMemberHandler(subId, jsonURL) {

	var committeeFirstName = jQuery("#cmFirstName").val();
	var committeeLastName = jQuery("#cmLastName").val();
	var committeeMiddleName = jQuery("#cmMiddleName").val();
	
	var committeeRoles;
	if (jQuery("#cmRoles").is("select")) {
		committeeRoles = jQuery("#cmRoles").val();
	} else {
		if (jQuery("#cmRoles").is(":checked"))
			committeeRoles = jQuery("#cmRoles").val();
	}
	if (committeeRoles == null)
		committeeRoles = new Array();

	jQuery(".editing").replaceWith('<div class="progress progress-striped active"><div class="bar" style="width:100%"></div></div>');

	jQuery.ajax({
		url:jsonURL,
		data:{
			subId:subId,
			firstName:committeeFirstName,
			lastName:committeeLastName,
			middleName:committeeMiddleName,
			roles:committeeRoles
		},
		dataType:'json',
		type:'POST',
		success:function(data){	
			if(data.success){
				var markup = '<div class="editCommitteeMember">';
				markup += '<span class="lastName">'+data.lastName+'</span><span class="seperator">,&nbsp;</span>';
				markup += '<span class="firstName">'+data.firstName+'&nbsp;</span>';
				markup += '<span class="middleName">'+data.middleName+'&nbsp;</span>';
				jQuery.each(data.roles,function(index,value) {
					markup += ' <span class="role label label-info">'+value+'</span> '
				
				});
				markup += '</div>';

				jQuery("li.add").html(markup);
				jQuery("li.add").addClass(data.id);
				jQuery("li.add").removeClass("add");					
			} else {
				var markup = '<div class="editing"><table>';
				markup += '<tr><td><b>Last Name</b></td><td><b>First Name</b></td><td><b>Middle Name</b></td><td></td></tr>'
					markup += '<tr>'
						markup += '<td><input id="cmLastName" class="span2" type="text" value="'+data.lastName+'" /></td>';
				markup += '<td><input id="cmFirstName" class="span2" type="text" value="'+data.firstName+'" /></td>';
				markup += '<td><input id="cmMiddleName" class="span2" type="text" value="'+data.middleName+'" /></td>';
				
				var availableRoles = jQuery.parseJSON(jQuery("#committeeMembers").attr("data-roles"));
				if (availableRoles.length == 1 && ( data.roles.length == 0 || data.roles[0] == availableRoles[0] )) {
					// Only one role, so just show a checkbox
					
					var checked = "";
					if (data.roles[0] == availableRoles[0]) 
						checked = 'checked="checked"'
					
					markup += '</tr>';
					markup += '<tr><td style="text-align:right;"><b>Role:</b> </td>';
					markup += '<td colspan="2"> ';
					markup += '<input class="single-role" id="cmRoles" type="checkbox" value="'+availableRoles[0]+'" '+checked+'> '+availableRoles[0];
					markup += '</td>';
					markup += '</tr>';
					
				} else {
					// Otherwise show a full drop down list
					markup += '</tr>';
					markup += '<tr><td style="text-align:right;"><b>Roles:</b></td>';
					markup += '<td colspan="2"><select id="cmRoles" multiple="multiple">';	
					
					// List all of the normal roles
					jQuery.each(availableRoles, function(index, value) {
						var selected = '';				
						if (data.roles.indexOf(value) > -1)
							var selected = 'selected="selected"';
						
						markup += '<option value="'+value+'" '+selected+'>'+value+'</option>'
					});
		
					markup += '</select></td>';
					markup += '</tr>';
				}
				markup += '</table><i class="icon-remove"></i>&nbsp<i class="icon-ok"></i>';					
				markup += '<span><a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><span class="badge badge-important"><i class="icon-warning-sign icon-white"></i></span></a></span>';
				markup += '</div>';

				jQuery("li.add").html(markup);
				jQuery('.tooltip-icon').tooltip();
				
				jQuery("select#cmRoles").multiselect({
					header: false,
					selectedList: 1,
					noneSelectedText: "No roles selected"
				});
			}
			refreshAll();
		},
		error:function(){
			jQuery("li.add").replaceWith('<span class="error"><a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
			jQuery('.tooltip-icon').tooltip();
		}

	});
}

/**
 * This function calls the method to delete
 * a committee member.
 */
function removeCommitteeMemberHandler(jsonURL, element){

	var id = element.closest("li").attr("class");
	
	jQuery(".editing").replaceWith('<div class="progress progress-striped active"><div class="bar" style="width:100%"></div></div>');

	jQuery.ajax({
		url:jsonURL,
		data:{
			id:id
		},
		dataType:'json',
		type:'POST',
		success:function(data){	
			if(data.success){				
				jQuery("li."+data.id).remove();					
			} else {
				
			}
			refreshAll();
		},
		error:function(){			
			jQuery("li."+id).html('<span class="error"><a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
			jQuery('.tooltip-icon').tooltip();
		}
	});
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
		}else if($this.closest("#committeeMembers").length){

			var oldValue = jQuery("#backup").html();			
			var swap = jQuery(".editing");
			swap.removeClass("editing");
			swap.addClass("editCommitteeMember")
			swap.html(oldValue);			
		} else {
			var classValue = '';
			var fieldItem;
			if(jQuery(".editing").hasClass("textarea")){
				classValue = classValue + 'textarea ';
				fieldItem = jQuery(".editing textarea");
			} else if(jQuery(".editing").hasClass("select")){
				classValue = classValue + 'select ';
				fieldItem = jQuery(".editing select");
			} else if(jQuery(".editing").hasClass("autocomplete")) { 
				classValue = classValue + 'autocomplete';
				fieldItem = jQuery(".editing autocomplete");
			} else if(jQuery(".editing").hasClass("subject")) {
				classValue = classValue + 'subject';
			} else if(jQuery(".editing").hasClass("date")) {
				classValue = classValue + 'date';
			} else {		
				fieldItem = jQuery(".editing input");
			}
			var id=jQuery(".editing").attr("id");
			
			var currentValue = jQuery("#backup").html();
			
			if(!currentValue){
				currentValue = '<i class="icon-pencil"></i> none';
				classValue += "empty ";
			}
			
			if(jQuery(".editing").hasClass("textarea")){
				currentValue = nl2br(currentValue);
			}
			
			
			jQuery(".editing").replaceWith('<span id="'+id+'" class="'+classValue+'">'+currentValue+'</span>');
			
			if (jQuery("#backup").attr("data-primary"))
				jQuery("#"+id).attr("data-primary",jQuery("#backup").attr("data-primary"));
			
			if (jQuery("#backup").attr("data-secondary"))
				jQuery("#"+id).attr("data-secondary",jQuery("#backup").attr("data-secondary"));
			
			if (jQuery("#backup").attr("data-tertiary"))
				jQuery("#"+id).attr("data-tertiary",jQuery("#backup").attr("data-tertiary"));
			
		}
		jQuery("#backup").remove();
	}
}

/**
 * Function to update custom actions.
 * 
 * @param url (The method to update custom actions)
 * @param subId (The submission id)
 * @param action (The name of the input field)
 * @param value (The value of the input field)
 */
function updateCustomActionsHandler(url, subId, action, value) {
	
	jQuery.ajax({
		url:url,
		data:{
			id:subId,
			action:action,
			value:value
		},
		dataType:'json',
		type:'POST',
		success:function(data){
			refreshAll();
		},
		error:function(){
			alert("Error updating custom action.");
		}
	});
	
}

/**
 * Function to update Action Log table on the view page
 * after a change has been made to the submission object.
 * 
 * @param url (The method to update the Action Log Table)
 * @param id (The submission id)
 */
function updateActionLog(url, id){
	
	jQuery("#actionLog").addClass("waiting");
	
	jQuery.ajax({
		url:url,
		data:{
			id:id
		},
		dataType:'html',
		type:'POST',
		success:function(data){
			jQuery("#actionLog tbody").replaceWith(data);
			jQuery("#actionLog").removeClass("waiting");
		},
		error:function(){
			alert("Error refreshing Action Log Table.");
		}
		
	});
	
}

/**
 * Function to update the Left Column on the view page
 * after a change has been made to the submission object.
 * 
 * @param url (The method to update the Left Column)
 * @param id (The submission id)
 */
function updateLeftColumn(url, id){
	
	jQuery(".side-box").addClass("waiting");
	
	jQuery.ajax({
		url:url,
		data:{
			id:id
		},
		dataType:'html',
		type:'POST',
		success:function(data){
			jQuery("#left-column").html(data);
			jQuery(".side-box").removeClass("waiting");
		},
		error:function(){
			alert("Error refreshing Left Column.");
		}
		
	});
	
}

/**
 * Function to update the Header on the view page
 * after a change has been made to the submission object.
 * 
 * @param url (The method to update the Header)
 * @param id (The submission id)
 */
function updateHeader(url, id){
	jQuery("#mainHeader").addClass("waiting");
	
	jQuery.ajax({
		url:url,
		data:{
			id:id
		},
		dataType:'html',
		type:'POST',
		success:function(data){
			jQuery("#mainHeader").html(data);
			jQuery("#mainHeader").removeClass("waiting");
		},
		error:function(){
			alert("Error refreshing the Header");
		}
	})
}

/**
 * Function to set the value of the hidden input "special_value"
 * to the id of the button clicked. Then the parent form will be submitted.
 *
 * @param form (The form object to submit)
 * @param value (The value tied to the button clicked. Stored in the buttons Id attribute.)
 *
 */
function assignSpecialValueAndSubmit(form, value){
		form.find("input[name='special_value']").val(value);
		form.submit();	
}

/**
 * Function to toggle subject/comment fields when "email student" is selected
 * in the "add file" dialog box.
 */
function toggleAddFileEmailOptions(){
	if(jQuery("#add-file-modal input[name='email_student']:checked").length){
		jQuery("#add-file-email-options").slideDown(500);
	} else {
		jQuery("#add-file-email-options").slideUp(500);
	}
}

/**
 * Function to toggle the add file options.
 */
function toggleFileOptions(){
	return function(){
		var value = jQuery("#add-file-modal input[name='uploadType']:checked").val();
		var container = jQuery(this).next(".fileContainer");
		jQuery(".fileContainer").slideUp(500);		
		container.slideDown(500);				
	}
}