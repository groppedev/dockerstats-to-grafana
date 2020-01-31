-- Dashboard Graphs

-- CPU %
SELECT  container,cpu_perc,sampler_time as time
  FROM docker_data order by sampler_time;

-- MEM USAGE
SELECT  container,mem_usage_mb,sampler_time as time
  FROM docker_data order by sampler_time;

-- NET I
SELECT  container,net_i_usage_mb,sampler_time as time
  FROM docker_data order by sampler_time;
  
-- NET O
SELECT  container,net_o_usage_mb,sampler_time as time
  FROM docker_data order by sampler_time;
  
-- BLOCK I
SELECT  container,block_i_usage_mb,sampler_time as time
  FROM docker_data order by sampler_time;
  
-- BLOCK O
SELECT  container,block_o_usage_mb,sampler_time as time
  FROM docker_data order by sampler_time;