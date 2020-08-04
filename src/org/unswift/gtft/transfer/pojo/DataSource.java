package org.unswift.gtft.transfer.pojo;

import org.unswift.base.system.service.impl.PageBean;
import org.unswift.core.pojo.LogPojo;
import org.unswift.core.utils.ObjectUtils;
import org.unswift.core.utils.SpringUtils;


/**
 * 数据源实体
 * @author Administrator
 *
 */
public class DataSource extends LogPojo{

	private static final long serialVersionUID = -1039106269098475095L;
	
	private String id;	//主键
	private String name;	//名称
	private String type;	//类型，db-conn：数据库链接，service：服务
	private String typeName;	//类型，db-conn：数据库链接，service：服务
	private String dbType;	//数据库类型
	private String dbTypeName;	//数据库类型
	private String dbDriver;	//数据库驱动
	private String url;	//数据库url
	private String username;	//数据库用户名
	private String password;	//数据库密码
	private Integer orderNo;	//序号
	private Integer maxOrderNo;//在当前父节点下的最大排序（非数据库字段）
	private Integer minOrderNo;//在当前父节点下的最小排序（非数据库字段）

	public DataSource(){
	
	}
	public DataSource(String id){
		this.id=id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}

	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return this.type;
	}
	public String getTypeName() {
		if(ObjectUtils.isEmpty(typeName) && ObjectUtils.isNotEmpty(type)){
			PageBean pageBean=SpringUtils.getBean("pageBean");
			typeName=pageBean.getDictionaryName("DATA_SOURCE_TYPE", type);
		}
		return typeName;
	}
	public String getDbTypeName() {
		if(ObjectUtils.isEmpty(dbTypeName) && ObjectUtils.isNotEmpty(dbType)){
			PageBean pageBean=SpringUtils.getBean("pageBean");
			dbTypeName=pageBean.getDictionaryName("DATABASE_TYPE", dbType);
		}
		return dbTypeName;
	}
	public void setDbType(String dbType){
		this.dbType=dbType;
	}
	public String getDbType(){
		return this.dbType;
	}

	public void setDbDriver(String dbDriver){
		this.dbDriver=dbDriver;
	}
	public String getDbDriver(){
		return this.dbDriver;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
}
