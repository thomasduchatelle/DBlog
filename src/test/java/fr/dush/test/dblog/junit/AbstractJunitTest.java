package fr.dush.test.dblog.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import fr.dush.test.dblog.junit.dbunitapi.DBUnitJUnit4ClassRunner;
import fr.dush.test.dblog.junit.dbunitapi.IDatabaseScriptsReader;

/**
 * Initialise le contexte SPRING et founie les m�thodes pour g�rer le contenu de la base de donn�es (avec DBUnit).
 *
 *
 * @author Thomas Duchatelle (thomas.duchatelle@capgemini.com)
 */
@RunWith(DBUnitJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:WEB-INF/spring/context-global.xml", "classpath:WEB-INF/spring/context-persistence.xml" })
public abstract class AbstractJunitTest extends TestCase implements IDatabaseScriptsReader {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractJunitTest.class);

	@Inject
	private BasicDataSource dataSource;

	/**
	 * Database population scripts. The scripts are executed in the list order, ie from first to last The list may be left empty so that
	 * there are no rows in the database (only tables)
	 */
	private List<String> databasePopulationScripts;

	private boolean dumpDatabase = false;

	private String dumpFilename = this.getClass().getName();

	private DatabaseConnection databaseConnection;

	@Override
	public void setDatabasePopulationScripts(List<String> databaseScripts) {
		this.databasePopulationScripts = databaseScripts;
	}

	@Override
	public void setDumpDatabase(boolean dumpDatabase) {
		this.dumpDatabase = dumpDatabase;
	}

	@Override
	public void setDumpFilename(String dumpFilename) {
		this.dumpFilename = this.getClass().getName() + "." + dumpFilename;
	}

	@PostConstruct
	public void initConnexion() throws Exception {
		logger.debug("Initialisation of AbstractJunitTest.");

		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, dataSource.getDriverClassName());
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, dataSource.getUrl());
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, dataSource.getUsername());
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, dataSource.getPassword());
	}

	// @Override
	protected IDataSet getDataSet() throws Exception {
		List<IDataSet> dataSets = new LinkedList<>();

		DataFileLoader loader = new FlatXmlDataFileLoader();
		for (String script : databasePopulationScripts) {
			dataSets.add(loader.load(script));
		}

		IDataSet dataset = new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets.size()]));

		return dataset;
	}

	/**
	 * Drops the database schema so that it can be created on the next test
	 */
	@After
	public void dropDatabase() throws Exception {
		if (dumpDatabase) {
			File targetDirFile = new File("target/bdd/");
			if (!targetDirFile.exists()) targetDirFile.mkdir();

			File dumpFile = new File(targetDirFile, new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss.S").format(new java.util.Date()) + "-db-"
					+ dumpFilename + ".xml");

			IDataSet fullDataSet = getDatasource().createDataSet();
			FlatXmlDataSet.write(fullDataSet, new FileOutputStream(dumpFile));
		}

	}

	@Override
	@Before
	public void setUp() throws Exception {
		logger.debug("--> setUp");
		super.setUp();
		if (databasePopulationScripts == null) {
			logger.warn("No databasePopulationScripts.");
			return;
		} else {
			DatabaseOperation.CLEAN_INSERT.execute(getDatasource(), getDataSet());
		}
		logger.debug("<-- setUp");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		logger.debug("--> tearDown");
		super.tearDown();
		if (databasePopulationScripts == null) {
			logger.warn("No databasePopulationScripts.");
			return;
		} else {
			DatabaseOperation.DELETE_ALL.execute(getDatasource(), getDataSet());
		}

		closeConnection();
		logger.debug("<-- tearDown");
	}

	private DatabaseConnection getDatasource() throws DatabaseUnitException, SQLException {
		if (databaseConnection == null || databaseConnection.getConnection().isClosed()) {
			databaseConnection = new DatabaseConnection(dataSource.getConnection());
		}
		return databaseConnection;
	}

	private void closeConnection() throws SQLException {
		if (databaseConnection != null) databaseConnection.close();
	}
}
