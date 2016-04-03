package de.beosign.jpatest.service;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import de.beosign.jpatest.util.PersistenceHelper;

@Stateful
@LocalBean
public class ExtendedEntityManagerTestService extends AbstractTestService
{
    @PersistenceContext(unitName = PersistenceHelper.PERSISTENCE_UNIT, type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

}
