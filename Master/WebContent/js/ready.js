/**
 * ready.js
 *
 * Author: Torben Brodt <[email]t.brodt@gmail.com[/email]>
 * Summary: Cross-browser wrapper for DOMContentLoaded
 * Updated: 07/09/2009
 * License: MIT / GPL
 * Version: 1.1
 *
 * URL:
 * [url]http://www.easy-coding.de[/url]
 * [url]http://jquery.com/dev/svn/trunk/jquery/MIT-LICENSE.txt[/url]
 * [url]http://jquery.com/dev/svn/trunk/jquery/GPL-LICENSE.txt[/url]
 *
 * Full Description:
 * A page has loaded after all external resources like images have been loaded.
 * Should all scripts wait for that? a better bevaviour is to wait for the dom content being ready.
 *
 * This script has workarounds for all the big browsers meaning the major versions of firefox, internet explorer, opera, safari and chrome.
 * You can use it without risk, since the normal "onload" behavior is the fallback solution.
 *
 * Most of the source is lended from jquery
 */
var ready=new function(){function f(){a||(a=1,b.addEventListener?(e="DOMContentLoaded",b.addEventListener(e,function(){b.removeEventListener(e,arguments.callee,!1),ready.ready()},!1)):b.attachEvent&&(e="onreadystatechange",b.attachEvent(e,function(){"complete"===b.readyState&&(b.detachEvent(e,arguments.callee),ready.ready())}),b.documentElement.doScroll&&c==c.top&&function(){if(!d.isReady){try{b.documentElement.doScroll("left")}catch(a){return void setTimeout(arguments.callee,0)}ready.ready()}}()),c.onload=ready.ready)}var e,a=0,b=document,c=window,d=this;d.isReady=0,d.readyList=[],d.ready=function(){if(!d.isReady){if(d.isReady=1,d.readyList){for(var a=0;a<d.readyList.length;a++)d.readyList[a].call(c,d);d.readyList=null}b.loaded=!0}},d.push=function(a){return f(),d.isReady?a.call(c,d):d.readyList.push(a),d}};