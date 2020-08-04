package org.unswift.gtft.transfer.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unswift.core.utils.ObjectUtils;
import org.unswift.core.utils.StringUtils;
import org.unswift.gtft.transfer.pojo.DataSource;
import org.unswift.core.pojo.Page;
import org.unswift.gtft.transfer.service.IDataSourceService;
import org.unswift.gtft.transfer.dao.IDataSourceMapper;
import org.unswift.core.annotations.Cache;
import org.unswift.core.annotations.CacheClear;
import org.unswift.core.conn.Session;
import org.unswift.core.conn.SessionFactory;
import org.unswift.core.exception.UnswiftException;
import org.unswift.core.pojo.DeletePojo;
import org.unswift.core.pojo.OrderNoMovePojo;
import org.unswift.core.pojo.StatusPojo;

/**
 * 数据源Service(封装各种业务操作)
 * @author Administrator
 *
 */
@Service("dataSourceService")
@Transactional(propagation=Propagation.NOT_SUPPORTED, rollbackFor=Exception.class) 
public class DataSourceService implements IDataSourceService{

	@Resource
    private IDataSourceMapper dataSourceMapper;

	@Override
	public Page findPageList(Page page, DataSource dataSource){
		List<DataSource> dataSourceList=dataSourceMapper.findPageList(page, dataSource);
		page.setData(dataSourceList);
		return page;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	@CacheClear("findDataSourceList")//清空缓存，allEntries变量表示所有对象的缓存都清除
	public void saveDataSource(DataSource dataSource){
		dataSource.setId(StringUtils.uuid());
		dataSource.setOrderNo(dataSourceMapper.findMax(null)+1);
		dataSource.setStatus(1);
		dataSourceMapper.insert(dataSource);
	}
	@Override
	public DataSource findById(String id) {
		return dataSourceMapper.findById(id);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	@CacheClear("findDataSourceList")//清空缓存，allEntries变量表示所有对象的缓存都清除
	public void updateDataSource(DataSource dataSource){
		dataSourceMapper.updateById(dataSource);
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	@CacheClear("findDataSourceList")//清空缓存，allEntries变量表示所有对象的缓存都清除
	public void deleteDataSource(String id){
		dataSourceMapper.deleteById(new DataSource(id));
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	@CacheClear("findDataSourceList")//清空缓存，allEntries变量表示所有对象的缓存都清除
	public void moveDataSource(String id, String operator) {
		DataSource dataSource=dataSourceMapper.findById(id);
		int orderNo=dataSource.getOrderNo();
		if(operator.equals("down")){
			DataSource after=dataSourceMapper.findAfter(orderNo);
			if(after!=null){
				dataSourceMapper.updateOrderNo(new OrderNoMovePojo(dataSource.getId(), after.getOrderNo(), after.getId(), orderNo));
			}
		}else{
			DataSource before=dataSourceMapper.findBefore(orderNo);
			if(before!=null){
				dataSourceMapper.updateOrderNo(new OrderNoMovePojo(dataSource.getId(), before.getOrderNo(), before.getId(), orderNo));
			}
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	@CacheClear("findDataSourceList")//清空缓存，allEntries变量表示所有对象的缓存都清除
	public void deleteDataSources(String[] ids) {
		dataSourceMapper.deleteDataSources(new DeletePojo(ids));
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void deleteRecycles(String[] ids) {
		dataSourceMapper.deleteRecycles(ids);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void clearRecycle() {
		dataSourceMapper.clearRecycle();
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED) 
	public void recoveryRecycles(String[] ids) {
		dataSourceMapper.updateStatus(new StatusPojo(ids, 1));
	}
	
	@Override
	@Cache("findDataSourceList")
	public List<DataSource> findAllList(String type) {
		DataSource task=new DataSource();
		task.setStatus(1);
		return dataSourceMapper.findList(task);
	}
	
	@Override
	public void connDataSource(DataSource dataSource) {
		Session session=null;
		try {
			session=createSession(dataSource);
			session.test();
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
	public Session createSession(DataSource dataSource) {
		if(dataSource.getType().equals("db-conn")){
			return SessionFactory.newJdbcSession(dataSource.getDbType(), dataSource.getDbDriver(), 
					dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
		}else{
			return SessionFactory.newHttpSession(dataSource.getDbType(), dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
		}
	}
	
	@Override
	public Session createTransactionSession(DataSource dataSource) {
		if(dataSource.getType().equals("db-conn")){
			return SessionFactory.newJdbcTransactionSession(dataSource.getDbType(), dataSource.getDbDriver(), 
					dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
		}else{
			return SessionFactory.newHttpSession(dataSource.getDbType(), dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
		}
	}
}
