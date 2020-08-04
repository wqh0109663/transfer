package org.unswift.gtft.transfer.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.unswift.core.utils.FileUtils;
import org.unswift.core.utils.JsonUtils;
import org.unswift.core.utils.NumberUtils;
import org.unswift.core.utils.ObjectUtils;
import org.unswift.core.utils.SpringUtils;
import org.unswift.core.utils.StringUtils;
import org.unswift.core.utils.SystemUtils;
import org.unswift.gtft.transfer.pojo.DataSource;
import org.unswift.gtft.transfer.pojo.TaskFieldBind;
import org.unswift.gtft.transfer.pojo.TaskLogger;
import org.unswift.gtft.transfer.pojo.TaskLoggerBatch;
import org.unswift.gtft.transfer.pojo.TransferTask;
import org.unswift.core.pojo.Page;
import org.unswift.gtft.transfer.service.IDataSourceService;
import org.unswift.gtft.transfer.service.ITransferTaskService;
import org.unswift.gtft.transfer.dao.ITaskFieldBindMapper;
import org.unswift.gtft.transfer.dao.ITaskLoggerBatchMapper;
import org.unswift.gtft.transfer.dao.ITaskLoggerMapper;
import org.unswift.gtft.transfer.dao.ITransferTaskMapper;
import org.unswift.base.system.pojo.Timer;
import org.unswift.base.system.service.IPlateService;
import org.unswift.base.system.service.ITimerService;
import org.unswift.base.system.service.impl.PageBean;
import org.unswift.core.annotations.Cache;
import org.unswift.core.conn.Session;
import org.unswift.core.exception.UnswiftException;
import org.unswift.core.pojo.DeletePojo;
import org.unswift.core.pojo.OrderNoMovePojo;
import org.unswift.core.pojo.StatusPojo;

/**
 * 数据抽取任务Service(封装各种业务操作)
 * @author Administrator
 *
 */
@Service("transferTaskService")
@Transactional(propagation=Propagation.NOT_SUPPORTED, rollbackFor=Exception.class) 
public class TransferTaskService implements ITransferTaskService, IPlateService{

	@Resource
    private ITransferTaskMapper transferTaskMapper;
	@Resource
	private ITaskFieldBindMapper raskFieldBindMapper;
	@Resource
	private ITaskLoggerMapper taskLoggerMapper;
	@Resource
	private ITaskLoggerBatchMapper taskLoggerBatchMapper;
	@Resource
	private ITimerService timerService;
	@Resource
	private IDataSourceService dataSourceService;

	@Override
	public Page findPageList(Page page, TransferTask transferTask){
		List<TransferTask> transferTaskList=transferTaskMapper.findPageList(page, transferTask);
		page.setData(transferTaskList);
		return page;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveTransferTask(TransferTask transferTask){
		transferTask.setId(StringUtils.uuid());
		TransferTask search=new TransferTask();
		search.setCoding(transferTask.getCoding());
		if(transferTaskMapper.findCount(search)>0){
			throw new UnswiftException("任务编号["+search.getCoding()+"]已存在");
		}
		Timer timer=new Timer();
		timer.setCoding(transferTask.getCoding());
		timer.setName(transferTask.getName());
		timer.setBean("dataExeService");
		timer.setStartExe(-1);
		String exePeriod=transferTask.getExePeriod();
		timer.setExeNumber(exePeriod.equals("-1")?0:-1);
		if(exePeriod.indexOf(",")!=-1){
			exePeriod=exePeriod.split("\\,")[1].trim();
		}
		timer.setExeTime("each "+NumberUtils.calc(exePeriod).longValue()+"mm");
		timer.setStartZero(-1);
		timer.setExeNumberd(0);
		timer.setExeStatus(0);
		timer.setDescription(transferTask.getName());
		timer.setStatus(transferTask.getStatus());
		timerService.saveTimer(timer);
		transferTask.setOrderNo(transferTaskMapper.findMax(null)+1);
		transferTaskMapper.insert(transferTask);
		List<TaskFieldBind> fieldBindList=JsonUtils.toJavaList(transferTask.getFieldBindJson(), TaskFieldBind.class);
		for (TaskFieldBind taskFieldBind : fieldBindList) {
			taskFieldBind.setId(StringUtils.uuid());
			taskFieldBind.setTaskId(transferTask.getId());
			taskFieldBind.setStatus(1);
			raskFieldBindMapper.insert(taskFieldBind);
		}
	}
	@Override
	public TransferTask findById(String id) {
		TransferTask transferTask=transferTaskMapper.findById(id);
		transferTask.setFieldBindList(raskFieldBindMapper.findList(new TaskFieldBind(id)));
		return transferTask;
	}
	
	@Override
	public TransferTask findByCoding(String coding) {
		TransferTask transferTask=transferTaskMapper.findByCoding(coding);
		transferTask.setFieldBindList(raskFieldBindMapper.findList(new TaskFieldBind(transferTask.getId())));
		return transferTask;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void updateTransferTask(TransferTask transferTask){
		Timer timer=new Timer();
		String coding=transferTask.getCoding();
		if(ObjectUtils.isEmpty(coding)){
			coding=transferTaskMapper.findById(transferTask.getId()).getCoding();
		}
		timer.setCoding(coding);
		timer.setName(transferTask.getName());
		timer.setBean("dataExeService");
		timer.setStartExe(-1);
		String exePeriod=transferTask.getExePeriod();
		timer.setExeNumber(exePeriod.equals("-1")?0:-1);
		if(exePeriod.indexOf(",")!=-1){
			exePeriod=exePeriod.split("\\,")[1].trim();
		}
		timer.setExeTime("each "+NumberUtils.calc(exePeriod).longValue()+"mm");
		timer.setStartZero(-1);
		timer.setExeNumberd(0);
		timer.setExeStatus(0);
		timer.setDescription(transferTask.getName());
		timer.setStatus(transferTask.getStatus());
		timerService.updateTimerByCoding(timer);
		transferTaskMapper.updateById(transferTask);
		raskFieldBindMapper.deleteByTaskId(transferTask.getId());
		List<TaskFieldBind> fieldBindList=JsonUtils.toJavaList(transferTask.getFieldBindJson(), TaskFieldBind.class);
		for (TaskFieldBind taskFieldBind : fieldBindList) {
			taskFieldBind.setId(StringUtils.uuid());
			taskFieldBind.setTaskId(transferTask.getId());
			taskFieldBind.setStatus(1);
			raskFieldBindMapper.insert(taskFieldBind);
		}
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void deleteTransferTask(String id){
		TransferTask transferTask=this.findById(id);
		timerService.deleteByCoding(transferTask.getCoding());
		transferTaskMapper.deleteById(new TransferTask(id));
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void moveTransferTask(String id, String operator) {
		TransferTask transferTask=transferTaskMapper.findById(id);
		int orderNo=transferTask.getOrderNo();
		if(operator.equals("down")){
			TransferTask after=transferTaskMapper.findAfter(orderNo);
			if(after!=null){
				transferTaskMapper.updateOrderNo(new OrderNoMovePojo(transferTask.getId(), after.getOrderNo(), after.getId(), orderNo));
			}
		}else{
			TransferTask before=transferTaskMapper.findBefore(orderNo);
			if(before!=null){
				transferTaskMapper.updateOrderNo(new OrderNoMovePojo(transferTask.getId(), before.getOrderNo(), before.getId(), orderNo));
			}
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void deleteTransferTasks(String[] ids) {
		TransferTask transferTask;
		StringBuilder codings=new StringBuilder();
		for (String id : ids) {
			transferTask=this.findById(id);
			codings.append(transferTask.getCoding()).append(",");
		}
		codings.deleteCharAt(codings.length()-1);
		timerService.deleteByCodings(codings.toString().split("\\,"));
		transferTaskMapper.deleteTransferTasks(new DeletePojo(ids));
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void deleteRecycles(String[] ids) {
		transferTaskMapper.deleteRecycles(ids);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void clearRecycle() {
		transferTaskMapper.clearRecycle();
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void recoveryRecycles(String[] ids) {
		TransferTask transferTask;
		StringBuilder codings=new StringBuilder();
		for (String id : ids) {
			transferTask=this.findById(id);
			codings.append(transferTask.getCoding()).append(",");
		}
		codings.deleteCharAt(codings.length()-1);
		timerService.recoveryRecyclesByCoding(codings.toString().split("\\,"));
		transferTaskMapper.updateStatus(new StatusPojo(ids, 1));
	}
	
	@Override
	public void testTask(TransferTask transferTask) {
		if(transferTask.getExeMethod().equals("increment")){
			if(ObjectUtils.isEmpty(transferTask.getLastUpdateField())){
				throw new UnswiftException("增量同步时必须指定时间戳字段");
			}
		}
		
		List<TaskFieldBind> fieldBindList;
		if(ObjectUtils.isNotEmpty(transferTask.getId()) && ObjectUtils.isEmpty(transferTask.getName())){
			transferTask=this.findById(transferTask.getId());
			fieldBindList=transferTask.getFieldBindList();
		}else{
			fieldBindList=JsonUtils.toJavaList(transferTask.getFieldBindJson(), TaskFieldBind.class);
		}
		DataSource dataSource=dataSourceService.findById(transferTask.getSourceId());
		String pk=ObjectUtils.init(transferTask.getSourcePk(), "ID");
		Session session=null;
		String sql;
		try {
			session=dataSourceService.createSession(dataSource);
			session.test();
			if(transferTask.getSourceType().equals("table")){
				sql="SELECT ";
				boolean pkExists=false;
				for (TaskFieldBind taskFieldBind : fieldBindList) {
					if(!taskFieldBind.getSourceField().startsWith("#")){
						sql+=taskFieldBind.getSourceField()+',';
						if(StringUtils.containIgnoreCase(pk, taskFieldBind.getSourceField())){
							pkExists=true;
						}
					}
				}
				if(!pkExists){
					sql+=pk+',';
				}
				sql=sql.substring(0, sql.length()-1);
				sql+=" FROM "+transferTask.getSourceTable()+" WHERE 1=1 "+(ObjectUtils.isEmpty(transferTask.getSourceSql())?"":"AND "+transferTask.getSourceSql());
			}else{
				sql=transferTask.getSourceSql();
			}
			System.out.println(sql);
			sql="SELECT COUNT(1) FROM ("+sql+") T";
			session.searchCount(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnswiftException(e.getMessage());
		} finally {
			if(ObjectUtils.isNotEmpty(session)){
				session.close();
			}
		}
		dataSource=dataSourceService.findById(transferTask.getTargetId());
		pk=ObjectUtils.init(transferTask.getTargetPk(), "ID");
		session=null;
		try {
			session=dataSourceService.createSession(dataSource);
			session.test();
			if(ObjectUtils.isEmpty(transferTask.getNotExistsCreate()) || transferTask.getNotExistsCreate().equals(-1)){
				sql="SELECT ";
				boolean pkExists=false;
				for (TaskFieldBind taskFieldBind : fieldBindList) {
					sql+=taskFieldBind.getTargetField()+',';
					if(StringUtils.containIgnoreCase(pk, taskFieldBind.getTargetField())){
						pkExists=true;
					}
				}
				if(!pkExists){
					sql+=pk+',';
				}
				sql=sql.substring(0, sql.length()-1);
				
				sql+=" FROM "+transferTask.getTargetTable();
				sql="SELECT COUNT(1) FROM ("+sql+") T";
				session.searchCount(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnswiftException(e.getMessage());
		} finally {
			if(ObjectUtils.isNotEmpty(session)){
				session.close();
			}
		}
		if(transferTask.getSourceId().equals(transferTask.getTargetId())){
			if(transferTask.getSourceType().equals("table")){
				if(transferTask.getSourceTable().equals(transferTask.getTargetTable())){
					throw new UnswiftException("数据来源跟目标不能相同");
				}
			}
		}
		
	}
	
	@Override
	public List<Map<String, String>> getSourceFieldList(String sourceId, String type, String tableNameOrSql) {
		DataSource dataSource=dataSourceService.findById(sourceId);
		Session session=null;
		try {
			session=dataSourceService.createSession(dataSource);
			List<Map<String, String>> fieldList=session.searchFields(type, tableNameOrSql);
			String fieldType,fieldLength,fieldScale;
			PageBean pageBean=SpringUtils.getBean("pageBean");
			for (Map<String, String> map : fieldList) {
				fieldType=map.get("fieldType");
				System.out.println(map.get("fieldName")+":"+fieldType+":"+pageBean.getDictionaryCodingByContainDesc("DATA_FIELD_TYPE", fieldType));
				fieldType=ObjectUtils.init(pageBean.getDictionaryCodingByContainDesc("DATA_FIELD_TYPE", fieldType), fieldType);
				if(fieldType.equals("double")){
					fieldLength=map.get("dataLength");
					fieldScale=map.get("dataScale");
					if(ObjectUtils.isNotEmpty(fieldLength)){
						if(ObjectUtils.isNotEmpty(fieldScale) && Integer.parseInt(fieldScale)>0){
							if(Integer.parseInt(fieldLength)>8){
								fieldType="decimal";
							}
						}else{
							if(Integer.parseInt(fieldLength)>8){
								fieldType="long";
							}else{
								fieldType="int";
							}
						}
					}
				}
				map.put("fieldType", fieldType);
			}
			return fieldList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnswiftException(e.getMessage());
		} finally {
			if(ObjectUtils.isNotEmpty(session)){
				session.close();
			}
		}
	}
	
	@Override
	public void issueTransferTask(String id, String operator) {
		TransferTask transferTask=transferTaskMapper.findById(id);
		timerService.issueTimerByCoding(transferTask.getCoding(), operator);
	}
	
	@Override
	public void createLogger(TaskLogger logger) {
		logger.setId(StringUtils.uuid());
		logger.setExeBatch(taskLoggerMapper.findMaxExeBatch(logger.getTaskId())+1);
		taskLoggerMapper.insert(logger);
	}
	
	@Override
	public void finishLogger(TaskLogger logger) {
		taskLoggerMapper.updateById(logger);
	}
	
	@Override
	public void createLoggerBatch(TaskLoggerBatch batchLogger) {
		batchLogger.setId(StringUtils.uuid());
		taskLoggerBatchMapper.insert(batchLogger);
	}
	
	@Override
	public void finishLoggerBatch(TaskLoggerBatch batchLogger) {
		taskLoggerBatchMapper.updateById(batchLogger);
	}
	
	@Override
	public void updateMaxTime(TransferTask transferTask) {
		transferTaskMapper.updateMaxTime(transferTask);
	}
	
	@Override
	public Page findLoggerPageList(Page page, TaskLogger logger){
		List<TaskLogger> transferTaskList=taskLoggerMapper.findPageList(page, logger);
		page.setData(transferTaskList);
		return page;
	}
	
	@Override
	public Page findLoggerBatchPageList(Page page, TaskLoggerBatch batch){
		List<TaskLoggerBatch> transferTaskList=taskLoggerBatchMapper.findPageList(page, batch);
		if(ObjectUtils.isNotEmpty(transferTaskList)){
			for (TaskLoggerBatch taskLoggerBatch : transferTaskList) {
				if(ObjectUtils.isNotEmpty(taskLoggerBatch.getSearchData())){
					taskLoggerBatch.setSearchData(FileUtils.reader(SystemUtils.system("data-task-back")+taskLoggerBatch.getSearchData(), "UTF-8"));
				}
				if(ObjectUtils.isNotEmpty(taskLoggerBatch.getExeSql())){
					taskLoggerBatch.setExeSql(FileUtils.reader(SystemUtils.system("data-task-back")+taskLoggerBatch.getExeSql(), "UTF-8"));
				}
			}
		}
		page.setData(transferTaskList);
		return page;
	}
	@Override
	public void loadPlate(Model model) {
		TransferTask transferTask=new TransferTask();
		transferTask.setSearchStatus("1");
		transferTask.setStatus(2);
		transferTask.setExeStatus(1);
		Page page=Page.initPage();
		page.setPage(1);
		page.setLimit(5);
		model.addAttribute("dataTaskPage", this.findPageList(page, transferTask));
	}
}
