package com.baolei.ghost.dal.daointerface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

public class BaseDAO extends SqlMapClientDaoSupport {
	protected Log log = LogFactory.getLog(getClass());
	@Autowired
	// 为了注入SqlMapClient所以多了一个baseDao
	public void setSqlMapClientBase(SqlMapClient sqlMapClient) {
		super.setSqlMapClient(sqlMapClient);
	}

}
