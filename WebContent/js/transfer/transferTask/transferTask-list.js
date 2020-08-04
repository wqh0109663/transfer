initPageList(true);
(function(page){
	var pageList,searchHeight=$("#search").height();
	page.loadList=function(type, deleteCount){
		var jump=$.refreshCurrPage(pageList, type, deleteCount);
		if(jump===true){
			return false;
		}
		pageList=$.table.render({
		    elem: '#transferTask-list'
		    ,method:"post"
		    ,height: "#page-list-"+(searchHeight+25)
		    ,url: window.PATH+"/transfer/transferTaskPageListData" //数据接口
		    ,where:{
		    	coding	:	$("#searchCoding").val()||"",
		    	name	:	$("#searchName").val()||""
		    }
		    ,page: true //开启分页
		    ,cols: [[ //表头
		    	{field: 'id', type:'checkbox', fixed: 'left', hide:"$status==2"}
		    	,{title: '编号', width: "10%",templet:"#startToolBar"}
				,{field: 'name', title: '名称', width: "10%"}
				,{field: 'exePeriod', title: '执行频率', width: "7%",templet:initExePeriod}
				,{field: 'sourceTable', title: '来源', width: "13%",templet:initSourceTable}
				,{field: 'targetTable', title: '目标', width: "13%",templet:initTargetTable}
				,{field: 'exeStatus', title: '执行进度',templet:initExeStatus, width: "20%"}
				,{field: 'exeResult', title: '最近一次执行结果',templet:initExeResult, width: "10%"}
				,{fixed: 'right', title: '顺序', width: 65, toolbar:"#moveToolBar"}
				,{fixed: 'right', title: '发布', width: 85, templet:"#issueToolBar"}
		    	,{fixed: 'right', title: '操作', width: 200, align:'center', toolbar:"#toolBar"}
		    ]]
		    ,done	:	function(page){
		    	curr=page.page;
		    	limit=page.limit;
		    	var data=page.data;
		    	if(data){
		    		var row;
					for (var i = 0, length=data.length; i < length; i++) {
						row=$("#list-container [data-index='"+i+"']").attr("dataid",data[i].id);
						if(data[i].exeStatus==1){
							row.find("[lay-event='start']").attr("starting","true").css("cursor","not-allowed");
						}
					}
				}
		    	$.element.init();
		    }
		});
		$.table.on('tool(transferTask-list)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
		    var data = obj.data //获得当前行数据
		    ,layEvent = obj.event; //获得 lay-event 对应的值
		    if(layEvent === 'del'){
		   		$.layer.confirm('确定要删除此行数据吗？', function(index){
		        	$.get(window.PATH+'/transfer/deleteTransferTask?id='+data.id,function(data){
		        		if(data.success){
		        			$.layer.alert("删除成功",{icon: 1});
		        			$.dynamicJs.loadList('delete',1);
							$.layer.close(index);
						}else{
							$.layer.alert("删除失败，原因："+data.msg,{icon: 2});
						}
		        	},"json")
		      	});
		    }else if(layEvent === 'edit'){
		    	page.editTransferTask(data);
		    }else if(layEvent == 'up'){
		    	page.upTransferTask(data.id);
		    }else if(layEvent == 'down'){
		    	page.downTransferTask(data.id);
		    }else if(layEvent === 'view'){
		    	page.viewTransferTask(data);
		    }else if(layEvent === 'start'){
		    	if($(this).attr("starting")=='true'){
		    		$.layer.alert("定时器任务正在执行，请稍后重试",{icon: 2});
		    	}else{
		    		page.startTimer(data);
		    	}
		    }
		});
		$.form.on('checkbox(select-all)', function(data){
			if(data.elem.checked){
				$("#list-container .layui-table-fixed input[name='delete-checkbox']").attr("checked",true).next().addClass("layui-form-checked");
			}else{
				$("#list-container .layui-table-fixed input[name='delete-checkbox']").attr("checked",false).next().removeClass("layui-form-checked");
			}
		});
		$.form.on('switch(issueModel)', function(obj){
			page.issueTransferTask(this.value,obj.elem.checked?'issue':'cancel');
		});
	};
	function initExePeriod(data){
		var exePeriod=data.exePeriod;
		if(exePeriod.indexOf(',')!=-1){
			return exePeriod.substring(exePeriod.indexOf(',')+1)+"分钟";
		}else{
			return data.exePeriodName||'';
		}
	}
	function initSourceTable(data){
		return data.sourceName+'['+data.sourceTable+']';
	}
	function initTargetTable(data){
		return data.targetName+'['+data.targetTable+']';
	}
	function initExeStatus(data){
		var index=data.LAY_INDEX;
		if(data.exeStatus==1){
			return '<div id="'+data.id+'-exestatus" class="progress-list" onclick="$.dynamicJs.stopTransferTask(\''+data.id+'\');"><div class="layui-progress" lay-filter="process-'+data.id+'" lay-showPercent="yes"><div class="layui-progress-bar" lay-percent="'+(data.exeProgress||0)+'%"></div></div></div>';
		}else if(data.exeStatus==2 && data.finishHint==1){
			return '<div id="'+data.id+'-exestatus" class="progress-list">执行完成['+(data.exeLastEndTime||'')+']</div>';
		}else{
			return '<div id="'+data.id+'-exestatus" class="progress-list"></div>';
		}
	}
	function initExeResult(data){
		if(data.exeResult){
			if(data.exeResult==1){
				return '<span style="color:green">成功</span>';
			}else{
				return '<span style="color:red">失败</span>';
			}
		}
		return '';
	}
	page.loadList();
	page.addTransferTask=function(){
		if(!$("#content-title [lay-id='addTransferTask']").exists()){
			$.element.tabAdd('content-title', {
		        title: '增加数据抽取任务' //用于演示
		        ,content: '<div id="save-form-body" class="tab-form-body"></div>'
		        ,id: "addTransferTask" //实际使用一般是规定好的id，这里以时间戳模拟下
			});
		}
		$("#save-form-body").load(window.PATH+"/transfer/addTransferTask",function(){
			$.element.tabChange('content-title', 'addTransferTask');
			$.dynamicJs.currTabs=$("#save-form-body");
			$.dynamicJs.currTabsForm=$.dynamicJs.currTabs.find(".layui-form");
			initTabPage();
		});
	};
	page.saveTransferTask=function(status){
		if($.valiForm('save-transferTask')){
			var fieldBindData=page.getFieldBindData();
			$.post(window.PATH+"/transfer/saveTransferTask",$("#save-transferTask").serialize()+"&status="+status+"&fieldBindJson="+$.json(fieldBindData),function(data){
				if(data.success){
					$.layer.alert("保存成功",{icon: 1});
					$("#save-transferTask [name='id']").val(data.id);
					$("#save-transferTask [name='coding']").addClass("layui-disabled").attr("disabled",true);
					if(status==2){
						$.element.tabChange('content-title', 'tab-list-page');
						page.closeTransferTask('addTransferTask');
						$.dynamicJs.loadList('add');
					}
				}else{
					$.layer.alert("保存失败，原因："+data.msg,{icon: 2});
				}
			},'json');
		}
	};
	page.testTransferTask=function(status){
		if($.valiForm('save-transferTask')){
			var fieldBindData=page.getFieldBindData();
			$.post(window.PATH+"/transfer/testTransferTask",$("#save-transferTask").serialize()+"&fieldBindJson="+$.json(fieldBindData),function(data){
				if(data.success){
					$.layer.alert("测试成功",{icon: 1});
				}else{
					$.layer.alert("测试失败，原因："+data.msg,{icon: 2});
				}
			},'json');
		}
	};
	page.closeTransferTask=function(tabId){
		$.element.tabDelete('content-title', tabId);
	}
	page.editTransferTask=function(data){
		if(!$("#content-title [lay-id='editTransferTask']").exists()){
			$.element.tabAdd('content-title', {
		        title: '编辑数据抽取任务' //用于演示
		        ,content: '<div id="update-form-body" class="tab-form-body"></div>'
		        ,id: "editTransferTask" //实际使用一般是规定好的id，这里以时间戳模拟下
			});
		}
		$("#update-form-body").load(window.PATH+"/transfer/editTransferTask?id="+data.id,function(){
			$.element.tabChange('content-title', 'editTransferTask');
			$.dynamicJs.currTabs=$("#update-form-body");
			$.dynamicJs.currTabsForm=$.dynamicJs.currTabs.find(".layui-form");
			initTabPage();
		});
	};
	page.updateTransferTask=function(){
		if($.valiForm('update-transferTask')){
			var fieldBindData=page.getFieldBindData2();
			$.post(window.PATH+"/transfer/updateTransferTask",$("#update-transferTask").serialize()+"&fieldBindJson="+$.json(fieldBindData),function(data){
				if(data.success){
					$.layer.alert("保存成功",{icon: 1});
					$("#update-transferTask [name='id']").val(data.id);
					$.element.tabChange('content-title', 'tab-list-page');
					page.closeTransferTask('editTransferTask');
					$.dynamicJs.loadList('edit');
				}else{
					$.layer.alert("保存失败，原因："+data.msg,{icon: 2});
				}
			},'json');
		}
	};
	page.testTransferTask2=function(status){
		if($.valiForm('update-transferTask')){
			var fieldBindData=page.getFieldBindData2();
			$.post(window.PATH+"/transfer/testTransferTask",$("#update-transferTask").serialize()+"&fieldBindJson="+$.json(fieldBindData),function(data){
				if(data.success){
					$.layer.alert("测试成功",{icon: 1});
				}else{
					$.layer.alert("测试失败，原因："+data.msg,{icon: 2});
				}
			},'json');
		}
	};
	page.viewTransferTask=function(data){
		if(!$("#content-title [lay-id='viewTransferTask']").exists()){
			$.element.tabAdd('content-title', {
		        title: '查看数据抽取任务' //用于演示
		        ,content: '<div id="view-form-body" class="tab-form-body"></div>'
		        ,id: "viewTransferTask" //实际使用一般是规定好的id，这里以时间戳模拟下
			});
		}
		$("#view-form-body").load(window.PATH+"/transfer/viewTransferTask?id="+data.id,function(){
			$.element.tabChange('content-title', 'viewTransferTask');
			$.dynamicJs.currTabs=$("#view-form-body");
			$.dynamicJs.currTabsForm=$.dynamicJs.currTabs.find(".layui-form");
			initTabPage();
		});
	};
	page.testTransferTask3=function(status){
		$.post(window.PATH+"/transfer/testTransferTask","id="+$("#view-transferTask [name='id']").val(),function(data){
			if(data.success){
				$.layer.alert("测试成功",{icon: 1});
			}else{
				$.layer.alert("测试失败，原因："+data.msg,{icon: 2});
			}
		},'json');
	};
	page.issueTransferTask=function(id, operator){
		$.layer.confirm('确定要'+(operator=='issue'?'发布':'取消发布')+'此任务吗？', function(index){
			$.get(window.PATH+"/transfer/issueTransferTask",{id:id,operator:operator},function(data){
				$.layer.close(index);
				if(data.success){
					$.dynamicJs.loadList('issue');
				}else{
					$.layer.alert((operator=='issue'?"发布":"撤销")+"失败，原因："+data.msg,{icon: 2});
				}
			},'json');
		});
		
	};
	page.downTransferTask=function(id){
		$.get(window.PATH+"/transfer/moveTransferTask",{id:id,operator:'down'},function(data){
			if(data.success){
				$.dynamicJs.loadList('move');
			}else{
				$.layer.alert("移动失败，原因："+data.msg,{icon: 2});
			}
		},'json');
	};
	page.upTransferTask=function(id){
		$.get(window.PATH+"/transfer/moveTransferTask",{id:id,operator:'up'},function(data){
			if(data.success){
				$.dynamicJs.loadList('move');
			}else{
				$.layer.alert("移动失败，原因："+data.msg,{icon: 2});
			}
		},'json');
	};
	page.deleteTransferTasks=function(){
		var ids=[];
		$("#list-container .layui-table-fixed input[name='layTableCheckbox']:checked").each(function(){
			if(this.value!='on' && !$(this).hasClass("checkbox-none")){
				ids.push(this.value);
			}
		});
		if(ids.length==0){
			$.layer.alert("请选择要删除的行",{icon: 2});
			return ;
		}
		$.layer.confirm('确定要删除选中的数据吗？', function(index){
			$.get(window.PATH+"/transfer/deleteTransferTasks?ids="+ids.join(','),function(data){
				if(data.success){
        			$.layer.alert("删除成功",{icon: 1});
        			$.dynamicJs.loadList('delete',ids.length);
				}else{
					$.layer.alert("删除失败，原因："+data.msg,{icon: 2});
				}
			},'json');
			$.layer.close(index);
		});
	};
	page.transferTaskRecycle=function(){
		$.layer.open({
			type		:	1,
			title		:	'数据抽取任务回收站',
			area		:	["90%","590px"],
			content		:	'<div id="recycle-body"></div>',
			cancel		:	function(index){$.layer.close(index);}
		});
		$("#recycle-body").load(window.PATH+"/transfer/transferTaskRecycle");
	};
	page.exeHistory=function(id, coding, name){
		$.layer.open({
			type		:	1,
			title		:	'执行历史<span style="color:red;">【编号：'+coding+"，名称："+name+'】</span>',
			area		:	["90%","590px"],
			content		:	'<div id="exe-history-body"></div>',
			cancel		:	function(index){$.layer.close(index);}
		});
		$("#exe-history-body").load(window.PATH+"/transfer/exeLoggerPageList?taskId="+id);
	}
	
	page.startTimer=function(data){
		$.get(window.PATH+"/system/startTimer?id="+data.coding+"&type=coding",function(data){
			if(data.success){
    			$.layer.alert("启动成功",{icon: 1});
    			$.dynamicJs.loadList('start');
			}else{
				$.layer.alert("启动失败，原因："+data.msg,{icon: 2});
			}
		},'json');
	};
	page.stopTransferTask=function(id){
		$.layer.confirm('确定要停止此任务吗？', function(index){
			$.get(window.PATH+"/transfer/stopTransferTask?id="+id,function(data){
				if(data.success){
        			$.layer.alert("任务正在强行停止，请等待5-10秒",{icon: 1});
				}else{
					$.layer.alert("停止失败，原因："+data.msg,{icon: 2});
				}
			},'json');
			$.layer.close(index);
		});
	}
	var interval=window.setInterval(function(){
		$.post(window.PATH+"/transfer/transferTaskPageListData",{page:curr,limit:limit,coding:$("#searchCoding").val()||"",name:$("#searchName").val()||""},function(page){
			var data=page.data;
			if(data){
				var row,process;
				for (var i = 0, length=data.length; i < length; i++) {
					row=$("#list-container [dataid='"+data[i].id+"']");
					if(row.exists()){
						process=row.find("[lay-filter='process-"+data[i].id+"']");
						if(data[i].exeStatus==1){
							if(!process.exists()){
								row.find("#"+data[i].id+"-exestatus").html('<div class="layui-progress" lay-filter="process-'+data.id+'" lay-showPercent="yes"><div class="layui-progress-bar" lay-percent="0%"></div></div>');
							}
							$.element.progress('process-'+data[i].id, (data[i].exeProgress||0)+"%");
							row.find("[lay-event='start']").attr("starting","true").css("cursor","not-allowed");
						}else if(data[i].exeStatus==2 && data[i].finishHint==1){
							row.find("#"+data[i].id+"-exestatus").html("执行完成["+(data[i].exeLastEndTime||'')+']');
							row.find("[lay-event='start']").attr("starting","false").css("cursor","pointer");
						}else{
							if(process.exists()){
								row.find("#"+data[i].id+"-exestatus").html("");
								row.find("[lay-event='start']").attr("starting","false").css("cursor","pointer");
							}
						}
					}
				}
			}
		},'json')
	},10000);
	$.intervals.push(interval);
})($.dynamicJs);