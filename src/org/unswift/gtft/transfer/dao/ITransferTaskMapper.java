package org.unswift.gtft.transfer.dao;

import java.util.List;
import org.unswift.gtft.transfer.pojo.TransferTask;
import org.apache.ibatis.annotations.Param;
import org.unswift.core.pojo.Page;
import org.unswift.core.pojo.DeletePojo;
import org.unswift.core.pojo.OrderNoMovePojo;
import org.unswift.core.pojo.StatusPojo;

public interface ITransferTaskMapper {
	
	List<TransferTask> findPageList(@Param("page")Page page, @Param("vo")TransferTask transferTask);
	void insert(TransferTask transferTask);
	TransferTask findById(@Param("id")String id);
	TransferTask findByCoding(@Param("coding")String coding);
	void updateById(TransferTask transferTask);
	void updateMaxTime(TransferTask transferTask);
	void deleteById(TransferTask transferTask);
	List<TransferTask> findList(TransferTask transferTask);
	
	TransferTask findBefore(@Param("orderNo")Integer orderNo);
	TransferTask findAfter(@Param("orderNo")Integer orderNo);
	int findMax(TransferTask transferTask);
	int findCount(@Param("vo")TransferTask transferTask);
	void updateOrderNo(OrderNoMovePojo orderNo);
	void deleteTransferTasks(DeletePojo deletePojo);
	void deleteRecycles(@Param("ids")String[] ids);
	void clearRecycle();
	void updateStatus(StatusPojo statusPojo);
}
