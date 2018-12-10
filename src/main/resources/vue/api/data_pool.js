define(["text!/vue/components/data_pool.html"], function(template) {
    return {
        template:template,
        data() {
            return {
              tableData: [],
              pageNum:1,
              total:0,
              pageSize:10,
              dbname:"",
              newDatapoolVisible:false
            }
        },
        mounted:function(){
        	let params = {page:this.pageNum,limit:this.pageSize}
        	this.query(params);
        },
        methods: {
        	query: function(params) {
        		let target = this;
        		post('../datapool/list',params,res =>{
        			target.tableData = res.data.dataList;
        			target.total = res.data.total;
        		});
        	},
        	newDatapool(){
 			   this.newDatapoolVisible = true;
 		    },
        	datapoolListClick(item){
        		let params = {datasetKey:item.dataset_sys_key};
        		this.$router.push({path:'/project',query:params})
        		this.$emit('currentMenu','project');
        	},
        	changePage (key, num) {
    	      switch (key) {
    	        case 'pageSize':
    	          this.pageSize = num;
    	          break;
    	        case 'pageNum':
    	          this.pageNum = num;
    	          break;
    	        default:
    	          break;
    	      }
    	    },handleSizeChange(val) {
    	    	this.pageSize = val;
    	    	this.query();
    	    },handleCurrentChange(val) {
    	    	this.pageNum =val;
    	    	this.query();
    	    },search(){
    	    	let params = {page:this.pageNum,limit:this.pageSize,dbname:this.dbname}
    	    	this.query(params);
    	    }
        }
    }

});



