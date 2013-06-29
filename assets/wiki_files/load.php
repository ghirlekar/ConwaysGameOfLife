mw.loader.implement("jquery.client",function(){(function($){var profileCache={};$.client={profile:function(nav){if(nav===undefined){nav=window.navigator;}if(profileCache[nav.userAgent]===undefined){var versionNumber,uk='unknown',x='x',wildUserAgents=['Opera','Navigator','Minefield','KHTML','Chrome','PLAYSTATION 3','Iceweasel'],userAgentTranslations=[[/(Firefox|MSIE|KHTML,?\slike\sGecko|Konqueror)/,''],['Chrome Safari','Chrome'],['KHTML','Konqueror'],['Minefield','Firefox'],['Navigator','Netscape'],['PLAYSTATION 3','PS3']],versionPrefixes=['camino','chrome','firefox','iceweasel','netscape','netscape6','opera','version','konqueror','lynx','msie','safari','ps3','android'],versionSuffix='(\\/|\\;?\\s|)([a-z0-9\\.\\+]*?)(\\;|dev|rel|\\)|\\s|$)',names=['camino','chrome','firefox','iceweasel','netscape','konqueror','lynx','msie','opera','safari','ipod','iphone','blackberry','ps3','rekonq','android'],nameTranslations=[],layouts=['gecko','konqueror','msie','opera','webkit'],layoutTranslations=[
['konqueror','khtml'],['msie','trident'],['opera','presto']],layoutVersions=['applewebkit','gecko'],platforms=['win','mac','linux','sunos','solaris','iphone'],platformTranslations=[['sunos','solaris']],translate=function(source,translations){var i;for(i=0;i<translations.length;i++){source=source.replace(translations[i][0],translations[i][1]);}return source;},ua=nav.userAgent,match,name=uk,layout=uk,layoutversion=uk,platform=uk,version=x;if(match=new RegExp('('+wildUserAgents.join('|')+')').exec(ua)){ua=translate(ua,userAgentTranslations);}ua=ua.toLowerCase();if(match=new RegExp('('+names.join('|')+')').exec(ua)){name=translate(match[1],nameTranslations);}if(match=new RegExp('('+layouts.join('|')+')').exec(ua)){layout=translate(match[1],layoutTranslations);}if(match=new RegExp('('+layoutVersions.join('|')+')\\\/(\\d+)').exec(ua)){layoutversion=parseInt(match[2],10);}if(match=new RegExp('('+platforms.join('|')+')').exec(nav.platform.toLowerCase())){platform=translate(match[1],
platformTranslations);}if(match=new RegExp('('+versionPrefixes.join('|')+')'+versionSuffix).exec(ua)){version=match[3];}if(name.match(/safari/)&&version>400){version='2.0';}if(name==='opera'&&version>=9.8){match=ua.match(/version\/([0-9\.]*)/i);if(match&&match[1]){version=match[1];}else{version='10';}}versionNumber=parseFloat(version,10)||0.0;profileCache[nav.userAgent]={name:name,layout:layout,layoutVersion:layoutversion,platform:platform,version:version,versionBase:(version!==x?Math.floor(versionNumber).toString():x),versionNumber:versionNumber};}return profileCache[nav.userAgent];},test:function(map,profile,exactMatchOnly){var conditions,dir,i,op,val;profile=$.isPlainObject(profile)?profile:$.client.profile();if(map.ltr&&map.rtl){dir=$('body').is('.rtl')?'rtl':'ltr';map=map[dir];}if(typeof map!=='object'||map[profile.name]===undefined){return!exactMatchOnly;}conditions=map[profile.name];if(conditions===false){return false;}if(conditions===null){return true;}for(i=0;i<conditions.
length;i++){op=conditions[i][0];val=conditions[i][1];if(typeof val==='string'){if(!(eval('profile.version'+op+'"'+val+'"'))){return false;}}else if(typeof val==='number'){if(!(eval('profile.versionNumber'+op+val))){return false;}}}return true;}};}(jQuery));;},{},{});mw.loader.implement("jquery.cookie",function(){(function($){$.cookie=function(key,value,options){if(arguments.length>1&&(!/Object/.test(Object.prototype.toString.call(value))||value===null||value===undefined)){options=$.extend({},options);if(value===null||value===undefined){options.expires=-1;}if(typeof options.expires==='number'){var days=options.expires,t=options.expires=new Date();t.setDate(t.getDate()+days);}value=String(value);return(document.cookie=[encodeURIComponent(key),'=',options.raw?value:encodeURIComponent(value),options.expires?'; expires='+options.expires.toUTCString():'',options.path?'; path='+options.path:'',options.domain?'; domain='+options.domain:'',options.secure?'; secure':''].join(''));}options=value
||{};var decode=options.raw?function(s){return s;}:decodeURIComponent;var pairs=document.cookie.split('; ');for(var i=0,pair;pair=pairs[i]&&pairs[i].split('=');i++){if(decode(pair[0])===key)return decode(pair[1]||'');}return null;};})(jQuery);;},{},{});mw.loader.implement("jquery.mwExtension",function(){(function($){$.extend({trimLeft:function(str){return str===null?'':str.toString().replace(/^\s+/,'');},trimRight:function(str){return str===null?'':str.toString().replace(/\s+$/,'');},ucFirst:function(str){return str.charAt(0).toUpperCase()+str.substr(1);},escapeRE:function(str){return str.replace(/([\\{}()|.?*+\-\^$\[\]])/g,'\\$1');},isDomElement:function(el){return!!el&&!!el.nodeType;},isEmpty:function(v){var key;if(v===''||v===0||v==='0'||v===null||v===false||v===undefined){return true;}if(v.length===0){return true;}if(typeof v==='object'){for(key in v){return false;}return true;}return false;},compareArray:function(arrThis,arrAgainst){if(arrThis.length!==arrAgainst.length){return false
;}for(var i=0;i<arrThis.length;i++){if($.isArray(arrThis[i])){if(!$.compareArray(arrThis[i],arrAgainst[i])){return false;}}else if(arrThis[i]!==arrAgainst[i]){return false;}}return true;},compareObject:function(objectA,objectB){var prop,type;if(typeof objectA===typeof objectB){if(typeof objectA==='object'){if(objectA===objectB){return true;}else{for(prop in objectA){if(prop in objectB){type=typeof objectA[prop];if(type===typeof objectB[prop]){switch(type){case'object':if(!$.compareObject(objectA[prop],objectB[prop])){return false;}break;case'function':if(objectA[prop].toString()!==objectB[prop].toString()){return false;}break;default:if(objectA[prop]!==objectB[prop]){return false;}break;}}else{return false;}}else{return false;}}for(prop in objectB){if(!(prop in objectA)){return false;}}}}}else{return false;}return true;}});}(jQuery));;},{},{});mw.loader.implement("mediawiki.notify",function(){(function(mw){'use strict';mw.notify=function(message,options){mw.loader.using(
'mediawiki.notification',function(){mw.notify=mw.notification.notify;mw.notify(message,options);});};}(mediaWiki));;},{},{});mw.loader.implement("mediawiki.util",function(){(function(mw,$){'use strict';var util={init:function(){var profile,$tocTitle,$tocToggleLink,hideTocCookie;profile=$.client.profile();if(profile.name==='opera'){util.tooltipAccessKeyPrefix='shift-esc-';}else if(profile.name==='chrome'){util.tooltipAccessKeyPrefix=(profile.platform==='mac'?'ctrl-option-':'alt-shift-');}else if(profile.platform!=='win'&&profile.name==='safari'&&profile.layoutVersion>526){util.tooltipAccessKeyPrefix='ctrl-alt-';}else if(profile.platform==='mac'&&profile.name==='firefox'&&profile.versionNumber>=14){util.tooltipAccessKeyPrefix='ctrl-option-';}else if(!(profile.platform==='win'&&profile.name==='safari')&&(profile.name==='safari'||profile.platform==='mac'||profile.name==='konqueror')){util.tooltipAccessKeyPrefix='ctrl-';}else if(profile.name==='firefox'&&profile.versionBase>'1'){util.
tooltipAccessKeyPrefix='alt-shift-';}util.$content=(function(){var i,l,$content,selectors;selectors=['.mw-body-primary','.mw-body','#bodyContent','#mw_contentholder','#article','#content','#mw-content-text','body'];for(i=0,l=selectors.length;i<l;i++){$content=$(selectors[i]).first();if($content.length){return $content;}}return util.$content;})();mw.hook('wikipage.content').fire(util.$content);$tocTitle=$('#toctitle');$tocToggleLink=$('#togglelink');if($('#toc').length&&$tocTitle.length&&!$tocToggleLink.length){hideTocCookie=$.cookie('mw_hidetoc');$tocToggleLink=$('<a href="#" class="internal" id="togglelink"></a>').text(mw.msg('hidetoc')).click(function(e){e.preventDefault();util.toggleToc($(this));});$tocTitle.append($tocToggleLink.wrap('<span class="toctoggle"></span>').parent().prepend('&nbsp;[').append(']&nbsp;'));if(hideTocCookie==='1'){util.toggleToc($tocToggleLink);}}},rawurlencode:function(str){str=String(str);return encodeURIComponent(str).replace(/!/g,'%21').replace(/'/g,
'%27').replace(/\(/g,'%28').replace(/\)/g,'%29').replace(/\*/g,'%2A').replace(/~/g,'%7E');},wikiUrlencode:function(str){return util.rawurlencode(str).replace(/%20/g,'_').replace(/%3A/g,':').replace(/%2F/g,'/');},wikiGetlink:function(str){return mw.config.get('wgArticlePath').replace('$1',util.wikiUrlencode(typeof str==='string'?str:mw.config.get('wgPageName')));},wikiScript:function(str){str=str||'index';if(str==='index'){return mw.config.get('wgScript');}else if(str==='load'){return mw.config.get('wgLoadScript');}else{return mw.config.get('wgScriptPath')+'/'+str+mw.config.get('wgScriptExtension');}},addCSS:function(text){var s=mw.loader.addStyleTag(text);return s.sheet||s;},toggleToc:function($toggleLink,callback){var $tocList=$('#toc ul:first');if($tocList.length){if($tocList.is(':hidden')){$tocList.slideDown('fast',callback);$toggleLink.text(mw.msg('hidetoc'));$('#toc').removeClass('tochidden');$.cookie('mw_hidetoc',null,{expires:30,path:'/'});return true;}else{$tocList.slideUp(
'fast',callback);$toggleLink.text(mw.msg('showtoc'));$('#toc').addClass('tochidden');$.cookie('mw_hidetoc','1',{expires:30,path:'/'});return false;}}else{return null;}},getParamValue:function(param,url){if(url===undefined){url=document.location.href;}var re=new RegExp('^[^#]*[&?]'+$.escapeRE(param)+'=([^&#]*)'),m=re.exec(url);if(m){return decodeURIComponent(m[1].replace(/\+/g,'%20'));}return null;},tooltipAccessKeyPrefix:'alt-',tooltipAccessKeyRegexp:/\[(ctrl-)?(option-)?(alt-)?(shift-)?(esc-)?(.)\]$/,updateTooltipAccessKeys:function($nodes){if(!$nodes){$nodes=$('#column-one a, #mw-head a, #mw-panel a, #p-logo a, input, label');}else if(!($nodes instanceof $)){$nodes=$($nodes);}$nodes.attr('title',function(i,val){if(val&&util.tooltipAccessKeyRegexp.test(val)){return val.replace(util.tooltipAccessKeyRegexp,'['+util.tooltipAccessKeyPrefix+'$6]');}return val;});},$content:null,addPortletLink:function(portlet,href,text,id,tooltip,accesskey,nextnode){var $item,$link,$portlet,$ul;if(
arguments.length<3){return null;}$link=$('<a>').attr('href',href).text(text);if(tooltip){$link.attr('title',tooltip);}$portlet=$('#'+portlet);if($portlet.length===0){return null;}$ul=$portlet.find('ul').eq(0);if($ul.length===0){$ul=$('<ul>');if($portlet.find('div:first').length===0){$portlet.append($ul);}else{$portlet.find('div').eq(-1).append($ul);}}if($ul.length===0){return null;}$portlet.removeClass('emptyPortlet');if($portlet.hasClass('vectorTabs')){$item=$link.wrap('<li><span></span></li>').parent().parent();}else{$item=$link.wrap('<li></li>').parent();}if(id){$item.attr('id',id);}if(tooltip){tooltip=$.trim(tooltip.replace(util.tooltipAccessKeyRegexp,''));if(accesskey){tooltip+=' ['+accesskey+']';}$link.attr('title',tooltip);if(accesskey){util.updateTooltipAccessKeys($link);}}if(accesskey){$link.attr('accesskey',accesskey);}if(nextnode&&nextnode.parentNode===$ul[0]){$(nextnode).before($item);}else if(typeof nextnode==='string'&&$ul.find(nextnode).length!==0){$ul.find(nextnode).eq(
0).before($item);}else{$ul.append($item);}return $item[0];},jsMessage:function(message){if(!arguments.length||message===''||message===null){return true;}if(typeof message!=='object'){message=$.parseHTML(message);}mw.notify(message,{autoHide:true,tag:'legacy'});return true;},validateEmail:function(mailtxt){var rfc5322Atext,rfc1034LdhStr,html5EmailRegexp;if(mailtxt===''){return null;}rfc5322Atext='a-z0-9!#$%&\'*+\\-/=?^_`{|}~';rfc1034LdhStr='a-z0-9\\-';html5EmailRegexp=new RegExp('^'+'['+rfc5322Atext+'\\.]+'+'@'+'['+rfc1034LdhStr+']+'+'(?:\\.['+rfc1034LdhStr+']+)*'+'$','i');return(null!==mailtxt.match(html5EmailRegexp));},isIPv4Address:function(address,allowBlock){if(typeof address!=='string'){return false;}var block=allowBlock?'(?:\\/(?:3[0-2]|[12]?\\d))?':'',RE_IP_BYTE='(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[0-9]?[0-9])',RE_IP_ADD='(?:'+RE_IP_BYTE+'\\.){3}'+RE_IP_BYTE;return address.search(new RegExp('^'+RE_IP_ADD+block+'$'))!==-1;},isIPv6Address:function(address,allowBlock){if(typeof
address!=='string'){return false;}var block=allowBlock?'(?:\\/(?:12[0-8]|1[01][0-9]|[1-9]?\\d))?':'',RE_IPV6_ADD='(?:'+':(?::|(?::'+'[0-9A-Fa-f]{1,4}'+'){1,7})'+'|'+'[0-9A-Fa-f]{1,4}'+'(?::'+'[0-9A-Fa-f]{1,4}'+'){0,6}::'+'|'+'[0-9A-Fa-f]{1,4}'+'(?::'+'[0-9A-Fa-f]{1,4}'+'){7}'+')';if(address.search(new RegExp('^'+RE_IPV6_ADD+block+'$'))!==-1){return true;}RE_IPV6_ADD='[0-9A-Fa-f]{1,4}'+'(?:::?'+'[0-9A-Fa-f]{1,4}'+'){1,6}';return address.search(new RegExp('^'+RE_IPV6_ADD+block+'$'))!==-1&&address.search(/::/)!==-1&&address.search(/::.*::/)===-1;}};mw.util=util;}(mediaWiki,jQuery));;},{},{"showtoc":"show","hidetoc":"hide"});mw.loader.implement("mediawiki.page.startup",function(){(function(mw,$){mw.page={};$('html').addClass('client-js').removeClass('client-nojs');$(mw.util.init);}(mediaWiki,jQuery));;},{},{});
/* cache key: enwiki:resourceloader:filter:minify-js:7:d509d59fa3dee47d685addd0faa2c494 */