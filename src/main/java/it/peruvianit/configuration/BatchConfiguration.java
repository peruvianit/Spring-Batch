package it.peruvianit.configuration;

import java.util.Collections;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import it.peruvianit.batch.ItemProcessorBatch;
import it.peruvianit.batch.ItemWriterBatch;
import it.peruvianit.listener.JobCompletionNotificationListener;
import it.peruvianit.model.TBL1000_ITEMS;
import it.peruvianit.polices.VerificationSkipper;
import it.peruvianit.vo.ItemVO;

@Configuration
@EnableBatchProcessing
@ComponentScan

@EnableAutoConfiguration
//file that contains the properties
@PropertySource("classpath:peruvianit.properties")
public class BatchConfiguration {

	/*
    Load the properties
    */
	@Value("${database.driver}")
	private String databaseDriver;
	@Value("${database.url}")
	private String databaseUrl;
	@Value("${database.username}")
	private String databaseUsername;
	@Value("${database.password}")
	private String databasePassword;

    // tag::readerwriterprocessor[]
    @Bean
    public ItemReader<TBL1000_ITEMS> reader() throws Exception {
//    	LAVORARE UTILIZZANDO JPQL
//    	String jpqlQuery = "SELECT I FROM TBL1000_ITEMS I"; 
//    	
//    	JpaPagingItemReader<TBL1000_ITEMS> reader = new JpaPagingItemReader<TBL1000_ITEMS>();
//    	
//    	reader.setQueryString(jpqlQuery);
//		reader.setEntityManagerFactory(entityManagerFactory().getObject());
//		reader.setPageSize(3);
//		reader.afterPropertiesSet();
//		reader.setSaveState(true);
//
//		return reader;
    	
/*
 *  UTILIZZO QUERY NATIVE    	
 */
    	String jpqlQuery = "SELECT * FROM TBL1000_ITEMS "; 
    	
    	JpaPagingItemReader<TBL1000_ITEMS> reader = new JpaPagingItemReader<TBL1000_ITEMS>();
    	
    	//creating a native query provider as it would be created in configuration
    	JpaNativeQueryProvider<TBL1000_ITEMS> queryProvider= new JpaNativeQueryProvider<TBL1000_ITEMS>();
    	queryProvider.setSqlQuery(jpqlQuery);
    	queryProvider.setEntityClass(TBL1000_ITEMS.class);
    	queryProvider.afterPropertiesSet();
    	
    	//reader.setParameterValues(Collections.<String, Object>singletonMap("limit", 3));
    	reader.setEntityManagerFactory(entityManagerFactory().getObject());
    	reader.setPageSize(3);
    	reader.setQueryProvider(queryProvider);
    	reader.afterPropertiesSet();
    	reader.setSaveState(true);

		return reader;
    }

    @Bean
    public ItemProcessor<TBL1000_ITEMS,ItemVO> processor() {
        return new ItemProcessorBatch();
    }

    @Bean
    public ItemWriter<ItemVO> writer() {
    	 return new ItemWriterBatch();
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1) {
        return jobs.get("import")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public SkipPolicy fileVerificationSkipper() {
        return new VerificationSkipper();
    }
    
    // JobCompletionNotificationListener (File loader)
    @Bean
    public JobExecutionListener listener() {
    	return new JobCompletionNotificationListener();
    }


    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<TBL1000_ITEMS> reader,
                      ItemWriter<ItemVO> writer, ItemProcessor<TBL1000_ITEMS, ItemVO> processor) {
    	
        return stepBuilderFactory.get("step1")
                .<TBL1000_ITEMS, ItemVO>chunk(2)
                .reader(reader)
                .faultTolerant()
                .skipPolicy(fileVerificationSkipper())
                .processor(processor)
                .writer(writer)
                .build();
    }
    // end::jobstep[]
    
    /**
     * As data source we use an external database
     *
     * @return
     */

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(databaseDriver);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        return dataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setPackagesToScan("it.peruvianit.model");
        lef.setDataSource(dataSource());
        lef.setJpaVendorAdapter(jpaVendorAdapter());
        lef.setJpaProperties(new Properties());
        return lef;
    }


    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setShowSql(true);

        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return jpaVendorAdapter;
    }
}