package org.unswift.gtft.transfer.service.impl;


import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unswift.base.system.pojo.Timer;
import org.unswift.base.system.pojo.TimerExecute;
import org.unswift.base.system.service.ITimerService;
import org.unswift.core.conn.Session;
import org.unswift.core.exception.UnswiftException;
import org.unswift.core.timer.ITimerHandlerService;
import org.unswift.core.utils.DateUtils;
import org.unswift.core.utils.FileUtils;
import org.unswift.core.utils.JsonUtils;
import org.unswift.core.utils.NumberUtils;
import org.unswift.core.utils.ObjectUtils;
import org.unswift.core.utils.SystemUtils;
import org.unswift.core.utils.UserSession;
import org.unswift.gtft.transfer.pojo.DataSource;
import org.unswift.gtft.transfer.pojo.TaskFieldBind;
import org.unswift.gtft.transfer.pojo.TaskLogger;
import org.unswift.gtft.transfer.pojo.TaskLoggerBatch;
import org.unswift.gtft.transfer.pojo.TransferTask;
import org.unswift.gtft.transfer.service.IDataSourceService;
import org.unswift.gtft.transfer.service.ITransferTaskService;

@Service("dataExeService")
public class DataExeService implements ITimerHandlerService<TimerExecute>{

	private Logger logger=LoggerFactory.getLogger(DataExeService.class);
	
	private static Map<String, Boolean> taskExeStatus=new HashMap<String, Boolean>();
	public static Map<String, Boolean> taskStopStatus=new HashMap<String, Boolean>();
	
	@Resource
	private ITimerService timerService;
	@Resource
	private ITransferTaskService transferTaskService;
	@Resource
	private IDataSourceService dataSourceService;
	
	
	@Override
	public void execute(TimerExecute timerExecute) {
		String timerId=timerExecute.getTimerId();
		Timer timer=timerService.findById(timerId);
		String coding=timer.getCoding();
		Boolean isExe=taskExeStatus.get(coding);
		try {
			while(ObjectUtils.isNotEmpty(isExe) && isExe){
				Thread.sleep(500);
			}
			taskExeStatus.put(coding, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TransferTask transferTask=transferTaskService.findByCoding(coding);
		TaskLogger taskLogger=new TaskLogger(transferTask.getId());
		transferTaskService.createLogger(taskLogger);
		try {
			this.isFullExe(transferTask);//设置是否全量更新
			Long maxTime=processTransferTask(timerExecute, transferTask, taskLogger);
			taskLogger.setExeResult(1);
			if(ObjectUtils.isNotNull(maxTime)){
				if(transferTask.isFullExe()){
					transferTask.setLastFullExeTime(new Date());
				}else{
					transferTask.setLastFullExeTime(null);
				}
				transferTask.setMaxValue(new Date(maxTime));
				transferTaskService.updateMaxTime(transferTask);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(ObjectUtils.isNotEmpty(transferTask.getMaxValue())){
				if(transferTask.isFullExe()){
					transferTask.setLastFullExeTime(new Date());
				}else{
					transferTask.setLastFullExeTime(null);
				}
				transferTaskService.updateMaxTime(transferTask);
			}
			taskLogger.setExeResult(-1);
			taskLogger.setExeMsg(e.getMessage());
			throw new UnswiftException(e.getMessage());
		} finally {
			taskExeStatus.put(coding, false);
			transferTaskService.finishLogger(taskLogger);
		}
		
	}

	private Long processTransferTask(TimerExecute timerExecute, TransferTask transferTask, TaskLogger taskLogger) throws Exception {
		DataSource dataSource=dataSourceService.findById(transferTask.getSourceId());
		DataSource targetDataSource=dataSourceService.findById(transferTask.getTargetId());
		timerService.updateExeProgress(timerExecute.getId(), 5);
		String sql=genericsSql(transferTask, dataSource);
		taskLogger.setSearchSql(sql);
		long count=findCount(dataSource, sql);
		taskLogger.setTotalCount(count);
		timerService.updateExeProgress(timerExecute.getId(), 10);
		int sourceLimit=ObjectUtils.init(transferTask.getSourceLimit(), 100);
		int exeCount=(int)(count%sourceLimit==0?count/sourceLimit:count/sourceLimit+1);
		double progress=90.0/exeCount;
		sql=appendPageSql(dataSource, sql, transferTask.getSourcePk());
		TaskLoggerBatch batchLogger;
		Long currTime,maxTime=null;
		double initProgress=10.0;
		Boolean stop;
		for (int i = 0, j=1; i < count; i+=sourceLimit, j++) {
			logger.info("数据转移sql批次["+j+"],进度："+initProgress+",查询语句SQL："+sql);
			batchLogger=new TaskLoggerBatch(taskLogger.getId(), j);
			try {
				transferTaskService.createLoggerBatch(batchLogger);
				currTime=processTaskBatch(transferTask, dataSource, targetDataSource, sql, sourceLimit, i, j, batchLogger, taskLogger);
				if(ObjectUtils.isEmpty(maxTime)){
					maxTime=currTime;
				}else{
					if(ObjectUtils.isNotEmpty(currTime) && maxTime<currTime){
						maxTime=currTime;
					}
				}
				if(ObjectUtils.isNotNull(maxTime)){
					transferTask.setMaxValue(new Date(maxTime));
				}
				batchLogger.setResult(1);
			} catch (Exception e) {
				e.printStackTrace();
				batchLogger.setResult(-1);
				batchLogger.setMsg(e.getMessage());
				if(transferTask.getExeMethod().equals("increment")){
					throw new UnswiftException(e.getMessage());
				}
			} finally {
				transferTaskService.finishLoggerBatch(batchLogger);
			}
			initProgress+=progress;
			timerService.updateExeProgress(timerExecute.getId(), initProgress);
			if(i!=0 && i%10000==0){
				Thread.sleep(1000);
			}
			stop=taskStopStatus.get(transferTask.getCoding());
			if(ObjectUtils.isNotEmpty(stop) && stop){
				taskStopStatus.remove(transferTask.getCoding());
				throw new UnswiftException("用户强行停止，用户："+UserSession.getUser("username"));
			}
		}
		return maxTime;
	}

	private Long processTaskBatch(TransferTask transferTask, DataSource dataSource, DataSource targetDataSource, String sql, 
			int sourceLimit, int i, int exeCount, TaskLoggerBatch batchLogger, TaskLogger taskLogger) {
		List<Map<String, Object>> dataList;
		Session session=null;
		int index;
		try {
			session=dataSourceService.createSession(dataSource);
			if(dataSource.getDbType().equals("mysql")){
				batchLogger.setSearchSql(sql+"["+i+","+sourceLimit+"]");
				if(dataSource.getType().equals("http-url")){
					index=sql.indexOf("?");
					sql=sql.substring(0, index)+i+sql.substring(index+1);
					index=sql.indexOf("?");
					sql=sql.substring(0, index)+sourceLimit+sql.substring(index+1);
					dataList=session.search(sql);
				}else{
					dataList=session.search(sql, i, sourceLimit);
				}
			}else if(dataSource.getDbType().equals("sql-server")){
				batchLogger.setSearchSql(sql+"["+(i+sourceLimit)+","+i+"]");
				index=sql.indexOf("?");
				sql=sql.substring(0, index)+(i+sourceLimit)+sql.substring(index+1);
				index=sql.indexOf("?");
				sql=sql.substring(0, index)+i+sql.substring(index+1);
				dataList=session.search(sql);
			}else{
				batchLogger.setSearchSql(sql+"["+(i+sourceLimit)+","+i+"]");
				if(dataSource.getType().equals("http-url")){
					index=sql.indexOf("?");
					sql=sql.substring(0, index)+(i+sourceLimit)+sql.substring(index+1);
					index=sql.indexOf("?");
					sql=sql.substring(0, index)+i+sql.substring(index+1);
					dataList=session.search(sql);
				}else{
					dataList=session.search(sql, i+sourceLimit, i);
				}
			}
			batchLogger.setTotalCount(dataList.size());
			String file="task-logger/"+transferTask.getCoding()+"/"+DateUtils.toString(new Date(),"yyyy-MM-dd")+"/search-data-"+batchLogger.getId()+".text";
			FileUtils.createFile(new File(SystemUtils.system("data-task-back")+file), true);
			FileUtils.write(SystemUtils.system("data-task-back")+file, JsonUtils.toJson(dataList), "UTF-8");
			batchLogger.setSearchData(file);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnswiftException(e.getMessage());
		} finally {
			if(ObjectUtils.isNotEmpty(session)){
				session.close();
			}
		}
		StringBuilder exeSqls=new StringBuilder();
		try {
			Long maxTime=null,tempTime;
			Timestamp timestamp;
			String valueSql;
			List<TaskFieldBind> fieldBindList=transferTask.getFieldBindList();
			List<String> sqlList=new ArrayList<String>();
			String pk=ObjectUtils.init(transferTask.getTargetPk(),"ID");
			pk=pk.toUpperCase();
			session=dataSourceService.createTransactionSession(targetDataSource);
			if(exeCount==1 && ObjectUtils.isNotEmpty(transferTask.getNotExistsCreate()) && transferTask.getNotExistsCreate().intValue()==1){
				sql=transferTask.getTargetTable();
				List<Map<String, String>> fields=session.searchFields("table", sql);
				if(fields.size()<=0){
					sql="CREATE TABLE "+transferTask.getTargetTable()+"(";
					for (TaskFieldBind field : fieldBindList) {
						sql+=field.getTargetField()+" "+javaToDbType(field, targetDataSource.getDbType())+",";
					}
					sql+=targetDataSource.getDbType().equals("oracle")?"CONSTRAINT PK_ID PRIMARY KEY("+pk+")":"PRIMARY KEY ("+pk+")";
					sql+=")";
					session.update(sql);
					exeSqls.append(sql).append(";").append(System.getProperty("line.separator"));
					taskLogger.setCreateSql(sql);
					batchLogger.setCreateSql(sql);
				}
			}
			if(exeCount==1 && transferTask.getTargetRule().equals("clear")){
				if(ObjectUtils.isEmpty(transferTask.getClearDataSql())){
					sql="DELETE FROM "+transferTask.getTargetTable();
				}else{
					sql=transferTask.getClearDataSql();
				}
				exeSqls.append(sql).append(";").append(System.getProperty("line.separator"));
				session.update(sql);
				batchLogger.setDeleteSql(sql);
				taskLogger.setDeleteSql(sql);
			}
			if(ObjectUtils.isNotEmpty(dataList)){
				List<Map<String, Object>> tempList;
				Map<String, Object> tempData;
				boolean insert,update;
				
				String sourceValue,targetValue,pkWhere;
				for (Map<String, Object> data : dataList) {
					if(ObjectUtils.isNotEmpty(transferTask.getLastUpdateField())){
						timestamp=(Timestamp)data.get(transferTask.getLastUpdateField().toUpperCase());
						if(ObjectUtils.isNotEmpty(timestamp)){
							if(ObjectUtils.isEmpty(maxTime)){
								maxTime=timestamp.getTime();
							}else{
								tempTime=timestamp.getTime();
								if(maxTime<tempTime){
									maxTime=tempTime;
								}
							}
						}
					}
					pkWhere=getPkWhere(pk, data, fieldBindList, dataSource.getDbType(), targetDataSource.getDbType());
					if(transferTask.getTargetRule().equals("clear")){
						insert=true;
					}else{
						insert=false;
					}
					if(transferTask.getTargetRule().equals("insert-or-update")){
						sql="SELECT * FROM "+transferTask.getTargetTable()+" WHERE "+pkWhere;
						exeSqls.append(sql).append(";").append(System.getProperty("line.separator"));
						tempList=session.search(sql);
						if(tempList.size()<=0){
							insert=true;
						}else if(tempList.size()>1){
							throw new RuntimeException("根据主键找到多条记录，SQL:"+sql);
						}else{
							tempData=tempList.get(0);
							sql="UPDATE "+transferTask.getTargetTable()+" SET ";
							update=false;
							for (TaskFieldBind taskFieldBind : fieldBindList) {
								if(taskFieldBind.getExeRule().equals("only-first")){
									continue;
								}
								if(taskFieldBind.getSourceField().startsWith("#")){
									sourceValue=valueToSql(taskFieldBind, taskFieldBind.getSourceField().substring(1), dataSource.getDbType(), targetDataSource.getDbType());
								}else{
									sourceValue=valueToSql(taskFieldBind, data.get(taskFieldBind.getSourceField().toUpperCase()), dataSource.getDbType(), targetDataSource.getDbType());
								}
								targetValue=valueToSql(taskFieldBind, tempData.get(taskFieldBind.getTargetField().toUpperCase()), dataSource.getDbType(), targetDataSource.getDbType());
								if(sourceValue.equals(targetValue)){
									continue;
								}
								sql+=taskFieldBind.getTargetField()+"="+sourceValue+",";
								update=true;
							}
							if(update){
								sql=sql.substring(0, sql.length()-1);
								sql+=" WHERE "+pkWhere;
								exeSqls.append(sql).append(";").append(System.getProperty("line.separator"));
								sqlList.add(sql);
								taskLogger.addUpdateCount();
								batchLogger.addUpdateCount();
							}
						}
					}
					if(transferTask.getTargetRule().equals("delete-insert") || insert){
						if(ObjectUtils.isNotEmpty(pkWhere) && !insert){
							sql="DELETE FROM "+transferTask.getTargetTable()+" WHERE "+pkWhere;
							session.update(sql);
							exeSqls.append(sql).append(";").append(System.getProperty("line.separator"));
							taskLogger.addDeleteCount();
							batchLogger.addDeleteCount();
						}
						sql="INSERT INTO "+transferTask.getTargetTable()+"(";
						valueSql=" VALUES(";
						for (TaskFieldBind taskFieldBind : fieldBindList) {
							sql+=taskFieldBind.getTargetField()+",";
							if(taskFieldBind.getSourceField().startsWith("#")){
								valueSql+=valueToSql(taskFieldBind, taskFieldBind.getSourceField().substring(1), dataSource.getDbType(), targetDataSource.getDbType())+",";
							}else{
								valueSql+=valueToSql(taskFieldBind, data.get(taskFieldBind.getSourceField().toUpperCase()), dataSource.getDbType(), targetDataSource.getDbType())+",";
							}
						}
						sql=sql.substring(0, sql.length()-1)+")"+valueSql.substring(1, valueSql.length()-1)+")";
						sqlList.add(sql);
						exeSqls.append(sql).append(";").append(System.getProperty("line.separator"));
						taskLogger.addInsertCount();
						batchLogger.addInsertCount();
					}
				}
			}
			if(sqlList.size()>0){
				logger.debug("批量执行sql："+sqlList);
				try {
					session.updateBatch(sqlList);
				} catch (Exception e) {
					if("Deadlock found when trying to get lock; try restarting transaction".equals(e.getMessage())){
						//如果报数据锁异常，则分开执行sql
						for (String exeSql : sqlList) {
							session.update(exeSql);
						}
					}else{
						throw new UnswiftException(e.getMessage());
					}
				}
			}
			String file="task-logger/"+transferTask.getCoding()+"/"+DateUtils.toString(new Date(),"yyyy-MM-dd")+"/exe-sql-"+batchLogger.getId()+".sql";
			FileUtils.createFile(new File(SystemUtils.system("data-task-back")+file), true);
			FileUtils.write(SystemUtils.system("data-task-back")+file, exeSqls.toString(), "UTF-8");
			batchLogger.setExeSql(file);
			session.commit();
			return maxTime;
		} catch (Exception e) {
			e.printStackTrace();
			if(ObjectUtils.isNotEmpty(session)){
				session.rollback();
			}
			throw new UnswiftException(e.getMessage());
		} finally {
			if(ObjectUtils.isNotEmpty(session)){
				session.close();
			}
		}
	}
	
	private String genericsSql(TransferTask transferTask, DataSource dataSource){
		String sql;
		List<TaskFieldBind> fieldBindList=transferTask.getFieldBindList();
		if(transferTask.getSourceType().equals("table")){
			sql="SELECT ";
			String lastUpdateField=transferTask.getLastUpdateField();
			if(ObjectUtils.isNotEmpty(lastUpdateField)){
				lastUpdateField=lastUpdateField.toUpperCase();
			}
			boolean setUpdateField=ObjectUtils.isEmpty(lastUpdateField)?true:false;
			for (TaskFieldBind taskFieldBind : fieldBindList) {
				if(!taskFieldBind.getSourceField().startsWith("#")){
					if(ObjectUtils.isNotEmpty(lastUpdateField) && taskFieldBind.getSourceField().equals(lastUpdateField)){
						setUpdateField=true;
					}
					sql+=taskFieldBind.getSourceField()+',';
				}
			}
			if(!setUpdateField){
				sql+=lastUpdateField+',';
			}
			sql=sql.substring(0, sql.length()-1);
			sql+=" FROM "+transferTask.getSourceTable()+" WHERE 1=1 "+(ObjectUtils.isEmpty(transferTask.getSourceSql())?"":"AND "+transferTask.getSourceSql());
		}else{
			sql=transferTask.getSourceSql();
		}
		if(!transferTask.isFullExe() && transferTask.getExeMethod().equals("increment") && !transferTask.getTargetRule().equals("clear")){
			if(ObjectUtils.isNotEmpty(transferTask.getMaxValue())){
				sql+=" AND "+transferTask.getLastUpdateField()+">=";
				String date=DateUtils.toString(transferTask.getMaxValue(), "yyyy-MM-dd HH:mm:ss.SSS");
				sql+=(dataSource.getDbType().equals("oracle")?"TO_TIMESTAMP('"+date+"','YYYY-MM-DD HH24:MI:SS.FF3')":"'"+date+"'");
			}
		}
		return sql;
	}

	private long findCount(DataSource dataSource, String sql){
		Session session=null;
		try {
			session=dataSourceService.createSession(dataSource);
			sql="SELECT COUNT(1) FROM ("+sql+") T";
			logger.info("数据转移sql查询总量："+sql);
			return session.searchCount(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnswiftException(e.getMessage());
		} finally {
			if(ObjectUtils.isNotEmpty(session)){
				session.close();
			}
		}
	}
	
	private String appendPageSql(DataSource dataSource, String sql, String pk){
		if(dataSource.getDbType().equals("oracle")){
			sql="SELECT * FROM (SELECT A.*,ROWNUM RN FROM ("+sql+") A WHERE ROWNUM<=?) WHERE RN>?";
		}else if(dataSource.getDbType().equals("mysql")){
			sql=sql+" LIMIT ?,?";
		}else if(dataSource.getDbType().equals("sql-server")){
			sql="SELECT W2.N,W1.* FROM ("+sql+") W1,(SELECT TOP ? ROW_NUMBER() OVER (ORDER BY "+pk+" DESC) N, "+pk+" FROM ("+sql+") W2) W2 WHERE ";
			String[] pkArray=pk.split("\\,");
			int i=0;
			for (String item : pkArray) {
				if(i>0){
					sql+=" AND ";
				}
				sql+="W1."+item+"=W2."+item;
				i++;
			}
			sql+=" AND W2.N>? ORDER BY W2.N ASC";
		}
		return sql;
	}
	
	private String getPkWhere(String pk, Map<String, Object> data, List<TaskFieldBind> fieldDefineList, String sourceDbType, String dbType){
		String[] pkArray=pk.split(",");
		Object itemValue;
		String where="";
		TaskFieldBind fieldDefine;
		int i=0,length=pkArray.length;
		for (String item : pkArray) {
			item=item.trim();
			itemValue=data.get(item.toUpperCase());
			if(ObjectUtils.isEmpty(itemValue)){
				where+=item+" IS NULL";
			}else{
				fieldDefine=getFieldDefine(item, fieldDefineList);
				where+=item+"="+valueToSql(fieldDefine, itemValue, sourceDbType, dbType);
			}
			
			if(i!=length-1){
				where+=" AND ";
			}
			i++;
		}
		return where;
	}
	
	private TaskFieldBind getFieldDefine(String field, List<TaskFieldBind> fieldDefineList){
		for (TaskFieldBind taskFieldBind : fieldDefineList) {
			if(taskFieldBind.getTargetField().equalsIgnoreCase(field)){
				return taskFieldBind;
			}
		}
		return null;
	}
	
	private String valueToSql(TaskFieldBind taskFieldBind, Object value, String sourceDbType, String dbType){
		if(ObjectUtils.isEmpty(value)){
			return "NULL";
		}
		String sqlValue,sqlFormat=null;
		boolean timestamp=false;
		if(taskFieldBind.getSourceType().equals("datetime")){
			sqlFormat="YYYY-MM-DD HH24:MI:SS.FF3";
			timestamp=true;
			sqlValue="'"+DateUtils.toString(value, "yyyy-MM-dd HH:mm:ss.SSS")+"'";
		}else if(taskFieldBind.getSourceType().equals("date")){
			sqlFormat=sourceDbType.equals("oracle")?"YYYY-MM-DD HH24:MI:SS":"YYYY-MM-DD";
			sqlValue="'"+(value instanceof String?value:DateUtils.toString(value, sourceDbType.equals("oracle")?"yyyy-MM-dd HH:mm:ss":"yyyy-MM-dd"))+"'";
		}else if(taskFieldBind.getSourceType().equals("time")){
			sqlFormat="HH24:MI:SS";
			sqlValue="'"+DateUtils.toString(value, "HH:mm:ss")+"'";
		}else if(taskFieldBind.getSourceType().equals("string") || taskFieldBind.getSourceType().equals("text")){
			if(ObjectUtils.isNotEmpty(value) && value.toString().indexOf("\\")!=-1){
				value=value.toString().replace("\\", "\\\\");
			}
			if(ObjectUtils.isNotEmpty(value) && value.toString().indexOf("'")!=-1){
				value=value.toString().replace("'", "\\'");
			}
			sqlValue="'"+value.toString()+"'";
		}else{
			sqlValue=value.toString();
		}
		if(dbType.equals("oracle")){
			if(ObjectUtils.isNotEmpty(sqlFormat)){
				sqlValue=(timestamp?"TO_TIMESTAMP":"TO_DATE")+"("+sqlValue+",'"+sqlFormat+"')";
			}
		}
		return sqlValue;
	}
	
	public String javaToDbType(TaskFieldBind field, String type){
		if(field.getTargetType().equals("int")){
			return type.equals("oracle")?"NUMBER("+ObjectUtils.init(field.getDataLength(), 8)+")":"INTEGER("+ObjectUtils.init(field.getDataLength(), 8)+")";
		}else if(field.getTargetType().equals("long")){
			return type.equals("oracle")?"NUMBER("+ObjectUtils.init(field.getDataLength(), 12)+")":"BIGINT("+ObjectUtils.init(field.getDataLength(), 12)+")";
		}else if(field.getTargetType().equals("double")){
			return type.equals("oracle")?"NUMBER("+ObjectUtils.init(field.getDataLength(), 8)+","+ObjectUtils.init(field.getDataScale(), 2)+")":
				"DOUBLE("+ObjectUtils.init(field.getDataLength(), 8)+","+ObjectUtils.init(field.getDataScale(), 2)+")";
		}else if(field.getTargetType().equals("decimal")){
			return type.equals("oracle")?"NUMBER("+ObjectUtils.init(field.getDataLength(), 12)+","+ObjectUtils.init(field.getDataScale(), 2)+")":
				"DECIMAL("+ObjectUtils.init(field.getDataLength(), 12)+","+ObjectUtils.init(field.getDataScale(), 2)+")";
		}else if(field.getTargetType().equals("string")){
			return type.equals("oracle")?"VARCHAR2("+ObjectUtils.init(field.getDataLength(), 255)+")":"VARCHAR("+ObjectUtils.init(field.getDataLength(), 255)+")";
		}else if(field.getTargetType().equals("text")){
			return type.equals("oracle")?"CLOB":"BLOB";
		}else if(field.getTargetType().equals("time")){
			return type.equals("oracle")?"DATE":"TIME";
		}else if(field.getTargetType().equals("date")){
			return type.equals("oracle")?"DATE":"DATETIME";
		}else if(field.getTargetType().equals("datetime")){
			return type.equals("oracle")?"TIMESTAMP":"TIMESTAMP";
		}
		return type.equals("oracle")?"VARCHAR2("+ObjectUtils.init(field.getDataLength(), 255)+")":"VARCHAR("+ObjectUtils.init(field.getDataLength(), 255)+")";
	}
	
	public void isFullExe(TransferTask transferTask){
		if(transferTask.getExeMethod().equals("increment") && ObjectUtils.isNotEmpty(transferTask.getFullExePeriod())){
			Date endTime=transferTask.getLastFullExeTime();
			if(ObjectUtils.isEmpty(endTime)){
				transferTask.setFullExe(true);
				logger.error("任务："+transferTask.getName()+":"+transferTask.isFullExe());
				return ;
			}
			long end=endTime.getTime();
			long time=System.currentTimeMillis();
			long period=NumberUtils.calc(transferTask.getFullExePeriod()).longValue()*60*1000;
			if(time-end>period){
				transferTask.setFullExe(true);
			}else{
				transferTask.setFullExe(false);
			}
		}else{
			transferTask.setFullExe(false);
		}
		logger.error("任务："+transferTask.getName()+":"+transferTask.isFullExe());
	}
}
