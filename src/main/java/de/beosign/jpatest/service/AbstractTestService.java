package de.beosign.jpatest.service;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static javax.persistence.PersistenceContextType.TRANSACTION;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContextType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.beosign.jpatest.TestResult;
import de.beosign.jpatest.domain.User;
import de.beosign.jpatest.util.DatabaseHelper;

public abstract class AbstractTestService implements TestService
{
    static final User               USER1            = new User("test1", "test1FirstName", "test1LastName");
    private static final AtomicLong INSTANCE_COUNTER = new AtomicLong(0);

    private static Logger           logger           = LogManager.getLogger();

    @Inject
    private DatabaseHelper          databaseHelper;

    @Inject
    private InnerTestServiceBean        testServiceBean2;

    @PostConstruct
    private void init()
    {
        logger.debug(getClass().getSimpleName() + " instances: {}", INSTANCE_COUNTER.incrementAndGet());
    }

    protected abstract EntityManager getEntityManager();

    private PersistenceContextType getPersistenceContextType()
    {
        return getPersistenceContextType(this);
    }

    public static PersistenceContextType getPersistenceContextType(TestService service)
    {
        PersistenceContextType type = PersistenceContextType.TRANSACTION;
        if (service instanceof ExtendedEntityManagerTestService)
        {
            type = PersistenceContextType.EXTENDED;
        }
        return type;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestResult testTxReqTxReq() throws SQLException
    {
        PersistenceContextType type = getPersistenceContextType();
        logger.debug("Testing T1-REQ (" + type + ") calling T2-REQ");
        TestResult testResult = new TestResult("testTxReqTxReq", REQUIRED, REQUIRED, type, TRANSACTION, false, "");

        getEntityManager().persist(USER1);

        testServiceBean2.createTxRequired();

        // no commit has been done yet
        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser > 0)
        {
            testResult.setMessage("No commit should have been done by now, user entries = " + countUser);
            return testResult;
        }

        logger.debug("Leaving");
        return testResult;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestResult testTxReqTxNew() throws SQLException
    {
        PersistenceContextType type = getPersistenceContextType();
        logger.debug("Testing T1-REQ (" + type + ") calling T2-NEW");
        TestResult testResult = new TestResult("testTxReqTxNew", REQUIRED, REQUIRES_NEW, type, TRANSACTION, false, "");

        getEntityManager().persist(USER1);

        testServiceBean2.createTxNew();

        // commit of "inner" TX has been done
        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 1)
        {
            testResult.setMessage("Commit of 'inner' TX should have been done by now (1 entry expected), but user entry count is actual " + countUser);
            return testResult;
        }

        logger.debug("Leaving");
        return testResult;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestResult testTxReqTxReqInnerException()
    {
        PersistenceContextType type = getPersistenceContextType();
        logger.debug("Testing T1-REQ (" + type + ") calling T2-REQ where T2 throws RTE");
        TestResult testResult = new TestResult("testTxReqTxReqInnerException", REQUIRED, REQUIRES_NEW, type, TRANSACTION, false, "");

        getEntityManager().persist(USER1);

        testServiceBean2.createTxRequiredWithInnerException();

        // won't be executed
        return testResult;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestResult testTxReqTxReqNewInnerException() throws SQLException
    {
        PersistenceContextType type = getPersistenceContextType();
        logger.debug("Testing T1-REQ (" + type + ") calling T2-NEW where T2 throws RTE");
        TestResult testResult = new TestResult("testTxReqTxReqNewInnerException", REQUIRED, REQUIRES_NEW, type, TRANSACTION, false, "");

        getEntityManager().persist(USER1);

        try
        {
            testServiceBean2.createTxNewWithInnerException();
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
            testResult.setSuccess(true);
        }

        // rollback of "inner" TX
        int countUser = databaseHelper.getCountUser().intValue();
        if (countUser != 0)
        {
            testResult.setMessage("'Inner' TX should have been rollbacked (0 entries expected), but user entry count is actual " + countUser);
            return testResult;
        }

        return testResult;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestResult testTxReqTxReqOuterException()
    {
        PersistenceContextType type = getPersistenceContextType();
        logger.debug("Testing T1-REQ (" + type + ") calling T2-REQ where T1 throws RTE");
        getEntityManager().persist(USER1);

        testServiceBean2.createTxRequired();

        throw new RuntimeException("Test RTE thrown from 'outer' TX");
    }

}
