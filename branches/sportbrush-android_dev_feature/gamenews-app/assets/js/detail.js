        var UI = {
            init:function(){
                var articleObj;
                if(appclient.isAndroidApp()){
                    articleObj = JSON.parse(client.getArticleDetail());

                }else{
                    NativeBridge.call('getArticleDetail',{},function(args){
                        articleObj = JSON.parse(args);
                    })
                }
                try{
                    $('#main_title').html(articleObj.title);
                    $('#sub_title').html(articleObj.refer+"&nbsp;&nbsp;&nbsp;"+articleObj.timeStamp);
                    $('#d_content').html(articleObj.content);
                    $('#source').attr("href",articleObj.sourceUrl);
					if(articleObj.extraInfo){
						$('.j-adv').html(articleObj.extraInfo).show();
					}else{
						$('.j-adv').hide();
					}
					if(!articleObj.sourceUrl){					
						$('.js-sourcebtn-ctn').remove();
					}
                }catch(e){

                }
            },
            loadImage:function(){
                try{
                    var bigPicListArr;
                    var defaulticon;
                    var isWifi;
                    if(appclient.isAndroidApp()){
                        bigPicListArr = eval(client.getPicInfo());
                        defaulticon = client.getLoadingPicInfo();
                    }else{
                        NativeBridge.call('getLoadingPicInfo',{},function(args){
                            var imageObj = JSON.parse(args);
                            defaulticon = imageObj.image;
                        })

                        NativeBridge.call('getPicInfo',{},function(args){
                            bigPicListArr = eval(args);
                        })
                    }
                    for(var m = 0;bigPicListArr&&m<bigPicListArr.length;m++){
                        var item = bigPicListArr[m];
                        if(item.src&&item.src!=''){
							if(item.width<100){
								 $('img[data-id="'+item.id+'"]').attr('src',item.src).attr('width',item.width).attr('height',item.height).addClass('loaded');
							}else{
								$('img[data-id="'+item.id+'"]').attr('src',item.src).attr('width','100%').addClass('loaded');
							}                           
                        }else{
                            $('img[data-id="'+item.id+'"]').attr('src',defaulticon).attr('width','100%');
                        }
                    }
                }catch (e){
                    appclient.log("loadImage error :"+JSON.stringify(e));
                }
            },
            initImageWH:function(item){
                try{
                    if(item.type=='video'){
                        $('video[data-id="'+id+'"]').attr('poster',src);
                    }else if(item.type=='image'){
//                        $('img[data-id="'+item.id+'"]').attr('width',"100%");
                        $('img[data-id="'+item.id+'"]').attr('width','100%');
                    }
                }catch (e){
                    appclient.log("initImageWH error :"+JSON.stringify(e));
                }
            }
        };

        window.appclient = {
            log:function(str){
                if(appclient.isAndroidApp()){
                    client.toast(str);
                }else{
                    alert(str);
                }
            },
            isAndroidApp:function(){
                if(navigator.userAgent.indexOf('android_jjww_')==0){
                    return true;
                }
                return false;
            },
            isIosApp:function(){
                if(navigator.userAgent.indexOf('ios_jjww_')==0){
                    return true;
                }
                return false;
            }
        }

        var webview = {
            scrollBottom:function(){
                try{
                    var body = document.getElementsByTagName('body')[0];
                    body.scrollTop = body.scrollHeight;
                }catch (e){
                    appclient.log("scrollBottom error :"+JSON.stringify(e));
                }
            },
			 /***
			 * @param type
			 * @param id
			 * @param src
			 */
			fillImageById:function(type,id,src){
                 try{
                     if(type=='video'){
                         $('video[data-id="'+id+'"]').attr('poster',src);
                     }else if(type=='image'){
                        $('img[data-id="'+id+'"]').attr('src',src);				//.attr('width',"100%")		
                     }
                     var body = document.getElementsByTagName('body')[0];
                     if(appclient.isAndroidApp()){
                         
                     }else{
                         setTimeout(function() {
                                    NativeBridge.call('bodyHeight',{id:body.scrollHeight},function(args){
                                                      })
                                    }, 100 );
                     }
                 }catch (e){
                 }
			},
			reloadFail:function(type,id,src){
				try{
					if(type=='image'){
						$('img[data-id="'+id+'"]').removeClass('loaded');
						if(src){
							$('img[data-id="'+id+'"]').attr('src',src).attr('width',"100%");;
						}
					}					
				}catch(e){
				}				
			},
            nativeScrollBottom:function(){
                var body = document.getElementsByTagName('body')[0];
                if(appclient.isAndroidApp()){
                    client.scrollTo(body.scrollHeight);
                }else{
                    NativeBridge.call('bodyHeight',{id:body.scrollHeight},function(args){
                                      })
                }
            }
        };

        //ios 通信
        window.NativeBridge = {
            callbacksCount : 1,
            callbacks : {},

            // Automatically called by native layer when a result is available
            resultForCallback : function resultForCallback(callbackId, resultArray) {
                try {
                    var callback = NativeBridge.callbacks[callbackId];
                    if (!callback) return;

                    callback.apply(null,resultArray);
                } catch(e) {alert(e)}
            },

            // Use this in javascript to request native objective-c code
            // functionName : string (I think the name is explicit :p)
            // args : array of arguments
            // callback : function with n-arguments that is going to be called when the native code returned
            call : function call(functionName, args, callback) {

                var hasCallback = callback && typeof callback == "function";
                var callbackId = hasCallback ? NativeBridge.callbacksCount++ : 0;

                if (hasCallback)
                    NativeBridge.callbacks[callbackId] = callback;

                var iframe = document.createElement("IFRAME");
                iframe.setAttribute("src", "js-frame:" + functionName + ":" + callbackId+ ":" + encodeURIComponent(JSON.stringify(args)));
                // For some reason we need to set a non-empty size for the iOS6 simulator...
                iframe.setAttribute("height", "1px");
                iframe.setAttribute("width", "1px");
                document.documentElement.appendChild(iframe);
                iframe.parentNode.removeChild(iframe);
                iframe = null;
            }
        };		
        //操IOS
        window.addEventListener("load",function () {
            UI.init();
            UI.loadImage();

            if(appclient.isAndroidApp()){
                client.onPageFinished()
            }else{
                NativeBridge.call('onPageFinished',{},function(args){

                })
            }
            try{
                window.delayLoad.init();
            }catch (e){
                appclient.log("delayLoad error :"+JSON.stringify(e));
            }

            $("img").on('click',function(){
                var id = $(this).data("id");
				var refer = $(this).data("refer");
				if(refer&&refer!=''){
					window.location.href = refer;
					return;
				}
                if(id&&id!=''){
                    if(appclient.isAndroidApp()){
                        client.onClickImage(parseInt(id));
                    }else{
                        NativeBridge.call('onClickImage',{id:parseInt(id)},function(args){

                        })
                    }
                }
            });			
        },false);
