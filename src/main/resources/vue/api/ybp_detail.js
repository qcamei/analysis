define(["text!/vue/components/ybp_detail.html"], function(template) {
    return {
        template:template,
        data() {
	        return {
	        	projectId:0,
	        	datasetKey:0,
	        	tableData: [],
	        	pageNum:1,
	            total:0,
	            pageSize:5,
	            showGoodsVisible:false,
	            sort:'s.total_cust desc',
	        	project: {
	        		"customers": '',
					"dbname": '',
					"description": '',
					"period": '',
					"product_matrix_customers": '',
					"product_matrix_sales": '',
					"projectName": '',
					"sales": '',
					"summarisedDate": '',
					"transactions":'',
					"type":'',
					"channel":'全部'
				},
				goods:{
					parentCode:'',
					parentDesc:'',
					prodCode:'',
					prodDesc:'',
					showGoodsData:[],
					pageNum:1,
		            total:0,
		            pageSize:10
				},
				initVisible:false
	        }
        },
        mounted:function(){
        	if(this.$route.query) {
        		this.projectId = this.$route.query.projectId;
        		this.datasetKey = this.$route.query.datasetKey;
        		if(this.$route.query.layid==11) {
            		this.init();
            	}
        	}
        	
        	
        },
        methods:{
        	init(){
        		if(!this.initVisible) {
        			this.dataForm();
                	this.list();
                	this.initVisible = true;
        		}
        	},
        	dataForm() {
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/project/detail',params,res=>{
        			target.project.dbname = res.data["Db Name"];
        			target.project.projectName = res.data["Project"];
        			target.project.type = res.data["Type"];
        			target.project.customers = res.data["Customers"];
        			target.project.description = res.data["Description"];
        			target.project.sales = res.data["Sales"];
        			target.project.transactions = res.data["Transactions"].replace(/(^\s*)|(\s*$)/g, "");
        			target.project.period = res.data["Period"];
        		});
        	},
        	list(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,page:this.pageNum,limit:this.pageSize};
        		post('/project/deptDataGrid',params,res=>{
        			console.log(res.data);
        			target.tableData = res.data.dataList;
        			target.total = res.data.total;
        		});
        	},handleSizeChange(val) {
    	    	this.pageSize = val;
    	    	this.list();
    	    },handleCurrentChange(val) {
    	    	this.pageNum =val;
    	    	this.list();
    	    },deleteProject(){
    	    	let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		this.$confirm('确定删除该沙盘?', '信息', {
        			confirmButtonText: '删除',
        			cancelButtonText: '取消',
        			type: 'warning',
        			callback: action => {
		        	    post("/project/delete",params,res=>{
		        	    	if(res.code==2000000) {
		                	   this.$router.push({path:'/project'})
		             		   this.$message({
	            	              type: 'success',
	            	              message:res.data
	            	            });
		             	    } else {
		             		   this.$message({
	            	              type: 'error',
	            	              message:res.msg
	            	            });
		             	    }
		            	});
	    	         }
    	         });
    	    },showGoods(){
    	    	let target = this;
    	    	this.showGoodsVisible = true;
    	    	let params = {datasetKey:this.datasetKey,projectId:this.projectId,parentCode:this.goods.parentCode,parentDesc:this.goods.parentDesc,prodCode:this.goods.prodCode,prodDesc:this.goods.prodDesc,page:this.goods.pageNum,limit:this.goods.pageSize,sort:this.sort};
    	    	post("/project/getGoodsList",params,res=>{
	        	    target.goods.showGoodsData = res.data.dataList;
	        	    target.goods.total = res.data.total;
	            });
    	    },handleSizeChange2(val) {
    	    	this.goods.pageSize = val;
    	    	this.showGoods();
    	    },handleCurrentChange2(val) {
    	    	this.goods.pageNum =val;
    	    	this.showGoods();
    	    },sortChange(item ) {
    	    	this.sort = " s."+item.prop+" "+(item.order.replace("ending",""));
    	    	this.showGoods();
    	    }
        }
    }

});



