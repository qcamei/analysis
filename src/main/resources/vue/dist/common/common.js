/**************************************时间格式化处理************************************/
function dateFtt(fmt,date)   
{ //author: meizz   
  var o = {   
    "M+" : date.getMonth()+1,                 //月份   
    "d+" : date.getDate(),                    //日   
    "h+" : date.getHours(),                   //小时   
    "m+" : date.getMinutes(),                 //分   
    "s+" : date.getSeconds(),                 //秒   
    "q+" : Math.floor((date.getMonth()+3)/3), //季度   
    "S"  : date.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
} 

//创建时间格式化显示
function crtTimeFtt(value){
    var crtTime = new Date(value);
    return top.dateFtt("MM-dd hh:mm",crtTime);//直接调用公共JS里面的时间类处理的办法     
}

//创建时间格式化显示
function crtTimeFttMin(value){
    var crtTime = new Date(value);
    return top.dateFtt("hh:mm",crtTime);//直接调用公共JS里面的时间类处理的办法     
}

//创建时间比较
function getInervalTime(v1,v2){
    var d1 = new Date(v1);
    var d2 = new Date(v2);
    var crtTime = new Date(d1.getTime()-d2.getTime()-8*60*1000*60);
    return top.dateFtt("hh:mm:ss",crtTime);//直接调用公共JS里面的时间类处理的办法     
}

function getQueryString(name) { 
	
	var reg = new RegExp(name + '=([^&]*)(&|$)', 'i');  
    var r = reg.exec(window.location.href);  
    if (r != null) {  
        return unescape(r[1]);  
    }  
    return null;
} 