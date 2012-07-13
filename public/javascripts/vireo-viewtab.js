/**
 * Swap to input field function
 */
function swapToInputHandler(){	
		return function() {
			if(jQuery(".editing").length == 0){
				
			//Clean up
			jQuery(".tooltip").remove();
			jQuery(this).find(".tooltip-icon").remove();
			jQuery("#backup").remove()
			
			var editItem = jQuery(this);       
			var value = editItem.text().trim();
			
			if(value=="none"){
				value="";
				jQuery("body").append('<div id="backup"></div>')
			} else {
				//Make back up info			
				jQuery("body").append('<div id="backup">'+editItem.html().trim()+'</div>')
			}
			
			//Graduation Semester
			if(editItem.attr("id")=="gradSemester"){
				var markup = '<div id="'+editItem.attr("id")+'" class="editing select">';
				markup += '<select id="gradMonth">';
				markup += jQuery("#gradMonthOptions").html();
				markup += '</select><select id="gradYear">'
				markup += jQuery("#gradYearOptions").html();
				markup += '</select><br /><i class="icon-remove"></i>&nbsp<i class="icon-ok"></i></div>';
				editItem.replaceWith(markup);
				
				jQuery("option").each(function(){
					if(value.indexOf(jQuery(this).text()) >= 0){
						jQuery(this).attr("selected", "selected");
					}
				})
			} else {
			
				//Text Areas
				if(editItem.hasClass("textarea")){
					editItem.replaceWith('<div id="'+editItem.attr("id")+'" class="editing textarea"><textarea class="field" textarea">'+value+'</textarea><br /><i class="icon-remove"></i>&nbsp<i class="icon-ok"></i></div>');
				//Select Drop Downs
				} else if(editItem.hasClass("select")){
					var selectCode = '<div id="'+editItem.attr("id")+'" class="editing select"><select class="field">';
					selectCode += jQuery("#"+editItem.attr("id")+"Options").html();
					selectCode += '</select><br /><i class="icon-remove"></i>&nbsp<i class="icon-ok"></i></div>';
					editItem.replaceWith(selectCode);
					jQuery(".field option").each(function(){
						if(jQuery(this).text()==value){
							jQuery(this).attr("selected","selected");
						}
					})
				//Input Fields
				} else {
					editItem.replaceWith('<div id="'+editItem.attr("id")+'" class="editing"><input class="field" type="text" value="'+value+'" /><br /><i class="icon-remove"></i>&nbsp<i class="icon-ok"></i></div>');
				}
				//jQuery(".editing input").setTimeout(jQuery(".editing").focus());
			}
		}
	}
}

/**
 * This function swaps the committee member content
 * into editable fields.
 */
function editCommitteeMemberHandler(){	
	return function(){
		if(jQuery(".editing").length == 0){
			
			//Clean up
			jQuery(".tooltip").remove();
			jQuery(this).find(".tooltip-icon").remove();
			jQuery("#backup").remove();
			
			//Backup
			jQuery("body").append('<div id="backup">'+jQuery(this).html().trim()+'</div>');
			
			var memberId = jQuery(this).parent("li").attr("class");		
			var firstName = jQuery(this).find(".firstName").text();
			var lastName = jQuery(this).find(".lastName").text();
			var middleName = jQuery(this).find(".middleName").text();
			
			var chair = (jQuery(this).find(".chair").text().trim()=="chair");
			var checked = "";
			if(chair){
				checked = 'checked="checked"';
			}
			
			var markup = '<div class="editing"><table>';
			markup += '<tr><td><b>Last Name</b></td><td><b>First Name</b></td><td><b>Middle Name</b></td><td></td></tr>'
			markup += '<tr>'
			markup += '<td><input id="memberId" class="hidden" type="hidden" value="'+memberId+'" />';
			markup += '<input id="cmLastName" class="span2" type="text" value="'+lastName+'" /></td>';
			markup += '<td><input id="cmFirstName" class="span2" type="text" value="'+firstName+'" /></td>';
			markup += '<td><input id="cmMiddleName" class="span2" type="text" value="'+middleName+'" /></td>';
			markup += '<td><input id="chair" type="checkbox" class="checkbox" '+checked+' > chair</input></td>';
			markup += '</tr>';
			markup += '</table><i class="icon-remove"></i>&nbsp<i class="icon-ok"></i></div>';
			
			jQuery(this).replaceWith(markup);
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
function commitChangesHandler(eventTarget, jsonURL, graduationURL, committeeURL, subId){
	var classValue = '';
	var fieldItem;
	var parent = eventTarget.parent();
	if(jQuery(".editing").hasClass("textarea")){
		classValue = classValue + 'textarea ';
		fieldItem = jQuery(".editing textarea");
	} else if(jQuery(".editing").hasClass("select")){
		classValue = classValue + 'select ';
		fieldItem = jQuery(".editing select");
	} else {
		fieldItem = jQuery(".editing input");
	}
	var id=jQuery(".editing").attr("id");
	var theValue;
	if(fieldItem.val().trim()){
		theValue = fieldItem.val();
	}
	
	var gradSemester;
	var monthValue;
	var yearValue;
	if(parent.attr("id")=="gradSemester"){
		gradSemester = true;
		monthValue = jQuery("#gradMonth").val().trim();
		yearValue = jQuery("#gradYear").val().trim();
	}
	
	var committeeMember;
	var committeeFirstName;
	var committeeLastName;
	var committeeMiddleName;
	var committeeChair;
	var committeeId;
	
	if(eventTarget.closest("#committeeMembers").length){
		committeeMember = true;
		committeeFirstName = jQuery("#cmFirstName").val().trim();
		committeeLastName = jQuery("#cmLastName").val().trim();
		committeeMiddleName = jQuery("#cmMiddleName").val().trim();
		committeeChair = jQuery("#chair").is(':checked');
		committeeId = jQuery("#memberId").val().trim();
	}
	
	jQuery(".editing").replaceWith('<div class="'+id+' progress progress-striped active"><div class="bar" style="width:100%"></div></div>');
	
	if(gradSemester){
		jQuery.ajax({
			url:graduationURL,
			data:{
				subId:subId,
				month:monthValue,
				year:yearValue
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
				
				if(data.success){
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="'+classValue+'"><i class="icon-pencil"></i> '+currentValue+'</span>');
				}
			},
			error:function(){
				jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'">'+jQuery("#backup").html()+' <a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
				jQuery('.tooltip-icon').tooltip();
			}
			
		});
	} else if(committeeMember) {
		jQuery.ajax({
			url:committeeURL,
			data:{
				id:committeeId,
				firstName:committeeFirstName,
				lastName:committeeLastName,
				middleName:committeeMiddleName,
				chair:committeeChair
			},
			dataType:'json',
			type:'POST',
			success:function(data){	
				if(data.success){
					markup = '<div class="editCommitteeMember">';
					markup += '<span class="lastName">'+data.lastName+'</span><span class="seperator">,&nbsp;</span>';
					markup += '<span class="firstName">'+data.firstName+'&nbsp;</span>';
					markup += '<span class="middleName">'+data.middleName+'&nbsp;</span>';						
					if(data.chair=="true"){
						markup += '<span class="chair label label-info">';
						markup += 'chair';
						markup += '</span>';
					}
					markup += '</div>';
					
					jQuery("li."+data.id).html(markup);
				} else {
					markup = '<div class="editCommitteeMember">';
					markup += '<span class="lastName">'+data.lastName+'</span><span class="seperator">,&nbsp;</span>';
					markup += '<span class="firstName">'+data.firstName+'&nbsp;</span>';
					markup += '<span class="middleName">'+data.middleName+'&nbsp;</span>';						
					if(data.chair=="true"){
						markup += '<span class="chair label label-info">';
						markup += 'chair';
						markup += '</span>';
					}
					markup += '<span><a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><span class="badge badge-important"><i class="icon-warning-sign icon-white"></i></span></a></span>';
					markup += '</div>';
					
					jQuery("li."+data.id).html(markup);
					jQuery('.tooltip-icon').tooltip();
				}
			},
			error:function(){
				jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'">'+jQuery("#backup").html()+' <a href="#" class="tooltip-icon" rel="tooltip" title="There was an error with your request."><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
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
				
				if(data.success){
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="'+classValue+'"><i class="icon-pencil"></i> '+currentValue+'</span>');
					jQuery("#degreeLevel").text(data.degreeLevel);						
				} else {
					jQuery("div."+id).replaceWith('<span id="'+id+'" class="error '+classValue+'"><i class="icon-pencil"></i> '+currentValue+' <a href="#" class="tooltip-icon" rel="tooltip" title="'+data.message+'"><div class="badge badge-important"><i class="icon-warning-sign icon-white"></i></div></a></span>');
					jQuery('.tooltip-icon').tooltip();
				}
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
 * This function cancels the currently edited field
 * and replaces the content with a backup stored in 
 * a hidden div (#backup)
 */
function cancelEditingHandler(){
	return function() {
		if(jQuery(this).closest("#committeeMembers").length){

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
			} else {
				fieldItem = jQuery(".editing input");
			}
			var id=jQuery(".editing").attr("id");
			
			var currentValue = jQuery("#backup").html();
			
			if(!currentValue){
				currentValue = '<i class="icon-pencil"></i> none';
				classValue += "empty ";
			}
			
			jQuery(".editing").replaceWith('<span id="'+id+'" class="'+classValue+'">'+currentValue+'</span>');
		}
		
		jQuery("#backup").remove();
	}
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