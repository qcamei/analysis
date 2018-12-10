define(["text!/vue/components/group_detail.html"], function(template) {
    return {
        template:template,
        props:['id'],
        data() {
            return {
            	form:{},
            	addForm:{},
            	filterText: '',
            	treeData: [],
            	menuTreeData:[],
            	elementListData:[],
            	disabled: true,
            	closable:false,
            	checkedKeys:[],
            	menuId:0,
            	multipleSelection:[],
            	leaders: [],
            	members: [],
                groupId:0,
                addGroupVisible:false,
                memberVisible:false,
                memberValue:'',
                leaderVisible:false,
                leaderValue:'',
                leaderId:0,
                memberId:0,
                updateGroupVisible:false,
                updateForm:{},
                restaurants:[],
                isTop:false,
                multipleSelection:[],
                elementList:[]

            }
        },
        mounted:function(){
        	if(this.$route.query) {
        		this.tabs = this.$route.query.layid;
        		this.datasetKey = this.$route.query.datasetKey;
        	}
        	this.list();
        	this.getUserList();
        },
        methods: {
     	   list() {
     		  let target = this;
     		  post('/group/tree',{groupType:this.id},res =>{
     			 target.treeData = res.data;
     			 setTimeout(function(){
     				 if(target.treeData[0]) {
     					target.$refs.tree.setCurrentKey(target.treeData[0].id);
     					target.getAuthorityMenu();
     					target.getMenuDetail(target.treeData[0]);
     				 }
    			 },500);
     			 target.disabled = true;
     			 
     		  });
           },getMenuDetail(node) {
        	   this.groupId = node.id;
			   this.getGroupUsers();
        	   this.form = node;
        	   this.addForm = {
        		  parentCode:node.code,
        		  parentLabel:node.label,
        		  code:'',
        		  label:'',
        		  description:'',
        		  parentId:node.id
        	   };
        	   this.updateForm = {
         		  code:node.code,
         		  label:node.label,
         		  description:node.description,
         		  id:node.id
         	   };
        	   this.getAuthorityMenu();
        	   this.disabled = true;
           },getAuthorityMenu() {
        	   let target = this;
        	   post('/group/getAuthorityMenu',{id:this.groupId},res =>{
        		   target.menuTreeData = res.data;
        	   });
           },selectable(){
        	   return !this.disabled;
           },authority(){//权限分配
        	   this.disabled = false;
           },handleSelectionChange(val) {
               this.multipleSelection = val;
           },switchAuthority(){
        	   for(let i = 0;i<this.menuTreeData.length;i++) {
    			   if(this.menuTreeData[i].resourceId) {
    				   this.checkedKeys.push(this.menuTreeData[i].resourceId);
    			   }
    			   this.menuTreeData[i].disabled = this.disabled;
    			   for(let j = 0;j<this.menuTreeData[i].children.length;j++) {
    				   if(this.menuTreeData[i].children[j].resourceId) {
    					   this.checkedKeys.push(this.menuTreeData[i].children[j].resourceId);
        			   }
    				   this.menuTreeData[i].children[j].disabled = this.disabled;
    			   }
    		   }
           },getGroupUsers(){//获取群组关联用户列表
        	   let target = this;
        	   post('/group/getGroupUsers',{groupId:this.groupId},res=>{
        		   target.leaders = res.data.leaders;
        		   target.members = res.data.members;
        	   });
           },groupUsers(){//关联用户
        	   this.closable = true;
           },saveGroupInfo(){//新增角色
        	   let target = this;
        	   let parentId = this.addForm.parentId;
        	   if(this.isTop) {
        		   parentId = -1;
        	   }
        	   post('/group/saveGroupInfo',{code:this.addForm.code,name:this.addForm.label,parentId:parentId,groupType:this.id},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		  target.list();
             		  target.addGroupVisible = false
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },updateGroupInfo(){//修改角色
        	   let target = this;
        	   post('/group/updateGroupInfo',{name:this.updateForm.label,id:this.updateForm.id,description:this.updateForm.description},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		    target.list();
             		    target.updateGroupVisible = false
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },deleteGroupInfo(){//修改角色
        	   let target = this;
        	   post('/group/deleteGroupInfo',{id:this.groupId},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		    target.list();
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },querySearchAsync(queryString, cb) {
               var restaurants = this.restaurants;
               var results = queryString ? restaurants.filter(this.createStateFilter(queryString)) : restaurants;
               clearTimeout(this.timeout);
               this.timeout = setTimeout(() => {
            	  cb(results);
               }, 1000 * Math.random());
           },createStateFilter(queryString) {
               return (state) => {
                 return (state.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0||state.username.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
               };
           },handleSelect(item) {
        	   this.leaderId = item.id;
           },handleSelect2(item) {
        	   this.memberId = item.id;
           },getUserList() {
        	   let target = this;
        	   post('/user/getUserList',{},res=>{
        		   target.restaurants = res.data;
        	   });
           },saveGroupLeader(){
        	   let target = this;
        	   post('/group/saveGroupLeader',{groupId:this.groupId,userId:this.leaderId},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		    target.leaderVisible=false;
             		    target.leaderId = 0;
             		    target.getGroupUsers();
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },saveGroupMember(){
        	   let target = this;
        	   post('/group/saveGroupMember',{groupId:this.groupId,userId:this.memberId},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		   	target.memberVisible=false;
             		   target.memberId = 0;
             		    target.getGroupUsers();
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },deleteGroupLeader(tag) {
        	   let target = this;
        	   post('/group/deleteGroupLeader',{groupId:this.groupId,userId:tag.id},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		    target.getGroupUsers();
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },deleteGroupMember(tag) {
        	   let target = this;
        	   post('/group/deleteGroupMember',{groupId:this.groupId,userId:tag.id},res=>{
        		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data
        	            });
             		    target.getGroupUsers();
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
        	   });
           },changeFun(val){
        	   this.multipleSelection = val;
	       },allotResourceAuthority(sign,item,data){
	    	   let params = {};
	    	   params.id = item.resource_id;
	    	   params.authorityId = this.groupId;
	    	   params.authorityType = 'group';
	    	   params.resourceId = item.id;
	    	   params.resourceType = item.type;
	    	   params.sign = sign?1:0;
	    	   params.parentId = data.id;
	    	   if(params.sign==1) {
	    		   delete params["id"];
	    	   }
	    	   post('/resourceAuthority/allotResourceAuthority',params,res=>{
	    		   if(res.code==2000000) {
             		   this.$message({
        	              type: 'success',
        	              message:res.data.msg
        	            });
             		  item.resource_id = res.data.id;
             	   } else {
             		   this.$message({
        	              type: 'error',
        	              message:res.msg
        	           });
             	   }
	    	   });
	       }
		   
        }
       
    }

});



