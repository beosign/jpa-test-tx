package de.beosign.jpatest.service;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.beosign.jpatest.domain.User;
import de.beosign.jpatest.util.PersistenceHelper;

@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InnerTestServiceBean
{
    static final User               USER2            = new User("test2", "test2FirstName", "test2LastName");
    private static final AtomicLong INSTANCE_COUNTER = new AtomicLong(0);

    private static final Logger     logger           = LogManager.getLogger();

    @PersistenceContext(unitName = PersistenceHelper.PERSISTENCE_UNIT)
    private EntityManager           em;

    @PostConstruct
    private void init()
    {
        logger.debug(getClass().getSimpleName() + " instances: {}", INSTANCE_COUNTER.incrementAndGet());
    }

    public void createSomethingWithRuntimeException()
    {
        logger.debug("Creating something with RTE");

        logger.debug("About to leave");
        throw new RuntimeException("Should rollback");

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createTxRequired()
    {
        logger.debug("Creating within same transaction");

        em.persist(USER2);

        logger.debug("About to leave");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createTxRequiredWithInnerException()
    {
        logger.debug("Creating within same transaction, but will throw RTE at the end");

        em.persist(USER2);

        logger.debug("Throwing runtime exception");
        throw new RuntimeException("Testing rollback of 'inner' exception");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createTxNewWithInnerException()
    {
        logger.debug("Creating within new transaction, but will throw RTE at the end");

        em.persist(USER2);

        logger.debug("Throwing runtime exception");
        throw new RuntimeException("Testing rollback of 'inner' exception");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createTxNew()
    {
        logger.debug("Creating with NEW transaction");

        em.persist(USER2);

        logger.debug("About to leave");
    }

}
