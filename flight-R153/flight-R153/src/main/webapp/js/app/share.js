/**
 *
 * Created by alabdullahwi on 5/26/2015.
 */


function displayShareWindow(){
    var htmlStr = "<img style='cursor:pointer; float: right;' src='img/icon_x.gif' onclick='hideShareWindow()'/>";
    $("#dialogShare").css('top', getBrowserWindowHeight()/2 - 50);
    $("#dialogShare").css('left', getBrowserWindowWidth()/2 - 50);
    htmlStr += "<br><a href='javascript:generateShare(0);' title='Share on Facebook' alt='Share on Facebook' style='color: #6FB56B'><img src='img/social-facebook.png' align='absmiddle'/>&nbsp;&nbsp;Facebook</a>";
    htmlStr += "<br><a href='javascript:generateShare(1);' title='Tweet' alt='Tweet' style='color: #6FB56B'><img src='img/social-twitter.png' align='absmiddle'/>&nbsp;&nbsp;Twitter</a>";
    htmlStr += "<br><a href='javascript:generateShare(2);' style='color: #6FB56B'><img src='img/email.png' align='absmiddle'/>&nbsp;&nbsp;Email</a>";
    htmlStr += "<br><a href='javascript:generateShare(3);' style='color: #6FB56B'><img src='img/copyLink.png' align='absmiddle'/>&nbsp;&nbsp;Copy Link</a>";
    /*htmlStr += "<br><a href='javascript:generateShare(4);'>Print</a>";
     htmlStr += "<br><a href='javascript:generateShare(5);'>Copy Embed Code</a>";*/

    /*
     var htmlStr = "<b>Share Options</b>";
     jQuery("#dialogShare").css('top', getBrowserWindowHeight()/2 - 50);
     jQuery("#dialogShare").css('left', getBrowserWindowWidth()/2 - 50);
     htmlStr += "<br><br><a href='javascript:generateShare(0);'>Facebook</a>";
     htmlStr += "<br><a href='javascript:generateShare(1);'>Twitter</a>";
     htmlStr += "<br><a href='javascript:generateShare(2);'>Email</a>";
     htmlStr += "<br><a href='javascript:generateShare(3);'>Copy Link</a>";
     htmlStr += "<br><a href='javascript:generateShare(4);'>Print</a>";
     htmlStr += "<br><a href='javascript:generateShare(5);'>Copy Embed Code</a>";
     htmlStr += "<br><br><a href='javascript:hideShareWindow();'><b>Close Window</b></a>";
     */

    $("#dialogShare").html(htmlStr);

    $("#mask").css('width',$(document).width());
    $("#mask").css('height',$(window).height());
    $("#mask").fadeIn(1000);
    $("#mask").fadeTo("slow",0.5);

    $("#dialogShare").fadeIn(100);
}


function hideShareWindow(){
    $("#mask").hide();
    $("#dialogShare").hide();
}

function shareFacebook(){
    var facebookUrl = "http://www.facebook.com/sharer.php?u=";
    addressString = escape(window.location);
    googlurl(addressString, function(shortUrl) {
        if (shortUrl != null) {
            window.open(facebookUrl+shortUrl,'','width=1024,height=600');
        } else {
            window.open(facebookUrl+addressString,'','width=1024,height=600');
        }
    });
}

function shareTwitter(){
    var addressString = "http://twitter.com/share?url=";
    addressString += escape(window.location);
    window.open(addressString,'','width=400,height=400');
}

function generateShare(selection){
    hideShareWindow();
    if(selection == 0){
        shareFacebook();
    } else if(selection == 1){
        shareTwitter();
    } else if(selection == 2){
        sendMail();
    } else if(selection == 3){
        alert(window.location);
    } else if(selection == 4){
        printPage();
    } else if(selection == 5){
        alert("Embedded Code");
    }

}


function googlurl(url, callback) {
    jsonlib.fetch({
        url: 'https://www.googleapis.com/urlshortener/v1/url',
        header: 'Content-Type: application/json',
        data: JSON.stringify({longUrl: url})}, function (m) {
        var result = null;
        try {
            result = JSON.parse(m.content).id;
            if (typeof result != 'string') result = null;
        } catch (e) {
            result = null;
        }
        callback(result);
    });
    //jQuery.ajax({
    //	type: 'POST',
    //	url: 'https://www.googleapis.com/urlshortener/v1/url',
    //	data: JSON.stringify({longUrl: url}),
    //	dataType: "json",
    //	success: function(response) {
    //		var result = null;
    //		try {
    //			result = JSON.parse(reponse.content).id;
    //			if (typeof result != 'string') result = null;
    //		} catch (e) {
    //			result = null;
    //		}
    //		callback(result);
    //	}
    //});
}