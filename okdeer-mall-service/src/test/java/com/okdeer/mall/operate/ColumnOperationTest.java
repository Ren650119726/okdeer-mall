package com.okdeer.mall.operate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.system.entity.SysDict;
import com.okdeer.archive.system.service.ISysDictServiceApi;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.Application;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ClassName: ColumnOperationTest
 *
 * @author wangf01
 * @Description: 运营栏目-test
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColumnOperationTest {

    static final JsonMapper mapper = new JsonMapper();

    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(ColumnOperationTest.class);

    @Reference(version = "1.0.0", check = false)
    ISysDictServiceApi sysDictServiceApi;

    /**
     * 新增路小宝（店铺、商品、手机充值）
     */
    @Test
    @Rollback
    public void addColumnOperationTest() {
        SysDict dict = new SysDict();
        dict.setId(UuidUtils.getUuid());
        dict.setDelFlag("0");
        dict.setSort(99);
        dict.setType("appPageType");
        dict.setLabel("手机充值");
        dict.setValue("telephoneCharging");
        dict.setDescription("运营栏目-手机充值");
        sysDictServiceApi.add(dict);
        System.out.println("dict对象:" + mapper.toJson(dict).toString());
        dict.setId(UuidUtils.getUuid());
        dict.setLabel("便利/服务店铺首页");
        dict.setValue("storeInfo");
        dict.setDescription("运营栏目-便利店/服务店首页");
        sysDictServiceApi.add(dict);
        System.out.println("dict对象:" + mapper.toJson(dict).toString());
        dict.setId(UuidUtils.getUuid());
        dict.setLabel("便利/服务商品详情");
        dict.setValue("storeSkuInfo");
        dict.setDescription("运营栏目-便利/服务商品详情");
        sysDictServiceApi.add(dict);
        System.out.println("dict对象:" + mapper.toJson(dict).toString());
        dict.setId(UuidUtils.getUuid());
        dict.setLabel("鹿小宝Tab导航");
        dict.setValue("luXiaoBao");
        dict.setType("columnOperationType");
        dict.setDescription("运营栏目-鹿小宝Tab导航");
        sysDictServiceApi.add(dict);
        System.out.println("dict对象:" + mapper.toJson(dict).toString());
    }
}
