package com.baolei.ghost.dal.daoimpl.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.daointerface.BaseDAO;
import com.baolei.ghost.dal.daointerface.ReportStatsDAO;
import com.baolei.ghost.dal.dataobject.ReportStatsDO;
import com.ibatis.sqlmap.client.SqlMapExecutor;

@Repository("reportStatsDAO")
public class ReportStatsDAOImpl extends BaseDAO implements ReportStatsDAO {

	public void insertReportStats(final List<ReportStatsDO> recordList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				int batch = 0;
				executor.startBatch();
				for (int i = 0, count = recordList.size(); i < count; i++) {
					ReportStatsDO record = new ReportStatsDO();
					record = recordList.get(i);
					record.setGmtCreate(new Date());
					record.setGmtModified(new Date());
					executor.insert("SQL_INSERT_REPORT_STATS", record);
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
	
	public Integer insertReportStats(ReportStatsDO record) {
		record.setGmtCreate(new Date());
		record.setGmtModified(new Date());
		Object newKey = getSqlMapClientTemplate().insert(
				"SQL_INSERT_REPORT_STATS",
				record);
		return (Integer) newKey;
	}
	
	public ReportStatsDO selectReportStatsByCode(String code) {
		ReportStatsDO key = new ReportStatsDO();
		key.setCode(code);
		ReportStatsDO record = (ReportStatsDO) getSqlMapClientTemplate().queryForObject(
				"SQL_SELECT_REPORT_STATS_BY_CODE", key);
		return record;
	}

	public int deleteReportStatsByCode(String code) {
		ReportStatsDO key = new ReportStatsDO();
		key.setCode(code);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_REPORT_STATS_BY_CODE",
				key);
		return rows;
	}

	@Override
	public List<ReportStatsDO> seleteReportStatsByConditions(Map param) {
		List<ReportStatsDO> reportList = (List<ReportStatsDO>)getSqlMapClientTemplate().queryForList("SQL_SELECT_REPORT_STATS_BY_CONDITIONS",param);
		return reportList;
	}

	
}
