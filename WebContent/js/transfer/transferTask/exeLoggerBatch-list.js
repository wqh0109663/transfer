(function(page){
	var height=467;
	page.loadExeLoggerBatchList=function(){
		$.laydate.render({elem: '#exeLoggerBatchStartTime'});
		$.laydate.render({elem: '#exeLoggerBatchEndTime'});
		$.table.render({
		    elem: '#exeLoggerBatch-list'
		    ,method:"post"
		    ,height: height
		    ,url: window.PATH+"/transfer/exeLoggerBatchPageListData" //数据接口
		    ,where:{
		    	loggerId		:	$("#exeLoggerBatchLoggerId").val()||"",
		    	exeBatch	:	$("#exeLoggerBatchExeBatch").val()||"",
		    	startTime	:	$("#exeLoggerBatchStartTime").val(),
		    	endTime		:	$("#exeLoggerBatchEndTime").val()
		    }
		    ,page: true //开启分页
		    ,cols: [[ //表头
		    	{field: 'exeBatch', title: '执行次', width: "8%"}
				,{field: 'startTime', title: '开始时间', width: "12%"}
				,{field: 'endTime', title: '结束时间', width: "12%"}
				,{field: 'searchData', title: '数据', width: "20%"}
				,{field: 'exeSql', title: '执行sql', width: "20%"}
				,{field: 'result', title: '执行结果', width: "7%",templet:initResult}
				,{field: 'msg', title: '错误原因'}
				,{title: '操作', fixed: 'right', width: "6%", align:'center', toolbar: '#exeLoggerBatchToolBar'}
		    ]]
		});
		$.table.on('tool(exeLoggerBatch-list)', function(obj){ //注：tool是工具条事件名，test是$.table原始容器的属性 lay-filter="对应的值"
		    var data = obj.data //获得当前行数据
		    ,layEvent = obj.event; //获得 lay-event 对应的值
		    if(layEvent === 'logger-batch-more'){
		    	page.loggerBatchMore(data);
		    }
		});
	};
	function initResult(data){
		if(data.result==1){
			return '<span style="color:green;cursor:pointer;">成功</span>';
		}else{
			return '<span style="color:red;cursor:pointer;">失败</span>';
		}
	}
	page.loadExeLoggerBatchList();
	page.loggerBatchMore=function(data){
		$.layer.open({
			title		:	'执行数量明细',
			area		:	["900px","440px"],
			content		:	'<div id="logger-batch-more-body"></div>',
			shadeClose	:	false,
			tipsMore	:	true,
			btn			:	["关闭"],
			yes			:	function(index){
				$.layer.close(index);
				return false;
			},
			cancel		:	function(index){$.layer.close(index);},
		});
		$("#logger-batch-more-body").load(window.PATH+"/transfer/loggerBatchExeCount",data);
	}
})($.dynamicJs);