package org.unswift.gtft.transfer.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.unswift.gtft.transfer.pojo.TaskLogger;
import org.unswift.gtft.transfer.pojo.TaskLoggerBatch;
import org.unswift.gtft.transfer.pojo.TransferTask;
import org.unswift.core.pojo.Page;


/**
 * 数据抽取任务Service(封装各种业务操作)
 * @author Administrator
 *
 */
@Service
public interface ITransferTaskService{

	Page findPageList(Page page, TransferTask transferTask);
	void saveTransferTask(TransferTask transferTask);
	TransferTask findById(String id);
	TransferTask findByCoding(String coding);
	void updateTransferTask(TransferTask transferTask);
	void updateMaxTime(TransferTask transferTask);
	void deleteTransferTask(String id);
	void moveTransferTask(String id, String operator);
	void deleteTransferTasks(String[] ids);
	void deleteRecycles(String[] ids);
	void clearRecycle();
	void recoveryRecycles(String[] id);
	void testTask(TransferTask transferTask);
	List<Map<String, String>> getSourceFieldList(String sourceId, String type, String tableNameOrsql);
	
	void issueTransferTask(String id, String operator);
	
	void createLogger(TaskLogger logger);
	void finishLogger(TaskLogger logger);
	void createLoggerBatch(TaskLoggerBatch batchLogger);
	void finishLoggerBatch(TaskLoggerBatch batchLogger);
	
	Page findLoggerPageList(Page page, TaskLogger logger);
	Page findLoggerBatchPageList(Page page, TaskLoggerBatch batch);
}
