package org.unswift.gtft.transfer.pojo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.unswift.core.utils.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;


/**
 * 任务日志实体
 * @author Administrator
 *
 */
public class TaskLogger implements Serializable{

	private static final long serialVersionUID = -1039106269098475095L;
	
	private String id;	//主键
	private String taskId;	//所属任务
	private Integer exeBatch;	//执行批次
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;	//开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;	//结束时间
	private Integer exeResult;	//执行结果
	private String exeMsg;	//执行消息
	private String searchSql;//查询数据sql
	private String deleteSql;	//清空表语句，空则没有做清空操作
	private String createSql;	//是否创建表
	private Long totalCount;	//执行总数
	private Long insertCount;	//插入总数
	private Long updateCount;	//更新总数
	private Long deleteCount;	//删除总数

	public TaskLogger(){
	
	}
	public TaskLogger(String taskId){
		this.taskId=taskId;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}

	public void setTaskId(String taskId){
		this.taskId=taskId;
	}
	public String getTaskId(){
		return this.taskId;
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

	public void setExeResult(Integer exeResult){
		this.exeResult=exeResult;
	}
	public Integer getExeResult(){
		return this.exeResult;
	}

	public void setExeMsg(String exeMsg){
		this.exeMsg=exeMsg;
	}
	public String getExeMsg(){
		return this.exeMsg;
	}
	public Long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	public Long getInsertCount() {
		return insertCount;
	}
	public void setInsertCount(Long insertCount) {
		this.insertCount = insertCount;
	}
	public void addInsertCount() {
		if(ObjectUtils.isNull(this.insertCount)){
			this.insertCount=1L;
		}else{
			this.insertCount++;
		}
	}
	public Long getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(Long updateCount) {
		this.updateCount = updateCount;
	}
	public void addUpdateCount() {
		if(ObjectUtils.isNull(this.updateCount)){
			this.updateCount=1L;
		}else{
			this.updateCount++;
		}
	}
	public Long getDeleteCount() {
		return deleteCount;
	}
	public void setDeleteCount(Long deleteCount) {
		this.deleteCount = deleteCount;
	}
	public void addDeleteCount() {
		if(ObjectUtils.isNull(this.deleteCount)){
			this.deleteCount=1L;
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
	public String getSearchSql() {
		return searchSql;
	}
	public void setSearchSql(String searchSql) {
		this.searchSql = searchSql;
	}
	public String getDeleteSql() {
		return deleteSql;
	}
	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}
}
