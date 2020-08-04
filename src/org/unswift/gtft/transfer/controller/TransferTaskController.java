package org.unswift.gtft.transfer.controller;

import java.util.Map;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;

import org.unswift.core.annotations.Authority;
import org.unswift.core.annotations.JLogger;
import org.unswift.core.authority.IAuthorityInit;
import org.unswift.gtft.transfer.pojo.TaskLogger;
import org.unswift.gtft.transfer.pojo.TaskLoggerBatch;
import org.unswift.gtft.transfer.pojo.TransferTask;
import org.unswift.gtft.transfer.service.ITransferTaskService;
import org.unswift.gtft.transfer.service.impl.DataExeService;
import org.unswift.core.pojo.Page;
import org.unswift.core.utils.ObjectUtils;


/**
 * 数据抽取任务Controller(后端访问入口)
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/transfer")
@Authority(name="数据抽取",index=10)
public class TransferTaskController implements IAuthorityInit{

	@Resource
	private ITransferTaskService transferTaskService;
	
	private static final String PATH="transfer/transferTask/";
	
	@JLogger("跳转到数据抽取任务管理界面(数据抽取任务管理入口请求)")
	@Authority(name="数据抽取任务",parent="#/transfer",index=2,depth=2)
	@RequestMapping(value = "/transferTaskPageList", method = RequestMethod.GET)
    public String transferTaskPageList(Model model) {
		return PATH+"transferTask-list";
	}
	
	@ResponseBody
	@JLogger("根据查询条件查询数据抽取任务列表数据")
	@Authority(blongTo="/transferTaskPageList")
	@RequestMapping(value = "/transferTaskPageListData", method = RequestMethod.POST)
    public Page transferTaskPageListData(TransferTask transferTask, Page page, Model model) {
		transferTask.setSearchStatus("1");
		return transferTaskService.findPageList(page, transferTask);
	}
	@JLogger("跳转到增加数据抽取任务页面")
	@Authority(name="增加数据抽取任务",parent="/transferTaskPageList",type="PURVIEW",index=1,depth=3)
	@RequestMapping(value = "/addTransferTask", method = RequestMethod.GET)
    public String addTransferTask(Model model) {
		return PATH+"add-transferTask";
	}
	
	@ResponseBody
	@JLogger("保存数据抽取任务信息")
	@Authority(blongTo="/addTransferTask")
	@RequestMapping(value = "/saveTransferTask", method = RequestMethod.POST)
    public Map<String, Object> saveTransferTask(TransferTask transferTask, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			result=testTransferTask(transferTask, model);
			if(!(Boolean)result.get("success")){
				return result;
			}
			if(ObjectUtils.isEmpty(transferTask.getId())){
				transferTaskService.saveTransferTask(transferTask);
			}else{
				transferTaskService.updateTransferTask(transferTask);
			}
			result.put("id", transferTask.getId());
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("测试数据抽取任务是否可以进行")
	@Authority(blongTo="/addTransferTask,/editTransferTask")
	@RequestMapping(value = "/testTransferTask", method = RequestMethod.POST)
    public Map<String, Object> testTransferTask(TransferTask transferTask, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.testTask(transferTask);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@JLogger("跳转到编辑数据抽取任务页面")
	@Authority(name="编辑数据抽取任务",parent="/transferTaskPageList",type="PURVIEW",index=2,depth=3)
	@RequestMapping(value = "/editTransferTask", method = RequestMethod.GET)
    public String editTransferTask(String id, Model model) {
		model.addAttribute("transferTask", transferTaskService.findById(id));
		return PATH+"edit-transferTask";
	}
	
	@ResponseBody
	@JLogger("更新数据抽取任务信息")
	@Authority(blongTo="/editTransferTask")
	@RequestMapping(value = "/updateTransferTask", method = RequestMethod.POST)
    public Map<String, Object> updateTransferTask(TransferTask transferTask, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			result=testTransferTask(transferTask, model);
			if(!(Boolean)result.get("success")){
				return result;
			}
			transferTaskService.updateTransferTask(transferTask);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@JLogger("跳转到查看数据抽取任务页面")
	@Authority(name="查看数据抽取任务",parent="/transferTaskPageList",type="PURVIEW",index=2,depth=3)
	@RequestMapping(value = "/viewTransferTask", method = RequestMethod.GET)
    public String viewTransferTask(String id, Model model) {
		model.addAttribute("transferTask", transferTaskService.findById(id));
		return PATH+"view-transferTask";
	}
	
	@ResponseBody
	@JLogger("删除单个数据抽取任务信息")
	@Authority(name="删除数据抽取任务",parent="/transferTaskPageList",type="PURVIEW",index=3,depth=3)
	@RequestMapping(value = "/deleteTransferTask", method = RequestMethod.GET)
	public Map<String, Object> deleteTransferTask(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.deleteTransferTask(id);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("数据抽取任务显示顺序移动")
	@Authority(name="数据抽取任务顺序移动",parent="/transferTaskPageList",type="PURVIEW",index=4,depth=3)
	@RequestMapping(value = "/moveTransferTask", method = RequestMethod.GET)
	public Map<String, Object> moveTransferTask(String id, String operator, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.moveTransferTask(id,operator);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("发布、注销数据抽取任务")
	@Authority(name="发布、注销数据抽取任务",parent="/transferTaskPageList",type="PURVIEW",index=5,depth=3)
	@RequestMapping(value = "/issueTransferTask", method = RequestMethod.GET)
	public Map<String, Object> issueTransferTask(String id, String operator, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.issueTransferTask(id, operator);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("删除多个数据抽取任务信息")
	@Authority(blongTo="/deleteTransferTask")
	@RequestMapping(value = "/deleteTransferTasks", method = RequestMethod.GET)
	public Map<String, Object> deleteTransferTasks(String[] ids, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.deleteTransferTasks(ids);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@JLogger("跳转到数据抽取任务回收站界面")
	@Authority(name="数据抽取任务回收站",parent="/transferTaskPageList",type="PURVIEW",index=6,depth=3)
	@RequestMapping(value = "/transferTaskRecycle", method = RequestMethod.GET)
    public String transferTaskRecycle(Model model) {
		return PATH+"transferTask-recycle";
	}
	
	@ResponseBody
	@JLogger("读取数据抽取任务回收站列表数据")
	@Authority(blongTo="/transferTaskRecycle")
	@RequestMapping(value = "/transferTaskRecycleData", method = RequestMethod.POST)
    public Page transferTaskRecycleData(TransferTask transferTask, Page page, Model model) {
		transferTask.setSearchStatus("0");
		transferTaskService.findPageList(page, transferTask);
		return page;
	}
	
	@ResponseBody
	@JLogger("删除数据抽取任务回收站中的单个数据抽取任务信息")
	@Authority(blongTo="/transferTaskRecycle")
	@RequestMapping(value = "/deleteTransferTaskRecycle", method = RequestMethod.GET)
	public Map<String, Object> deleteTransferTaskRecycle(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.deleteRecycles(new String[]{id});
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("删除数据抽取任务回收站中的被选择的数据抽取任务信息(多个)")
	@Authority(blongTo="/transferTaskRecycle")
	@RequestMapping(value = "/deleteTransferTaskRecycles", method = RequestMethod.GET)
	public Map<String, Object> deleteTransferTaskRecycles(String[] ids, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.deleteRecycles(ids);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("清理数据抽取任务回收中的所有数据")
	@Authority(blongTo="/transferTaskRecycle")
	@RequestMapping(value = "/clearTransferTaskRecycle", method = RequestMethod.GET)
	public Map<String, Object> clearTransferTaskRecycle(Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.clearRecycle();
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("恢复数据抽取任务回收站中单个员工数据")
	@Authority(blongTo="/transferTaskRecycle")
	@RequestMapping(value = "/recoveryTransferTaskRecycle", method = RequestMethod.GET)
	public Map<String, Object> recoveryTransferTaskRecycle(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.recoveryRecycles(new String[]{id});
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("恢复数据抽取任务回收站中被选择的数据抽取任务数据(多个)")
	@Authority(blongTo="/transferTaskRecycle")
	@RequestMapping(value = "/recoveryTransferTaskRecycles", method = RequestMethod.GET)
	public Map<String, Object> recoveryTransferTaskRecycles(String[] ids, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			transferTaskService.recoveryRecycles(ids);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	
	@ResponseBody
	@JLogger("恢复数据抽取任务回收站中被选择的数据抽取任务数据(多个)")
	@Authority(blongTo="/addTransferTask,/editTransferTask")
	@RequestMapping(value = "/autoGetFieldBind", method = RequestMethod.POST)
	public Map<String, Object> autoGetFieldBind(String sourceId, String type, String tableName, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			result.put("fields", transferTaskService.getSourceFieldList(sourceId, type, tableName));
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@JLogger("跳转到数据抽取任务执行日志界面")
	@Authority(name="数据抽取任务执行日志",parent="/transferTaskPageList",type="PURVIEW",index=7,depth=3)
	@RequestMapping(value = "/exeLoggerPageList", method = RequestMethod.GET)
    public String exeLoggerPageList(String taskId, Model model) {
		model.addAttribute("taskId", taskId);
		return PATH+"exeLogger-list";
	}
	
	@ResponseBody
	@JLogger("读取数据抽取任务执行日志列表数据")
	@Authority(blongTo="/exeLoggerPageList")
	@RequestMapping(value = "/exeLoggerPageListData", method = RequestMethod.POST)
    public Page exeLoggerPageListData(TaskLogger logger, Page page, Model model) {
		transferTaskService.findLoggerPageList(page, logger);
		return page;
	}
	
	@JLogger(value="日志数量明细",recordData=false)
	@Authority(blongTo="/exeLoggerPageList")
	@RequestMapping(value = "/loggerExeCount", method = RequestMethod.POST)
	public String loggerExeCount(TaskLogger logger, Page page, Model model) {
		model.addAttribute("logger", logger);
		return PATH+"view-loggerExeCount";
	}
	
	@JLogger("跳转到数据抽取任务执行日志批次明细界面")
	@Authority(name="数据抽取任务执行日志批次明细",parent="/transferTaskPageList",type="PURVIEW",index=8,depth=3)
	@RequestMapping(value = "/exeLoggerBatchPageList", method = RequestMethod.GET)
    public String exeLoggerBatchPageList(String loggerId, Model model) {
		model.addAttribute("loggerId", loggerId);
		return PATH+"exeLoggerBatch-list";
	}
	
	@ResponseBody
	@JLogger("读取数据抽取任务执行日志批次明细列表数据")
	@Authority(blongTo="/exeLoggerBatchPageList")
	@RequestMapping(value = "/exeLoggerBatchPageListData", method = RequestMethod.POST)
    public Page exeLoggerBatchPageListData(TaskLoggerBatch batch, Page page, Model model) {
		transferTaskService.findLoggerBatchPageList(page, batch);
		return page;
	}
	
	@JLogger(value="批次日志数量明细",recordData=false)
	@Authority(blongTo="/exeLoggerBatchPageList")
	@RequestMapping(value = "/loggerBatchExeCount", method = RequestMethod.POST)
	public String loggerBatchExeCount(TaskLoggerBatch logger, Page page, Model model) {
		model.addAttribute("logger", logger);
		return PATH+"view-loggerBatchExeCount";
	}
	
	@ResponseBody
	@JLogger("主页板块-数据任务加载")
	@Authority(blongTo="#/index")
	@RequestMapping(value = "/plateDataTask", method = RequestMethod.GET)
    public Page plateDataTask(Model model) {
		TransferTask transferTask=new TransferTask();
		transferTask.setSearchStatus("1");
		transferTask.setStatus(2);
		transferTask.setExeStatus(1);
		Page page=Page.initPage();
		page.setPage(1);
		page.setLimit(5);
		return transferTaskService.findPageList(page, transferTask);
	}
	
	@ResponseBody
	@JLogger("强制停止任务")
	@Authority(blongTo="/transferTaskPageList")
	@RequestMapping(value = "/stopTransferTask", method = RequestMethod.GET)
    public Map<String, Object> stopTransferTask(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			TransferTask task=transferTaskService.findById(id);
			if(ObjectUtils.isNotEmpty(task)){
				DataExeService.taskStopStatus.put(task.getCoding(), true);
			}
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
}