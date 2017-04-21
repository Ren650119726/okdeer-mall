package com.okdeer.mall.activity.discount.mapper;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.base.BaseServiceTest;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.DEFAULT)
public class ActivityBusinessRelMapperTest extends BaseServiceTest {

	private ActivityBusinessRel addRel;

	private ActivityBusinessRel updateRel;

	private String deleteId;

	private String findId;

	@Resource
	private ActivityBusinessRelMapper activityBusinessRelMapper;

	public ActivityBusinessRelMapperTest(ActivityBusinessRel addRel, ActivityBusinessRel updateRel, String deleteId,
			String findId) {
		this.addRel = addRel;
		this.updateRel = updateRel;
		this.deleteId = deleteId;
		this.findId = findId;
	}

	@Parameters
	public static Collection<Object[]> initParam() {
		ActivityBusinessRel addRel = createRel();
		ActivityBusinessRel updateRel = createUpdateRel(addRel);
		String findId = addRel.getId();
		String deleteId = addRel.getId();

		return Arrays.asList(new Object[][] { { addRel, updateRel, deleteId, findId } });
	}

	private static ActivityBusinessRel createRel() {
		ActivityBusinessRel addRel = new ActivityBusinessRel();
		addRel.setId(UuidUtils.getUuid());
		addRel.setBusinessId("219");
		addRel.setBusinessType(ActivityBusinessType.CITY);
		addRel.setActivityId(UuidUtils.getUuid());
		addRel.setSort(0);
		return addRel;
	}

	private static ActivityBusinessRel createUpdateRel(ActivityBusinessRel rel) {
		ActivityBusinessRel updateRel = BeanMapper.map(rel, ActivityBusinessRel.class);
		updateRel.setBusinessId("3");
		updateRel.setBusinessType(ActivityBusinessType.PROVINCE);
		return updateRel;
	}

	@Test
	public void testAdd() {
		int result = activityBusinessRelMapper.add(addRel);
		assertEquals(1, result);
	}

	@Test
	public void testUpdate() {
		int result = activityBusinessRelMapper.update(updateRel);
		assertEquals(1, result);
	}

	@Test
	public void testFindById() {
		ActivityBusinessRel rel = activityBusinessRelMapper.findById(findId);
		assertEquals(findId, rel.getId());
	}

	@Test
	public void testDelete() {
		int result = activityBusinessRelMapper.delete(deleteId);
		assertEquals(1, result);
	}

}
