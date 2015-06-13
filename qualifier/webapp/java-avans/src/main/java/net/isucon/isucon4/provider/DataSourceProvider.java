package net.isucon.isucon4.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import net.isucon.isucon4.config.Config;
import net.isucon.isucon4.config.DataSourceConfig;

import com.zaxxer.hikari.HikariDataSource;

public class DataSourceProvider implements Provider<DataSource> {

	private HikariDataSource dataSource;

	@Inject
	public DataSourceProvider(Config config) {
		dataSource = new HikariDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl(config.getJdbc().getUrl());
		dataSource.setUsername(config.getJdbc().getUsername());
		dataSource.setPassword(config.getJdbc().getPassword());
		dataSource.setAutoCommit(false);

		// connection pool options
		DataSourceConfig dataSourceConfig = config.getDataSource();
		if (dataSourceConfig != null) {
			// TODO If you wanto to customize HikariCP's options. you should customize here.
			dataSource.setMinimumIdle(dataSourceConfig.getMinimumIdle());
			dataSource.setMaximumPoolSize(dataSourceConfig.getMaximumPoolSize());
		}
	}

	@Override
	public DataSource get() {
		return dataSource;
	}
}
