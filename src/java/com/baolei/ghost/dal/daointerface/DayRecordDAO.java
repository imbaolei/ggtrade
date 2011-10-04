package com.baolei.ghost.dal.daointerface;

import java.util.List;

import com.baolei.ghost.dal.dataobject.DayRecordDO;

public interface DayRecordDAO {
	
	public List<DayRecordDO> selectDayRecord();

}
