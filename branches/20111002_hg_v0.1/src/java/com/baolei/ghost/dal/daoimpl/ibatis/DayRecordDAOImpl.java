package com.baolei.ghost.dal.daoimpl.ibatis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.baolei.ghost.dal.daointerface.BaseDAOMssql;
import com.baolei.ghost.dal.daointerface.DayRecordDAO;
import com.baolei.ghost.dal.dataobject.DayRecordDO;
import com.ibatis.sqlmap.client.SqlMapClient;


public class DayRecordDAOImpl extends BaseDAOMssql implements DayRecordDAO{
	
	public List<DayRecordDO> selectDayRecord(){
		DayRecordDO dayRecordDO = new DayRecordDO();
		List<DayRecordDO> record = (List<DayRecordDO>) getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_DAY_NUMBER", dayRecordDO);
		return record;
	}
	
	

}
