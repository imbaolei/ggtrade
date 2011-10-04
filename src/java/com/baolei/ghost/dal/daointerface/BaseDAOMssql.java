package com.baolei.ghost.dal.daointerface;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

public class BaseDAOMssql extends SqlMapClientDaoSupport {
	protected Log log = LogFactory.getLog(getClass());
	
	// 为了注入SqlMapClient所以多了一个baseDao
	@Autowired
	public void setSqlMapClientBase(@Qualifier("sqlMapClientMssql")SqlMapClient sqlMapClient) {
		super.setSqlMapClient(sqlMapClient);
	}
	
	@Autowired
	public void setDateSource(@Qualifier("dataSourceMssql")DataSource dataSource) {
		super.setDataSource(dataSource);
	}

}
