package com.zucchetti.dc.dockerstats;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

//CONTAINER      CPU %   MEM USAGE / LIMIT       MEM %  NET I/O               BLOCK I/O      PIDS
//xxxxx   0.01%   167.4 MiB / 11.62 GiB   1.41%  2.906 GB / 599.5 MB  122.4 MB / 0 B   9
public class DockerStats
{
	private String container;
	private double cpuPerc;
	private double memPerc;
	private double memUsageMb;
	private double netIUsageMb;
	private double netOUsageMb;
	private double blockIUsageMb;
	private double blockOUsageMb;

	private static class IOUsage
	{
		public double iUsage;
		public double oUsage;
	}

	private DockerStats() {}

	public static DockerStats calculateDockerStats(DockerStatsRecord record)
	{
		DockerStats ds = new DockerStats();
		
		ds.container = record.container;
		ds.cpuPerc = percentageResolver(record.cpuPerc);
		ds.memPerc = percentageResolver(record.memPerc);
		ds.memUsageMb = memoryUsageResolver(record.memUsage);
		
		IOUsage ioNetUsage = ioUsageResolver(record.netioUsage);
		ds.netIUsageMb = ioNetUsage.iUsage;
		ds.netOUsageMb = ioNetUsage.oUsage;
		
		IOUsage ioBlockUsage = ioUsageResolver(record.blockioUsage);
		ds.blockIUsageMb = ioBlockUsage.iUsage;
		ds.blockOUsageMb = ioBlockUsage.oUsage;
		
		return ds;
	}

	private static IOUsage ioUsageResolver(String ioUsageAsString)
	{
		IOUsage usage = new IOUsage();

		String[] ioUsageValues = StringUtils.split(ioUsageAsString, "/");
		String[] iUsageValues = StringUtils.split(ioUsageValues[0].trim(), " ");
		String[] oUsageValues = StringUtils.split(ioUsageValues[1].trim(), " ");

		usage.iUsage = UnitConverter.resolveValueMB(iUsageValues[0], Unit.valueOf(iUsageValues[1]));
		usage.oUsage = UnitConverter.resolveValueMB(oUsageValues[0], Unit.valueOf(oUsageValues[1]));
		
		return usage;
	}

	private static double memoryUsageResolver(String memoryUsageAsString)
	{
		String[] memUsageValues = StringUtils.split(StringUtils.split(memoryUsageAsString, "/")[0].trim(), " ");
		return UnitConverter.resolveValueMB(memUsageValues[0], Unit.valueOf(memUsageValues[1]));
	}

	private static double percentageResolver(String percValueAsString)
	{
		return Double.valueOf(percValueAsString.trim().replace("%", ""));
	}

	public String getContainer() 
	{
		return container;
	}

	public double getCpuPerc() 
	{
		return cpuPerc;
	}

	public double getMemUsageMb() 
	{
		return memUsageMb;
	}

	public double getMemPerc() 
	{
		return memPerc;
	}

	public double getNetIUsageMb() 
	{
		return netIUsageMb;
	}

	public double getNetOUsageMb() 
	{
		return netOUsageMb;
	}

	public double getBlockIUsageMb() 
	{
		return blockIUsageMb;
	}

	public double getBlockOUsageMb() 
	{
		return blockOUsageMb;
	}

	@Override
	public String toString() 
	{
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		if (container != null)
			builder.append("container", container);
		builder.append("cpuPerc", cpuPerc);
		builder.append("memPerc", memPerc);
		builder.append("memUsageMb", memUsageMb);
		builder.append("netIUsageMb", netIUsageMb);
		builder.append("netOUsageMb", netOUsageMb);
		builder.append("blockIUsageMb", blockIUsageMb);
		builder.append("blockOUsageMb", blockOUsageMb);
		return builder.toString();
	}
}
