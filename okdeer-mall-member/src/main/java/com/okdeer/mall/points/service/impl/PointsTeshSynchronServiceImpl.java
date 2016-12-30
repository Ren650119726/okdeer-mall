package com.okdeer.mall.points.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.points.common.TeshCodeMsgConstant;
import com.okdeer.mall.points.common.TeshConstant;
import com.okdeer.mall.points.service.PointsTeshSynchronService;
import com.okdeer.mall.points.service.TeshSynchronLogService;
import com.okdeer.mall.points.util.TeshSignUtils;
import com.okdeer.mall.points.vo.TeshProductDetailVo;
import com.okdeer.mall.points.vo.TeshProductPicVo;
import com.okdeer.mall.points.vo.TeshProductSkuVo;
import com.okdeer.mall.points.vo.TeshProductVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * ClassName: PointsTeshSynchronServiceImpl 
 * @Description: 商品同步
 * @author tangy
 * @date 2016年12月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月17日                               tangy
 */
@Service
public class PointsTeshSynchronServiceImpl implements PointsTeshSynchronService {
	/**
	 * 日志对象
	 */
	private static final Logger logger = LoggerFactory.getLogger(PointsTeshSynchronServiceImpl.class);
	
	/**
	 * URL
	 */
	//@Value("${teshUrl}")
	private String teshUrl;
	
	/**
	 * 公钥
	 */
	//@Value("${teshAPPSecret}")
	private String appSecret;
	
	/**
	 * app类型
	 */
	//@Value("${teshAppkey}")
	private String appkey;
	
	/**
	 * 合作商编号
	 */
	//@Value("${teshDistCode}")
	private String distCode;
	
	/**
	 * 用户类型   1：合作商   2：商城用户
	 */
	public static final String typeId = "1";
	
	/**
	 * 平台
	 */
	public static final String platform = "";
	
	/**
	 * 版本
	 */
	public static final String version = "1.0.0";
	
	/**
	 * 同步日志
	 */
	@Autowired
	private TeshSynchronLogService teshSynchronLogService;
	
	@Override
	public void synchron() throws ServiceException {
		// TODO Auto-generated method stub
		teshSynchronLogService.findBySynchronTime(DateUtils.getDate());
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public PageUtils<TeshProductVo> findTeshProductByAll(Integer pageNo, Integer pageSize) throws ServiceException {
		// 参数验证
		if (pageNo == null || pageSize == null) {
			List<TeshProductVo> teshProductVos = new ArrayList<TeshProductVo>();
			return new PageUtils<TeshProductVo>(teshProductVos);
		}
		String timestamp = DateUtils.getDateTime();
		String action = TeshConstant.TESH_IF_GETPRODUCTLIST;
		JSONObject page = new JSONObject();
		page.put(TeshConstant.TESH_PARAM_PAGENO, pageNo);
		page.put(TeshConstant.TESH_PARAM_PAGESIZE, pageSize);
		// 请求参数
		JSONObject json = getRequestJson(timestamp, action, page.toString());
		logger.info("findTeshProductByAll 请求josn{}", json.toString());
		String resultJosn = HttpClientUtil.httpPostWithJSON(teshUrl, json);
		JSONObject jsonObject = new JSONObject().fromObject(resultJosn);
		// 获取请求状态和请求提示信息
		String code = jsonObject.get("code").toString();
		String msg = jsonObject.get("msg").toString();
		logger.info("findTeshProductByAll 请求返回code{}, msg{}", code, msg);
		
		PageUtils<TeshProductVo> pageList = new PageUtils<TeshProductVo>(null);		
		if (TeshCodeMsgConstant.TESH_CODE_SUCCESS.equals(code)) {
			String data = jsonObject.get("data").toString();
			logger.info("findTeshProductByAll 请求返回data{}", data);
			JSONObject dataJson = new JSONObject().fromObject(data);
			Integer total = (Integer) dataJson.get("total");
			String result = dataJson.get("result").toString();
			JSONArray array = JSONArray.fromObject(result);
			List<TeshProductVo> teshProductVos = 
					(List<TeshProductVo>) JSONArray.toCollection(array, TeshProductVo.class);
			pageList.setList(teshProductVos);
			pageList.setPageNum(pageNo);
			pageList.setPageSize(pageSize);
			pageList.setTotal(total);
		} else {
			throw new ServiceException(TeshCodeMsgConstant.TESH_CODE_FAILURES);
		}
		return pageList;
	}

	@SuppressWarnings({ "static-access", "rawtypes" })
	@Override
	public List<TeshProductDetailVo> findDetailBySkuCodes(List<String> skuCodes) throws ServiceException {
		if (CollectionUtils.isEmpty(skuCodes)) {
			return new ArrayList<TeshProductDetailVo>();
		}
		String timestamp = DateUtils.getDateTime();
		String action = TeshConstant.TESH_IF_GETPRODUCTDETAIL;
		JSONObject productList = new JSONObject();
        List<Map<String, String>> list = new ArrayList<Map<String,String>>();
        for (String skuCode : skuCodes) {
        	Map<String, String> map = new HashMap<String, String>();
			map.put("skuCode", skuCode);
			list.add(map);
		}
		productList.put("productList", skuCodes);
		
		// 请求参数
		JSONObject json = getRequestJson(timestamp, action, productList.toString());
		logger.info("findDetailBySkuCodes 请求josn{}", json.toString());
		String resultJosn = HttpClientUtil.httpPostWithJSON(teshUrl, json);
		JSONObject jsonObject = new JSONObject().fromObject(resultJosn);
		// 获取请求状态和请求提示信息
		String code = jsonObject.get("code").toString();
		String msg = jsonObject.get("msg").toString();
		logger.info("findDetailBySkuCodes 请求返回code{}, msg{}", code, msg);
		
		List<TeshProductDetailVo> teshProductVos = null;
		if (TeshCodeMsgConstant.TESH_CODE_SUCCESS.equals(code)) {
			String data = jsonObject.get("data").toString();
			logger.info("findDetailBySkuCodes 请求返回data{}", data);
			JSONArray array = JSONArray.fromObject(data);
			Map<String, Class> classMap = new HashMap<String, Class>(); 
			classMap.put("imageList", TeshProductPicVo.class);
			classMap.put("skuList", TeshProductSkuVo.class);
			teshProductVos = new ArrayList<TeshProductDetailVo>();
			for (Object object : array) {
				TeshProductDetailVo teshProductDetailVo = 
						(TeshProductDetailVo) JSONObject.toBean((JSONObject) object, TeshProductDetailVo.class, classMap);
				if (teshProductDetailVo != null) {
					teshProductVos.add(teshProductDetailVo);
				}
			}
		} else {
			throw new ServiceException(TeshCodeMsgConstant.TESH_CODE_FAILURES);
		}
		return teshProductVos;
	}

	/**
	 * 
	 * @Description:    组装请求参数
	 * @param timestamp 请求时间
	 * @param action    请求接口
     * @param pageNo    页码
     * @param pageSize  每页数
	 * @return JSONObject  
	 * @author tangy
	 * @throws ServiceException 
	 * @date 2016年12月17日
	 */
	private JSONObject getRequestJson(String timestamp, String action, String data) throws ServiceException{
		JSONObject json = new JSONObject();
		json.put(TeshConstant.TESH_PARAM_ACTION, action);
		json.put(TeshConstant.TESH_PARAM_APP_KEY, appkey);
		json.put(TeshConstant.TESH_PARAM_DIST_CODE, distCode);
		json.put(TeshConstant.TESH_PARAM_TYPEID, typeId);
		json.put(TeshConstant.TESH_PARAM_PLATFORM, "");
		json.put(TeshConstant.TESH_PARAM_TIMESTAMP, timestamp);
		json.put(TeshConstant.TESH_PARAM_VERSION, version);
		json.put(TeshConstant.TESH_PARAM_DATA, data);
		String signature;
		try {
			signature = getSign(timestamp, data.toString(), action);
			json.put(TeshConstant.TESH_PARAM_SIGN, signature);
		} catch (Exception e) {
			throw new ServiceException(TeshCodeMsgConstant.TESH_SIGNATURE_FAILURES);
		}
		return json;
	}
	
	/**
	 * 
	 * @Description: 获取签名
	 * @param timestamp   请求时间
	 * @param page        分页信息
	 * @param action      接口名称
	 * @throws Exception   
	 * @return String  
	 * @author tangy
	 * @date 2016年12月17日
	 */
	private String getSign(String timestamp, String page, String action) throws Exception{
		StringBuffer signature = new StringBuffer();
    	signature.append(appSecret);
    	signature.append(TeshConstant.TESH_PARAM_ACTION).append(action);
    	signature.append(TeshConstant.TESH_PARAM_APP_KEY).append(appkey);
    	signature.append(TeshConstant.TESH_PARAM_DIST_CODE).append(distCode);
    	signature.append(TeshConstant.TESH_PARAM_TYPEID).append(typeId);
    	signature.append(TeshConstant.TESH_PARAM_DATA).append(page);
    	signature.append(TeshConstant.TESH_PARAM_FORMAT).append("json");
    	signature.append(TeshConstant.TESH_PARAM_PLATFORM).append(platform);
    	signature.append(TeshConstant.TESH_PARAM_SIGN_METHOD).append("md5");
    	signature.append(TeshConstant.TESH_PARAM_TIMESTAMP).append(timestamp);
    	signature.append(TeshConstant.TESH_PARAM_VERSION).append(version);
    	signature.append(appSecret);
		return TeshSignUtils.sign(signature.toString());
	}
}
