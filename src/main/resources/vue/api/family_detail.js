define(["text!/vue/components/family_detail.html"], function(template) {
    return {
        template:template,
        data() {
	        return {
	        	options: [],
	        	options2: [],
	            value: '',
	            value2: '',
	            minSale:0,
	            tableData: [],
	            parentKey:0,
	            leftData:[],
	            rightData:[],
	            leftData2:[],
	            rightData2:[],
	            g:2,
	            currentRow:null,
	            familyName:'',
	        	targetLab:'',
	        	solutionKey:0,
	        	solutionName:'',
	        	groupKey:0,
	        	groupName:'',
	        	top20Visible:false,
	        	top20Data:[],
	        	distributionVisible:false,
	        	distributionData:[],
	        	currentData:null,
	        	currentIndex:0,
	        	activeName:'second',
	        	skuVisible:false,
	        	skuData:[],
	        	clusterKey:0,
	        	skuTitle:'',
	        	initVisible:false
	        }
        },
        mounted:function(){
        	if(this.$route.query) {
        		this.projectId = this.$route.query.projectId;
        		this.datasetKey = this.$route.query.datasetKey;
        		if(this.$route.query.layid==22) {
            		this.init();
            	}
        	}
        	
        },
        methods:{
        	init(){
        		if(!this.initVisible) {
        			this.dataForm();
                	this.dataForm2();
                	this.initVisible = true;
        		}
        	},
        	changeSel(val){
        		this.parentKey = val;
        		this.solutionsList();
        		this.solutionsList2();
        		let target = this;
        		let obj = {};
                obj = this.options.find((item)=>{
                    return item.value === val;
                });
                this.targetLab = obj.label;
                this.familyName = obj.label+this.g;
        		
        	},
        	changeSel2(val){
        		
        		this.solutionKey = val;
        		this.solutionsList3();
        		let target = this;
        		let obj = {};
                obj = this.options2.find((item)=>{
                    return item.value === val;
                });
                this.solutionName = obj.label;
        		
        	},
        	dataForm(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/family/familys',params,res=>{
        			target.options = [];
        			for(let i = 0;i<res.data.length;i++) {
        				let obj = {};
        				obj.value=res.data[i].prod_sys_key;
        				obj.label=res.data[i].prod_desc;
        				target.options.push(obj);
        			}
        		});
        	},
        	dataForm2(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/family/digitalList',params,res=>{
        			target.options2 = [];
        			for(let i = 0;i<res.data.length;i++) {
        				let obj = {};
        				obj.value=res.data[i].solution_key;
        				obj.label=res.data[i].solution_name;
        				target.options2.push(obj);
        			}
        		});
        	},
        	leftCellClick(item){
        		this.g = item.g;
        		this.solutionsList2();
        		this.familyName = this.targetLab+this.g;
        	},
        	leftCellClick2(item){
        		this.groupKey = item.group_key;
        		this.groupName = item.group_name
        		this.currentData = item;
        		this.currentIndex = item.index;
        		this.solutionsList4();
        	},
        	rightCellClick(item){
        		this.clusterKey = item.cluster_key;
        		this.skuTitle = item.group_name;
        		this.skuVisible = true;
        		this.skuList();
        	},
        	leftTableRow2(item){
        		item.row.index = item.rowIndex;
        	},
        	handleCurrentChange(val){
        		this.currentRow = val;
        	},
        	solutionsList(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,parentKey:this.parentKey};
        		post('/family/solutionsList',params,res=>{
        			target.leftData=res.data;
        			target.$refs.leftTable.setCurrentRow(target.leftData[0]);
        		});
        	},
        	solutionsList2(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,parentKey:this.parentKey,g:this.g};
        		post('/family/solutionsList2',params,res=>{
        			target.rightData=res.data;
        		});
        	},
        	solutionsList3(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,solutionKey:this.solutionKey};
        		post('/family/solutionsList3',params,res=>{
        			target.leftData2=res.data;
        			target.$refs.leftTable2.setCurrentRow(target.leftData2[0]);
        			target.groupKey = target.leftData2[0].group_key;
        			target.groupName = target.leftData2[0].group_name;
        			target.solutionsList4();
        			target.currentData = target.leftData2[0];
        			target.currentIndex = 0;
        		});
        	},
        	solutionsList4(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,solutionKey:this.solutionKey,groupKey:this.groupKey};
        		post('/family/solutionsList4',params,res=>{
        			target.rightData2=res.data;
        		});
        	},calAll(){//运算全部Hier1的家族
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,minSale:this.minSale};
        		this.$confirm('确定计算全部Hier1的家族?', '信息', {
        			confirmButtonText: '计算',
        			cancelButtonText: '取消',
        			type: 'warning',
        			callback: action => {
		        	    post("/family/runClusterAll",params,res=>{
		        	       if(res.code==2000000) {
		        	    	   this.$emit('startJQueue');
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
        		
        	},cal(){//仅运算选择Hier1
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,deptKey:this.value,deptName:this.targetLab,minSale:this.minSale};
        		this.$confirm('确定计算该Hier1下的商品?', '信息', {
        			confirmButtonText: '计算',
        			cancelButtonText: '取消',
        			type: 'warning',
        			callback: action => {
		        	    post("/family/runCluster",params,res=>{
		        	    	if(res.code==2000000) {
		        	    	   this.$emit('startJQueue');
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
        	},top20(){//查看前20名商品
        		this.top20Visible = true;
        		let target = this;
    	    	let params = {datasetKey:this.datasetKey,projectId:this.projectId,parentKey:this.parentKey};
    	    	post("/family/top20",params,res=>{
	        	    target.top20Data = res.data;
	            });
        	},distribution() {//查看消费者数量分布
        		this.distributionVisible = true;
        		let target = this;
    	    	let params = {datasetKey:this.datasetKey,projectId:this.projectId,parentKey:this.parentKey};
    	    	post("/family/distributionList",params,res=>{
	        	    target.distributionData = res.data;
	            });
        	},saveClusterSolution(){//保存命名
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,deptKey:this.parentKey,numCluster:this.g,name:this.familyName};
        		post("/family/saveClusterSolution",params,res=>{
        			if(res.code==2000000) {
        			   target.$emit('themeList');
        			   target.dataForm2();	
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
        	},updateSolutionName(){//重命名商品组合
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,solutionKey:this.solutionKey,name:this.solutionName};
            	post("/family/updateSolutionName",params,res=>{
            		if(res.code==2000000) {
            		   target.dataForm2();	
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
        	},deleteSolution(){//删除商品组合
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,solutionKey:this.solutionKey};
        		this.$confirm('确定删除该商品组合'+this.solutionName+'?', '信息', {
        			confirmButtonText: '确定',
        			cancelButtonText: '取消',
        			type: 'warning',
        			callback: action => {
		        	    post("/family/deleteSolution",params,res=>{
		        	    	target.solutionName = '';
		        	    	target.value2 = '';
		        	    	target.dataForm2();
		        	    	target.leftData2=[];
		        	    	target.rightData2=[];
		        	    	if(res.code==2000000) {
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
        	},updateGroupName(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,groupKey:this.groupKey,name:this.groupName};
            	post("/family/updateGroupName",params,res=>{
            		if(res.code==2000000) {
            		   target.currentData.group_name = this.groupName;	
            		   target.leftData2[currentIndex] = target.currentData;
            		   target.$emit('themeList');
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
        	},skuList(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,groupKey:this.clusterKey};
        		post('/family/solutionsList4',params,res=>{
        			target.skuData = res.data;
        		});
        		
        	}
        }
    }

});



