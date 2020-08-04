(function(page){
	var recycleList,height=467;
	page.loadRecycleList=function(type, deleteCount){
		var jump=$.refreshCurrPage(recycleList, type, deleteCount);
		if(jump===true){
			return false;
		}
		recycleList=$.table.render({
		    elem: '#recycle-list'
		    ,method:"post"
		    ,height: height
		    ,url: window.PATH+"/transfer/dataSourceRecycleData" //数据接口
		    ,where:{
		    	name	:	$("#recycleName").val()||""
		    }
		    ,page: true //开启分页
		    ,cols: [[ //表头
		    	{field: 'id', type:'checkbox', fixed: 'left'}
		    	,{field: 'name', title: '名称', width: "10%"}
				,{field: 'typeName', title: '类型', width: "8%"}
				,{field: 'dbTypeName', title: '数据库类型', width: "8%"}
				,{field: 'dbDriver', title: '数据库驱动', width: "10%"}
				,{field: 'url', title: 'URL'}
				,{field: 'username', title: '用户名', width: "8%"}
				,{field: 'password', title: '密码', width: "8%"}
				,{fixed: 'right', title: '操作', width: 170, align:'center', toolbar: '#recycleToolBar'}
		    ]]
		});
		$.table.on('tool(recycle-list)', function(obj){ //注：tool是工具条事件名，test是$.table原始容器的属性 lay-filter="对应的值"
		    var data = obj.data //获得当前行数据
		    ,layEvent = obj.event; //获得 lay-event 对应的值
		    if(layEvent === 'del'){
		   		$.layer.confirm('确定要删除此行数据吗？', function(index){
		        	$.get(window.PATH+'/transfer/deleteDataSourceRecycle?id='+data.id,function(data){
		        		if(data.success){
		        			$.layer.alert("删除成功",{icon: 1});
		        			$.dynamicJs.loadRecycleList('delete',1);
						}else{
							$.layer.alert("删除失败，原因："+data.msg,{icon: 2});
						}
		        	},"json")
		        	$.layer.close(index);
		      	});
		    }else if(layEvent === 'recovery'){
		    	page.recoveryRecycle(data.id);
		    }
		});
		$.form.on('checkbox(recycle-select-all)', function(data){
			if(data.elem.checked){
				$("#recycle-list-container .layui-table-fixed input[name='recycle-delete-checkbox']").attr("checked",true).next().addClass("layui-form-checked");
			}else{
				$("#recycle-list-container .layui-table-fixed input[name='recycle-delete-checkbox']").attr("checked",false).next().removeClass("layui-form-checked");
			}
		});
	};
	function initCheckbox(data){
		return '<input type="checkbox" name="recycle-delete-checkbox" value="'+data.id+'" lay-skin="primary">';
	}
	function initType(data){
		if(data.type=='SYSTEM'){
			return '系统';
		}else if(data.type=='PERMANENT'){
			return '永久';
		}else{
			return '临时';
		}
	}
	page.loadRecycleList();
	page.deleteRecycles=function(){
		var ids=[];
		$("#recycle-list-container .layui-table-fixed input[name='layTableCheckbox']:checked").each(function(){
			if(this.value!='on'){
				ids.push(this.value);
			}
		});
		if(ids.length==0){
			$.layer.alert("请选择要删除的行",{icon: 2});
			return ;
		}
		$.layer.confirm("删除后数据将无法恢复，确定要删除选中的数据吗？",{icon: 3, title:'提示'},function(index){
			$.get(window.PATH+"/transfer/deleteDataSourceRecycles?ids="+ids.join(','),function(data){
				if(data.success){
        			$.layer.alert("删除成功",{icon: 1});
        			$.dynamicJs.loadRecycleList('delete',ids.length);
				}else{
					$.layer.alert("删除失败，原因："+data.msg,{icon: 2});
				}
			},'json');
			$.layer.close(index);
		});
	};
	page.clearRecycle=function(){
		$.layer.confirm("清空回收站后数据将无法恢复，确定要清空回收站吗？",{icon: 3, title:'提示'},function(index){
			$.get(window.PATH+"/transfer/clearDataSourceRecycle",function(data){
				if(data.success){
        			$.layer.alert("清空回收站完成",{icon: 1});
        			$.dynamicJs.loadRecycleList('load');
				}else{
					$.layer.alert("清空回收站失败，原因："+data.msg,{icon: 2});
				}
			},'json');
			$.layer.close(index);
		});
	};
	page.recoveryRecycle=function(id){
		$.layer.confirm("确定要恢复此数据吗？",{icon: 3, title:'提示'},function(index){
			$.get(window.PATH+"/transfer/recoveryDataSourceRecycle?id="+id,function(data){
				if(data.success){
        			$.layer.alert("恢复数据成功",{icon: 1});
        			$.dynamicJs.loadRecycleList('delete',1);
					$.dynamicJs.loadList('add');
				}else{
					$.layer.alert("恢复数据失败，原因："+data.msg,{icon: 2});
				}
			},'json');
			$.layer.close(index);
		});
	};
	page.recoveryRecycles=function(){
		var ids=[];
		$("#recycle-list-container .layui-table-fixed input[name='layTableCheckbox']:checked").each(function(){
			if(this.value!='on'){
				ids.push(this.value);
			}
		});
		if(ids.length==0){
			$.layer.alert("请选择要恢复的行",{icon: 2});
			return ;
		}
		$.layer.confirm("确定要恢复选中的数据吗？",{icon: 3, title:'提示'},function(index){
			$.get(window.PATH+"/transfer/recoveryDataSourceRecycles?ids="+ids.join(','),function(data){
				if(data.success){
        			$.layer.alert("恢复数据成功",{icon: 1});
        			$.dynamicJs.loadRecycleList('delete',ids.length);
					$.dynamicJs.loadList('add');
				}else{
					$.layer.alert("恢复数据失败，原因："+data.msg,{icon: 2});
				}
			},'json');
			$.layer.close(index);
		});
	};
})($.dynamicJs);