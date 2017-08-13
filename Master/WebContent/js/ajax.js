//http://stackoverflow.com/questions/8567114/how-to-make-an-ajax-call-without-jquery
function ajax(url, method, data, async)
{
    method = typeof method !== 'undefined' ? method : 'GET';
    async = typeof async !== 'undefined' ? async : false;
    var xhReq 
    if (window.XMLHttpRequest)
    {
        xhReq = new XMLHttpRequest();
    }
    else
    {
        xhReq = new ActiveXObject("Microsoft.XMLHTTP");
    }


    if (method == 'POST')
    {
        xhReq.open(method, url, async);
        xhReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhReq.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhReq.send(data);
    }
    else
    {
        if(typeof data !== 'undefined' && data !== null)
        {
            url = url+'?'+data;
        }
        xhReq.open(method, url, async);
        xhReq.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhReq.send(null);
    }
}