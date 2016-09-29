package com.okdeer.mall.operate.column.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.operate.column.mapper.SysIconManagerMapper;
import com.okdeer.mall.operate.column.service.SysIconManagerService;
import com.okdeer.mall.operate.entity.SysIconManager;
import com.okdeer.mall.operate.enums.SysIconType;
import com.okdeer.mall.operate.service.ISysIconManagerServiceApi;

/**
 * 
 * ClassName: SysIconManagerServiceImpl 
 * @Description: 系统图标管理（服务栏目更多）Service实现类
 * @author lijun
 * @date 2016年9月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构1.1需求                         2016年9月23日                                 lijun               新增
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ISysIconManagerServiceApi")
public class SysIconManagerServiceImpl implements SysIconManagerService, ISysIconManagerServiceApi {
	
	/**
	 * Mapper注入
	 */
	@Autowired
	private SysIconManagerMapper sysIconManagerMapper;

	@Override
	public SysIconManager findSysIcon() throws ServiceException {
		SysIconManager sysIconManager = sysIconManagerMapper.findSysIcon();
		if (sysIconManager == null) {
			sysIconManager = new SysIconManager();
		}
		return sysIconManager;
	}

	
	@Override
	public void updateSysIconInfo(SysIconManager sysIconManager) throws ServiceException {
		if(StringUtils.isBlank(sysIconManager.getId())){
			sysIconManager.setId(UuidUtils.getUuid());
			sysIconManager.setType(SysIconType.moreColumn);
			sysIconManagerMapper.insertSelective(sysIconManager);
		}else{
			sysIconManager.setType(SysIconType.moreColumn);
			sysIconManagerMapper.updateSelective(sysIconManager);
		}
	}
}
