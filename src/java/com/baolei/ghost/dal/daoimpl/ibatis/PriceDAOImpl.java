package com.baolei.ghost.dal.daoimpl.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.daointerface.BaseDAO;
import com.baolei.ghost.dal.daointerface.PriceDAO;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.ibatis.sqlmap.client.SqlMapExecutor;

@Repository("priceDAO")
public class PriceDAOImpl extends BaseDAO implements PriceDAO {

	public Integer insertPrice(PriceDO record) {
		record.setGmtCreate(new Date());
		record.setGmtModified(new Date());
		Object newKey = getSqlMapClientTemplate().insert("SQL_INSERT_PRICE",
				record);
		return (Integer) newKey;
	}

	public int updatePriceById(PriceDO record) {
		int rows = getSqlMapClientTemplate().update("SQL_UPDATE_PRICE_BY_ID",
				record);
		return rows;
	}

	public int updatePriceByIdSelective(PriceDO record) {
		int rows = getSqlMapClientTemplate().update(
				"SQL_UPDATE_PRICE_BY_ID_SELECTIVE", record);
		return rows;
	}
	
	public void updatePricesByIdBatch(final List<PriceDO> priceList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				executor.startBatch();
				int batch = 0;
				for (int i = 0, count = priceList.size(); i < count; i++) {
					getSqlMapClientTemplate().update("SQL_UPDATE_PRICE_BY_ID_SELECTIVE", priceList.get(i));
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

	public PriceDO selectPriceById(Integer id) {
		PriceDO key = new PriceDO();
		key.setId(id);
		PriceDO record = (PriceDO) getSqlMapClientTemplate().queryForObject(
				"SQL_SELECT_PRICE_BY_ID", key);
		return record;
	}

	public int deletePriceById(Integer id) {
		PriceDO key = new PriceDO();
		key.setId(id);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_PRICE_BY_ID",
				key);
		return rows;
	}

	public int deletePriceByCode(String code) {
		PriceDO key = new PriceDO();
		key.setCode(code);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_PRICE_BY_CODE",
				key);
		return rows;
	}

	public List<PriceDO> selectPriceByCodeAndPeriod(String code, String period) {
		PriceDO key = new PriceDO();
		key.setCode(code);
		key.setPeriod(period);
		List<PriceDO> recordList = getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_PRICE_BY_CODE_AND_PERIOD", key);
		return recordList;
	}

	public void insertPrices(final List<PriceDO> priceList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				int batch = 0;
				executor.startBatch();
				for (int i = 0, count = priceList.size(); i < count; i++) {
					executor.insert("SQL_INSERT_PRICE", priceList.get(i));
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
}