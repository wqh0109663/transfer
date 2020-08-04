package org.unswift.gtft.transfer.dao;

import java.util.List;
import org.unswift.gtft.transfer.pojo.TaskFieldBind;
import org.apache.ibatis.annotations.Param;

public interface ITaskFieldBindMapper {
	
	void insert(TaskFieldBind taskFieldBind);
	void deleteByTaskId(@Param("taskId")String taskId);
	List<TaskFieldBind> findList(@Param("vo")TaskFieldBind taskFieldBind);
	
}
