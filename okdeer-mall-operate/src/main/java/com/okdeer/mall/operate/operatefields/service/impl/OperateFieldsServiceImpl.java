
package com.okdeer.mall.operate.operatefields.service.impl;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.Enabled;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.operatefields.bo.OperateFieldsBo;
import com.okdeer.mall.operate.operatefields.entity.OperateFields;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;
import com.okdeer.mall.operate.operatefields.mapper.OperateFieldsContentMapper;
import com.okdeer.mall.operate.operatefields.mapper.OperateFieldsMapper;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsService;

/**
 * ClassName: OperateFieldsServiceImpl 
 * @Description: TODO
 * @author zengjizu
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class OperateFieldsServiceImpl extends BaseServiceImpl implements OperateFieldsService {

	private static final int DEFAULT_SORT = 10010;

	@Autowired
	private OperateFieldsMapper operateFieldsMapper;

	@Autowired
	private OperateFieldsContentMapper operateFieldsContentMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return operateFieldsMapper;
	}

	@Override
	public List<OperateFields> findList(OperateFieldsQueryParamDto queryParamDto) {
		return operateFieldsMapper.findList(queryParamDto);
	}

	@Override
	public List<OperateFieldsBo> findListWithContent(OperateFieldsQueryParamDto queryParamDto) {
		return operateFieldsMapper.findListWithContent(queryParamDto);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void save(OperateFields operateFields, List<OperateFieldsContent> operateFieldscontentList) {
		// 保存运营栏位信息
		saveOperateFields(operateFields);
		// 保存运营栏位内容信息
		saveOperateFieldsContent(operateFields.getId(), operateFieldscontentList);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void update(OperateFields operateFields, List<OperateFieldsContent> operateFieldscontentList) {
		// 修改运营栏位信息
		operateFields.setUpdateTime(new Date());
		operateFieldsMapper.update(operateFields);
		// 修改运营栏位内容信息
		updateOperateFieldsContent(operateFields.getId(), operateFieldscontentList);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateSort(String id, boolean isUp) {
		OperateFields operateFields = operateFieldsMapper.findById(id);
		int sort = operateFields.getSort();
		int type = -1;
		if (isUp) {
			type = 1;
		}
		OperateFields compareOperateFields = operateFieldsMapper.findCompareBySort(operateFields.getId(),sort, type);

		if (operateFields.getSort() == compareOperateFields.getSort()) {
			if (isUp) {
				operateFields.setSort(sort + 1);
			} else {
				operateFields.setSort(sort - 1);
			}
		} else {
			operateFields.setSort(compareOperateFields.getSort());
			compareOperateFields.setSort(sort);
		}
		operateFieldsMapper.update(operateFields);
		operateFieldsMapper.update(compareOperateFields);
	}

	/**
	 * @Description: 保存运营栏位内容信息
	 * @param fieldId
	 * @param operateFieldscontentList
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	private void saveOperateFieldsContent(String fieldId, List<OperateFieldsContent> operateFieldscontentList) {
		for (OperateFieldsContent operateFieldsContent : operateFieldscontentList) {
			operateFieldsContent.setId(UuidUtils.getUuid());
			operateFieldsContent.setFieldId(fieldId);
			operateFieldsContentMapper.add(operateFieldsContent);
		}
	}

	/**
	 * @Description: 修改运营栏位信息
	 * @param fieldId
	 * @param operateFieldscontentList
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	private void updateOperateFieldsContent(String fieldId, List<OperateFieldsContent> operateFieldscontentList) {
		operateFieldsContentMapper.deleteByFieldId(fieldId);
		saveOperateFieldsContent(fieldId, operateFieldscontentList);
	}

	/**
	 * @Description: 保存运营栏位信息
	 * @param operateFields
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	private void saveOperateFields(OperateFields operateFields) {
		operateFields.setSort(getMinSort(operateFields) - 10);
		operateFields.setId(UuidUtils.getUuid());
		operateFields.setCreateTime(new Date());
		operateFields.setDisabled(Disabled.valid);
		operateFields.setEnabled(Enabled.NO);
		operateFieldsMapper.add(operateFields);
	}

	/**
	 * @Description: 获取最小排序值
	 * @param operateFields
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	private int getMinSort(OperateFields operateFields) {
		Integer sort = operateFieldsMapper.queryMinSort(operateFields.getType(), operateFields.getBusinessId());
		if (sort == null) {
			return DEFAULT_SORT;
		}
		return sort;
	}

	/**
     * 根据店铺Id和店铺商品Id查找关联的运营栏位
     * @param storeId 店铺Id
     * @param storeSkuId 店铺商品Id
     * @return 栏位列表
     * @author zhaoqc
     * @date 2017-4-18
     */
    @Override
    public List<OperateFields> getGoodsRalationFields(String storeId, String storeSkuId) {
       
        return null;
    }

}
