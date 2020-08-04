(function(page){
	var height=467;
	page.loadExeLoggerList=function(){
		$.laydate.render({elem: '#exeLoggerStartTime'});
		$.laydate.render({elem: '#exeLoggerEndTime'});
		$.table.render({
		    elem: '#exeLogger-list'
		    ,method:"post"
		    ,height: height
		    ,url: window.PATH+"/transfer/exeLoggerPageListData" //数据接口
		    ,where:{
		    	taskId		:	$("#exeLoggerTaskId").val()||"",
		    	exeBatch	:	$("#exeLoggerExeBatch").val()||"",
		    	startTime	:	$("#exeLoggerStartTime").val(),
		    	endTime		:	$("#exeLoggerEndTime").val()
		    }
		    ,page: true //开启分页
		    ,cols: [[ //表头
		    	{field: 'exeBatch', title: '执行次', width: "8%"}
				,{field: 'startTime', title: '开始时间', width: "15%"}
				,{field: 'endTime', title: '结束时间', width: "15%"}
				,{field: 'exeResult', title: '执行结果', width: "7%",templet:initExeResult}
				,{field: 'exeMsg', title: '错误原因'}
				,{title: '操作', fixed: 'right', width: "15%", align:'center', toolbar: '#exeLoggerToolBar'}
		    ]]
		});
		$.table.on('tool(exeLogger-list)', function(obj){ //注：tool是工具条事件名，test是$.table原始容器的属性 lay-filter="对应的值"
		    var data = obj.data //获得当前行数据
		    ,layEvent = obj.event; //获得 lay-event 对应的值
		    if(layEvent === 'logger-view'){
		    	page.loggerBatchView(data);
		    }else if(layEvent === 'logger-exe-count'){
		    	page.loggerExeCount(data);
		    }
		});
	};
	function initExeResult(data){
		if(data.exeResult==1){
			return '<span style="color:green;cursor:pointer;">成功</span>';
		}else{
			return '<span style="color:red;cursor:pointer;">失败</span>';
		}
	}
	page.loadExeLoggerList();
	page.loggerBatchView=function(data){
		$.layer.open({
			type		:	1,
			title		:	'执行历史<span style="color:red;">【批次：'+data.exeBatch+'】</span>',
			area		:	["90%","590px"],
			content		:	'<div id="logger-batch-body"></div>',
			cancel		:	function(index){$.layer.close(index);}
		});
		$("#logger-batch-body").load(window.PATH+"/transfer/exeLoggerBatchPageList?loggerId="+data.id);
	};
	page.loggerExeCount=function(data){
		$.layer.open({
			title		:	'执行数量明细',
			area		:	["900px","440px"],
			content		:	'<div id="exe-count-body"></div>',
			shadeClose	:	false,
			tipsMore	:	true,
			btn			:	["关闭"],
			yes			:	function(index){
				$.layer.close(index);
				return false;
			},
			cancel		:	function(index){$.layer.close(index);},
		});
		$("#exe-count-body").load(window.PATH+"/transfer/loggerExeCount",data);
	}
})($.dynamicJs);