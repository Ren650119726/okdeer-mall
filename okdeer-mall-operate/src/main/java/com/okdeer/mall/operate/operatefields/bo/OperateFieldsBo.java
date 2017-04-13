
package com.okdeer.mall.operate.operatefields.bo;

import java.util.List;

import com.okdeer.mall.operate.operatefields.entity.OperateFields;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;

public class OperateFieldsBo extends OperateFields {

	private List<OperateFieldsContent> operateFieldscontentList;

	public List<OperateFieldsContent> getOperateFieldscontentList() {
		return operateFieldscontentList;
	}

	public void setOperateFieldscontentList(List<OperateFieldsContent> operateFieldscontentList) {
		this.operateFieldscontentList = operateFieldscontentList;
	}

}
