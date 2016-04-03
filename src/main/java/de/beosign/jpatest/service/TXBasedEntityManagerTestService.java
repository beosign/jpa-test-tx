package de.beosign.jpatest.service;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.beosign.jpatest.util.PersistenceHelper;

@Stateless
@LocalBean
public class TXBasedEntityManagerTestService extends AbstractTestService
{
    @PersistenceContext(unitName = PersistenceHelper.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

}
