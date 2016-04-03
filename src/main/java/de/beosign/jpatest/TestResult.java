package de.beosign.jpatest;

import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceContextType;

public class TestResult
{
    private TransactionAttributeType transactionBean1;
    private TransactionAttributeType transactionBean2;
    private PersistenceContextType   persistenceContextType1;
    private PersistenceContextType   persistenceContextType2;

    private boolean                  success;
    private String                   message;
    private String                   name;

    public TestResult(String name, TransactionAttributeType transactionBean1, TransactionAttributeType transactionBean2, PersistenceContextType persistenceContextType1,
            PersistenceContextType persistenceContextType2, boolean success, String message)
    {
        this.name = name;
        this.transactionBean1 = transactionBean1;
        this.transactionBean2 = transactionBean2;
        this.persistenceContextType1 = persistenceContextType1;
        this.persistenceContextType2 = persistenceContextType2;
        this.success = success;
        this.message = message;
    }

    public TransactionAttributeType getTransactionBean1()
    {
        return transactionBean1;
    }

    public TransactionAttributeType getTransactionBean2()
    {
        return transactionBean2;
    }

    public PersistenceContextType getPersistenceContextType1()
    {
        return persistenceContextType1;
    }

    public PersistenceContextType getPersistenceContextType2()
    {
        return persistenceContextType2;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setTransactionBean1(TransactionAttributeType transactionBean1)
    {
        this.transactionBean1 = transactionBean1;
    }

    public void setTransactionBean2(TransactionAttributeType transactionBean2)
    {
        this.transactionBean2 = transactionBean2;
    }

    public void setPersistenceContextType1(PersistenceContextType persistenceContextType1)
    {
        this.persistenceContextType1 = persistenceContextType1;
    }

    public void setPersistenceContextType2(PersistenceContextType persistenceContextType2)
    {
        this.persistenceContextType2 = persistenceContextType2;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "TestResult [name=" + name + ", success=" + success + ", message=" + message + ", transactionBean1=" + transactionBean1 + ", transactionBean2=" + transactionBean2
                + ", persistenceContextType1=" + persistenceContextType1 + ", persistenceContextType2=" + persistenceContextType2 + "]";
    }

}
