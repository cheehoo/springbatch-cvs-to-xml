package com.timwe.batch;

import java.util.Calendar;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The main class that initializes and process the cdr and chunk into either xml 
 * or database based on the configuration
 * 
 * the input folder - src/main/resources/cdr/input
 * the output folder in xml - src/main/resources/cdr/output
 *  
 * Run the batch with the following command :
 * java -Xms4g -Xmx4g -cp target/springbatch-cvs-to-xml.jar  com.timwe.batch.App
 * 
 * @author cheehoo
 * @since 1.0.0
 * 
 */
public class App 
{
    public static void main( String[] args )
    {
		String[] springConfig = { "cdr/config/job-cdr-batch.xml" };

		ApplicationContext context = new ClassPathXmlApplicationContext(
				springConfig);

		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("cdrProcessingJob");

		try {

			System.out.println("Start DateTime : "+Calendar.getInstance().getTime());
			JobExecution execution = jobLauncher.run(job, new JobParameters());
			System.out.println("Exit Status : " + execution.getStatus());
			System.out.println("End DateTime : "+Calendar.getInstance().getTime());

		} catch (Exception e) {
			e.printStackTrace();
		}

    }
}
