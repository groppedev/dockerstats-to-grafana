package com.zucchetti.dc.dockerstats;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

//  CONTAINER      CPU %   MEM USAGE / LIMIT       MEM %  NET I/O               BLOCK I/O      PIDS
public class DockerStatsRecord 
{
	public String container;
	public String cpuPerc;
	public String memUsage;
	public String memPerc;
	public String netioUsage;
	public String blockioUsage;
	
	@Override
	public String toString() 
	{
		ToStringBuilder builder = new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE);
		if (container != null)
			builder.append("container", container);
		if (cpuPerc != null)
			builder.append("cpuPerc", cpuPerc);
		if (memUsage != null)
			builder.append("memUsage", memUsage);
		if (memPerc != null)
			builder.append("memPerc", memPerc);
		if (netioUsage != null)
			builder.append("netioUsage", netioUsage);
		if (blockioUsage != null)
			builder.append("blockioUsage", blockioUsage);
		return builder.toString();
	}
	

	
}
