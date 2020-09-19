$(document).ready( function(){
	
	pages.init();
	
} );

var pages = {
	
	init : function() {
		pages.initInterface();
	} ,
	
	initInterface : function() {
		$("#btn_sms").click( function() {
			var os = pages.checkMobileOs();

			if( os == "android" ) {
				// 안드로이드
				var message = $("#txt_sms_body").text().replace(/%/g , "%25")
				location.href = "sms:01012345678?body=" + message;
			}
			else if( os == "ios" ) {
				// IOS
				location.href = "sms:01012345678&body=" + $("#txt_sms_body").text();				
			}
		});
	} ,
	
	// 안드로이드/IOS 구분
	checkMobileOs : function() {
		var userAgent = navigator.userAgent.toLowerCase();
		var os = "";
		if( userAgent.indexOf("android") > -1 ) {
			os = "android";
		}
		else if( userAgent.indexOf("iphone") > -1 || userAgent.indexOf("ipad") > -1 || userAgent.indexOf("ipod") > -1 ) {
			os = "ios";
		}
		
		return os;
	}
};
