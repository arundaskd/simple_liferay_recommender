package fooservice.service.persistence.impl;

import java.util.List;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;

import fooservice.service.persistence.FooFinder;

public class FooFinderImpl extends BasePersistenceImpl implements FooFinder{

	public List<?> getAllBehaviour() {
		Session session = openSession();
		String sql = " SELECT cn.value,e.classPK,e.userId,count(*) as c FROM ct_analytics_analyticsevent e "
				+ " join classname_ cn "
				+ " where "
				+ " cn.value not like \"com.liferay.portal.kernel.model.Layout\" "
				+ " and cn.value not like \"com.liferay.dynamic.data.lists.model.DDLRecordSet\" "
				+ " and e.eventType like 'view' and e.classnameid=cn.classnameid group by cn.value,e.classPK,e.userId ";
		//System.out.println(sql);
		SQLQuery q = session.createSQLQuery(sql);
		List<?> response = QueryUtil.list(q, getDialect(), 0, 1000);
		return response;
	}
}
