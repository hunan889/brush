
window.appclient = {
    log: function(str) {
        if (appclient.isAndroidApp()) {
            client.toast(str);
        } else {
            alert(str);
        }
    },
    isAndroidApp: function() {
        if (navigator.userAgent.indexOf('android_jjww_') == 0) {
            return true;
        }
        return false;
    },
    isIosApp: function() {
        if (navigator.userAgent.indexOf('ios_jjww_') == 0) {
            return true;
        }
        return false;
    }
}


//ios 通信
window.NativeBridge = {
    callbacksCount: 1,
    callbacks: {},

    // Automatically called by native layer when a result is available
    resultForCallback: function resultForCallback(callbackId, resultArray) {
        try {
            var callback = NativeBridge.callbacks[callbackId];
            if (!callback) return;

            callback.apply(null, resultArray);
        } catch (e) {
            alert(e)
        }
    },

    // Use this in javascript to request native objective-c code
    // functionName : string (I think the name is explicit :p)
    // args : array of arguments
    // callback : function with n-arguments that is going to be called when the native code returned
    call: function call(functionName, args, callback) {

        var hasCallback = callback && typeof callback == "function";
        var callbackId = hasCallback ? NativeBridge.callbacksCount++ : 0;

        if (hasCallback)
            NativeBridge.callbacks[callbackId] = callback;

        var iframe = document.createElement("IFRAME");
        iframe.setAttribute("src", "js-frame:" + functionName + ":" + callbackId + ":" + encodeURIComponent(JSON.stringify(args)));
        // For some reason we need to set a non-empty size for the iOS6 simulator...
        iframe.setAttribute("height", "1px");
        iframe.setAttribute("width", "1px");
        document.documentElement.appendChild(iframe);
        iframe.parentNode.removeChild(iframe);
        iframe = null;
    }
};
//操IOS
window.addEventListener("load", function() {

    $('.j-btn-share').on('click', function() {
        var canvas = $('#myCanvas')[0],
            orgImg = new Image(),
            ctx = canvas.getContext('2d'),
            content;

        orgImg.crossOrigin = "*";
        orgImg.onload = function() {
            
            ctx.drawImage(orgImg, 50, 50);
            //删除字符串前的提示信息 "data:image/png;base64,"  
            content = canvas.toDataURL().substring(22);


            var opt = {
                  title: "这是一个分享的title",
                  url: 'https://huanjubao.yy.com',
                  content: content
              };

            if(appclient.isAndroidApp()){
                client.onShareImg(JSON.stringify(opt));
            }else{
                NativeBridge.call('onShareImg', {id:opt},function(args){
                                  
                })
            }
        }
        orgImg.src = document.getElementById('orgsrc').src;
    });


}, false);