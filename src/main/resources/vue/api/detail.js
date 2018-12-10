define(["text!/vue/components/detail.html"], function(template) {
    return {
        template:template,
        data() {
	        return {
	        	tabs:11,
	        	projectId:0,
	        	datasetKey:0,
	        	projectName:'',
	        	period:'',
	        	channel:'全部',
	        	activeName:'first',
	        }
        },
        mounted:function(){
        	if(this.$route.query) {
        		this.tabs = this.$route.query.layid;
        		this.projectId = this.$route.query.projectId;
        		this.datasetKey = this.$route.query.datasetKey;
        	}
        	this.changeTabs(this.tabs);
        	this.dataForm();
        },
        methods:{
        	changeTabs(tabs) {
        		this.tabs = tabs;
        		if(this.tabs==11) {
        			this.activeName='first';
        			this.$refs.ybp.init();
        		} else if(this.tabs==22){
        			this.activeName='second';
        			this.$refs.family.init();
        		} else if(this.tabs==33){
        			this.activeName='third';
        			this.$refs.theme.init();
        		} else if(this.tabs==44){
        			this.activeName='fourth';
        			this.$refs.segment.init();
        		}
        	},
        	dataForm() {
        		let target = this;
        		let params = {datasetKey:this.datasetKey,projectId:this.projectId};
        		post('/project/detail',params,res=>{
        			target.projectName = res.data["Project"];
        			target.period = res.data["Period"];
        		});
        	},checkJQueue(){
        		this.$emit('startJQueue');
        	},themeList(){
        		this.$refs.theme.list();
        	}
        	
        }
    }

});



