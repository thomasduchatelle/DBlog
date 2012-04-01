package fr.dush.test.dblog.dao.context;

import javax.sql.DataSource;

import fr.dush.test.dblog.exceptions.ConfigurationException;

public interface IDatasourceFactory {

	DataSource createDataSource() throws ConfigurationException;

}