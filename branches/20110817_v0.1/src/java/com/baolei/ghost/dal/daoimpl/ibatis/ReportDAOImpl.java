package com.baolei.ghost.dal.daoimpl.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.daointerface.BaseDAO;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StockDO;
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
	
	public StockDO selectReportByCode(String code) {
		ReportDO key = new ReportDO();
		key.setCode(code);
		StockDO record = (StockDO) getSqlMapClientTemplate().queryForObject(
				"SQL_SELECT_REPORT_BY_CODE", key);
		return record;
	}

	public int deleteReportByCode(String code) {
		ReportDO key = new ReportDO();
		key.setCode(code);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_REPORT_BY_CODE",
				key);
		return rows;
	}
}
