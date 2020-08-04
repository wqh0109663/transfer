package org.unswift.gtft.transfer.pojo;

import java.util.Date;
import java.util.List;

import org.unswift.base.system.service.IParameterService;
import org.unswift.base.system.service.impl.PageBean;
import org.unswift.core.pojo.LogPojo;
import org.unswift.core.utils.NumberUtils;
import org.unswift.core.utils.ObjectUtils;
import org.unswift.core.utils.SpringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 数据抽取任务实体
 * @author Administrator
 *
 */
public class TransferTask extends LogPojo{

	private static final long serialVersionUID = -1039106269098475095L;
	
	private String id;	//主键
	private String coding;	//编号
	private String name;	//名称
	private String exePeriod;	//执行速率，-1：为自定义执行
	private String exePeriodName;	//执行速率，-1：为自定义执行
	private String exePeriodSub;	//执行速率，-1：为自定义执行
	private String fullExePeriod;	//全量知悉速率，如果当前任务为增量抽取任务，则可以配置全量执行速率
	private boolean isFullExe;
	private String sourceId;	//数据来源
	private String sourceName;	//数据来源
	private String sourceType;	//数据来源方式，table：表，sql：sql语句
	private String sourceSql;	//数据源SQL
	private String sourceTable;	//来源表
	private String sourcePk;	//来源表主键
	private Integer sourceLimit;	//数据源每次抓取数量
	private String targetId;	//数据目标
	private String targetName;	//数据目标
	private String targetTable;	//目标表
	private String targetPk;	//目标表主键
	private Integer targetLimit;	//目标源每次更新数量
	private String targetRule;	//目标规则
	private Integer notExistsCreate;	//表不存在时创建表
	private String exeMethod;	//执行方式，full：全量，increment：增量
	private String clearDataSql;	//全量任务时清理数据的SQL，空时全部清理
	private String lastUpdateField;	//最后更新时间字段名称
	private Date lastFullExeTime;	//最后全量执行时间
	private Date maxValue;	//更新的最大值
	private Integer exeStatus;//执行状态
	private Integer exeProgress;//执行进度
	private Integer exeResult;//最后一次执行结果
	private Date exeLastTime;	//最后一次执行开始时间
	private Date exeLastEndTime; //最后一次执行结束时间
	private String exeTime;		//最后一次所花时间
	private Integer exeNumberd;
	private Integer orderNo;	//序号
	private Integer maxOrderNo;//在当前父节点下的最大排序（非数据库字段）
	private Integer minOrderNo;//在当前父节点下的最小排序（非数据库字段）
	private String noId;//非id查询
	private String fieldBindJson; //绑定字段集合
	private Integer finishHint;
	private String searchStatus;
	private List<TaskFieldBind> fieldBindList;
	
	public TransferTask(){
	
	}
	public TransferTask(String id){
		this.id=id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}

	public void setCoding(String coding){
		this.coding=coding;
	}
	public String getCoding(){
		return this.coding;
	}

	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}

	public String getExePeriod() {
		if(ObjectUtils.isNotEmpty(exePeriodSub)){
			exePeriod+=','+exePeriodSub;
		}
		return exePeriod;
	}
	public void setExePeriod(String exePeriod) {
		this.exePeriod = exePeriod;
	}
	public String getExePeriodName() {
		if(ObjectUtils.isEmpty(exePeriodName) && ObjectUtils.isNotEmpty(exePeriod) && exePeriod.indexOf(",")==-1){
			PageBean pageBean=SpringUtils.getBean("pageBean");
			exePeriodName=pageBean.getDictionaryName("GTFT_TASK_PERIOD", exePeriod);
		}
		return exePeriodName;
	}
	public String getFullExePeriod() {
		return fullExePeriod;
	}
	public void setFullExePeriod(String fullExePeriod) {
		this.fullExePeriod = fullExePeriod;
	}
	public void setSourceId(String sourceId){
		this.sourceId=sourceId;
	}
	public String getSourceId(){
		return this.sourceId;
	}

	public void setSourceType(String sourceType){
		this.sourceType=sourceType;
	}
	public String getSourceType(){
		return this.sourceType;
	}

	public void setSourceSql(String sourceSql){
		this.sourceSql=sourceSql;
	}
	public String getSourceSql(){
		return this.sourceSql;
	}

	public void setSourceTable(String sourceTable){
		this.sourceTable=sourceTable;
	}
	public String getSourceTable(){
		return this.sourceTable;
	}

	public void setSourcePk(String sourcePk){
		this.sourcePk=sourcePk;
	}
	public String getSourcePk(){
		return this.sourcePk;
	}

	public void setSourceLimit(Integer sourceLimit){
		this.sourceLimit=sourceLimit;
	}
	public Integer getSourceLimit(){
		return this.sourceLimit;
	}

	public void setTargetId(String targetId){
		this.targetId=targetId;
	}
	public String getTargetId(){
		return this.targetId;
	}

	public void setTargetTable(String targetTable){
		this.targetTable=targetTable;
	}
	public String getTargetTable(){
		return this.targetTable;
	}

	public void setTargetPk(String targetPk){
		this.targetPk=targetPk;
	}
	public String getTargetPk(){
		return this.targetPk;
	}

	public void setTargetLimit(Integer targetLimit){
		this.targetLimit=targetLimit;
	}
	public Integer getTargetLimit(){
		return this.targetLimit;
	}

	public void setExeMethod(String exeMethod){
		this.exeMethod=exeMethod;
	}
	public String getExeMethod(){
		return this.exeMethod;
	}

	public void setClearDataSql(String clearDataSql){
		this.clearDataSql=clearDataSql;
	}
	public String getClearDataSql(){
		return this.clearDataSql;
	}

	public void setLastUpdateField(String lastUpdateField){
		this.lastUpdateField=lastUpdateField;
	}
	public String getLastUpdateField(){
		return this.lastUpdateField;
	}

	public void setMaxValue(Date maxValue){
		this.maxValue=maxValue;
	}
	public Date getMaxValue(){
		return this.maxValue;
	}

	public void setOrderNo(Integer orderNo){
		this.orderNo=orderNo;
	}
	public Integer getOrderNo(){
		return this.orderNo;
	}
	public Integer getMaxOrderNo() {
		return maxOrderNo;
	}
	public void setMaxOrderNo(Integer maxOrderNo) {
		this.maxOrderNo = maxOrderNo;
	}
	public Integer getMinOrderNo() {
		return minOrderNo;
	}
	public void setMinOrderNo(Integer minOrderNo) {
		this.minOrderNo = minOrderNo;
	}
	public String getNoId() {
		return noId;
	}
	public void setNoId(String noId) {
		this.noId = noId;
	}
	public String getFieldBindJson() {
		return fieldBindJson;
	}
	public void setFieldBindJson(String fieldBindJson) {
		this.fieldBindJson = fieldBindJson;
	}
	public String getExePeriodSub() {
		return exePeriodSub;
	}
	public void setExePeriodSub(String exePeriodSub) {
		this.exePeriodSub = exePeriodSub;
	}
	public String getSearchStatus() {
		return searchStatus;
	}
	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}
	public List<TaskFieldBind> getFieldBindList() {
		return fieldBindList;
	}
	public void setFieldBindList(List<TaskFieldBind> fieldBindList) {
		this.fieldBindList = fieldBindList;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public String getTargetRule() {
		return targetRule;
	}
	public void setTargetRule(String targetRule) {
		this.targetRule = targetRule;
	}
	public Integer getNotExistsCreate() {
		return notExistsCreate;
	}
	public void setNotExistsCreate(Integer notExistsCreate) {
		this.notExistsCreate = notExistsCreate;
	}
	public Integer getExeStatus() {
		return exeStatus;
	}
	public void setExeStatus(Integer exeStatus) {
		this.exeStatus = exeStatus;
	}
	public Integer getExeProgress() {
		return exeProgress;
	}
	public void setExeProgress(Integer exeProgress) {
		this.exeProgress = exeProgress;
	}
	public Date getExeLastTime() {
		return exeLastTime;
	}
	public void setExeLastTime(Date exeLastTime) {
		this.exeLastTime = exeLastTime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getExeLastEndTime() {
		return exeLastEndTime;
	}
	public void setExeLastEndTime(Date exeLastEndTime) {
		this.exeLastEndTime = exeLastEndTime;
	}
	public Integer getFinishHint() {
		if(ObjectUtils.isNull(finishHint) && ObjectUtils.isNotNull(exeStatus) && ObjectUtils.isNotNull(exeLastEndTime)){
			finishHint=-1;
			if(exeStatus.intValue()==2){
				IParameterService parameterService=SpringUtils.getBean("parameterService");
				long timerDelayPrompt=NumberUtils.calc(parameterService.findParameterValue("timer_delay_prompt")).longValue();
				if(System.currentTimeMillis()-exeLastEndTime.getTime()<timerDelayPrompt){
					finishHint=1;
				}
			}
		}
		return finishHint;
	}
	public String getExeTime() {
		if(ObjectUtils.isEmpty(exeTime) && ObjectUtils.isNotEmpty(exeLastTime) && ObjectUtils.isNotEmpty(exeLastEndTime)){
			long start=exeLastTime.getTime();
			long end=exeLastEndTime.getTime();
			if(end>start){
				long time=end-start;
				if(time<1000){
					exeTime=time+"毫秒";
				}else if(time<60000){
					exeTime=NumberUtils.round((time*1.0/1000), 2)+"秒";
				}else if(time<60L*60*1000){
					exeTime=NumberUtils.round((time*1.0/60000), 2)+"分钟";
				}else{
					exeTime=NumberUtils.round((time*1.0/60/60000), 2)+"小时";
				}
			}
		}
		return exeTime;
	}
	public Integer getExeNumberd() {
		return exeNumberd;
	}
	public void setExeNumberd(Integer exeNumberd) {
		this.exeNumberd = exeNumberd;
	}
	public Integer getExeResult() {
		return exeResult;
	}
	public void setExeResult(Integer exeResult) {
		this.exeResult = exeResult;
	}
	public boolean isFullExe() {
		return isFullExe;
	}
	public void setFullExe(boolean isFullExe) {
		this.isFullExe = isFullExe;
	}
	public Date getLastFullExeTime() {
		return lastFullExeTime;
	}
	public void setLastFullExeTime(Date lastFullExeTime) {
		this.lastFullExeTime = lastFullExeTime;
	}
}
