package com.zucchetti.dc.dockerstats;

import java.util.EnumSet;

import org.apache.commons.lang3.StringUtils;


/*
 * 
 CONTAINER      CPU %   MEM USAGE / LIMIT       MEM %  NET I/O               BLOCK I/O      PIDS
xxxxx   0.01%   167.4 MiB / 11.62 GiB   1.41%  2.906 GB / 599.5 MB  122.4 MB / 0 B   9
 * 
 */
public class DockerData 
{
	private String container;
	private String cpuPerc;
	private String memUsage;
	private double memUsageValue;
	private String memPerc;

	private double netI;
	private double netO;
	private double blockI;
	private double blockO;

	private String netio;
	private String blockio;

	public double getBlockI() {
		return blockI;
	}
	public double getBlockO() {
		return blockO;
	}
	public double getNetI() {
		return netI;
	}
	public double getNetO() {
		return netO;
	}

	public void setNetio(String netio) 
	{
		String[] netioValues = StringUtils.split(netio, "/");
		String[] netiValues = StringUtils.split(netioValues[0].trim(), " ");
		String[] netoValues = StringUtils.split(netioValues[1].trim(), " ");

		this.netI = resolveValueMB(netiValues[0], Unit.valueOf(netiValues[1]));
		this.netO = resolveValueMB(netoValues[0], Unit.valueOf(netoValues[1]));
	}
	public void setBlockio(String blockio) 
	{
		String[] blockioValues = StringUtils.split(blockio, "/");
		String[] blockiValues = StringUtils.split(blockioValues[0].trim(), " ");
		String[] blockoValues = StringUtils.split(blockioValues[1].trim(), " ");
		
		this.blockI = resolveValueMB(blockiValues[0], Unit.valueOf(blockiValues[1]));
		this.blockO = resolveValueMB(blockoValues[0], Unit.valueOf(blockoValues[1]));
	}
	
	public String getContainer() {
		return container;
	}
	public void setContainer(String container) {
		this.container = container.trim();
	}
	public String getCpuPerc() {
		return cpuPerc;
	}
	public void setCpuPerc(String cpuPerc) {
		this.cpuPerc = cpuPerc.trim().replace("%", "");
	}
	public double getMemUsageValue() {
		return memUsageValue;
	}
	public void setMemUsage(String memUsage) 
	{
		String[] memUsageValues = StringUtils.split(StringUtils.split(memUsage, "/")[0].trim(), " ");
		this.memUsageValue = resolveValueMB(memUsageValues[0], Unit.valueOf(memUsageValues[1]));
	}
	public String getMemPerc() {
		return memPerc;
	}
	public void setMemPerc(String memPerc) {
		this.memPerc = memPerc.trim().replace("%", "");
	}
	@Override
	public String toString() {
		return "DockerStat [" + (container != null ? "container=" + container + ", " : "")
				+ (cpuPerc != null ? "cpuPerc=" + cpuPerc + ", " : "")
				+ (memUsage != null ? "memUsage=" + memUsage + ", " : "") + "memUsageValue=" + memUsageValue + ", "
				+ (memPerc != null ? "memPerc=" + memPerc + ", " : "") + "netI=" + netI + ", netO=" + netO + ", blockI="
				+ blockI + ", blockO=" + blockO + ", " + (netio != null ? "netio=" + netio + ", " : "")
				+ (blockio != null ? "blockio=" + blockio : "") + "]";
	}
	
	public static double resolveValueMB(String valueAsString, Unit unit)
	{
		double value = Double.valueOf(valueAsString);
		
		EnumSet<Unit> enumBSet = EnumSet.of(Unit.B);
		EnumSet<Unit> enumKbSet = EnumSet.of(Unit.KiB, Unit.kB);
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
