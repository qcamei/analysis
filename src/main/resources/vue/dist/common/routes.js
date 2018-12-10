define(["common/ResolveComponent"], function(ResolveComponent) {
	
	
	
	let routes = [
	    {
	        path: '/',
	        component: ResolveComponent("../../api/home"),
	        name: '',
	        children:[{
		        path: '/user',
		        component: ResolveComponent("../../api/user"),
		        name: '用户管理',
		    },{
		        path: '/menu',
		        component: ResolveComponent("../../api/menu"),
		        name: '菜单管理',
		    },{
		        path: '/group_content',
		        component: ResolveComponent("../../api/group"),
		        name: '角色组管理',
		        children:[
		        	{
			        	path:'/group',
			        	components:{
			        		default:ResolveComponent("../../api/group_detail"),
			        	}
			        }
		        ]
		    },{
		        path: '/groupType',
		        component: ResolveComponent("../../api/group_type"),
		        name: '角色类型管理'
		        
		    },{
		        path: '/data_pool',
		        component: ResolveComponent("../../api/data_pool"),
		        name: '数据池',
		    },{
		        path: '/project',
		        component: ResolveComponent("../../api/project"),
		        name: '仪表盘'
		    },{
	        	path: '/detail',
		        component: ResolveComponent("../../api/detail"),
		        name: '详情',
		        children:[
		            {
			        	path:'/content',
			        	components:{
			        		default:ResolveComponent("../../api/ybp_detail"),
			        		family_detail:ResolveComponent("../../api/family_detail"),
			        		theme_detail:ResolveComponent("../../api/theme_detail"),
			        		segment_detail:ResolveComponent("../../api/segment_detail"),
			        	}
			        }
		        ]
	        },
	        ]
	    },
	    {
	        path: '/404',
	        component: {},
	        name: '',
	        hidden: true
	    },
	    {
	        path: '*',
	        hidden: true,
	        redirect: { path: '/404' }
	    }
	];



    return routes;

});
