package org.jboss.jdf.example.ticketmonster.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.jdf.example.ticketmonster.admin.client.shared.AdminService;
import org.jboss.jdf.example.ticketmonster.model.Show;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ApplicationScoped @Service
public class AdminServiceImpl implements AdminService {

    @Inject
    private EntityManager entityManager;

    @Override
    public List<Show> retrieveShows() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Show> criteriaQuery = criteriaBuilder.createQuery(Show.class);
        Root<Show> from = criteriaQuery.from(Show.class);
        return entityManager.createQuery(criteriaQuery.select(from)).getResultList();
    }

    @Override
    public Map<Long, Long> retrieveOccupiedCounts() {
      Map <Long, Long> occupiedCounts = new HashMap<Long, Long>();

      Query occupiedCountsQuery = entityManager.createQuery("" +
          		"select s.performance.id, SUM(s.occupiedCount) from SectionAllocation s GROUP BY s.performance.id");
      List<Object[]> results = occupiedCountsQuery.getResultList();
      for (Object[] result : results) {
        occupiedCounts.put((Long) result[0], (Long) result[1]); 
      }
      
      return occupiedCounts;
    }
}