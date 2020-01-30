package com.zucchetti.dc.dockerstats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;

public class Parser 
{
	private static StreamFactory streamFactory = StreamFactory.newInstance();

	private static final String ORIG_FILE_PATH;
	private static final String WORK_DIR_PATH;
	private static final String DB_CONNECTION_URL;
	
	static
	{
		DB_CONNECTION_URL = SystemUtils.getEnvironmentVariable("DB_CONNECTION_URL", "jdbc:postgresql://127.0.0.1:5433/grafana-dockerstat");
		ORIG_FILE_PATH = SystemUtils.getEnvironmentVariable("ORIG_FILE_PATH", "C:\\docs\\GUT\\dockerstats.txt");
		WORK_DIR_PATH = SystemUtils.getEnvironmentVariable("WORK_DIR_PATH", "C:\\docs\\GUT\\dockerstats-work");
		
		System.out.println("DB_CONNECTION_URL -> " + DB_CONNECTION_URL);
		System.out.println("ORIG_FILE_PATH -> " + ORIG_FILE_PATH);
		System.out.println("WORK_DIR_PATH -> " + WORK_DIR_PATH);

		try(InputStream mappingIn = Parser.class.getResourceAsStream("/mapping.xml");)
		{
			streamFactory.load(mappingIn);
		} 
		catch (IOException e) 
		{
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException, SQLException 
	{

		// create a BeanIO StreamFactory
		StreamFactory factory = StreamFactory.newInstance();
		// load the mapping file from the working directory
		factory.load(Parser.class.getResourceAsStream("/mapping.xml"));

		try(Connection c = getConnection();
				PreparedStatement stmInsert = c.prepareStatement("insert into docker_data(container, cpu_perc, mem_perc, mem_usage_mb, "
						+ "net_i_usage_mb, net_o_usage_mb, block_i_usage_mb, block_o_usage_mb, sampler_time) "
						+ "values (?,?,?,?,?,?,?,?,?);");
				PreparedStatement stmDelete = c.prepareStatement("delete from docker_data;");)
		{

			stmDelete.execute();

			Path workDir = prepareWorkDir();
			List<Path> paths = splitOrigFile(workDir);
			for(Path pt : paths)
			{
				BeanReader in = null;
				Timestamp sqlTimestamp = null;
				try
				{
					System.out.println(pt);
					// create a BeanReader to read from "input.csv"
					in = factory.createReader("dockerStatStream", pt.toFile());
					Object record = null;
					while ((record = in.read()) != null)
					{
						// process each record
						if("dockerStatDateHeader".equals(in.getRecordName()))
						{
							@SuppressWarnings("unchecked")
							Map<String,Object> dateheader = (Map<String,Object>) record;
							// "Wed Jan 22 11:35:00 CET 2020";
							System.out.println(record);
							sqlTimestamp = resolveSqlTimestamp((String) dateheader.get("date"));
							System.out.println(sqlTimestamp);
						}
						else if ("dockerStat".equals(in.getRecordName()))
						{
							DockerData dc = (DockerData) record;
							//System.out.println(dc);
	
							stmInsert.setString(1, dc.getContainer());
							stmInsert.setDouble(2, Double.valueOf(dc.getCpuPerc()));
							stmInsert.setDouble(3, Double.valueOf(dc.getMemPerc()));
							stmInsert.setDouble(4, dc.getMemUsageValue());
							stmInsert.setDouble(5, dc.getNetI());
							stmInsert.setDouble(6, dc.getNetO());
							stmInsert.setDouble(7, dc.getBlockI());
							stmInsert.setDouble(8, dc.getBlockO());
							stmInsert.setTimestamp(9, sqlTimestamp);
							
							stmInsert.execute();
							
						}
					}
				}
				finally
				{
					in.close();
				}
			}
		}
	}

	private static Timestamp resolveSqlTimestamp(String dateStringToParse) 
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
		LocalDateTime l = LocalDateTime.parse(dateStringToParse, formatter);
		Timestamp sqlTimestamp = Timestamp.valueOf(l);
		return sqlTimestamp;
	}

	private static List<Path> splitOrigFile(Path workDir) throws IOException, UnsupportedEncodingException 
	{
		List<Path> paths = new ArrayList<Path>();
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		try(BufferedReader bf = Files.newBufferedReader(Paths.get(ORIG_FILE_PATH));)
		{
			String line = null;
			while((line = bf.readLine()) != null)
			{
				if(line.startsWith("-"))
				{
					counter++;
					Path tmpFilePath = workDir.resolve(counter + "-block.txt");
					Files.write(tmpFilePath, sb.toString().getBytes("UTF8"));
					paths.add(tmpFilePath);
					sb = new StringBuilder();
				}
				else
				{
					sb.append(line).append("\n");
				}
			}
		}
		return paths;
	}

	public static Connection getConnection() throws SQLException 
	{
		Properties connectionProps = new Properties();
		connectionProps.put("user", "postgres");
		connectionProps.put("password", "groppe");

		Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/grafana-dockerstat",connectionProps);
		return conn;
	}

	private static Path prepareWorkDir() throws IOException 
	{
		Path workDir = Paths.get(WORK_DIR_PATH);
		FileUtils.forceDelete(workDir.toFile());
		Files.createDirectory(workDir);
		return workDir;
	}

}
