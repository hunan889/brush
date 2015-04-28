var delayLoad = {
    init:function(obj){
        var self = this;
        this.lazy = typeof obj === 'string' ? document.getElementById(obj) : document.getElementsByTagName('body')[0];
        this.aImg = this.lazy.getElementsByTagName('img');
        this.load();
        try{
            $(window).on('scroll',function(){
                self.load();
            });
            $(window).on('resize',function(){
                self.load();
            });
        }catch (e){

        }

    },
    /**
     * 执行按需加载图片，并将已加载的图片标记为已加载
     * @return 无
     */
    load: function () {
        var self = this;
        var aNotLoaded = self.loaded(0);
        if (self.loaded(1).length != self.aImg.length) {
            var notLoadedLen = aNotLoaded.length;
            for (var i = 0; i < notLoadedLen; i++) {
                var A = ($(window).scrollTop()>($(aNotLoaded[i]).offset().top+$(aNotLoaded[i]).height()));
                var B = (($(window).scrollTop()+$(window).height())<$(aNotLoaded[i]).offset().top);
                //$(window).scrollTop()>($(aNotLoaded[i]).offset().top+$(aNotLoaded[i]).height())||
                if(A||B){
                }else{
                    var id = $(aNotLoaded[i]).attr('data-id');
                    if(!$(aNotLoaded[i]).hasClass('loaded')){
                        if(id){
                            if(appclient.isAndroidApp()){
                                client.loadImage(parseInt(id));
                            }else{
                                NativeBridge.call('loadImage',{'id':id},function(args){

                                })
                            }
                        }
                        $(aNotLoaded[i]).addClass('loaded');
                    }
                }
            }
        }
    },

    /**
     * 已加载或者未加载的图片数组
     * @param {Number} status 图片是否已加载的状态，0代表未加载，1代表已加载
     * @return Array 返回已加载或者未加载的图片数组
     */
    loaded: function (status) {
        var array = [];
        for (var i = 0; i < this.aImg.length; i++) {
            var hasClass = $(this.aImg[i]).hasClass('loaded');
            if (!status) {
                if (!hasClass)
                    array.push(this.aImg[i])
            }
            if (status) {
                if (hasClass)
                    array.push(this.aImg[i])
            }
        }
        return array;
    }
};