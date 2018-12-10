define(["text!/vue/components/group_type.html"], function(template) {
    return {
        template:template,
        data() {
            return {
            	datasetKey:0,
            	tableData: [],
                pageNum:1,
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
	    			target.tableData = res.data;
	    		});
           },editUserInfo() {
        	   
           },addUserInfo() {
        	   
           },handleEdit(index,item){
	    	   this.editUserVisible = true;
	    	   this.form = item;
	       },handleAdd(index,item){
	    	   this.addUserVisible = true;
	       }
           
		   
        }
       
    }

});



