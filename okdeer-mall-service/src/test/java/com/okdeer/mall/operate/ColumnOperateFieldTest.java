package com.okdeer.mall.operate;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.operate.dto.OperateFieldContentDto;
import com.okdeer.mall.operate.dto.StoreActivitGoodsQueryDto;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColumnOperateFieldTest {
    /** 日志对象 */
    private static final Logger logger = LoggerFactory.getLogger(ColumnOperateFieldTest.class);

    @Autowired
    private OperateFieldsService operateFieldsService;
    
    @Test
    public void testGetGoodsOfStoreActivityFields() throws Exception {
        StoreActivitGoodsQueryDto queryDto = new StoreActivitGoodsQueryDto();
        queryDto.setBusinessType(7);
        List<String> storeIds = new ArrayList<>();
        storeIds.add("5592971b276511e6aaff00163e010eb1");
        storeIds.add("39596d64ad7ba69a611e681620050569");
        storeIds.add("562fddf5276511e6aaff00163e010eb1");
        storeIds.add("2c91c086565f7f6a01565f7f962c0001");
        storeIds.add("56583c03276511e6aaff00163e010eb1");
        storeIds.add("565d444d276511e6aaff00163e010eb1");
        queryDto.setStoreIds(storeIds);
        queryDto.setTemplate(3);
        queryDto.setSort(2);
        queryDto.setSortType(0);
        
        List<OperateFieldContentDto> contents = this.operateFieldsService.getGoodsOfStoreActivityFields(queryDto);
   
        System.out.println(contents.size());
    }
    
    @Test
    public void testGetSingleGoodsOfOperateField() throws Exception {
        String goodsId = "0a1071d2276511e6aaff00163e010eb1";
        String storeId = "56583c03276511e6aaff00163e010eb1";
        OperateFieldContentDto contentDto = this.operateFieldsService.getSingleGoodsOfOperateField(goodsId, storeId);
     
        System.out.println(contentDto.getName());
    }
    
    @Test
    public void testInitStoreOperateFieldData() throws Exception {
        //开心小卖部
        String storeId = "5592971b276511e6aaff00163e010eb1";
        
        this.operateFieldsService.initStoreOperateFieldData(storeId);
    }
    
}
