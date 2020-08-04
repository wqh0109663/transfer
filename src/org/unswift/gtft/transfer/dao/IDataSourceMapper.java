package org.unswift.gtft.transfer.dao;

import java.util.List;
import org.unswift.gtft.transfer.pojo.DataSource;
import org.apache.ibatis.annotations.Param;
import org.unswift.core.pojo.Page;

import org.unswift.core.pojo.DeletePojo;
import org.unswift.core.pojo.OrderNoMovePojo;
import org.unswift.core.pojo.StatusPojo;

public interface IDataSourceMapper {
	
	List<DataSource> findPageList(@Param("page")Page page, @Param("vo")DataSource dataSource);
	void insert(DataSource dataSource);
	DataSource findById(@Param("id")String id);
	void updateById(DataSource dataSource);
	void deleteById(DataSource dataSource);
	List<DataSource> findList(@Param("vo")DataSource dataSource);
	
	DataSource findBefore(@Param("orderNo")Integer orderNo);
	DataSource findAfter(@Param("orderNo")Integer orderNo);
	int findMax(DataSource dataSource);
	void updateOrderNo(OrderNoMovePojo orderNo);
	void deleteDataSources(DeletePojo deletePojo);
	void deleteRecycles(@Param("ids")String[] ids);
	void clearRecycle();
	void updateStatus(StatusPojo statusPojo);
}
