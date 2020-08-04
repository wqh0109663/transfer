package org.unswift.gtft.transfer.pojo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.unswift.core.utils.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;


/**
 * 任务批次日志实体
 * @author Administrator
 *
 */
public class TaskLoggerBatch implements Serializable{

	private static final long serialVersionUID = -1039106269098475095L;
	
	private String id;	//主键
	private String loggerId;	//所属日志
	private Integer exeBatch;	//执行批次
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;	//开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;	//结束时间
	private String searchData;	//查询数据
	private String exeSql;	//导入执行sql
	private Integer result;	//结果
	private String msg;	//消息
	private String searchSql;//查询数据sql
	private String deleteSql;	//清空表语句，空则没有做清空操作
	private Integer totalCount;	//执行总数
	private Integer insertCount;	//插入总数
	private Integer updateCount;	//更新总数
	private Integer deleteCount;	//删除总数
	private String createSql;	//是否创建表

	public TaskLoggerBatch(){
	
	}
	public TaskLoggerBatch(String loggerId, int exeBatch){
		this.loggerId=loggerId;
		this.exeBatch=exeBatch;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}

	public void setLoggerId(String loggerId){
		this.loggerId=loggerId;
	}
	public String getLoggerId(){
		return this.loggerId;
	}

	public void setExeBatch(Integer exeBatch){
		this.exeBatch=exeBatch;
	}
	public Integer getExeBatch(){
		return this.exeBatch;
	}

	public void setStartTime(Date startTime){
		this.startTime=startTime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getStartTime(){
		return this.startTime;
	}

	public void setEndTime(Date endTime){
		this.endTime=endTime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getEndTime(){
		return this.endTime;
	}

	public void setSearchData(String searchData){
		this.searchData=searchData;
	}
	public String getSearchData(){
		return this.searchData;
	}

	public void setExeSql(String exeSql){
		this.exeSql=exeSql;
	}
	public String getExeSql(){
		return this.exeSql;
	}
	
	public void addExeSql(String sql){
		if(ObjectUtils.isEmpty(this.exeSql)){
			this.exeSql=sql;
		}else{
			this.exeSql+=";"+System.getProperty("line.separator")+sql;
		}
	}

	public void setResult(Integer result){
		this.result=result;
	}
	public Integer getResult(){
		return this.result;
	}

	public void setMsg(String msg){
		this.msg=msg;
	}
	public String getMsg(){
		return this.msg;
	}
	public String getSearchSql() {
		return searchSql;
	}
	public void setSearchSql(String searchSql) {
		this.searchSql = searchSql;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getInsertCount() {
		return insertCount;
	}
	public void setInsertCount(Integer insertCount) {
		this.insertCount = insertCount;
	}
	public void addInsertCount() {
		if(ObjectUtils.isNull(this.insertCount)){
			this.insertCount=1;
		}else{
			this.insertCount++;
		}
	}
	public Integer getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(Integer updateCount) {
		this.updateCount = updateCount;
	}
	public void addUpdateCount() {
		if(ObjectUtils.isNull(this.updateCount)){
			this.updateCount=1;
		}else{
			this.updateCount++;
		}
	}
	public Integer getDeleteCount() {
		return deleteCount;
	}
	public void setDeleteCount(Integer deleteCount) {
		this.deleteCount = deleteCount;
	}
	public void addDeleteCount() {
		if(ObjectUtils.isNull(this.deleteCount)){
			this.deleteCount=1;
		}else{
			this.deleteCount++;
		}
	}
	public String getCreateSql() {
		return createSql;
	}
	public void setCreateSql(String createSql) {
		this.createSql = createSql;
	}
	public String getDeleteSql() {
		return deleteSql;
	}
	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}

}
