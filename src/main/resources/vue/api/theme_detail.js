define(["text!/vue/components/theme_detail.html"], function(template) {
    return {
        template:template,
        props:['layid'],
        data() {
	        return {
	            keyword:'',
	            currentRow:null,
	        	tableData:[],
	        	rotationData:[],
	        	numFactorData:[],
	        	themeData:[],
	        	savedSolutionsData:[],
	        	factorResultData:[],
	        	series:[],
	        	categories:[],
	        	chart1:null,
	        	chart2:null,
	        	factorName:'',
	        	activeName:'second',
	        	analysisName:'',
	        	minSpend:0,
	        	maxSpend:1000000,
	        	multipleSelection: [],
	        	purchaseThemesData:[],
	        	pageNum:1,
	            total:0,
	            pageSize:10,
	        	parentKey:0,
	        	currentForm:{},
	        	unsaveForm:{
	        		numFactor:3,
	        		analysisKey:0,
	        		rotation:'PARSIMAX',
	        		includeGroups:'',
	        		title:'',
	        		index:0
	        	},
	        	saveForm:{
	        		numFactor:3,
	        		analysisKey:0,
	        		rotation:'PARSIMAX',
	        		includeGroups:'',
	        		title:'',
	        		index:0
	        	},
	        	skuVisible:false,
	        	skuData:[],
	        	clusterKey:0,
	        	skuTitle:'',
	        	factorInd:'',
	        	segmentation:{//运算市场细分
	        		parentKey:0,
	        		numFactor:0,
	        		minCluster:'0',
	        		minDept:'0',
	        		minSeg:2,
	        		maxSeg:30,
	        		solutionKey:0,
	        		selectedFactors:'',
	        		solutionName:'',
	        		themeName:'',
	        		analysisKey:0
	        	},
	        	themeTheadNames:[],
	        	factorResultListData:[],
	        	chartTable:'chart',
	        	clusterDistributionData:[],
	        	deptDistributionData:[],
	        	clusterVisible:false,
	        	deptVisible:false,
	        	initVisible:false,
	        	distributionVisible:false,
	        	distributionData:[]
	        	
	        }
        },
        mounted:function(){
        	let target = this;
        	
        	if(this.$route.query) {
        		this.projectId = this.$route.query.projectId;
        		this.datasetKey = this.$route.query.datasetKey;
        		if(this.$route.query.layid==33) {
            		this.init();
            	}
        	}
        	
        	
        },
        methods:{
        	init(){
        		if(!this.initVisible) {
        			this.list();
                	this.purchaseThemes();
                	this.getSavedSolutions();
                	this.initVisible = true;
        		}
        		
        	},
        	list(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/theme/list',params,res=>{
        			target.tableData=res.data;
        		});
        	},handleSizeChange(val) {
    	    	this.pageSize = val;
    	    	this.getFactorResultList();
    	    },handleCurrentChange(val) {
    	    	this.pageNum =val;
    	    	this.getFactorResultList();
    	    },handleCurrentChange2(val) {
    	    	this.currentRow = val;
    	    },
        	changeFun(val){
        		this.multipleSelection = val;
        	},
        	changeFun2(val){
        		let selectedFactors = '';
        		for(let i =0;i<val.length;i++) {
        			selectedFactors+=val[i].factor_ind+",";
        		}
        		if(selectedFactors.length>0) {
            		selectedFactors=selectedFactors.substr(0,selectedFactors.length-1);
            	}
        		this.segmentation.selectedFactors = selectedFactors;
        	},
        	runFactorAnalysis(){
        		let target = this;
        		let keys='',codes='',groups='';
        		for(let i = 0;i<this.multipleSelection.length;i++) {
        			let obj = this.multipleSelection[i];
        			keys+=obj.dept_key+",";
                	codes+=obj.dept_code+",";
                	groups+=obj.solution_key+",";
        		}
        		if(keys.length>0){
            		keys = keys.substr(0,keys.length-1);
            	}
            	if(codes.length>0){
            		codes = codes.substr(0,codes.length-1);
            	}
            	if(groups.length>0){
            		groups = groups.substr(0,groups.length-1);
            	}
            	let param = "";
            	
            	if(keys!="") {
            		param = {
            			keys:keys,
            			codes:codes,
            			groups:groups
                	}
            		param = JSON.stringify(param);
            	}
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,analysisName:this.analysisName,minSpend:this.minSpend,maxSpend:this.maxSpend,"params":param};
        		post("/theme/runFactorAnalysis",params,res=>{
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
        	},
        	getNumFactor() {
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,analysisKey:this.unsaveForm.analysisKey};
        		post('/theme/getNumFactor',params,res=>{
        			//
        			target.numFactorData=res.data;
        			target.$refs.numFactorTable.setCurrentRow(target.numFactorData[0]);
        			target.unsaveForm.numFactor = target.numFactorData[0].num_factor;
        			target.$refs.rotationTable.setCurrentRow(target.rotationData[0]);
        			target.unsaveForm.rotation = target.rotationData[0].name;
        			target.unsaveForm.title = target.themeData[0].analysis_name+"-"+target.unsaveForm.numFactor+" 主题 "+target.unsaveForm.rotation+"（未保存）";
        			target.loadFactorResult(target.unsaveForm);
        		});
        	},
        	getNumFactorClick(item){
        		this.unsaveForm.numFactor = item.num_factor;
        		this.unsaveForm.title = this.themeData[0].analysis_name+"-"+this.unsaveForm.numFactor+" 主题 "+this.unsaveForm.rotation+"（未保存）";
        		this.loadFactorResult(this.unsaveForm);
        	},
        	getRotationClick(item){
        		this.unsaveForm.rotation = item.name;
        		this.unsaveForm.title = this.themeData[0].analysis_name+"-"+this.unsaveForm.numFactor+" 主题 "+this.unsaveForm.rotation+"（未保存）";
        		this.loadFactorResult(this.unsaveForm);
        	},
        	purchaseThemes(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/theme/purchaseThemes',params,res=>{
        			target.themeData=res.data;
        			if(target.themeData.length>0) {
        				target.$refs.themeTable.setCurrentRow(target.themeData[0]);
        				target.unsaveForm.analysisKey = target.themeData[0].analysis_key;
            			target.unsaveForm.includeGroups = target.themeData[0].include_groups;
            			target.getNumFactor();
            			target.rotationData = [{name:'PARSIMAX'},{name:'EQUAMAX'}];
        			} else {
        				//this.activeName="first";
        			}
        			
        		});
        	},
        	purchaseThemesClick(item, column, cell, event){
        		if(column.label==="主题分析") {
        			this.unsaveForm.analysisKey = item.analysis_key;
            		this.unsaveForm.includeGroups = item.include_groups;
            		this.unsaveForm.title = item.analysis_name+"-"+this.unsaveForm.numFactor+" 主题 "+this.unsaveForm.rotation+"（未保存）";
            		this.loadFactorResult(this.unsaveForm);
        		}
        		
        	},
        	deleteFactorAnalysis(index,item){
        		let target = this;
        		let analysisKey = item.analysis_key;
        		let analysisName = item.analysis_name;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,analysisKey:analysisKey};
        		this.$confirm('确定删除'+analysisName+'主题分析?', '信息', {
        			confirmButtonText: '删除',
        			cancelButtonText: '取消',
        			type: 'warning',
        			callback: action => {
		        	    post("/theme/deleteFactorAnalysis",params,res=>{
		        	       if(res.code==2000000) {
		        	    	   target.themeData.splice(index,1);
		        	    	   target.numFactorData = [];
		        	    	   target.rotationData = [];
		        	    	   target.leftChart([],[]);
		        	    	   target.chart1.update({"xAxis":{categories:[]}});
		        	    	   target.chart2.update({"series":[]});
		        	    	   target.chart2.update({"xAxis":{categories:[],labels:{ enabled:false }}});
			            	   target.chart2.update({"series":[]});
		            		   target.rightChart([],[]);
		        	    	   target.purchaseThemes();
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
        	},
        	getSavedSolutions(getSavedSolutions){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/theme/getSavedSolutions',params,res=>{
        			
        			target.savedSolutionsData=res.data;
        			
        		});
        	}, 
        	getSavedSolutionsClick(item) {
        		this.saveForm.analysisKey = item.fact_corr_key;
        		this.saveForm.includeGroups = item.include_groups;
        		this.saveForm.rotation = item.rotation;
        		this.saveForm.numFactor = item.num_factor;
        		this.saveForm.index = item.index;
        		this.saveForm.title = item.analysis_name +"（已保存）";
        		this.loadFactorResult(this.saveForm);
        	},
        	getSavedSolutionsClick2(item) {
        		this.segmentation.parentKey = item.fact_corr_key;
        		this.segmentation.numFactor = item.num_factor;
        		this.segmentation.solutionName = item.solution_name;
        		this.segmentation.themeName = item.theme_name;
        		this.segmentation.analysisKey = item.fact_corr_key;
        		this.segmentation.solutionKey = item.analysis_key;
        		this.segmentation.minSeg = item.min_num_seg;
        		this.segmentation.maxSeg = item.max_num_seg;
        		this.segmentation.selectedFactors = '';
        		this.purchaseThemes2();
        		this.getClusterDistribution();
        		this.getDeptDistribution();
        	},
        	getSavedSolutionsRow(item){
        		item.row.index = item.rowIndex;
        	},
        	deleteFactorSolution(index,item){
        		let target = this;
        		let analysisKey = item.fact_corr_key;
        		let analysisName = item.analysis_name;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,analysisKey:analysisKey};
        		this.$confirm('确定删除'+analysisName+'主题方案?', '信息', {
        			confirmButtonText: '删除',
        			cancelButtonText: '取消',
        			type: 'warning',
        			callback: action => {
		        	    post("/theme/deleteFactorSolution",params,res=>{
		        	       if(res.code==2000000) {
		        	    	   target.savedSolutionsData.splice(index,1);
		        	    	   //target.numFactorData = [];
		        	    	   //target.rotationData = [];
		        	    	   target.leftChart([],[]);
		        	    	   target.chart1.update({"xAxis":{categories:[]}});
		        	    	   target.chart2.update({"series":[]});
		        	    	   target.chart2.update({"xAxis":{categories:[],labels:{ enabled:false }}});
			            	   target.chart2.update({"series":[]});
		            		   target.rightChart([],[]);
		            		   target.saveForm.index = null;
		        	    	   target.getSavedSolutions();
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
        	},
        	loadFactorResult(object){
        		this.currentForm = object;
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,numFactor:object.numFactor,analysisKey:object.analysisKey,rotation:object.rotation,includeGroups:object.includeGroups};
        		this.getFactorResultList();
        		post('/theme/loadFactorResult',params,res=>{
        			if(res.code == 2000000){
        				target.series = [];
            			target.factorResultData=res.data;
            			target.categories = res.data.categories;
            			let list = res.data.series;
            			let ll = list.length>0?list[0]:[];
            			for(let i = 0;i<ll.length;i++) {
    						let x = [];
    						for(let j=0;j<list.length;j++) {
    							if(list[j][i]==0) { continue;}
    							if(list[j][i]) {
    								x.push(list[j][i])
    							} else {
    								x.push('')
    							}
    							
    						}
    						let item = {
    							data:x,
    							events:{
    								click: function(e) { 
    									var index = e.point.index;
    									target.factorInd = (index+1);
    									target.factorName = target.categories[index];
    					            	post('/theme/loadFactorResult2',{datasetKey:target.datasetKey,projectId:target.projectId,numFactor:object.numFactor,analysisKey: object.analysisKey,rotation: object.rotation,includeGroups: object.includeGroups,factorInd:index},res2=>{
    					            		target.chart2.update({"xAxis":{categories:res2.data.categories,labels:{ enabled:false }}});
    					            		target.chart2.update({"series":[{data:res2.data.series,events:{ click: function(e) {
    					            			target.clusterKey = res2.data.groupKeys[e.point.index];
    					            			target.skuTitle = res2.data.categories[e.point.index];
    					            			target.skuVisible = true;
    					            			target.skuList();
    					            		}}}]});
    					            	});
    					            } 
    							}
    						} 
    						target.series.push(item);
    					}
    					
            			target.leftChart(res.data.categories,target.series);
            			target.rightChart([],[]);
            			target.chart1.update({"title":{text:object.title}});
        			} else {
        				target.leftChart([],[]);
            			target.rightChart([],[]);
            			this.$message({
          	              type: 'error',
          	              message:res.msg
          	            });
        			}
        			
        		});
        	},
        	leftChart(categories,series){
        		this.chart1 = Highcharts.chart('container', {
        			chart: {type: 'column'},
        			title: {text: ''},
        			xAxis: {categories: categories},
        			yAxis: {
        				min: 0,
        				title: {text: 'values'}
        			},
        			legend:{
        				enabled:false
        			},
        			tooltip: {
        				pointFormat: '<span style="color:{series.color}"><b>{point.y}</b>({point.percentage:.0f}%)</span><br/>',
        				shared: true
        			},
        			plotOptions: {column: {stacking: 'normal'}},
        			credits: {enabled:false},
        			exporting: {enabled:false},
        			series: series
        		});
        	},
        	rightChart(categories,series){
        		this.chart2 = Highcharts.chart('container2', {
        			chart: {type: 'bar'},
        			title: {
        				text:null,
        			},
        			xAxis: {
        				categories: categories
        			},
        			plotOptions: {
        				series: {
        				      dataLabels: {
        				        align: 'left'
        				        ,enabled: true
        				        ,format: '{x}'
        				        ,inside: true
        				   }
        				}
        			},
        			yAxis: {
        				title: {text: ''}
        			},
        			exporting: {
        		        enabled:false
        			},
        			legend:{
        				enabled:false
        			},
        			tooltip:{
        				enabled:false
        			},
        			series: [{
        				data: [0,0,0,0,0],
        			}]
        		});
        	},saveFactorName(){
        		let target = this;
        		if(!this.currentForm) {
        			 this.$message({
       	               type: 'error',
       	               message:"请选择主题方案！"
        			 });  
        			 return ;
        		}
        		let params = { 
        			datasetKey: this.datasetKey, 
        			projectId: this.projectId,
        			numFactor:this.currentForm.numFactor,
        			analysisKey:this.currentForm.analysisKey,
        			name:this.factorName,
        			rotation:this.currentForm.rotation,
        			factorInd:this.factorInd,
        			includeGroups:this.currentForm.includeGroups
            	};
        		post("/theme/saveFactorName",params,res=>{
        		   if(res.code==2000000) {
        			   this.factorName = '';	
        	    	   target.getSavedSolutions();
        	    	   target.loadFactorResult(target.currentForm);
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
        	},purchaseThemes2(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,parentKey:this.segmentation.parentKey,numFactor:this.segmentation.numFactor};
        		post("/theme/purchaseThemes2",params,res=>{
            		target.purchaseThemesData = res.data;
            		this.$refs.purchaseThemesTable.toggleAllSelection();
            	});
        	},skuList(){
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,groupKey:this.clusterKey};
        		post('/family/solutionsList4',params,res=>{
        			target.skuData = res.data;
        		});
        		
        	},updateThemeTheadNames(length){
            	this.themeTheadNames = [];
    			//构造列表 列名
    			for(let i = 1;i<=length;i++) {
    				this.themeTheadNames.push('Theme '+i);
    			}
            },getFactorResultList(){
            	let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId,numFactor:this.currentForm.numFactor,analysisKey: this.currentForm.analysisKey,rotation: this.currentForm.rotation,includeGroups: this.currentForm.includeGroups,page:this.pageNum,limit:this.pageSize};
        		this.updateThemeTheadNames(this.currentForm.numFactor);
        		post("/theme/getFactorResult",params,res=>{
            		target.factorResultListData = res.data.dataList;
            		target.total = res.data.total;
            	});
            },runCustomerProfile(){//初始化
            	let target = this;
            	let params= {datasetKey:this.datasetKey,projectId:this.projectId,solutionKey:this.segmentation.solutionKey,selectedFactors: this.segmentation.selectedFactors,solutionName: this.segmentation.solutionName,themeName:this.segmentation.themeName,minDept:this.segmentation.minDept,minCluster:this.segmentation.minCluster,analysisKey:this.segmentation.analysisKey};
            	post("/theme/runCustomerProfile",params,res=>{
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
            },runSegmentationJob(){//计算市场细分
            	let target = this;
            	let params= {datasetKey:this.datasetKey,projectId:this.projectId,solutionKey:this.segmentation.solutionKey,minSeg:this.segmentation.minSeg,maxSeg:this.segmentation.maxSeg};
            	post("/theme/runSegmentationJob",params,res=>{
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
            },getClusterDistribution(){
            	let target = this;
            	let params= {datasetKey:this.datasetKey,projectId:this.projectId,analysisKey:this.segmentation.analysisKey};
            	post("/theme/getClusterDistribution",params,res=>{
            		target.clusterDistributionData = res.data;
            	});
            },getDeptDistribution(){
            	let target = this;
            	let params= {datasetKey:this.datasetKey,projectId:this.projectId,analysisKey:this.segmentation.analysisKey};
            	post("/theme/getDeptDistribution",params,res=>{
            		target.deptDistributionData = res.data;
            	});
            },distribution() {//查看消费者数量分布
        		this.distributionVisible = true;
        		let target = this;
    	    	let params = {datasetKey:this.datasetKey,projectId:this.projectId};
    	    	post("/theme/distributionList",params,res=>{
	        	    target.distributionData = res.data;
	            });
        	}
        	
        }
    }

});



