package com.baolei.ghost.dal.daoimpl.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.daointerface.BaseDAO;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.ibatis.sqlmap.client.SqlMapExecutor;

@Repository("reportDAO")
public class ReportDAOImpl extends BaseDAO implements ReportDAO {

	public void insertReports(final List<ReportDO> recordList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				int batch = 0;
				executor.startBatch();
				for (int i = 0, count = recordList.size(); i < count; i++) {
					ReportDO record = new ReportDO();
					record = recordList.get(i);
					record.setGmtCreate(new Date());
					record.setGmtModified(new Date());
					executor.insert("SQL_INSERT_REPORT", record);
					batch++;
					if(batch==300){
	    				executor.executeBatch();
	    				batch = 0;
	    			}
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	
	public Integer insertReport(ReportDO record) {
		record.setGmtCreate(new Date());
		record.setGmtModified(new Date());
		Object newKey = getSqlMapClientTemplate().insert(
				"SQL_INSERT_REPORT",
				record);
		return (Integer) newKey;
	}
	
	public List<ReportDO> selectReportByCode(String code,String order) {
		Map map = new HashMap();
		map.put("code", code);
		map.put("order", order);
		List<ReportDO> record = (List<ReportDO>) getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_REPORT_BY_CODE", map);
		return record;
	}

	public int deleteReportByCode(String code) {
		ReportDO key = new ReportDO();
		key.setCode(code);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_REPORT_BY_CODE",
				key);
		return rows;
	}

	@Override
	public List<ReportDO> seleteReportsByConditions(Map param) {
		List<ReportDO> reportList = (List<ReportDO>)getSqlMapClientTemplate().queryForList("SQL_SELECT_REPORT_BY_CONDITIONS",param);
		return reportList;
	}

	@Override
	public List<String> seleteCodesFromReport(Map param) {
		List<String> codeList = (List<String>)getSqlMapClientTemplate().queryForList("SQL_SELECT_CODE_FROM_REPORT",param);
		return codeList;
	}

	@Override
	public int countCodesFromReport(Map param) {
		return (Integer) getSqlMapClientTemplate().queryForObject("SQL_COUNT_CODE_FROM_REPORT",param);
	}
}
