$(document).ready( function(){
	
	pages.init();
	
} );

var pages = {
	
	init : function() {
		pages.initInterface();
	} ,
	
	initInterface : function() {
		$("#btn_toast").click( function() {
            var json = {
                title : "알림" ,
                message : "테스트 팝업 ...... TEST!!!!"
            };
            webkit.messageHandlers.appAlertPopup.postMessage( JSON.stringify(json) );
		});
	}
}

// App -> Web 호출
function appGetMessage( json ) {
    console.log( "json=" + json )
    $("#txt_getMessage").text( json )
    
    var data = JSON.parse( json );
    $("#txt_getMessage_title").text( data.title )
    $("#txt_getMessage_msg").text( data.message )
}

// App -> Web 호출 후 리턴
function appGetMessageReturn( json ) {
    console.log( "json=" + json )
    $("#txt_getMessage").text( json )
    
    var data = JSON.parse( json );
    $("#txt_getMessage_title").text( data.title )
    $("#txt_getMessage_msg").text( data.message )
    
    return "appGetMessageReturn 호출 !!!"
}
