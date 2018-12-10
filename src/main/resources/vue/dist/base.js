(function(){
    requirejs.config({
        baseUrl: "/vue/dist",
        paths:{
            "promise":"js/q",
            "vue":"js/vue",
            "vue.router":"js/vue-router.min",
            "text":"js/text",
            "css":"js/css.min",
            "ELEMENT":"js/element-ui",
            "vue.i18n":"js/vue-i18n",
            "moment":"js/moment",
            "vue.editable":"js/vue-editable",
        },
        waitSeconds: 15,
        map:{

        },
        urlArgs: "version=" + Date.now(),

        shim: {
            promise:{
                exports:"Q"
            },
            "vue": {
                exports: "Vue"
            },
            "vue.router": {
                exports: "VueRouter"
            },
            "vue.i18n": {
                exports: "VueI18n"
            },
            "moment": {
                exports: "Moment"
            },
            "vue.editable": {
                exports: "VueEditable"
            },
        },
        callback:function(){

        },
        deps:["vue","vue.router","promise","ELEMENT","index","vue.i18n","moment","vue.editable"] // 默认要加载的js
    });
})();