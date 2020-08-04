package org.unswift.gtft.transfer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.unswift.gtft.transfer.pojo.DataSource;
import org.unswift.core.conn.Session;
import org.unswift.core.pojo.Page;


/**
 * 数据源Service(封装各种业务操作)
 * @author Administrator
 *
 */
@Service
public interface IDataSourceService{

	Page findPageList(Page page, DataSource dataSource);
	void saveDataSource(DataSource dataSource);
	DataSource findById(String id);
	void updateDataSource(DataSource dataSource);
	void deleteDataSource(String id);
	void moveDataSource(String id, String operator);
	void deleteDataSources(String[] ids);
	void deleteRecycles(String[] ids);
	void clearRecycle();
	void recoveryRecycles(String[] id);
	
	List<DataSource> findAllList(String type);
	
	void connDataSource(DataSource dataSource);
	
	Session createSession(DataSource dataSource);
	Session createTransactionSession(DataSource dataSource);
}
