define(["text!/vue/components/menu.html"], function(template) {
    return {
        template:template,
        data() {
            return {
            	datasetKey:0,
            	tableData: [],
                projectName:"",
                index:0,
                temp:{},
                menuId:0,
                editMenuVisible:false,
                form:{},
                elementData:[]
            }
        },
        mounted:function(){
        	
        	if(this.$route.query) {
        		this.tabs = this.$route.query.layid;
        		this.datasetKey = this.$route.query.datasetKey;
        	}
        	this.list();
        },
        methods: {
     	   list() {
	       		const target = this;
	       		const params = {page:this.pageNum,limit:this.pageSize,projectName:this.projectName};
	       		if(this.datasetKey!==null) {
	       			params.datasetKey = this.datasetKey;
	       		}
	    		post('/menu/listMenu',params,res =>{
	    			target.tableData = [];
	    			for(let i = 0;i<res.data.length;i++) {
	    				let obj = res.data[i];
	    				if(!res.data[i].children) {
	    					res.data[i].children = [];
	    				}
	    				obj.expand = false;
	    				obj.isleaf = true;
	    				obj.enabled = obj.enabled==="1"?true:false;
						target.tableData.push(obj);
	    				for(let j=0;j<res.data[i].children.length;j++) {
	    					if(j==0) {
	    						obj.expand = true;
	    						obj.isleaf = false;
	    					}
	    					let child = res.data[i].children[j];
	    					child.enabled = child.enabled==="1"?true:false;
	    					child.isleaf = true;
	    					target.tableData.push(child);
	    					delete child.children;
	    				}
	    				delete obj.children;
	    			}
	    		});
           },cellStyle(item) {
        	   if(item.column.label==='hidden') {
        		   return "display:none";
        	   }
           },indexMethod(index){
        	   if(index==0) {
        		   index = this.index+index;
        	   }
        	   this.index = index;
	           return index;
	       },handleCheck(row) {
	    	   this.temp[row.id] = !row.expand;
	    	   row.expand = !row.expand;
	       },rowStyle(item) {
	    	   let target = this;
	    	   let str = '';
	    	   Object.keys(target.temp).forEach(function(key){
	    		   if(item.row.parentId === (key|0)&&!target.temp[key]) {
	    			   str =  'display:none;'
	    		   }
	    	   });
	    	   return str;
	       },updateMenuInfo(){
	    	   if(this.menuId===0) {
	    		   this.$message({
      	                type: 'error',
      	                message:"请选择一个菜单！"
      	           });
	    	   }
	       },handleEdit(index,item){
	    	   this.editMenuVisible = true;
	    	   this.form = item;
	    	   this.selectAuthorityElementByMenuId();
	       },selectAuthorityElementByMenuId() {
	    	   let target = this;
	    	   post('/element/selectAuthorityElementByMenuId',{menuId:this.form.id},res=>{
	    		   target.elementData = res.data;
//	    		   console.log(res);
	    	   });
	       }
		   
        }
       
    }

});



