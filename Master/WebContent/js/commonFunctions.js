var enableHTML5Valid = false;

if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str){
    return this.slice(0, str.length) == str;
  };
}
if (typeof String.prototype.endsWith != 'function') {
  String.prototype.endsWith = function (str){
    return this.slice(-str.length) == str;
  };
}
function removeClass(el, remove) {
    var newClassName = "";
    var i;
    var classes = el.className.split(" ");
    for(i = 0; i < classes.length; i++) {
        if(classes[i] !== remove) {
            newClassName += classes[i] + " ";
        }
    }
    el.className = newClassName;
}
function djAlert(s) {
	// replacement for alert js function
    var title_msg = 'Alert';
    $("<div></div>").html(s).dialog({
        title: title_msg,
        resizable: false,
        modal: true,
        buttons: {
            "Ok": function() 
            {
                $( this ).dialog( "close" );
            }
        }
    });	
}
function prepareUrl(url) {
	var x = url.indexOf('?') ? "&" : "?";
	if (url.indexOf('_dummy')==-1) 
			url += x+'_dummy='+(new Date()).getTime();
}
function submitDefaultForm() {
	if(!isFormChanged() && defaultFormName != "filterForm") {
		warningDialog(formNotChanged);
		return false;
	}
	if( typeof defaultSubmitBtnName !== 'undefined' ) 
		return submitFormInternal(document.getElementById(defaultFormName),defaultSubmitBtnName);
	else
		return submitFormInternal(document.getElementById(defaultFormName));
}
function submitDefaultFormDelete() {
	if( typeof defaultSubmitBtnName !== 'undefined' ) 
		return submitFormInternal(document.getElementById(defaultFormName),defaultSubmitBtnName);
	else
		return submitFormInternal(document.getElementById(defaultFormName));
}
function submitDefaultFormDisable() {
	if( typeof defaultSubmitBtnName !== 'undefined' ) 
		return submitFormInternal(document.getElementById(defaultFormName),defaultSubmitBtnName);
	else
		return submitFormInternal(document.getElementById(defaultFormName));
}
function isFormChanged() {
	var formChanged = false;
	$('input[type=text], select, radio, hidden').each(function(index,data) {
		var value = $(this).val(); 
		var oldValue = $(this).attr("oldVal");
		if (value != oldValue) {
			formChanged = true;
		}
	});
	$('textarea').each(function(index,data) {
		var value = $(this).val().hashCode(); 
		var oldValue = $(this).attr("oldVal");
		if (value != oldValue) {
			formChanged = true;
		}
	});
	$('input[type=checkbox]').each(function(index,data) {
		var value = $(this).is(":checked");
		value = "" + value + "";
		var oldValue = $(this).attr("oldVal");
		if (value != oldValue) {
			formChanged = true;
		}
	});
	$('input[type=password]').each(function(index,data) {
		var value = $(this).val().hashCode(); 
		var oldValue = $(this).attr("oldVal");
		if (value != oldValue) {
			formChanged = true;
		}
	});
	return formChanged;
}
function submitFormInternal(form, submitBtnId) {
	if (formSubmitted) {
		showError(label.formSubmitted);
		return;
	}
	formSubmitted=true;
	try {
		showLoadingDiv()
	} catch (e){
		//ignore error
	}

	if (enableHTML5Valid) {
		if (submitBtnId) {
			$("#"+submitBtnId).click();
		} else {
			form.submit();
		}
	} else {
		form.submit();	
	}
	return false;
}
function showError(err) {
	djAlert(err);
}
function trim (str) {
	if (!String.prototype.trim) {
		str = str.replace(/^\s+/, '');
		for (var i = str.length - 1; i >= 0; i--) {
			if (/\S/.test(str.charAt(i))) {
				str = str.substring(0, i + 1);
				break;
			}
		}
	 } else {
		 str = str.trim();
	 }
	return str;
}
function gotoRelativeUrl(url) {
	showLoadingDiv();
	document.location.href=appPath+url;
	return false;
}
function clearErrors() {
	$(".jqErrtooltip").attr("title","");
	$(".jqErrtooltip").hide();
	$("._masterMsgErrTemp").remove();
	return false;
}
function handleError(code, message) {
	internalHandleError(code, message, "ERR");
}
function handleMessage(code, message) {
	internalHandleError(code, message, "MSG");
}
function internalHandleError(code, message, type) {
	var messageBody ;
	if (type == 'ERR') {
		messageBody = _masterERRTd.replace("|"+type+"_TXT|",message);
	} else {
		messageBody = _masterMSGTd.replace("|"+type+"_TXT|",message);
	}
	$("#_app"+type).append(messageBody);
	$("#_app"+type).show();
	
	if (type == 'ERR') {
		if (code != 'global') {
			var b = document.getElementById(code + '_' + type);
			
			if (b!=null) {
				$("#"+code+"_"+type).attr("title",message);
				$("#"+code+"_"+type).show();
			} else {
				$('<div class="jqErrtooltip jqerrbckg" id="'+code+'_'+type+'" style="display:none" title="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>').insertAfter($("#"+code));
				$("#"+code+"_"+type).attr("title",message);
			 	$("#"+code+"_"+type).show();
			 	showErrorTooltip();
			}
		}
	}
	return false;
}
function applySearchOrder(elem) {
	var e = elem.getAttribute("_col");
	var currValue = document.getElementById("orderColumn").value;
	if (currValue==null) 
		currValue='';
	
	var currAsc = document.getElementById("orderAsc").value;
	if (currAsc=='') 
		currAsc='true';
	
	if (currValue==e) {
		//change asc desc
		currAsc = 'true'==currAsc ? 'false' : 'true';
	} else {
		//new column first time
		currAsc = 'true';
		currValue=e;
	}
	document.getElementById("orderAsc").value = currAsc;
	document.getElementById("orderColumn").value = currValue;

	document.getElementById('submitSearchBtn').click();
	return false;
}	

function applySearchOrderCss() {	
	var elements = document.getElementsByTagName("th");

	if (elements) {
		var n = elements.length;
		for (var i = 0; i < n; i++) {
			var element = elements[i];
		    var s = element.getAttribute("_col");
			if (!(s==null || s=='undefined' || s=="null")) {
				var orderColumn = document.getElementById("orderColumn").value;
				var orderAsc = document.getElementById("orderAsc").value;
				orderAsc = orderAsc=='false' ? "Asc" : "Desc";
				
				removeClass(element,"thSortedAsc");
				removeClass(element,"thSortedDesc");
				element.className = s==orderColumn ? " thSorted"+orderAsc : " thSortedOff ";
			}
		}
	}
}
function isMobile() {
	return (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) || (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.platform)));
}
function checkMenuLabel(label) {
	// here we can handle label values, if needed in future
	return label!=null;
}
function goToMenuItem(url, label) {
	if (jQuery.trim(""+url)=='' || jQuery.trim(""+url)=='null') 
           return false;
	if ('/app/OPEN_NEW_TAB'==url) {
		var win = window.open(appPath+url, '_blank');
		win.focus();
		return;
	}
	if (url.length>5 && url.substring(0,5)=='/app/') {
		showLoadingDiv();
		document.location=appPath+url;
	}
	checkMenuLabel(label);
	return false;
}

function showHideFilter(filterName) {
	var elem = $("#" + filterName);
	var sign = elem.html();

	var status;
	if(sign.match("-")) {
		elem.html("+");
		status = "on";
	} else {
		elem.html("-");
		status = "off";
	} 
	var action = appPath + "/app/saveFilter/"+filterName+"/"+status;
	
	setTimeout(function(){
		$.ajax({
	   		type: 'GET',
	   		url: action,
	   		async: false, 
	   		success: function() {
	   		}
	   	});
	},500);
}

function deleteItem(url) {
	confirmDeleteDialog(url, label_delete_messagetext);
   	return false;
}
function disableItem(url) {
	confirmDisableDialog(url, label_disable_messagetext);
   	return false;
}

function confirmDeleteDialog(url, text) { 
    $("#_dialogDivImg").attr("src",appPath+"/themes/common/qmarks/d"+getRandomInt(1,8)+".jpg"); 
    var buttons = {}; 
    buttons[ label_ok ] =function() {$(this).dialog("close"); document.forms[defaultFormName].action=appPath+url; submitDefaultFormDelete();}; 
    buttons[ label_close ] =function() { $(this).dialog("close");}; 
    if (text) 
        $('#_dialogDivContent').html(text); 
    else 
        $('#_dialogDivContent').html(""); 
    $('#_dialogDiv').dialog({ buttons:buttons, title:label_confirmation_messagetitle, resizable: false,modal: true,width:350}); 
    $('#_dialogDiv').dialog('open');                  
}
function confirmDisableDialog(url, text) { 
    $("#_dialogDivImg").attr("src",appPath+"/themes/common/qmarks/d"+getRandomInt(1,8)+".jpg"); 
    var buttons = {}; 
    buttons[ label_ok ] =function() {$(this).dialog("close"); document.forms[defaultFormName].action=appPath+url; submitDefaultFormDisable();}; 
    buttons[ label_close ] =function() { $(this).dialog("close");}; 
    if (text) 
        $('#_dialogDivContent').html(text); 
    else 
        $('#_dialogDivContent').html(""); 
    $('#_dialogDiv').dialog({ buttons:buttons, title:label_confirmation_messagetitle, resizable: false,modal: true,width:350}); 
    $('#_dialogDiv').dialog('open');                  
}

function deleteNoFormItem(url) {
	confirmDeleteNoFormDialog(url, label_delete_messagetext);
   	return false;
}

function confirmDeleteNoFormDialog(url, text) { 
	alert(url);
    $("#_dialogDivImg").attr("src",appPath+"/themes/common/qmarks/d"+getRandomInt(1,8)+".jpg"); 
    var buttons = {}; 
    buttons[ label_ok ] =function() {$(this).dialog("close"); document.location=appPath+url;}; 
    buttons[ label_close ] =function() { $(this).dialog("close");}; 
    if (text) 
        $('#_dialogDivContent').html(text); 
    else 
        $('#_dialogDivContent').html(""); 
    $('#_dialogDiv').dialog({ buttons:buttons, title:label_confirmation_messagetitle, resizable: false,modal: true,width:350}); 
    $('#_dialogDiv').dialog('open');                  
}

function confirmDialog(url, text) { 
    
    $("#_dialogDivImg").attr("src",appPath+"/themes/common/qmarks/p"+getRandomInt(1,8)+".jpg"); 
    var buttons = {}; 
    buttons[ label_ok ] =function() {$(this).dialog("close"); document.forms[defaultFormName].action=appPath+url; submitDefaultForm();}; 
    buttons[ label_close ] =function() { $(this).dialog("close");}; 
    if (text) 
        $('#_dialogDivContent').html(text); 
    else 
        $('#_dialogDivContent').html(""); 
    $('#_dialogDiv').dialog({ buttons:buttons, title:label_confirmation_messagetitle, resizable: false,modal: true,width:350}); 
    $('#_dialogDiv').dialog('open');                  
}

function warningDialog(text) { 
    
    $("#_dialogDivImg").attr("src",appPath+"/themes/common/qmarks/u"+getRandomInt(1,8)+".jpg"); 
    var buttons = {}; 
    buttons[ label_ok ] =function() {$(this).dialog("close");}; 
    if (text) 
        $('#_dialogDivContent').html(text); 
    else 
        $('#_dialogDivContent').html(""); 
    $('#_dialogDiv').dialog({ buttons:buttons, title:label_warning_messagetitle, resizable: false,modal: true,width:350}); 
    $('#_dialogDiv').dialog('open');                  
}

function logoutDialog(text) { 
    
    $("#_dialogDivImg").attr("src",appPath+"/logout.png"); 
    var buttons = {}; 
    buttons[ label_ok ] =function() {document.location.href=appPath+"/app/logout?loginType="+loginType}; 
    if (text) 
        $('#_dialogDivContent').html(text); 
    else 
        $('#_dialogDivContent').html(""); 
    $('#_dialogDiv').dialog({
    	buttons:buttons, title:label_warning_messagetitle, resizable: false,modal: true,width:350,
    	dialogClass: 'no-close' 
    }); 
    $('#_dialogDiv').dialog('open');                  
}

function modalDialog(html, title, buttons) {
	$('#_modalDialogDiv').html(html); 
	$('#_modalDialogDiv').dialog({ buttons:buttons, title:title, resizable: true, modal: true, width:'auto', height:'auto', resize:'auto'}).position({my:"center", at:"center", of:window}); 
    $('#_modalDialogDiv').dialog('open');
    $(".ui-dialog ").css("z-index", "99999");
    return $('#_modalDialogDiv');
}
function exportDialog(title, buttons) {	
	$('#_exportDialogDiv').dialog({buttons:buttons, title:title, resizable: true, modal: true, width: 360, height: 170, dialogClass: 'no-close'}).position({my:"center", at:"center", of:window}); 
    $('#_exportDialogDiv').dialog('open');
    $(".ui-dialog ").css("z-index", "99999");
    
    return $('#_exportDialogDiv');
}

function getRandomInt(min, max) { 
    try { 
        return Math.floor(Math.random() * (max - min + 1)) + min; 
    } catch (e) { 
        return 1; 
    } 
}

function checkIsReadOnly(elem) {
	var readonly = $(elem).attr("readonly");
    if(readonly && readonly.toLowerCase()!=='false') { // this is readonly
        return true;
    }			
	readonly = $(elem).attr("readOnly");
    if(readonly && readonly.toLowerCase()!=='false') { // this is readonly
        return true;
    }			
    return false;
}
function checkIsDisabled(elem) {
	return $(elem).is(':disabled');
}

function getDateAsString(today) {
	if (today==null || today=="" || today=='unknown') 
		return "";
	var d = ""+today.getDate();
	var m = ""+(today.getMonth()+1);
	var y = ""+today.getFullYear();
	if (d.length==1) 
		d="0"+d;
	if (m.length==1) 
		m="0"+m;
	var dToday = appDatePattern.toLowerCase().replace("dd",d);
	dToday = dToday.toLowerCase().replace("mm",m);
	dToday = dToday.toLowerCase().replace("yyyy",y);
	return dToday;
}

function showLoadingDiv() {
	$(".ui-widget-header").hide();
	 $('#_loadingDiv').dialog({autoOpen: true,
	        height: 90, 
	        width: "auto",
	        create: function () {
	        	$(".ui-widget-header").hide();
	        },
	        modal: true,
	         my: "center",
	         at: "center",
	         of: window});	
	 return true;
}
function hideLoadingDiv() {
	 $('#_loadingDiv').dialog("close");
	 $(".ui-widget-header").show();
}
function checkIsLoginPage(h) {
	if (h!=null && (""+h).indexOf("var loginPage=true;")!=-1) {
		document.location = appPath+"/app/login/"+loginType;
		return true;
	}
	return false;
}
function sessionCheck() {
	var action = appPath + "/sessionCheck";
		$.ajax({
	   		type: 'POST',
	   		url: action,
	   		async: true,
	   		data : { UQ : appUnique, lt : loginType, usr : appUsr}, 
	   		cache : false,
	   		success: function(data) {
	   			if (data=="-1") {
	   				sessionRemainSecCount=-100;
	   				logoutDialog(sessionExpired);
	   			} else if (data=="-2") {
	   				document.location = appPath+"/app/home";
	   			} else {
	   				sessionRemainSecCount=data;
	   				setTimeout("sessionCheck()", 10000);
	   			}
	   		}
	   	});	
}
function resetSearchPage() {
	var url = window.location.href;
	if (url.indexOf("?") != -1)
		url = url.substring(0, url.indexOf("?"));
	document.location = url;
}
function goToHome() {
	document.location = appPath+"/app/home";
}

String.prototype.hashCode = function() {
	var hash = 0;
	if (this.length == 0) 
		return hash;
	for (var i = 0; i < this.length; i++) {
		var char = this.charCodeAt(i);
		hash = ((hash<<5)-hash)+char;
		hash = hash & hash;
	}
	return hash;
}
function djSortTableGet(x) {
	return x.is("[sortValue]") ? x.attr("sortValue") : x.html();
}
function djSortTable(id, nodeNo, sortType, thElem) {
	
	var ord = $(thElem).hasClass('thSortedDesc');
	$(thElem).addClass(ord ? 'thSortedAsc' : 'thSortedDesc');
	
	//remove on all th nodes order class
	$('#'+id+' th').each(function() {
		$(this).removeClass("thSortedDesc");
		$(this).removeClass("thSortedAsc");
		$(this).addClass("thSortedOff");
	});

	//ad order class just on one node
	$(thElem).addClass(ord ? 'thSortedAsc' : 'thSortedDesc');
	//remove thSortedOff just for selected header
	$(thElem).removeClass("thSortedOff");
	
	//make a sort 
	$('#'+id+'>tbody>tr').tsort('td:eq('+nodeNo+')[ sorted]',{sortFunction:function(a,b){
		var x=$('td:eq('+nodeNo+')',a.e);
		var y=$('td:eq('+nodeNo+')',b.e);
		
		var valA = djSortTableGet(x);
		var valB = djSortTableGet(y);
		
		if (sortType=='num') {
			valA = parseNumber(valA);
			valB = parseNumber(valB);
		}
		if (!ord)
			return valA===valB?0:(valA>valB?1:-1);
		else
			return valA===valB?0:(valA>valB?-1:1);
	}});
	//after sort, set colors
	var i=0;
	$('#'+id+'').find("tbody").children().each(function() {
		$(this).removeClass("odd");
		if (i++%2==0 && $(this).attr("total")!='true') {
				$(this).addClass("odd");
		}
		
	});	
}

function parseNumber(value) {
	if (jQuery.trim(value)=="") 
		return 0;
	if (isNaN(value)) {
		value=""+value;
		value = value.replace(appTousandSeparator,"");
		value = value.replace(appDecimalSeparator,".");
	}
	return isNaN(value) ? 0 : parseFloat(value);
}

function generateUUID(){
    var d = new Date().getTime();
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
}
