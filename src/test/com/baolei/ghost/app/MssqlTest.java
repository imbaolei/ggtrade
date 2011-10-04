package com.baolei.ghost.app;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.common.AbstractTestCase;
import com.baolei.ghost.dal.daointerface.DayRecordDAO;
import com.baolei.ghost.dal.dataobject.DayRecordDO;

public class MssqlTest extends AbstractTestCase  {
	@Autowired
	private DayRecordDAO dayRecordDAO;
	
	@Test
	public void testa() {
		List<DayRecordDO> dayRecordList = dayRecordDAO.selectDayRecord();
		for(DayRecordDO dayRecordDO : dayRecordList){
			System.out.println(dayRecordDO.getDayNumber());
		}
	}
}
