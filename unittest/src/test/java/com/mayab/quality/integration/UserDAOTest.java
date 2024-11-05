package com.mayab.quality.integration;

import java.io.File;
import java.io.FileInputStream;
import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mayab.quality.loginunittest.dao.UserMySqlDao;
import com.mayab.quality.loginunittest.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;



class UserDAOTest extends DBTestCase {
    UserMySqlDao daoMySql;

    public UserDAOTest() {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3306/calidad2024");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "root");
    }

    @BeforeEach
    protected void setUp() throws Exception {
        daoMySql = new UserMySqlDao(); 
        IDatabaseConnection connection = getConnection();
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

        try {
            DatabaseOperation.TRUNCATE_TABLE.execute(connection, getDataSet());
            DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());
        } catch (Exception e) {
            fail("Error in setup: " + e.getMessage());
        } finally {
            connection.close();
        }
    }

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/initDB.xml"));
    }

    @Test
    public void testAgregarUsuario() {
        User usuario = new User("username2", "correo2@correo.com", "123456");
        daoMySql.save(usuario);

        try {
            IDatabaseConnection conn = getConnection();
            conn.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
            IDataSet databaseDataSet = conn.createDataSet();
            ITable actualTable = databaseDataSet.getTable("usuarios");
            //read xml with expected result
            IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("src/resources/create.xml"));
            ITable expectedTable = expectedDataSet.getTable("usuarios");


            Assertion.assertEquals(expectedTable, actualTable);
        } catch (Exception e) {
            fail("Error in insert test: " + e.getMessage());
        }
    }
    
    @Test
    public void testAddUser() {
        // Initialize a new User object
        User usuario = new User("username2", "correo2@correo.com", "123456");
        
        // Save the user and retrieve the new ID if necessary
        int newID = daoMySql.save(usuario);

     // Verify data in database
        try {
            // This is the full database
            IDatabaseConnection conn = getConnection(); // Connection
            IDataSet databaseDataSet = conn.createDataSet(); // DB

            QueryDataSet actualTable = new QueryDataSet(getConnection());
            actualTable.addTable("insertTMP", "SELECT * FROM usuarios WHERE id = " + newID);

            String actualName = (String) actualTable.getTable("insertTMP").getValue(0, "name");
            String actualEmail = (String) actualTable.getTable("insertTMP").getValue(0, "email");
            String actualPassword = (String) actualTable.getTable("insertTMP").getValue(0, "password");

            // Read XML with the expected result
            IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("src/resources/create.xml"));
            ITable expectedTable = expectedDataSet.getTable("usuarios");

            assertThat(actualName, is(usuario.getName()));
            assertThat(actualEmail, is(usuario.getEmail()));
            assertThat(actualPassword, is(usuario.getPassword()));

        } catch (Exception e) {
            // TODO: handle exception
            fail("Error in insert test: " + e.getMessage());
        }

    }

}
