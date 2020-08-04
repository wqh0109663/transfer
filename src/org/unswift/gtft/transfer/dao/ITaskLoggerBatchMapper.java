package org.unswift.gtft.transfer.dao;

import java.util.List;
import org.unswift.gtft.transfer.pojo.TaskLoggerBatch;
import org.apache.ibatis.annotations.Param;
import org.unswift.core.pojo.Page;

import org.unswift.core.pojo.DeletePojo;
import org.unswift.core.pojo.OrderNoMovePojo;
import org.unswift.core.pojo.StatusPojo;

public interface ITaskLoggerBatchMapper {
	
	List<TaskLoggerBatch> findPageList(@Param("page")Page page, @Param("vo")TaskLoggerBatch taskLoggerBatch);
	void insert(TaskLoggerBatch taskLoggerBatch);
	TaskLoggerBatch findById(@Param("id")String id);
	void updateById(TaskLoggerBatch taskLoggerBatch);
	void deleteById(TaskLoggerBatch taskLoggerBatch);
	List<TaskLoggerBatch> findList(TaskLoggerBatch taskLoggerBatch);
	
	TaskLoggerBatch findBefore(@Param("orderNo")Integer orderNo);
	TaskLoggerBatch findAfter(@Param("orderNo")Integer orderNo);
	int findMax(TaskLoggerBatch taskLoggerBatch);
	void updateOrderNo(OrderNoMovePojo orderNo);
	void deleteTaskLoggerBatchs(DeletePojo deletePojo);
	void deleteRecycles(@Param("ids")String[] ids);
	void clearRecycle();
	void updateStatus(StatusPojo statusPojo);
}
