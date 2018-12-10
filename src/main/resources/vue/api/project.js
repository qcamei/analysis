define(["text!/vue/components/project.html"], function(template) {
    return {
        template:template,
        data() {
            return {
            	datasetKey:0,
            	tableData: [],
            	dataPoolData:[],
                pageNum:1,
                total:0,
                pageSize:9,
                projectName:"",
                newProjectVisible:false,
                startDate:'',
                endDate:'',
                form:{
                	datasetKey:'',
                	projectName:"",
                	description:"",
                	customer:'ALL',
                	period:'',
                	datetime:'',
                	goods:'L1',
                	type:'#ALL',
                	pickerOptions:{}
                }
            }
        },
        beforeMount:function(){
        	const target = this;
        	this.form.pickerOptions=  {
        		shortcuts: [{
                text: '最近一季',
                onClick(picker) {
                  let start = new Date(target.startDate);
                  let end = new Date(target.endDate);
                  let temp = new Date(target.endDate);
                  temp.setTime(temp.getTime() - 3600 * 1000 * 24 * 7*13);
                  if(temp.getTime()>start) {
                      start = temp;
                  }
                  picker.$emit('pick', [start, end]);
                  target.form.period = '#13 weeks';
                }
              }, {
                text: '最近半年',
                onClick(picker) {
                  let start = new Date(target.startDate);
                  let end = new Date(target.endDate);
                  let temp = new Date(target.endDate);
                  temp.setTime(temp.getTime() - 3600 * 1000 * 24 * 7*26);
                  if(temp.getTime()>start) {
                  	  start = temp;
                  }
                  picker.$emit('pick', [start, end]);
                  target.form.period = '#26 weeks';
                }
              }, {
                text: '最近一年',
                onClick(picker) {
                  let start = new Date(target.startDate);
                  let end = new Date(target.endDate);
                  let temp = new Date(target.endDate);
                  temp.setTime(temp.getTime() - 3600 * 1000 * 24 * 7*52);
                  if(temp.getTime()>start) {
                	  start = temp;
                  }
                  picker.$emit('pick', [start, end]);
                  target.form.period = '#52 weeks';
                }
              }],
              disabledDate(time){
        		 time = target.$moment(time.getTime()).format("YYYY-MM-DD");
              	 return time < target.startDate||time>target.endDate;
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
     	   detail(item,layid) {
     		  this.$router.push({
         		 path:'/content',
                 query: {
                	 layid: layid,
                	 projectId:item.projectId,
                	 datasetKey:item.dataset_sys_key
                 }
              });
           },list() {
	       		const target = this;
	       		const params = {page:this.pageNum,limit:this.pageSize,projectName:this.projectName};
	       		if(this.datasetKey!==null) {
	       			params.datasetKey = this.datasetKey;
	       		}
	    		post('../project/list',params,res =>{
	    			target.tableData = res.data.dataList;
	    			target.total = res.data.total;
	    		});
           },handleSizeChange(val) {
	   	    	this.pageSize = val;
		    	this.list();
		   },handleCurrentChange(val) {
		    	this.pageNum =val;
		    	this.list();
		   },search(){
			   this.pageNum = 1;
			   this.pageSize = 9;
   	    	   this.list();
		   },newProject(){
			   this.getDataPool();
			   this.newProjectVisible = true;
		   },saveProject(){
			   if(!(this.form.period&&this.form.period.indexOf("#")>-1)) {
				   const startDate = this.$moment(this.form.datetime[0]).format("DD MMM YYYY");
	               const endDate = this.$moment(this.form.datetime[1]).format("DD MMM YYYY");
	               this.form.period = startDate+"-"+endDate
			   }
			   this.form.period = "#52 weeks";
			   console.log(this.form.period);
               const params = {projName:this.form.projectName,projDesc:this.form.description,period:this.form.period,type:this.form.type,customer:this.form.customer,goodsLevel:this.form.goods,datasetKey:this.form.datasetKey};
               post('/project/save',params,res=>{
            	   if(res.code==2000000) {
            		   this.$emit('startJQueue');
            		   this.newProjectVisible = false;
            		   this.$message({
           	              type: 'success',
           	              message:"正在新建沙盘，请稍等...!"
           	            });
            	   } else {
            		   this.$message({
           	              type: 'error',
           	              message:res.msg
           	            });
            	   }
            	   
               });
		   },getDataPool() {
			   const params = {};
			   const target = this;
			   post('/datapool/list',params,res =>{
	    			target.dataPoolData = res.data.dataList;
	    			target.form.datasetKey = res.data.dataList[0].dataset_sys_key;
	    			
	    			let date = target.dataPoolData[0].tran_period.split("-");
	    			target.form.datetime = date;
	    			target.startDate = target.$moment(date[0]).format("YYYY-MM-DD");
	    			target.endDate = target.$moment(date[1]).format("YYYY-MM-DD");
	    		});
		   },getDatasetKey(index,row){
			   this.form.datasetKey = row.dataset_sys_key;
			   let date = this.dataPoolData[index].tran_period.split("-");
			   this.form.datetime = date;
			   this.startDate = this.$moment(date[0]).format("YYYY-MM-DD");
			   this.endDate = this.$moment(date[1]).format("YYYY-MM-DD");
		   }
		   
        }
       
    }

});



