function redirectMobileSite(url){
	var searchagent= new Array("iphone","ipod","ipad","aspen","dream","android","cupcake","blackberry","opera mini","webos","incognito","webmate");
	var stragent=navigator.userAgent;
	var i,strfind;
	for(i=0;i<searchagent.length; i++){
		struseragent=stragent.toLowerCase();
		strfind=struseragent.search(searchagent[i]);
		if(strfind>=0){
			location=url;
		}
	}
}

function parseUri (str) {
	var	o   = parseUri.options,
		m   = o.parser[o.strictMode ? "strict" : "loose"].exec(str),
		uri = {},
		i   = 14;

	while (i--) uri[o.key[i]] = m[i] || "";

	uri[o.q.name] = {};
	uri[o.key[12]].replace(o.q.parser, function ($0, $1, $2) {
		if ($1) uri[o.q.name][$1] = $2;
	});
	return uri;
};

function getUrlMobile(guid) {
	return 'mainm.do';
}