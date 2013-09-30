package com.baolei.ghost.dal.daoimpl.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.daointerface.BaseDAO;
import com.baolei.ghost.dal.daointerface.StatisticsDAO;
import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StatisticsIndustryDO;
import com.ibatis.sqlmap.client.SqlMapExecutor;

@Repository("statisticsDAO")
public class StatisticsDAOImpl  extends BaseDAO implements StatisticsDAO {

	@Override
	public void insertStatisticses(final List<StatisticsDO> recordList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				int batch = 0;
				executor.startBatch();
				for (int i = 0, count = recordList.size(); i < count; i++) {
					StatisticsDO record = new StatisticsDO();
					record = recordList.get(i);
					record.setGmtCreate(new Date());
					record.setGmtModified(new Date());
					executor.insert("SQL_INSERT_STATISTICS", record);
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

	@Override
	public Integer insertStatistics(StatisticsDO record) {
		record.setGmtCreate(new Date());
		record.setGmtModified(new Date());
		Object newKey = getSqlMapClientTemplate().insert(
				"SQL_INSERT_STATISTICS",
				record);
		return (Integer) newKey;
	}

	@Override
	public List<StatisticsDO> selectStatisticsByTime(Date time) {
		Map map = new HashMap();
		map.put("time", time);
		List<StatisticsDO> record = (List<StatisticsDO>) getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_STATISTICS_BY_TIME", map);
		return record;
	}

	@Override
	public int deleteStatisticsByDate(Date date) {
		StatisticsDO key = new StatisticsDO();
		key.setTime(date);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_STATISTICS_BY_TIME",
				key);
		return rows;
	}

	@Override
	public List<StatisticsIndustryDO> seleteIndustryStatistics(Map map) {
		List<StatisticsIndustryDO> record = (List<StatisticsIndustryDO>) getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_STATISTICS_BY_TIME_GROUP_BY_INDUSTRY", map);
		return record;
	}

	@Override
	public List<Date> selectDatesFromStatistics(Map param) {
		List<Date> record = (List<Date>) getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_DATES_FROME_STATISTICS", param);
		return record;
	}

}
