package org.unswift.gtft.transfer.pojo;

import org.unswift.core.pojo.LogPojo;


/**
 * 任务字段绑定实体
 * @author Administrator
 *
 */
public class TaskFieldBind extends LogPojo{

	private static final long serialVersionUID = -1039106269098475095L;
	
	private String id;	//主键
	private String taskId;	//所属任务
	private String sourceField;	//来源字段
	private String sourceType;	//来源类型
	private String targetField;	//目标字段
	private String targetType;	//目标类型
	private String dataLength;	//数据长度
	private String dataScale;	//数据长度
	private String exeRule;	//执行规则
	private Integer orderNo;	//序号

	public TaskFieldBind(){
	
	}
	public TaskFieldBind(String taskId){
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

	public void setSourceField(String sourceField){
		this.sourceField=sourceField;
	}
	public String getSourceField(){
		return this.sourceField;
	}

	public void setSourceType(String sourceType){
		this.sourceType=sourceType;
	}
	public String getSourceType(){
		return this.sourceType;
	}

	public void setTargetField(String targetField){
		this.targetField=targetField;
	}
	public String getTargetField(){
		return this.targetField;
	}

	public void setTargetType(String targetType){
		this.targetType=targetType;
	}
	public String getTargetType(){
		return this.targetType;
	}
	public String getExeRule() {
		return exeRule;
	}
	public void setExeRule(String exeRule) {
		this.exeRule = exeRule;
	}
	public void setOrderNo(Integer orderNo){
		this.orderNo=orderNo;
	}
	public Integer getOrderNo(){
		return this.orderNo;
	}
	public String getDataLength() {
		return dataLength;
	}
	public void setDataLength(String dataLength) {
		this.dataLength = dataLength;
	}
	public String getDataScale() {
		return dataScale;
	}
	public void setDataScale(String dataScale) {
		this.dataScale = dataScale;
	}


}
