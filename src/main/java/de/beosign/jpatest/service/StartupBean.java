package de.beosign.jpatest.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceContextType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.beosign.jpatest.TestResult;
import de.beosign.jpatest.util.DatabaseHelper;

/**
 * Used during startup to insert some test data.
 * 
 * @author Florian Dahlmanns
 */
// Because we want EJB functionality (TX management), we use @Singleton, and not @ApplicationScoped
// See http://germanescobar.net/2010/04/4-areas-of-possible-confusion-in-jee6.html
@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.NEVER)
public class StartupBean
{
    private static final Logger logger = LogManager.getLogger(StartupBean.class);

    @Inject
    private DatabaseHelper      databaseHelper;

    @PostConstruct
    private void init()
    {
        logger.debug("Initializing");
        List<TestResult> testResults = new ArrayList<>();

        List<Class<? extends TestService>> testServices = new ArrayList<>();
        testServices.add(TXBasedEntityManagerTestService.class);
        testServices.add(ExtendedEntityManagerTestService.class);

        for (Class<? extends TestService> testServiceClass : testServices)
        {
            try
            {
                TestService ts = lookup(testServiceClass);
                testResults.add(testTxReqTxReq(ts));
                databaseHelper.deleteAllUsers();
            }
            catch (Exception e)
            {
                logger.error("testTxReqTxReq", e);
            }

            try
            {
                TestService ts = lookup(testServiceClass);
                testResults.add(testTxReqTxNew(ts));
                databaseHelper.deleteAllUsers();
            }
            catch (SQLException | NamingException e)
            {
                logger.error("testTxReqTxNew", e);
            }

            try
            {
                TestService ts = lookup(testServiceClass);
                testResults.add(testTxReqTxReqInnerException(ts));
                databaseHelper.deleteAllUsers();
            }
            catch (SQLException | NamingException e)
            {
                logger.error("testTxReqTxReqInnerException", e);
            }

            try
            {
                TestService ts = lookup(testServiceClass);
                testResults.add(testTxReqTxNewInnerException(ts));
                databaseHelper.deleteAllUsers();
            }
            catch (SQLException | NamingException e)
            {
                logger.error("testTxReqTxNewInnerException", e);
            }

            try
            {
                TestService ts = lookup(testServiceClass);
                testResults.add(testTxReqTxReqOuterException(ts));
                databaseHelper.deleteAllUsers();
            }
            catch (SQLException | NamingException e)
            {
                logger.error("testTxReqTxReqOuterException", e);
            }
        }

        logger.info("=========================");
        logger.info("== TEST RESULTS ==");
        logger.info("== " + testResults.size() + " total, " + testResults.stream().filter(tr -> tr.isSuccess()).count() + " SUCCESS, "
                + testResults.stream().filter(tr -> !tr.isSuccess()).count() + " FAILED");
        testResults.forEach(tr -> logger.info(tr));
        logger.info("=========================");

    }

    public TestResult testTxReqTxReq(TestService testService) throws SQLException
    {
        TestResult testResult = testService.testTxReqTxReq();
        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 2)
        {
            testResult.setSuccess(false);
            testResult.setMessage("Commit should have led to two user entries, but found " + countUser);
        }
        else
        {
            testResult.setSuccess(true);
        }
        logger.debug(testResult);
        return testResult;
    }

    public TestResult testTxReqTxNew(TestService testService) throws SQLException
    {
        TestResult testResult = testService.testTxReqTxNew();
        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 2)
        {
            testResult.setSuccess(false);
            testResult.setMessage("Two commits should have led to two user entries, but found " + countUser);
        }
        else
        {
            testResult.setSuccess(true);
        }
        logger.debug(testResult);
        return testResult;
    }

    public TestResult testTxReqTxReqInnerException(TestService testService) throws SQLException
    {
        PersistenceContextType type = AbstractTestService.getPersistenceContextType(testService);

        TestResult testResult = new TestResult("testTxReqTxReqInnerException", TransactionAttributeType.REQUIRED, TransactionAttributeType.REQUIRED,
                type,
                PersistenceContextType.TRANSACTION, false, "");
        try
        {
            testService.testTxReqTxReqInnerException();

        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
            testResult.setSuccess(true);
        }

        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 0)
        {
            testResult.setSuccess(false);
            testResult.setMessage("No commits expected , but found " + countUser + " users");
        }

        logger.debug(testResult);
        return testResult;
    }

    public TestResult testTxReqTxNewInnerException(TestService testService) throws SQLException
    {
        TestResult testResult = testService.testTxReqTxReqNewInnerException();
        testResult.setSuccess(true);

        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 1)
        {
            testResult.setSuccess(false);
            testResult.setMessage("1 entry expected, but found " + countUser + " users");
        }

        String login = databaseHelper.executeSimpleQuery("SELECT login from USER");
        if (!AbstractTestService.USER1.getLogin().equals(login))
        {
            testResult.setSuccess(false);
            testResult.setMessage("Wrong user: expected " + AbstractTestService.USER1.getLogin() + ", but found " + login);
        }

        logger.debug(testResult);
        return testResult;
    }

    public TestResult testTxReqTxReqOuterException(TestService testService) throws SQLException
    {
        PersistenceContextType type = AbstractTestService.getPersistenceContextType(testService);

        TestResult testResult = new TestResult("testTxReqTxReqOuterException", TransactionAttributeType.REQUIRED, TransactionAttributeType.REQUIRED,
                type,
                PersistenceContextType.TRANSACTION, false, "");

        try
        {
            testService.testTxReqTxReqOuterException();
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
            testResult.setSuccess(true);
        }

        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 0)
        {
            testResult.setSuccess(false);
            testResult.setMessage("0 entries expected, but found " + countUser + " users");
        }

        logger.debug(testResult);
        return testResult;
    }

    private static <T> T lookup(Class<T> clazz) throws NamingException
    {
        InitialContext ic = new InitialContext();
        return clazz.cast(ic.lookup("java:global/JpaTest/" + clazz.getSimpleName() + "!" + clazz.getName()));

    }

}
