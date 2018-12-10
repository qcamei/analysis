define(["text!/vue/components/group.html"], function(template) {
    return {
        template:template,
        data() {
            return {
            	datasetKey:0,
            	groupTypeData: [],
                keyword:"",
                editUserVisible:false,
                addUserVisible:false,
                form:{
                	
                }
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
	    		post('/groupType/getGroupTypeList',params,res =>{
	    			target.groupTypeData = res.data;
	    			
	    			target.activeName = res.data[1].code;
	    		});
           },editUserInfo() {
        	   
           },addUserInfo() {
        	   
           },handleEdit(index,item){
	    	   this.editUserVisible = true;
	    	   this.form = item;
	       },handleAdd(index,item){
	    	   this.addUserVisible = true;
	       },handleClick() {
	    	   return false;
	       }
           
		   
        }
       
    }

});



