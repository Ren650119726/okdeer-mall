
package com.okdeer.mall.operate.operatefields.service.impl;

import static com.okdeer.mall.operate.contants.OperateFieldContants.CITY_OPERATE_FIELD_KEY;
import static com.okdeer.mall.operate.contants.OperateFieldContants.STORE_OPERATE_FIELD_KEY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.registry.redis.RedisRegistry;
import com.okdeer.archive.goods.base.entity.GoodsCategoryAssociation;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.goods.base.service.GoodsSpuCategoryServiceApi;
import com.okdeer.archive.goods.menu.GoodsStoreMenuApi;
import com.okdeer.archive.store.dto.GoodsStoreMenuDto;
import com.okdeer.archive.store.dto.GoodsStoreMenuParamDto;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.Enabled;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.FieldGoodsQueryDto;
import com.okdeer.mall.operate.dto.FieldInfoDto;
import com.okdeer.mall.operate.dto.OperateFieldContentDto;
import com.okdeer.mall.operate.dto.OperateFieldDto;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.dto.StoreActivitGoodsQueryDto;
import com.okdeer.mall.operate.entity.ColumnNativeSubject;
import com.okdeer.mall.operate.enums.OperateFieldsAppPointType;
import com.okdeer.mall.operate.enums.OperateFieldsBusinessType;
import com.okdeer.mall.operate.enums.OperateFieldsContentType;
import com.okdeer.mall.operate.enums.OperateFieldsType;
import com.okdeer.mall.operate.mapper.ColumnNativeSubjectMapper;
import com.okdeer.mall.operate.operatefields.bo.OperateFieldsBo;
import com.okdeer.mall.operate.operatefields.entity.OperateFields;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;
import com.okdeer.mall.operate.operatefields.mapper.OperateFieldsContentMapper;
import com.okdeer.mall.operate.operatefields.mapper.OperateFieldsMapper;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsService;

/**
 * ClassName: OperateFieldsServiceImpl 
 * @Description: 
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

    private static final Logger logger = LoggerFactory.getLogger(OperateFieldsServiceImpl.class);
    
	private static final int DEFAULT_SORT = 10010;

	@Autowired
	private OperateFieldsMapper operateFieldsMapper;

	@Autowired
	private OperateFieldsContentMapper operateFieldsContentMapper;

	@Reference(version="1.0.0", check=false)
	private StoreInfoServiceApi storeInfoServiceApi;
	
    /**
     * redis接入
     */
    //@Autowired
    //private IRedisTemplateWrapper<String, OperateFieldDto> redisTemplateWrapper;
	
	@Autowired
    private RedisTemplate<String, OperateFieldDto> redisTemplate;
    
    @Reference(version="1.0.0", check=false)
    private GoodsSpuCategoryServiceApi goodsSpuCategoryServiceApi;
    
    @Reference(version="1.0.0", check=false)
    private GoodsStoreMenuApi goodsStoreMenuApi;
    
    @Reference(version="1.0.0", check=false)
    private GoodsNavigateCategoryServiceApi navigateCategoryServiceApi;
    
    @Autowired
    private ColumnNativeSubjectMapper nativeSubjectMapper;
    
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    
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
	public void updateSort(String id, boolean isUp) throws Exception {
		OperateFields operateFields = operateFieldsMapper.findById(id);
		int sort = operateFields.getSort();
		int type = -1;
		if (isUp) {
			type = 1;
		}
		OperateFields compareOperateFields = operateFieldsMapper.findCompareBySort(operateFields,type);

		if (compareOperateFields == null) {
			throw new Exception("没有可以移动的数据");
		}
		if (sort == compareOperateFields.getSort().intValue()) {
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

    /**
     * 初始化店铺运营栏位
     * @param storeId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
    @Override
    public synchronized List<OperateFieldDto> initStoreOperateFieldData(String storeId) throws Exception {
        OperateFieldsQueryParamDto queryParamDto = new OperateFieldsQueryParamDto();
        queryParamDto.setType(OperateFieldsType.STORE);
        queryParamDto.setBusinessId(storeId);
        queryParamDto.setEnabled(Enabled.YES);
        
        FieldInfoDto fieldInfo = null;
        //Set<TypedTuple<OperateFieldDto>> tuples = new HashSet<>();
        List<OperateFieldDto> operateFields = new ArrayList<>();
        //查出属于该店铺的所有店铺运营栏位
        List<OperateFieldsBo> fieldsList = this.findListWithContent(queryParamDto);
        for(OperateFieldsBo fields : fieldsList) {
            fieldInfo = createFieldInfoDto(fields);
            List<OperateFieldContentDto> contentDtos = new ArrayList<>();
            OperateFieldContentDto contentDto = null;
            List<OperateFieldsContent> contentList = fields.getOperateFieldscontentList();
            
            int template = fieldInfo.getTemplate();
            for(OperateFieldsContent content : contentList) {
                OperateFieldsContentType type = content.getType();
                
                if(type == OperateFieldsContentType.SINGLE_GOODS) {
                    //单品选择
                    contentDto = this.getSingleGoodsOfOperateField(content.getBusinessId(), storeId);
                    if(contentDto != null) {
                        contentDtos.add(contentDto);
                    } else {
                        contentDtos = null;
                        break;
                    }
                } else if(type == OperateFieldsContentType.STORE_ACTIVITY) {
                    //店铺活动
                    contentDtos = getGoodsOfStoreActivityField(storeId, content.getBusinessType().getCode(),
                            template, content.getSortType().getCode(), content.getSort());
                } else if(type == OperateFieldsContentType.STORE_MENU) {
                    //指定店铺菜单
                    OperateFieldsBusinessType businessType = content.getBusinessType();
                    if(businessType == OperateFieldsBusinessType.STORE_MENU) {
                        //店铺菜单
                        GoodsStoreMenuParamDto paramDto = new GoodsStoreMenuParamDto();
                        paramDto.setPkId(content.getBusinessId());
                        paramDto.setStoreId(storeId);
                        List<GoodsStoreMenuDto> menuDtos = this.goodsStoreMenuApi.findByParam(paramDto);
                        if(CollectionUtils.isNotEmpty(menuDtos)) {
                           //businessId为店铺菜单
                            GoodsStoreMenuDto goodsStoreMenuDto = menuDtos.get(0);
                            Integer menuType = goodsStoreMenuDto.getType();
                            if(menuType == 0) {
                                //标签
                                contentDtos = getGoodsOfStoreLebelFields(storeId, goodsStoreMenuDto.getPkId(), 
                                        template, content.getSort(), content.getSortType().getCode());
                            } else if(menuType == 1 || menuType == 2) {
                                //为分类 1:一级分类 2:二级分类
                                contentDtos = this.getGoodsOfCategoryFields(storeId, goodsStoreMenuDto.getPkId(), 
                                        template, content.getSort(), content.getSortType().getCode());
                            }
                        }
                    } else if (businessType == OperateFieldsBusinessType.NAVIGATE) {
                        //导航分类
                        contentDtos = getGoodsOfStoreNavigateFields(storeId, content.getBusinessId(),
                                template, content.getSort(), content.getSortType().getCode());
                    }
                } else if(type == OperateFieldsContentType.GOODS_CLASSIFY) {
                    //指定商品分类
                    contentDtos = this.getGoodsOfCategoryFields(storeId, content.getBusinessId(), 
                            template, content.getSort(), content.getSortType().getCode());
                } else if (type == OperateFieldsContentType.H5_LINK) {
                    //H5链接
                    contentDto = h5LinkFieldContent(content);
                    contentDtos.add(contentDto);
                } else if (type == OperateFieldsContentType.NATIVE_SUBJECT) {
                    //原生专题页
                    contentDto = nativeSubjectContent(content);
                    contentDtos.add(contentDto);
                } else if (type == OperateFieldsContentType.BUSINESS_ENTRANCE) {
                    //业务入口
                    contentDto = businessEntranceContent(content);
                    contentDtos.add(contentDto);
                }
            }
            
            if(CollectionUtils.isNotEmpty(contentDtos)) {
                OperateFieldDto operateField = new OperateFieldDto();
                operateField.setFieldInfo(fieldInfo);
                operateField.setContentList(contentDtos);
                
                operateFields.add(operateField);
                /*tuples.add(new TypedTuple<OperateFieldDto>() {
                    @Override
                    public int compareTo(TypedTuple<OperateFieldDto> o) {
                        return this.getScore().compareTo(o.getScore());
                    }
                    
                    @Override
                    public OperateFieldDto getValue() {
                        return operateField;
                    }
                    
                    @Override
                    public Double getScore() {
                        return operateField.getFieldInfo().getSort().doubleValue();
                    }
                });*/
            }
        }
        //将运营栏位信息缓存进Redis
        //redisTemplateWrapper.zAdd(STORE_OPERATE_FIELD_KEY + storeId, operateFields, fieldInfo.getSort());
        //缓存之前先删除之前该店铺下存储的栏位信息
        redisTemplate.delete(STORE_OPERATE_FIELD_KEY + storeId);
        if(CollectionUtils.isNotEmpty(operateFields)) {
            //redisTemplate.opsForZSet().add(STORE_OPERATE_FIELD_KEY + storeId, tuples);
            redisTemplate.opsForList().rightPushAll(STORE_OPERATE_FIELD_KEY + storeId, operateFields);
        }
        
        return operateFields;
    }    
    
    
    
    /**
     * 初始化城市运营栏位
     * @param cityId
     * @param storeId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
    @Override
    public List<OperateFieldDto> initCityOperateFieldData(String cityId) throws Exception {
        OperateFieldsQueryParamDto queryParamDto = new OperateFieldsQueryParamDto();
        queryParamDto.setType(OperateFieldsType.CITY);
        queryParamDto.setBusinessId(cityId);
        queryParamDto.setEnabled(Enabled.YES);
        
        FieldInfoDto fieldInfo = null;
        //Set<TypedTuple<OperateFieldDto>> tuples = new HashSet<>();
        List<OperateFieldDto> operateFields = new ArrayList<>();
        //查出该城市下的城市运营栏位
        List<OperateFieldsBo> fieldsList = this.findListWithContent(queryParamDto);
        for(OperateFieldsBo fields : fieldsList) {
            fieldInfo = createFieldInfoDto(fields);
            List<OperateFieldContentDto> contentDtos = new ArrayList<>();
            OperateFieldContentDto contentDto = null;
            List<OperateFieldsContent> contentList = fields.getOperateFieldscontentList();
            
            int template = fieldInfo.getTemplate();
            for(OperateFieldsContent content : contentList) {
                OperateFieldsContentType type = content.getType();
                if(type == OperateFieldsContentType.STORE_ACTIVITY) {
                    //contentDtos = getGoodsOfCityStoreActivityField(cityId, content.getBusinessType().getCode(),
                    //template, content.getSortType().getCode(), content.getSort());*/
                    //当城市运营栏位指向内容是店铺活动是，往redis中存储规则
                    contentDto = storeActivityFieldOfCity(content.getBusinessType().getCode(),
                            template, content.getSortType().getCode(), content.getSort());
                    contentDtos.add(contentDto);
                } else if (type == OperateFieldsContentType.H5_LINK) {
                    contentDto = h5LinkFieldContent(content);
                    contentDtos.add(contentDto);
                } else if(type == OperateFieldsContentType.NATIVE_SUBJECT) {
                    contentDto = nativeSubjectContent(content);
                    contentDtos.add(contentDto);
                } else if(type == OperateFieldsContentType.BUSINESS_ENTRANCE) {
                    contentDto = businessEntranceContent(content);
                    contentDtos.add(contentDto);
                }
            }
            
            if(contentDtos != null) {
                OperateFieldDto operateField = new OperateFieldDto();
                operateField.setFieldInfo(fieldInfo);
                operateField.setContentList(contentDtos);
                
                operateFields.add(operateField);
                /*tuples.add(new TypedTuple<OperateFieldDto>() {
                    
                    @Override
                    public int compareTo(TypedTuple<OperateFieldDto> o) {
                        return this.getScore().compareTo(o.getScore());
                    }
                    
                    @Override
                    public OperateFieldDto getValue() {
                        return operateField;
                    }
                    
                    @Override
                    public Double getScore() {
                        return operateField.getFieldInfo().getSort().doubleValue();
                    }
                });*/
            } 
        }
        
        //将运营栏位信息缓存进Redis
        //redisTemplateWrapper.zAdd(CITY_OPERATE_FIELD_KEY + cityId, operateField, fieldInfo.getSort());
        //缓存之前先删除之前该店铺下存储的栏位信息
        Lock lock = redisLockRegistry.obtain(CITY_OPERATE_FIELD_KEY + cityId);
        try {
            if(lock.tryLock(10, TimeUnit.SECONDS)) {
                redisTemplate.delete(CITY_OPERATE_FIELD_KEY + cityId);
                if(CollectionUtils.isNotEmpty(operateFields)) {
                    //redisTemplate.opsForZSet().add(CITY_OPERATE_FIELD_KEY + cityId, tuples);
                    redisTemplate.opsForList().rightPushAll(CITY_OPERATE_FIELD_KEY + cityId, operateFields);
                }
            }
        }finally {
            lock.unlock();
        }
        return operateFields;
    }
    
    private List<OperateFieldContentDto> getGoodsOfStoreLebelFields(String storeId, String labelId, int template, 
            int sort, int sortType) throws Exception {
        FieldGoodsQueryDto queryDto = new FieldGoodsQueryDto();
        queryDto.setLabelId(labelId);
        queryDto.setTemplate(template + 1);
        queryDto.setSort(sort - 1);
        queryDto.setSortType(sortType);
        queryDto.setStoreId(storeId);
     
        List<OperateFieldContentDto> contentDtos = this.getGoodsOfStoreLabelField(queryDto);
        if(contentDtos.size() == (template + 1)) {
            return contentDtos;
        } else {
            return null;
        }
    }
    
    public List<OperateFieldContentDto> getGoodsOfStoreNavigateFields(String storeId, String navigateId, 
            int template, int sort, int sortType) throws Exception {
        //查出导航分类的二级分类和商品三级分类之间的关系
        List<GoodsCategoryAssociation> associations = this.navigateCategoryServiceApi.getGoodsCategoryAssociation(navigateId);
        
        List<String> categoryIds = new ArrayList<>();
        associations.forEach(association -> {
            if(association != null) {
                categoryIds.add(association.getSpuCategoryId());
            }
        });
        
        
        FieldGoodsQueryDto queryDto = new FieldGoodsQueryDto();
        queryDto.setCategoryIds(categoryIds);
        queryDto.setSort(sort - 1);
        queryDto.setTemplate(template + 1);
        queryDto.setSortType(sortType);
        queryDto.setStoreId(storeId);
        
        List<OperateFieldContentDto> contentDtos = this.getGoodsOfCategoryField(queryDto);
        if(contentDtos.size() == (template + 1)) {
            return contentDtos;
        } else {
            return null;
        }
    }
    
    private List<OperateFieldContentDto> getGoodsOfCategoryFields(String storeId, String categoryId, int template, int sort, int sortType) throws Exception {
        //查找出分类下的所有三级分类
        List<GoodsSpuCategory> categorys = goodsSpuCategoryServiceApi.findCategorysByPid(categoryId, "3");
        List<String> categoryIds = new ArrayList<>();
        categorys.forEach(category -> {
            categoryIds.add(category.getId());
        });
        
        FieldGoodsQueryDto queryDto = new FieldGoodsQueryDto();
        queryDto.setCategoryIds(categoryIds);
        queryDto.setSort(sort - 1);
        queryDto.setTemplate(template + 1);
        queryDto.setSortType(sortType);
        queryDto.setStoreId(storeId);
        
        List<OperateFieldContentDto> contentDtos = this.getGoodsOfCategoryField(queryDto);
        
        if(contentDtos.size() == (template + 1)) {
            return contentDtos;
        } else {
            return null;
        }
    }
    
    /**
     * 店铺运营位查找店铺活动的商品
     * @param businessType 0:特惠活动 1:低价活动
     * @param template 模板
     * @param 排序类型   0 价格从高到低  1 排序值从高到低 2 价格从低到高  3排序值从低到高 
     * @param sortStart 排序开始值
     * @throws Exception 
     */
    @Override
    public List<OperateFieldContentDto> getGoodsOfStoreActivityField(String storeId, int businessType, int template, 
            int sortType, int sort) throws Exception {
        StoreActivitGoodsQueryDto queryDto = new StoreActivitGoodsQueryDto();
        //转换业务类型使之和数据库一致 
        if(businessType == 0) {
            businessType = 5;
        } else if(businessType == 1) {
            businessType = 7;
        }
        queryDto.setBusinessType(businessType);
        queryDto.setTemplate(template + 1);
        queryDto.setSort(sort - 1);
        queryDto.setSortType(sortType);
        queryDto.setStoreId(storeId);
       
        List<OperateFieldContentDto> contentDtos = this.getGoodsOfStoreActivityFields(queryDto);
        if(contentDtos.size() == (template + 1)) {
            return contentDtos;
        } else {
            return null;
        }
    }
    
    /**
     * 城市运营位指向内容为店铺活动的缓存其规则
     * @param businessType 0:特惠活动 1:低价活动
     * @param template 模板
     * @param 排序类型   0 价格从高到低  1 排序值从高到低 2 价格从低到高  3排序值从低到高 
     * @param sortStart 排序开始值
     * @throws Exception 
     */
    private OperateFieldContentDto storeActivityFieldOfCity(int businessType, int template, 
            int sortType, int sort) {
        OperateFieldContentDto contentDto = new OperateFieldContentDto();
        //由于redis中栏位指向信息和规则字段不同，所以用以下几个只想类型为商品的字段代替存储商品信息
        //对应关系如下
        /**
         * isLowPrice -> 栏位业务类型 0:特惠活动 1:低价活动
         * sellableStock -> 栏位模板类型 0一统江山 1 二分天下 3 三足鼎立
         * tradeMax -> 排序类型  0 价格从高到低  1 排序值从高到低 2 价格从低到高  3排序值从低到高 
         * lowPriceUpper -> 排序开始值
         */
        contentDto.setIsLowPrice(businessType);
        contentDto.setSellableStock(template);
        contentDto.setTradeMax(sortType);
        contentDto.setLowPriceUpper(sort);
        
        return contentDto;
    }
    
    /**
     * 创建H5链接的栏位内容
     * @param content
     * @return
     */
    private OperateFieldContentDto h5LinkFieldContent(OperateFieldsContent content) {
        OperateFieldContentDto contentDto = new OperateFieldContentDto();
        contentDto.setPointType(OperateFieldsAppPointType.H5_LINK.getCode());
        contentDto.setTitle(content.getTitle());
        contentDto.setPointContent(content.getLinkUrl());
        contentDto.setImageUrl(content.getImageUrl());
        
        return contentDto;
    }
    
    /**
     * 创建原生主题的栏位内容
     * @param content
     * @return
     */
    private OperateFieldContentDto nativeSubjectContent(OperateFieldsContent content) {
        OperateFieldContentDto contentDto = new OperateFieldContentDto();
        contentDto.setPointType(OperateFieldsAppPointType.NATIVE_SUBJECT.getCode());
        contentDto.setPointContent(content.getBusinessId());
        contentDto.setImageUrl(content.getImageUrl());
        //查询专题的标题
        ColumnNativeSubject nativeSubject = this.nativeSubjectMapper.findById(content.getBusinessId());
        if (nativeSubject != null) {
            contentDto.setTitle(nativeSubject.getName());
        }
        
        return contentDto;
    }
    
    private OperateFieldContentDto businessEntranceContent(OperateFieldsContent content) {
        OperateFieldContentDto contentDto = new OperateFieldContentDto();
        OperateFieldsBusinessType businessType = content.getBusinessType();
        if(businessType == OperateFieldsBusinessType.STORE_INDEX_PAGE) {
            contentDto.setPointType(OperateFieldsAppPointType.STORE_INDEX.getCode());
        } else if(businessType == OperateFieldsBusinessType.GOODS_DETAIL) {
            contentDto.setPointType(OperateFieldsAppPointType.GOODS_DETAIL.getCode());
        } else if(businessType == OperateFieldsBusinessType.STORE_MENU) {
            contentDto.setPointType(OperateFieldsAppPointType.STORE_MENU.getCode());
        }
        
        contentDto.setPointContent(content.getBusinessId());
        contentDto.setImageUrl(content.getImageUrl());
        
        return contentDto;
    }
    
    private FieldInfoDto createFieldInfoDto(OperateFieldsBo fieldBo) {
        FieldInfoDto fieldInfo = new FieldInfoDto();
        fieldInfo.setTitle("");
        fieldInfo.setHeadPic(fieldBo.getHeadPic());
        fieldInfo.setTarget("");
        fieldInfo.setId(fieldBo.getId());
        fieldInfo.setType(fieldBo.getType().getCode());
        fieldInfo.setBusinessId(fieldBo.getBusinessId());
        fieldInfo.setName(fieldBo.getName());
        fieldInfo.setTemplate(fieldBo.getTemplate().ordinal());
        fieldInfo.setSort(fieldBo.getSort());
        
        return fieldInfo;
    }

    /**
     * 查找店铺活动关联的商品运营位内容
     * @param queryDto
     * @return
     * @author zhaoqc
     * @date 2017-4-19
     */
    @Override
    public List<OperateFieldContentDto> getGoodsOfStoreActivityFields(StoreActivitGoodsQueryDto queryDto) throws Exception {
        return this.operateFieldsContentMapper.getGoodsOfStoreActivityFields(queryDto);
    }

    /**
     * 根据店铺Id和skuId查找运营栏位关联的商品信息
     * @param goodsId 商品Id
     * @param storeId 店铺Id
     * @return
     * @author zhaoqc
     * @date 2017-4-19
     */
    @Override
    public OperateFieldContentDto getSingleGoodsOfOperateField(String goodsId, String storeId) throws Exception {
        return this.operateFieldsContentMapper.getSingleGoodsOfOperateField(goodsId, storeId);
    }

    @Override
    public List<OperateFieldContentDto> getGoodsOfCategoryField(FieldGoodsQueryDto queryDto) throws Exception {
        return this.operateFieldsContentMapper.getGoodsOfCategoryField(queryDto);
    }

    /**
     * 根据店铺菜单->菜单为标签查找所属的商品
     * @param queryDto
     * @return
     * @throws Exception
     */
    @Override
    public List<OperateFieldContentDto> getGoodsOfStoreLabelField(FieldGoodsQueryDto queryDto) throws Exception {
        return this.operateFieldsContentMapper.getGoodsOfStoreLabelField(queryDto);
    }

	@Override
	public void initOperationField(String storeId) throws Exception {
		if(StringUtils.isNotBlank(storeId)){
			operateFieldsMapper.initOperationField(storeId);
		}
		
	}
    
}
