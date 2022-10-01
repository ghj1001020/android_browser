$(document).ready( function(){
	
	pages.init();
	
} );

var pages = {
	
	init : function() {
		pages.initInterface();
	} ,
	
	initInterface : function() {
        // Web -> App 호출
		$("#btnPopup").click( function() {
		    var os = pages.checkMobileOs();

            var json = {
                title : "알림" ,
                message : "웹 -> 앱 호출 팝업 ......"
            };
		    if( os == "android" ) {
                window.browserApp.appAlertPopup( JSON.stringify(json) )
		    }
		    else if( os == "ios" ) {
                webkit.messageHandlers.appAlertPopup.postMessage( JSON.stringify(json) );
		    }
		});
        
        $("#btnSms").click( function() {
            var os = pages.checkMobileOs();

            if( os == "android" ) {
                // 안드로이드
                var message = $("#txtSmsBody").text().replace(/%/g , "%25")
                location.href = "sms:01012345678?body=" + message;
            }
            else if( os == "ios" ) {
                // IOS
                location.href = "sms:01012345678&body=" + $("#txtSmsBody").text();
            }
        });
        
        $("#btnTel").click( function() {
            console.log("tel = " + $(this).text() );
            location.href = $(this).text();
        });
        
        $("#btnDownload").click( function() {
            location.href = $("#txtDownload").text();
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
}

// App -> Web 호출
function appGetMessage( json ) {
    console.log( "json=" + json )
    $("#txtAppMessage").text( json )
}

// App -> Web 호출 후 App으로 값 리턴
function appGetMessageReturn( json ) {
    console.log( "json=" + json )
    $("#txtAppMessageReturn").text( json )
    
    return "appGetMessageReturn 호출 !!!"
}
