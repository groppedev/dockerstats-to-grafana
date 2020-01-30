package com.zucchetti.dc.dockerstats;

import java.util.EnumSet;

public class UnitConverter {

	public static double resolveValueMB(String valueAsString, Unit unit)
	{
		double value = Double.valueOf(valueAsString);
		
		EnumSet<Unit> enumBSet = EnumSet.of(Unit.B);
		EnumSet<Unit> enumKbSet = EnumSet.of(Unit.KiB, Unit.kB);
		EnumSet<Unit> enumMbSet = EnumSet.of(Unit.MB, Unit.MiB);
		EnumSet<Unit> enumGbSet = EnumSet.of(Unit.GB, Unit.GiB);
	
		if(enumKbSet.contains(unit))
		{
			value = value / 1024;
		}
		else if(enumGbSet.contains(unit))
		{
			value = value * 1024;
		}
		else if(enumBSet.contains(unit))
		{
			value = (value / 1024) / 1024;
		}
		System.out.println("val -> '" + value + "[" + valueAsString + "]' Unit -> " + unit + " to MB");
		return value;
	}

}
