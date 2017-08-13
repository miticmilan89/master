 $(document).ready(function() {
	 appjobkey = generateUUID();
	 setTimeout("sessionCheck()", 3000);
	 
	 if (document.getElementById("PAGE_TITLE")!=null)
		 document.getElementById("CONTENT_TITLE").innerHTML=document.getElementById("PAGE_TITLE").innerHTML;
	 var s = trim(document.getElementById("CONTENT_TITLE").innerHTML);
	 document.getElementById("CONTENT_TITLE").innerHTML = s;
	 if (s=="")
		 document.getElementById("CONTENT_TITLE").style.display="none";
	
	 if (document.getElementById("PAGE_TOOLBAR")!=null)
		 document.getElementById("CONTENT_TOOLBAR").innerHTML=document.getElementById("PAGE_TOOLBAR").innerHTML;
	 else
		 document.getElementById("CONTENT_TOOLBAR").style.display="none";
	
	 applySearchOrderCss();
	
	 jQuery.timer = function (interval, callback) 
	 { 
		 interval = interval || 100;        
		 if (!callback) 
			 return false; 
		 _timer = function (interval, callback) { 
			 this.stop = function () {clearInterval(self.id);}; 
			 this.internalCallback = function () {callback(self);}; 
			 this.reset = function (val) { 
				 if (self.id) clearInterval(self.id); 
				 val = val || 100; 
				 this.id = setInterval(this.internalCallback, val); 
	      	  }; 
	      	  this.interval = interval; 
	      	  this.id = setInterval(this.internalCallback, this.interval); 
	      	  var self = this; 
	   	 }; 
	   	 return new _timer(interval, callback); 
	 };
	 
	 $.timer(1000, function (timer) { 
	     if (sessionRemainSecCount == -100) {
	    	 timer.stop();
	    	 return;
	     } 
		 if (sessionRemainSecCount < 30) {
			 $('#sessionTimer').css("color","red"); 
		 } else {
			 $('#sessionTimer').css("color","");
		 } 
	     c = sessionRemainSecCount--;
	     var sec = c;
	     var min = Math.floor(sec/60);
	     sec = sec % 60;
	     c= min > 0 ?  min + " min " + sec : "" + sec;
	     $('#sessionTimer').html("" + (c) + " sec"); 
	     if (sessionRemainSecCount <= 0) {
	    	 timer.stop();
	    	 logoutDialog(sessionExpired);
	     } 
	 }); 
	
	 $("#tabs").tabs();
	
	 $.widget("ui.tooltip", $.ui.tooltip, {
		 options: {
			 content: function () {
				 return $(this).prop('title');
			 }
		 }
	 });
	
	 $('.jqHelptooltip').tooltip({
	     position: {
	         my: "center bottom-5",
	         at: "center top",
	         using: function (position, feedback) {
	             $(this).css(position);
	             $(this).addClass("jqhelptooltip");
	             $("<div>")
	                 .addClass(feedback.vertical)
	                 .addClass(feedback.horizontal)
	                 .appendTo(this);
	         }
	     },
	     content: function() {
	         return $(this).attr('title');
	     }
	 });	
	
	 $('.jqErrtooltip').tooltip({
	     position: {
	         my: "center bottom-5",
	         at: "center top",
	         using: function (position, feedback) {
	             $(this).css(position);
	             $(this).addClass("jqerrtooltip");
	             $("<div>")
	                 .addClass(feedback.vertical)
	                 .addClass(feedback.horizontal)
	                 .appendTo(this);
	         }
	     }
	 });
	
	 pageLoaded=true;
	
	 var jqueryDatePattern = ""+appDatePattern.toLowerCase().replace('yyyy','yy');
	
	 $( ".datepicker" ).datepicker( "option", $.datepicker.regional['it'] );
	 $( ".datepicker" ).datepicker({
		 showButtonPanel: true,
		 dateFormat : ""+jqueryDatePattern,
		 changeMonth: true,
	     changeYear: true,
	     showOtherMonths: true,
	     selectOtherMonths: true,
	     beforeShow: function(input, inst) {
		 	if (checkIsReadOnly($(input)) || checkIsDisabled($(input))) 
				return false;
		 },
		 beforeShowDay : function(date){
			 var x = getDateAsString(date);
			 var x1 = getDateAsString(new Date());
			 var x2 = getDateAsString($( this).datepicker( "getDate" ));
			 if(x==x1) {
				 return [true, "todayHighlighted"];
			 } else if(x==x2) {
				 return [true, "selectedDateHighlighted"];
			 }else{
			 	return [true];
			 }
		}					 
	 });
	
	 $( ".datepicker" ).datepicker( "option", "showAnim", "slideDown");
	 $( ".timepicker" ).timepicker( "option", $.datepicker.regional['it'] );
		
	 var isTimeAMPM = appTimePattern.indexOf("a")!=-1;
	
	 $( ".timepicker" ).timepicker({
		 showPeriod: isTimeAMPM,
		 showLeadingZero: true,
		 amPmText: ['AM', 'PM'],
		 timeSeparator: ':',
		 nowButtonText: 'Now',
		 showNowButton: true,
		 closeButtonText: 'Close',
		 showCloseButton: true
	 });
	
	 $(".ddmenu").button({
		text: false,
		icons: {
			primary: "ui-icon-wrench"
		}
	 }).click(function() {
		 var menu = $( this ).next().toggle().position({
			 my: "left top",
			 at: "right top",
			 of: this
		 });
		 $(".list ul").not(menu).hide();
		 $( document ).one( "click", function() {
			 menu.hide();
		 });
		 return false;
	 }).next().hide().menu();
	
	 $(".accordionFilter button").click(function(e) {
		 e.stopPropagation();
	 });

	 $(".menuItemHeader").each(function() {
		 $(this).removeClass("ui-menu-item");
	 });
	
	 $( document ).ajaxError(function(event, jqxhr, settings, exception) {
		 return false;
	 });
	
	 $( document ).ajaxSuccess(function(event, jqxhr, settings, exception) {
		 var x = appPath+"/sessionCheck";
		 if(settings.url!=x)
			 sessionRemainSecCount = maxInactiveSeconds - 5;
	 });
	
	 $("#merchantLabel")
	 .autocomplete({
		 source: appPath+"/app/ajax/merchantList",
		 minLength: 0,
		 autoFocus: true,
		 selectFirst: true,		
		 select: function( event, ui ) {
			 $("#merchantLabel").val(ui.item.label);
			 return false;
		 }
	 }).focus(function () {
		 $(this).autocomplete("search");
	 }).autocomplete( "instance" )._renderItem = function( ul, item ) {
		 var l = item.item1;
		 var v = item.value;
		
		 if (l!=null && l.length>41) l = l.substring(0,40)+"...";
		
		 var c1 = "<div style='float:left;width:150px'>"+v+"</div>";
		 var c2 = l!=null && l!='' ? "<div style='float:left;width:350px'>"+l+"</div>" : "";
		 return $( "<li>" ).append( c1+ c2 ).appendTo( ul );
	 };
	
	 $("#terminalLabel")
	 .autocomplete({
		 source: function(request, response) {
			 var merchLabel = $("#merchantLabel").val();
			 var merchId;
			 if(merchLabel.indexOf(" ") != -1)
				 merchId = merchLabel.substring(0, merchLabel.indexOf(" "));
			 else
				 merchId = merchLabel.substring(0, merchLabel.length);
			 request.merchId = merchId;
			 $.ajax({
				 type : "get",
				 url : appPath + "/app/ajax/terminalList",
				 dataType : "json",
				 data : request,
				 success : function(r) {
					 response(r);
				 }
				});
		 },
		 minLength: 0,
		 autoFocus: true,
		 selectFirst: true,		
		 select: function( event, ui ) {
			 $("#terminalLabel").val(ui.item.label);
			 return false;
		 }
	 }).focus(function () {
		 $(this).autocomplete("search");
	 }).autocomplete( "instance" )._renderItem = function( ul, item ) {
		 var v = item.value != null ? item.value : "";
		 var l = item.item1;
		 if (l!=null && l.length>41) l = l.substring(0,40)+"...";
		
		 var c1 = "<div style='float:left;width:150px'>"+v+"</div>";
		 var c2 = "<div style='float:left;width:350px'>"+l+"</div>";
	   	 return $( "<li>" ).append( c1+ c2 ).appendTo( ul );
	 };
	 
	 //make HTML5 responsive design on tables with class _masterRespTable
	var headertext = [];
	$("._masterRespTable th").each(function() {
		var current = $(this).html();
		headertext.push( current.replace( /\r?\n|\r/,"") );
	});
	
	var j=0;
	$("._masterRespTable td").each(function() { 
		$(this).attr("data-th", headertext[j]);
		j++;
		if (headertext.length<=j)
			j=0;
	});
	
	var pageUrl = window.location.href;
	if (pageUrl.indexOf("/list") > -1) {
		$("#CONTENT_TOOLBAR").append("<button type='button' class='toolbarButton' onclick='return resetSearchPage()'>" + resetBtn + "</button>");
	}

	$('input[type=text], select, radio, hidden').each(function() {
		var value = $(this).val(); 
		$(this).attr("oldVal",value);
	});
	$('textarea').each(function(index,data) {
		var value = $(this).val().hashCode(); 
		$(this).attr("oldVal",value);
	});
	$('input[type=checkbox]').each(function() {
		var value = $(this).is(":checked"); 
		$(this).attr("oldVal",value);
	});
	$('input[type=password]').each(function() {
		var value = $(this).val().hashCode(); 
		$(this).attr("oldVal",value);
	});
	
	$(".requiredValidation").keyup(function() {
		var val = $(this).val();
		if (val == '') {
			$(this).next().html("<label style='color: red; margin-left: 5px;'>This field is required.</label>");
			$(this).next().css("display", "block");
		} else {
			$(this).next().html("");
		}
	});
	
	$(".urlValidation").keyup(function() {
		var myRegExp =/^(?:(?:https?|ftp):\/\/)(?:\S+(?::\S*)?@)?(?:(?!10(?:\.\d{1,3}){3})(?!127(?:\.\d{1,3}){3})(?!169\.254(?:\.\d{1,3}){2})(?!192\.168(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/[^\s]*)?$/i;
		var val = $(this).val();
		if (val != '') {
			if (!myRegExp.test(val)) {
				$(this).next().html("<label style='color: red; margin-left: 5px;'>" + notValidUrl + "</label>");
				$(this).next().css("display", "block");
			} else {
				$(this).next().html("<label style='color: green; margin-left: 5px;'>" + validUrl + "</label>");
				$(this).next().css("display", "block");
			}
		} else {
			if (!$(this).hasClass("requiredValidation")) {
				$(this).next().html("");
			}
		}
	});
	
	$(".emailValidation").keyup(function() {
		var myRegExp =/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
		var val = $(this).val();
		if (val != '') {
			if (!myRegExp.test(val)) {
				$(this).next().html("<label style='color: red; margin-left: 5px;'>" + notValidEmail + "</label>");
				$(this).next().css("display", "block");
			} else {
				$(this).next().html("<label style='color: green; margin-left: 5px;'>" + validEmail + "</label>");
				$(this).next().css("display", "block");
			}
		} else {
			if (!$(this).hasClass("requiredValidation")) {
				$(this).next().html("");
			}
		}
	});
});

 