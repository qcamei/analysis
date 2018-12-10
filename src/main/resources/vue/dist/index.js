define(["vue","vue.router","ELEMENT","common/routes","vue.i18n","moment","vue.editable"], function(Vue,VueRouter,element,routes,VueI18n,Moment,VueEditable) {
    Vue.use(VueRouter);
    Vue.use(element);
    Vue.use(VueI18n);
    Vue.use(VueEditable);
    Vue.prototype.$moment = Moment;
    var router = new VueRouter({
        routes:routes
    });
   
    var app = new Vue({
        router:router,
        el:"#app",
    });


    // do the clearance
    document.body.setAttribute("class","clear");

});


