package org.unswift.gtft.transfer.controller;

import java.util.Map;
import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;

import org.unswift.core.annotations.Authority;
import org.unswift.core.annotations.JLogger;
import org.unswift.core.authority.IAuthorityInit;
import org.unswift.gtft.transfer.pojo.DataSource;
import org.unswift.gtft.transfer.service.IDataSourceService;
import org.unswift.core.pojo.Page;


/**
 * 数据源Controller(后端访问入口)
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/transfer")
@Authority(name="数据抽取",index=10)
public class DataSourceController implements IAuthorityInit{

	@Resource
	private IDataSourceService dataSourceService;
	
	private static final String PATH="transfer/dataSource/";
	
	@JLogger("跳转到数据源管理界面(数据源管理入口请求)")
	@Authority(name="数据源管理",parent="#/transfer",index=1,depth=2)
	@RequestMapping(value = "/dataSourcePageList", method = RequestMethod.GET)
    public String dataSourcePageList(Model model) {
		return PATH+"dataSource-list";
	}
	
	@ResponseBody
	@JLogger("根据查询条件查询数据源列表数据")
	@Authority(blongTo="/dataSourcePageList")
	@RequestMapping(value = "/dataSourcePageListData", method = RequestMethod.POST)
    public Page dataSourcePageListData(DataSource dataSource, Page page, Model model) {
		dataSource.setStatus(1);
		return dataSourceService.findPageList(page, dataSource);
	}
	@JLogger("跳转到增加数据源页面")
	@Authority(name="增加数据源",parent="/dataSourcePageList",type="PURVIEW",index=1,depth=3)
	@RequestMapping(value = "/addDataSource", method = RequestMethod.GET)
    public String addDataSource(Model model) {
		return PATH+"add-dataSource";
	}
	
	@ResponseBody
	@JLogger("保存数据源信息")
	@Authority(blongTo="/addDataSource")
	@RequestMapping(value = "/saveDataSource", method = RequestMethod.POST)
    public Map<String, Object> saveDataSource(DataSource dataSource, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			result=connDataSource(dataSource, model);
			if(!(Boolean)result.get("success")){
				return result;
			}
			dataSourceService.saveDataSource(dataSource);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("链接数据源信息")
	@Authority(blongTo="/addDataSource")
	@RequestMapping(value = "/connDataSource", method = RequestMethod.POST)
    public Map<String, Object> connDataSource(DataSource dataSource, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		
		try {
			dataSourceService.connDataSource(dataSource);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@JLogger("跳转到编辑数据源页面")
	@Authority(name="编辑数据源",parent="/dataSourcePageList",type="PURVIEW",index=2,depth=3)
	@RequestMapping(value = "/editDataSource", method = RequestMethod.GET)
    public String editDataSource(String id, Model model) {
		model.addAttribute("dataSource", dataSourceService.findById(id));
		return PATH+"edit-dataSource";
	}
	
	@ResponseBody
	@JLogger("更新数据源信息")
	@Authority(blongTo="/editDataSource")
	@RequestMapping(value = "/updateDataSource", method = RequestMethod.POST)
    public Map<String, Object> updateDataSource(DataSource dataSource, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			result=connDataSource(dataSource, model);
			if(!(Boolean)result.get("success")){
				return result;
			}
			dataSourceService.updateDataSource(dataSource);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	@ResponseBody
	@JLogger("删除单个数据源信息")
	@Authority(name="删除数据源",parent="/dataSourcePageList",type="PURVIEW",index=3,depth=3)
	@RequestMapping(value = "/deleteDataSource", method = RequestMethod.GET)
	public Map<String, Object> deleteDataSource(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.deleteDataSource(id);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("数据源显示顺序移动")
	@Authority(name="数据源顺序移动",parent="/dataSourcePageList",type="PURVIEW",index=4,depth=3)
	@RequestMapping(value = "/moveDataSource", method = RequestMethod.GET)
	public Map<String, Object> moveDataSource(String id, String operator, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.moveDataSource(id,operator);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("删除多个数据源信息")
	@Authority(blongTo="/deleteDataSource")
	@RequestMapping(value = "/deleteDataSources", method = RequestMethod.GET)
	public Map<String, Object> deleteDataSources(String[] ids, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.deleteDataSources(ids);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@JLogger("跳转到数据源回收站界面")
	@Authority(name="数据源回收站",parent="/dataSourcePageList",type="PURVIEW",index=5,depth=3)
	@RequestMapping(value = "/dataSourceRecycle", method = RequestMethod.GET)
    public String dataSourceRecycle(Model model) {
		return PATH+"dataSource-recycle";
	}
	
	@ResponseBody
	@JLogger("读取数据源回收站列表数据")
	@Authority(blongTo="/dataSourceRecycle")
	@RequestMapping(value = "/dataSourceRecycleData", method = RequestMethod.POST)
    public Page dataSourceRecycleData(DataSource dataSource, Page page, Model model) {
		dataSource.setStatus(0);
		dataSourceService.findPageList(page, dataSource);
		return page;
	}
	
	@ResponseBody
	@JLogger("删除数据源回收站中的单个数据源信息")
	@Authority(blongTo="/dataSourceRecycle")
	@RequestMapping(value = "/deleteDataSourceRecycle", method = RequestMethod.GET)
	public Map<String, Object> deleteDataSourceRecycle(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.deleteRecycles(new String[]{id});
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("删除数据源回收站中的被选择的数据源信息(多个)")
	@Authority(blongTo="/dataSourceRecycle")
	@RequestMapping(value = "/deleteDataSourceRecycles", method = RequestMethod.GET)
	public Map<String, Object> deleteDataSourceRecycles(String[] ids, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.deleteRecycles(ids);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("清理数据源回收中的所有数据")
	@Authority(blongTo="/dataSourceRecycle")
	@RequestMapping(value = "/clearDataSourceRecycle", method = RequestMethod.GET)
	public Map<String, Object> clearDataSourceRecycle(Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.clearRecycle();
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("恢复数据源回收站中单个员工数据")
	@Authority(blongTo="/dataSourceRecycle")
	@RequestMapping(value = "/recoveryDataSourceRecycle", method = RequestMethod.GET)
	public Map<String, Object> recoveryDataSourceRecycle(String id, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.recoveryRecycles(new String[]{id});
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@JLogger("恢复数据源回收站中被选择的数据源数据(多个)")
	@Authority(blongTo="/dataSourceRecycle")
	@RequestMapping(value = "/recoveryDataSourceRecycles", method = RequestMethod.GET)
	public Map<String, Object> recoveryDataSourceRecycles(String[] ids, Model model) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			dataSourceService.recoveryRecycles(ids);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
}