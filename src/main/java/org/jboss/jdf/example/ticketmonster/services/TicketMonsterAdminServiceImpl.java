package org.jboss.jdf.example.ticketmonster.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.jdf.example.ticketmonster.admin.client.shared.TicketMonsterAdminService;
import org.jboss.jdf.example.ticketmonster.model.Show;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ApplicationScoped @Service
public class TicketMonsterAdminServiceImpl implements TicketMonsterAdminService {

    @Inject
    private EntityManager entityManager;

    @Override
    public List<Show> listAllShows() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Show> criteriaQuery = criteriaBuilder.createQuery(Show.class);
        Root<Show> from = criteriaQuery.from(Show.class);
        return entityManager.createQuery(criteriaQuery.select(from)).getResultList();
    }
}