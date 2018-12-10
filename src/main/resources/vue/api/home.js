define(["text!/vue/components/home.html","css!/vue/css/home.css"], function(template) {
    return {
        template:template,
        data() {
			return {
				sysName:'行为分析',
				isCollapse:true,
				username: 'admin',
				sysUserAvatar: '/static/images/0.jpg',
				defaultMenu:'project',
				activeName:"runningTasks",
				form: {
					name: '',
					region: '',
					date1: '',
					date2: '',
					delivery: false,
					type: [],
					resource: '',
					desc: ''
				},
				runningList:[],
				finishedList:[],
				rotate:'',
				jobnum:'',
				screen:'full',
				menus:[]
			}
		},
		mounted:function(){
			try{
				var reg =/#\/(\S*)\?/;
				if(this.$router.match(location).hash.indexOf("?")<0) {
					this.defaultMenu = (this.$router.match(location).hash+"?").match(reg)[1];
				} else {
					this.defaultMenu = (this.$router.match(location).hash).match(reg)[1];
				}
	        	if(this.defaultMenu.indexOf("content")>=0) {
	        		this.defaultMenu = "project";
	        	}
			} catch(err){
				console.log(err);
			}
        	
        	//任务管理器
        	if(sessionStorage.getItem("jobnum")) {
        		this.checkJQueue();
        	}
        	this.listMenu();
        },
		methods: {
			collapse() {
				this.isCollapse = !this.isCollapse;
			},
			selectMenu(index){
				this.defaultMenu = index;
			},
			help() {
				this.$notify({
		          title: '名词解释',
		          offset: 40,
		          dangerouslyUseHTMLString: true,
		          message: '<div id="helpCallOut"><br><b style="color: #b6ad90;">ALL PROJECTS</b><br>Select the project you wish to work on before continuing<br><br><b style="color: #b6ad90;">PROJECT DATA</b><br>This page provides a summary of the project data<br><br><b style="color: #b6ad90;">DISTANCE MATRIX</b><br>This page provides a summary of the project data by department. Here is no need to re-run any of the summaries or distance matrices here<br><br><b style="color: #b6ad90;">PRODUCTS</b><br>Here you can browse the product hierarchy for the current project. Select the search level from eth drop-down and type any characters into the box to search<br><br></div>',
		          duration: 0
		        });
			},
			workbench() {
				let target = this;
				this.workbenchData1 = [];
				this.finishedList = [];
				post('/getArcZincProess',{},function(res){
					target.runningList = [];
					target.finishedList = [];
					let list = res.data;
					for(let i = 0;i<list.length;i++) {
						var obj = list[i];
						obj.start_time = target.$moment(obj.start_time).format("MM-DD HH:mm");
						if(list[i].status=='RUNNING') {
							obj.arc_time = target.$moment(obj.arc_time).format("MM-DD HH:mm");
							obj.inerval_time = getInervalTime(obj.arc_time,obj.start_time);
							obj.inerval_time = target.$moment(list[i].arc_time).subtract(list[i].start_time).format("00:00:ss");
							target.runningList.push(obj);
						} else {
							obj.arc_time = target.$moment(obj.complete_time).format("MM-DD HH:mm");
							obj.inerval_time = target.$moment(obj.complete_time).subtract(list[i].start_time).format("00:00:ss");
							target.finishedList.push(obj);
						}
					}
				});
			},checkJQueue(){
				let target = this;
				post('/checkJQueue',{},res=>{
					 if(res.code==2000000&&res.data.jobnum>0) {
						 if(res.data.jobnum!=target.jobnum) {
							 target.workbench();
						 }
						 target.rotate = 'fa fa-spin';
						 target.jobnum = res.data.jobnum;
						 sessionStorage.setItem("jobnum",res.data.jobnum);
						 setTimeout(function(){
							 target.checkJQueue();
						 },5000);
					 } else {
						 target.workbench();
						 target.rotate = '';
						 sessionStorage.removeItem("jobnum");
						 target.jobnum = '';
						 if(target.$router.match(location).hash.indexOf("/project")>=0){
							 target.$refs.detail.list();
						 } else {
							 target.$refs.detail.$refs.theme.purchaseThemes();
							 target.$refs.detail.$refs.segment.list();
						 }
						 
					 }
				});
			},fullScreen: function() {
				
	            var e = document.documentElement
	              , a = e.requestFullScreen || e.webkitRequestFullScreen || e.mozRequestFullScreen || e.msRequestFullscreen;
	            "undefined" != typeof a && a && a.call(e)
	            this.screen='exit';
	        },exitScreen: function() {
	            document.documentElement;
	            let aa = document.exitFullscreen ? document.exitFullscreen() : document.mozCancelFullScreen ? document.mozCancelFullScreen() : document.webkitCancelFullScreen ? document.webkitCancelFullScreen() : document.msExitFullscreen && document.msExitFullscreen();
	            this.screen='full';
	        },listMenu() {
	        	let target = this;
	        	let res = {
      			  "code" : 2000000,
      			  "msg" : "OK",
      			  "data" : [ {
      			    "id" : 14,
      			    "parentId" : -1,
      			    "children" : [ ],
      			    "icon" : "fa fa-database fa-fw",
      			    "title" : "数据池",
      			    "href" : "data_pool",
      			    "spread" : false,
      			    "code" : "dataPool",
      			    "enabled" : "1",
      			    "description" : null
      			  }, {
      			    "id" : 15,
      			    "parentId" : -1,
      			    "children" : [ ],
      			    "icon" : "fa fa-line-chart fa-fw",
      			    "title" : "仪表盘",
      			    "href" : "project",
      			    "spread" : false,
      			    "code" : "project",
      			    "enabled" : "1",
      			    "description" : null
      			  } ]
      			};
  	        	target.menus = res.data;
        		/*post('/menu/a',{},res=>{
	        		target.menus = res.data;
	        	},error=>{
	        		
	        	});*/
	        }			
			
		}
    }

});



