package org.unswift.gtft.transfer.dao;

import java.util.List;
import org.unswift.gtft.transfer.pojo.TaskLogger;
import org.apache.ibatis.annotations.Param;
import org.unswift.core.pojo.Page;

import org.unswift.core.pojo.DeletePojo;
import org.unswift.core.pojo.OrderNoMovePojo;
import org.unswift.core.pojo.StatusPojo;

public interface ITaskLoggerMapper {
	
	List<TaskLogger> findPageList(@Param("page")Page page, @Param("vo")TaskLogger taskLogger);
	void insert(TaskLogger taskLogger);
	TaskLogger findById(@Param("id")String id);
	void updateById(TaskLogger taskLogger);
	void deleteById(TaskLogger taskLogger);
	List<TaskLogger> findList(TaskLogger taskLogger);
	
	TaskLogger findBefore(@Param("orderNo")Integer orderNo);
	TaskLogger findAfter(@Param("orderNo")Integer orderNo);
	int findMax(TaskLogger taskLogger);
	int findMaxExeBatch(@Param("taskId")String taskId);
	void updateOrderNo(OrderNoMovePojo orderNo);
	void deleteTaskLoggers(DeletePojo deletePojo);
	void deleteRecycles(@Param("ids")String[] ids);
	void clearRecycle();
	void updateStatus(StatusPojo statusPojo);
}
