initPageList(true);
(function(page){
	var pageList,searchHeight=$("#search").height();
	page.loadList=function(type, deleteCount){
		var jump=$.refreshCurrPage(pageList, type, deleteCount);
		if(jump===true){
			return false;
		}
		pageList=$.table.render({
		    elem: '#dataSource-list'
		    ,method:"post"
		    ,height: "#page-list-"+(searchHeight+25)
		    ,url: window.PATH+"/transfer/dataSourcePageListData" //数据接口
		    ,where:{
		    	name	:	$("#searchName").val()||""
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
				,{title: '顺序', width: 65, toolbar:"#moveToolBar"}
		    	,{fixed: 'right', title: '操作', width: 210, align:'center', toolbar:"#toolBar"}
		    ]]
		});
		$.table.on('tool(dataSource-list)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
		    var data = obj.data //获得当前行数据
		    ,layEvent = obj.event; //获得 lay-event 对应的值
		    if(layEvent === 'del'){
		   		$.layer.confirm('确定要删除此行数据吗？', function(index){
		        	$.get(window.PATH+'/transfer/deleteDataSource?id='+data.id,function(data){
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
		    	page.editDataSource(data);
		    }
		  });
		$.form.on('checkbox(select-all)', function(data){
			if(data.elem.checked){
				$("#list-container .layui-table-fixed input[name='delete-checkbox']").attr("checked",true).next().addClass("layui-form-checked");
			}else{
				$("#list-container .layui-table-fixed input[name='delete-checkbox']").attr("checked",false).next().removeClass("layui-form-checked");
			}
		});
	};
	page.loadList();
	page.addDataSource=function(){
		$.layer.open({
			title		:	'增加数据源',
			area		:	["900px","440px"],
			content		:	'<div id="form-body"></div>',
			shadeClose	:	false,
			tipsMore	:	true,
			btn			:	["保存","测试链接","取消"],
			yes			:	function(index){
				if($.valiForm('save-dataSource')){
					$.post(window.PATH+"/transfer/saveDataSource",$("#save-dataSource").serialize(),function(data){
						if(data.success){
							$.layer.alert("保存成功",{icon: 1});
							$.dynamicJs.loadList('add');
							$.layer.close(index);
						}else{
							$.layer.alert("保存失败，原因："+data.msg,{icon: 2});
						}
					},'json')
				}
				return false;
			},
			btn2		:	function(index){
				if($.valiForm('save-dataSource')){
					$.post(window.PATH+"/transfer/connDataSource",$("#save-dataSource").serialize(),function(data){
						if(data.success){
							$.layer.alert("链接成功",{icon: 1});
						}else{
							$.layer.alert("链接失败，原因："+data.msg,{icon: 2});
						}
					},'json')
				}
				return false;
			},
			btn3		:	function(index){$.layer.close(index);},
			cancel		:	function(index){$.layer.close(index);},
		});
		$("#form-body").load(window.PATH+"/transfer/addDataSource");
	};
	page.editDataSource=function(data){
		$.layer.open({
			title		:	'编辑数据源',
			area		:	["900px","440px"],
			content		:	'<div id="form-body"></div>',
			shadeClose	:	false,
			tipsMore	:	true,
			btn			:	["更新","测试链接","取消"],
			yes			:	function(index){
				if($.valiForm('update-dataSource')){
					$.post(window.PATH+"/transfer/updateDataSource",$("#update-dataSource").serialize(),function(data){
						if(data.success){
							$.layer.alert("更新成功",{icon: 1});
							$.dynamicJs.loadList('edit');
							$.layer.close(index);
						}else{
							$.layer.alert("更新失败，原因："+data.msg,{icon: 2});
						}
					},'json')
				}
				return false;
			},
			btn2		:	function(index){
				if($.valiForm('update-dataSource')){
					$.post(window.PATH+"/transfer/connDataSource",$("#update-dataSource").serialize(),function(data){
						if(data.success){
							$.layer.alert("链接成功",{icon: 1});
						}else{
							$.layer.alert("链接失败，原因："+data.msg,{icon: 2});
						}
					},'json')
				}
				return false;
			},
			btn3		:	function(index){$.layer.close(index);},
			cancel		:	function(index){$.layer.close(index);},
		});
		$("#form-body").load(window.PATH+"/transfer/editDataSource?id="+data.id);
	};
	page.downDataSource=function(id){
		$.get(window.PATH+"/transfer/moveDataSource",{id:id,operator:'down'},function(data){
			if(data.success){
				$.dynamicJs.loadList('move');
			}else{
				$.layer.alert("移动失败，原因："+data.msg,{icon: 2});
			}
		},'json');
	};
	page.upDataSource=function(id){
		$.get(window.PATH+"/transfer/moveDataSource",{id:id,operator:'up'},function(data){
			if(data.success){
				$.dynamicJs.loadList('move');
			}else{
				$.layer.alert("移动失败，原因："+data.msg,{icon: 2});
			}
		},'json');
	};
	page.deleteDataSources=function(){
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
			$.get(window.PATH+"/transfer/deleteDataSources?ids="+ids.join(','),function(data){
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
	page.dataSourceRecycle=function(){
		$.layer.open({
			type		:	1,
			title		:	'数据源回收站',
			area		:	["90%","590px"],
			content		:	'<div id="recycle-body"></div>',
			cancel		:	function(index){$.layer.close(index);}
		});
		$("#recycle-body").load(window.PATH+"/transfer/dataSourceRecycle");
	};
})($.dynamicJs);