package br.com.topsys.database;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import br.com.topsys.auditoria.TSAuditoria;
import br.com.topsys.constant.TSConstant;
import br.com.topsys.constant.TSConstraint;
import br.com.topsys.database.factory.TSSequenceFactory;
import br.com.topsys.database.jdbc.TSDBList;
import br.com.topsys.exception.TSApplicationException;
import br.com.topsys.exception.TSBusinessException;
import br.com.topsys.exception.TSDataBaseException;
import br.com.topsys.exception.TSSystemException;
import br.com.topsys.util.TSCryptoUtil;
import br.com.topsys.util.TSLogUtil;
import br.com.topsys.util.TSPropertiesUtil;
import br.com.topsys.util.TSServiceLocatorUtil;
import br.com.topsys.util.TSUtil;

public abstract class TSDataBaseBrokerAb implements TSDataBaseBrokerIf {

	private Connection connection = null;

	protected PreparedStatement statement;

	protected ResultSet resultSet;

	protected CallableStatement callableStatement;

	protected int incremento = 1;

	protected String jndi;

	private StringBuilder sql = new StringBuilder();

	private String query;

	protected Calendar GMT;

	private Object[] parametros;

	private final static String MENSAGEM_METODO_INVALIDO = "Esse metódo não pode ser executado quando utilizado a classe TSDataBaseBroker, ou seja quando o projeto utiliza EJB!";

	public TSDataBaseBrokerAb() {

		this.jndi = this.getProperty(TSConstant.JNDI_CONNECTION);

		this.connection = this.getConnection(this.jndi);

	}

	public TSDataBaseBrokerAb(String jndi) {
		this.jndi = jndi;
		this.connection = this.getConnection(jndi);
	}

	public TSDataBaseBrokerAb(String className, String url, String user, String password) {
		try {
			Class.forName(className);
			this.connection = this.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new TSSystemException(e);
		}

	}

	public Long getSequenceNextValue(String nome) {
		return TSSequenceFactory.getSequenceIf(this.jndi).getNextValue(nome);
	}

	public Long getSequenceNextValue(String nome, String className) {
		return TSSequenceFactory.getSequenceIf(this.jndi, className).getNextValue(nome);
	}

	public Long getSequenceCurrentValue(String nome) {
		return TSSequenceFactory.getSequenceIf(this.jndi).getCurrentValue(nome);
	}

	public Long getSequenceCurrentValue(String nome, String className) {
		return TSSequenceFactory.getSequenceIf(this.jndi, className).getCurrentValue(nome);
	}

	private String getProperty(String chave) {
		return TSPropertiesUtil.getInstance().getProperty(chave);
	}

	private Connection getConnection(String jndi) {
		Connection connection = null;
		DataSource dataSource = null;

		try {

			dataSource = (DataSource) TSServiceLocatorUtil.getInstance().getDataSource(jndi);

			connection = dataSource.getConnection();

		} catch (Exception e) {
			throw new TSSystemException(e);
		}

		return connection;
	}

	private Connection getConnection(String url, String user, String password) {
		Connection connection = null;

		try {

			connection = DriverManager.getConnection(url, user, password);

		} catch (Exception e) {
			throw new TSSystemException(e);
		}

		return connection;
	}

	public Connection getConnection() {

		return connection;
	}

	public void addBatch() throws TSApplicationException {
		try {

			statement.addBatch();

		} catch (SQLException e) {
			// this.rollbackAutoCommitFalse();
			this.close();

			this.catchException(e);

		}

	}

	public void beginTransaction() {
		TSLogUtil.getInstance().severe(MENSAGEM_METODO_INVALIDO);
		throw new TSSystemException(MENSAGEM_METODO_INVALIDO, null);

	}

	public void close() {
		try {

			if (this.resultSet != null) {

				this.resultSet.close();
			}
		} catch (SQLException e) {
		}

		try {
			if (this.callableStatement != null) {

				this.callableStatement.close();
			}
		} catch (SQLException e) {
		}

		try {
			if (this.statement != null) {

				this.statement.close();
			}
		} catch (SQLException e) {
		}

		try {
			if (this.connection != null) {

				this.connection.close();
			}
		} catch (SQLException e) {
		}

	}

	public void endTransaction() {
		TSLogUtil.getInstance().severe(MENSAGEM_METODO_INVALIDO);
		throw new TSSystemException(MENSAGEM_METODO_INVALIDO, null);

	}

	public TSDBList executeQuery() {
		TSDBList list = null;
		try {
			list = new TSDBList(this.resultSet = statement.executeQuery());

		} catch (SQLException e) {
			this.close();

			throw new TSSystemException(e);
		}

		return list;
	}

	public boolean getAutoCommit() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List getCollectionBean(Class bean, String... values) {
		List array = new ArrayList();

		TSAuditoria auditoria = new TSAuditoria(this.query, this.parametros);

		try { 

			auditoria.begin();

			this.resultSet = this.statement.executeQuery();

			if (this.resultSet.last() && this.resultSet.getRow() >= 500) {

				inserirAuditoria(this.query, this.resultSet.getRow());
			}

			this.resultSet.beforeFirst();

			for (; this.resultSet.next();) {
				array.add(this.getObjectPopulate(bean, values));
			}

		} catch (Exception e) {

			throw new TSSystemException(e);

		} finally {
			this.close();
			auditoria.end();
		}

		return array;
	}

	private void inserirAuditoria(String sql, int row) {

		PreparedStatement ps = null;
		Connection conn = null;
		try {

			conn = this.getConnection(this.getProperty(TSConstant.JNDI_CONNECTION));
			ps = conn.prepareStatement("INSERT INTO AUDITORIA_SQL_LINHAS(SQL, QUANTIDADE) VALUES(?,?)");
			ps.setObject(1, sql);
			ps.setObject(2, row);
			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
			} catch (SQLException ex) {
			}

			try {
				if (conn != null) {

					conn.close();
				}
			} catch (SQLException ex) {
			}

		}

	}

	public Object getObjectBean(Class bean, String... values) {
		Object objeto = null;

		TSAuditoria auditoria = new TSAuditoria(this.query, this.parametros);

		try {

			auditoria.begin();

			this.resultSet = this.statement.executeQuery();

			if (this.resultSet.next()) {
				objeto = this.getObjectPopulate(bean, values);
			}

		} catch (Exception e) {

			throw new TSSystemException(e);
		} finally {
			this.close();
			auditoria.end();
		}

		return objeto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getList() {
		List objeto = new ArrayList();
		
		TSAuditoria auditoria = new TSAuditoria(this.query, this.parametros);

		try {

			auditoria.begin();
			
			this.resultSet = this.statement.executeQuery();

			while (this.resultSet.next()) {
				objeto.add(this.resultSet.getObject(1));
			}

		} catch (Exception e) {

			throw new TSSystemException(e);
			
		} finally {
			this.close();
			auditoria.end();
		}

		return objeto;
	}

	public Object getObject() {
		Object objeto = null;

		TSAuditoria auditoria = new TSAuditoria(this.query, this.parametros);

		try {

			auditoria.begin();

			this.resultSet = this.statement.executeQuery();

			if (this.resultSet.next()) {
				objeto = this.resultSet.getObject(1);
			}

		} catch (Exception e) {

			throw new TSSystemException(e);
		} finally {
			this.close();
			auditoria.end();
		}

		return objeto;
	}

	private Object getObjectPopulate(Class beanClass, String property[]) {

		StringBuilder methodName = new StringBuilder();
		Object objBean = null;
		TSDBList list = null;

		boolean flagDecrypt = false;

		try {

			objBean = beanClass.newInstance();

			list = new TSDBList(this.resultSet);

			for (int i = 0; i < property.length; i++) {

				String elementProperty[] = property[i].split("\\.");

				Class beanClassTmp = null;

				Object beanTmp = objBean;

				for (int y = 0; y < elementProperty.length; y++) {

					beanClassTmp = beanTmp.getClass();

					methodName.append("get");

					if (y == 0 && elementProperty[0].startsWith("decrypt")) {
						flagDecrypt = true;
						y = y + 1;
					}

					methodName.append(elementProperty[y].substring(0, 1).toUpperCase());

					methodName.append(elementProperty[y].substring(1));

					Method methodGet = beanClassTmp.getMethod(methodName.toString(), null);

					String returnClassName = methodGet.getReturnType().getName();

					methodName.replace(0, 1, "s");

					Method methodSet = beanClassTmp.getMethod(methodName.toString(),
							new Class[] { methodGet.getReturnType() });

					if (returnClassName.equals("java.lang.String")) {
						if (flagDecrypt) {
							methodSet.invoke(beanTmp,
									new Object[] { TSCryptoUtil.desCriptografar(list.getBytes(i + 1)) });
							flagDecrypt = false;
						} else {
							methodSet.invoke(beanTmp, new Object[] { list.getString(i + 1) });
						}

					} else if (returnClassName.equals("java.lang.Byte")) {
						methodSet.invoke(beanTmp, new Object[] { list.getByte(i + 1) });

					} else if (returnClassName.equals("java.lang.Short")) {
						methodSet.invoke(beanTmp, new Object[] { list.getShort(i + 1) });

					} else if (returnClassName.equals("java.lang.Integer")) {
						methodSet.invoke(beanTmp, new Object[] { list.getInteger(i + 1) });

					} else if (returnClassName.equals("java.lang.Long")) {
						if (flagDecrypt) {
							methodSet.invoke(beanTmp,
									new Object[] { !TSUtil.isEmpty(TSCryptoUtil.desCriptografar(list.getBytes(i + 1)))
											? Long.valueOf(TSCryptoUtil.desCriptografar(list.getBytes(i + 1)))
											: null });
							flagDecrypt = false;
						} else {

							methodSet.invoke(beanTmp, new Object[] { list.getLong(i + 1) });
						}

					} else if (returnClassName.equals("java.lang.Float")) {
						methodSet.invoke(beanTmp, new Object[] { list.getFloat(i + 1) });

					} else if (returnClassName.equals("java.lang.Double")) {
						methodSet.invoke(beanTmp, new Object[] { list.getDouble(i + 1) });

					} else if (returnClassName.equals("java.lang.Boolean")) {
						methodSet.invoke(beanTmp, new Object[] { list.getBoolean(i + 1) });

					} else if (returnClassName.equals("java.sql.Timestamp")) {
						methodSet.invoke(beanTmp, new Object[] { list.getTimestamp(i + 1) });

					} else if (returnClassName.equals("java.util.Date")) {

						methodSet.invoke(beanTmp, new Object[] { list.getTimestamp(i + 1) });

					} else if (returnClassName.equals("java.math.BigDecimal")) {
						methodSet.invoke(beanTmp, new Object[] { list.getBigDecimal(i + 1) });

					} else {
						Object beanReturn = methodGet.invoke(beanTmp, null);
						if (beanReturn == null) {
							beanReturn = methodGet.getReturnType().newInstance();
							methodSet.invoke(beanTmp, new Object[] { beanReturn });
						}
						beanTmp = beanReturn;
					}
					methodName.setLength(0);
				}

			}

		} catch (Exception e) {
			
			throw new TSSystemException(e);
		}
		return objBean;

	}

	public void rollback() {
		TSLogUtil.getInstance().severe(MENSAGEM_METODO_INVALIDO);
		throw new TSSystemException(MENSAGEM_METODO_INVALIDO, null);

	}

	public void set(Object... values) {

		if (values == null) {
            this.close();
			throw new IllegalArgumentException();

		}

		for (int x = 0; x < values.length; x++) {

			this.set(values[x]);

		}
		this.parametros = values;

	}

	public void set(Object value) {
		try {

			if (value == null) {

				statement.setNull(this.incremento++, Types.NULL);

			} else {

				if (value instanceof Timestamp) {
					if (this.GMT != null) {
						this.GMT.setTimeInMillis((((Timestamp) value).getTime()));
						statement.setTimestamp(this.incremento++, new Timestamp(this.GMT.getTimeInMillis()));
					} else {
						statement.setTimestamp(this.incremento++, (Timestamp) value);
					}

				} else if (value instanceof Date) {

					statement.setDate(this.incremento++, new java.sql.Date(((Date) value).getTime()));

				} else {

					statement.setObject(this.incremento++, value);

				}

			}
		} catch (SQLException e) {
			this.close();

			throw new TSSystemException(e);
		}
	}

	public void set(Object value, Calendar GMT) {
		try {

			if (value == null) {

				statement.setNull(this.incremento++, Types.NULL);

			} else {

				if (value instanceof Timestamp) {

					statement.setTimestamp(this.incremento++, (Timestamp) value, GMT);

				} else if (value instanceof Date) {

					statement.setDate(this.incremento++, new java.sql.Date(((Date) value).getTime()), GMT);

				} else {

					statement.setObject(this.incremento++, value);

				}

			}
		} catch (SQLException e) {
			this.close();

			throw new TSSystemException(e);
		}
	}

	public void setGMT(String GMT) {
		this.GMT = Calendar.getInstance(TimeZone.getTimeZone(GMT));
	}

	public void setPropertySQL(String query, Object... objects) {
		try {
			this.query = getProperty(query);
			statement = this.getConnection().prepareStatement(this.query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			this.set(objects);

		} catch (Exception e) {
			this.close();
			throw new TSSystemException(e);

		}

	}

	public void setPropertySQL(String query) {
		try {
			this.query = this.getProperty(query);
			statement = this.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

		} catch (Exception e) {
			this.close();
			throw new TSSystemException(e);

		}

	}

	public void setSQL(String query, Object... objects) {

		try {
			this.query = query;
			statement = this.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			this.set(objects);

		} catch (SQLException e) {
			this.close();

			throw new TSSystemException(e);
		}

	}

	public void setSQL(String query) {
		try {
			this.query = query;
			statement = this.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

		} catch (SQLException e) {
			this.close();

			throw new TSSystemException(e);
		}

	}

	protected void catchException(SQLException e) throws TSApplicationException {

		if (String.valueOf(TSConstraint.getUnique()).equals(e.getSQLState())
				|| e.getErrorCode() == TSConstraint.getUnique()) {

			throw new TSBusinessException(TSConstant.MENSAGEM_UNIQUE);

		} else if (String.valueOf(TSConstraint.getForeignKey()).equals(e.getSQLState())
				|| e.getErrorCode() == TSConstraint.getForeignKey()) {

			throw new TSBusinessException(TSConstant.MENSAGEM_FOREIGNKEY);

		} else if (String.valueOf(TSConstraint.getRaiseException()).contains(e.getSQLState())
				|| String.valueOf(e.getErrorCode()).equals(TSConstraint.getRaiseException())) {

			String message = TSUtil.isNotEmpty(e.getMessage()) ? e.getMessage()
					.replaceFirst(TSPropertiesUtil.getInstance().getProperty(TSConstant.TRATAMENTO_RAISE), "") : null;

			throw new TSDataBaseException(message);

		} else {
			throw new TSSystemException(e);
		}
	}

	public void addPropertySQL(String chave) {
		sql.append(this.getProperty(chave));
		sql.append(" ");

	}

	public String getPropertySQL() {
		return this.sql.toString();

	}

}
