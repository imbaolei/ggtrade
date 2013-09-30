package com.baolei.ghost.dal.daoimpl.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.daointerface.BaseDAO;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.ibatis.sqlmap.client.SqlMapExecutor;

@Repository("stockDAO")
public class StockDAOImpl extends BaseDAO implements StockDAO {

	@Override
	public StockDO selectStockById(Integer id) {
		StockDO key = new StockDO();
		key.setId(id);
		StockDO record = (StockDO) getSqlMapClientTemplate().queryForObject(
				"SQL_SELECT_STOCK_BY_ID", key);
		return record;
	}

	@Override
	public StockDO selectStockByCode(String code) {
		StockDO key = new StockDO();
		key.setCode(code);
		StockDO record = (StockDO) getSqlMapClientTemplate().queryForObject(
				"SQL_SELECT_STOCK_BY_CODE", key);
		return record;
	}

	@Override
	public int deleteStockById(Integer id) {
		StockDO key = new StockDO();
		key.setId(id);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_STOCK_BY_ID",
				key);
		return rows;
	}

	@Override
	public int deleteStockByCode(String code) {
		StockDO key = new StockDO();
		key.setCode(code);
		int rows = getSqlMapClientTemplate().delete("SQL_DELETE_STOCK_BY_CODE",
				key);
		return rows;
	}

	@Override
	public Integer insertStock(StockDO record) {
		record.setGmtCreate(new Date());
		record.setGmtModified(new Date());
		Object newKey = getSqlMapClientTemplate().insert("SQL_INSERT_STOCK",
				record);
		return (Integer) newKey;
	}

	@Override
	public void insertStocks(final List<StockDO> stockList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				int batch = 0;
				executor.startBatch();
				for (int i = 0, count = stockList.size(); i < count; i++) {
					StockDO stockDO = stockList.get(i);
					stockDO.setGmtCreate(new Date());
					stockDO.setGmtModified(new Date());
					executor.insert("SQL_INSERT_STOCK", stockDO);
					batch++;
					if (batch == 300) {
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
	public int updateStockById(StockDO record) {
		int rows = getSqlMapClientTemplate().update("SQL_UPDATE_STOCK_BY_ID",
				record);
		return rows;
	}

	@Override
	public int updateStockByIdSelective(StockDO record) {
		int rows = getSqlMapClientTemplate().update(
				"SQL_UPDATE_STOCK_BY_ID_SELECTIVE", record);
		return rows;
	}

	@Override
	public void updateStocksByIdBatch(final List<StockDO> stockList) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				executor.startBatch();
				int batch = 0;
				for (int i = 0, count = stockList.size(); i < count; i++) {
					StockDO stockDO = stockList.get(i);
					stockDO.setGmtModified(new Date());
					getSqlMapClientTemplate().update(
							"SQL_UPDATE_STOCK_BY_ID_SELECTIVE",
							stockDO);
					batch++;
					if (batch == 300) {
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
	public List<StockDO> selectAllStock() {
		List<StockDO> record = (List<StockDO>) getSqlMapClientTemplate().queryForList(
				"SQL_SELECT_ALL_STOCK");
		return record;
	}

}
