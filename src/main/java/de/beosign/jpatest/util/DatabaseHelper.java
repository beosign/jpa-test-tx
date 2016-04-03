package de.beosign.jpatest.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationScoped
public class DatabaseHelper
{
    private static final Logger logger = LogManager.getLogger();

    @Resource(mappedName = "java:jboss/datasources/jpatest")
    private DataSource          dataSource;

    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }

    public Number getCountUser() throws SQLException
    {
        Number count = executeSimpleQuery("SELECT count(*) FROM User");
        if (count == null)
        {
            count = new Long(0);
        }
        return count;
    }

    public void deleteAllUsers() throws SQLException
    {
        executeSql("DELETE FROM User");
    }

    @SuppressWarnings("unchecked")
    public <T> T executeSimpleQuery(String sql) throws SQLException
    {
        T result = null;
        try (
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery())
        {
            logger.debug("Executing: " + sql);
            if (rs.next())
            {
                result = (T) rs.getObject(1);
            }
        }
        logger.debug("Result: " + result);
        return result;
    }

    public void executeSql(String sql) throws SQLException
    {
        try (
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql))
        {
            logger.debug("Executing: " + sql);
            ps.executeUpdate();
        }
    }

}
