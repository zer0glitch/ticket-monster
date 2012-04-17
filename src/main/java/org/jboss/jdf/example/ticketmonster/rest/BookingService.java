package org.jboss.jdf.example.ticketmonster.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.jdf.example.ticketmonster.model.Booking;
import org.jboss.jdf.example.ticketmonster.model.Performance;
import org.jboss.jdf.example.ticketmonster.model.Seat;
import org.jboss.jdf.example.ticketmonster.model.Section;
import org.jboss.jdf.example.ticketmonster.model.Ticket;
import org.jboss.jdf.example.ticketmonster.model.TicketCategory;
import org.jboss.jdf.example.ticketmonster.model.TicketPriceCategory;
import org.jboss.jdf.example.ticketmonster.service.SeatAllocationService;


/**
 * <p>
 *     A JAX-RS endpoint for handling {@link Booking}s. Inherits the GET
 *     methods from {@link BaseEntityService}, and implements additional REST methods.
 * </p>
 *
 * @author Marius Bogoevici
 */
@Path("/bookings")
/**
 * <p>
 *     This is a stateless service, so a single shared instance can be used in this case.
 * </p>
 */
@Singleton
public class BookingService extends BaseEntityService<Booking> {

    @Inject
    SeatAllocationService seatAllocationService;

    public BookingService() {
        super(Booking.class);
    }

    /**
     * <p>
     *     A method for deleting bookings.
     * </p>
     * @param id
     * @return
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteBooking(@PathParam("id") Long id) {
        Booking booking = getEntityManager().find(Booking.class, id);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        getEntityManager().remove(booking);
        return Response.ok().build();
    }

    /**
     * <p>
     *     A method for creating new bookings.
     * </p>
     * @param bookingRequest
     * @return
     */
    @SuppressWarnings("unchecked")
    @POST
    /**
     * <p> Data is received in JSON format. For easy handling, it will be unmarshalled in the support
     * {@link BookingRequest} class.
     */
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBooking(BookingRequest bookingRequest) {
        try {
            // load the performance for which this request has been created
            Performance performance = getEntityManager().find(Performance.class, bookingRequest.getPerformance());

            // identify the ticket price categories in this request
            Set<Long> priceCategoryIds = new HashSet<Long>();
            for (TicketRequest ticketRequest : bookingRequest.getTicketRequests()) {
                if (priceCategoryIds.contains(ticketRequest.getPriceCategory())) {
                    throw new RuntimeException("Duplicate price category id");
                }
                priceCategoryIds.add(ticketRequest.getPriceCategory());
            }
            // load the ticket price categories
            List<TicketPriceCategory> ticketPriceCategories =
                    (List<TicketPriceCategory>) getEntityManager().createQuery("select p from TicketPriceCategory p where p.id in :ids").setParameter("ids", priceCategoryIds).getResultList();

            // organize price categories in a map indexed by their id
            // we will need them later, and this provides a more efficient way of accessing them
            Map<Long, TicketPriceCategory> priceCategoriesById = new HashMap<Long, TicketPriceCategory>();
            for (TicketPriceCategory loadedPriceCategory : ticketPriceCategories) {
                priceCategoriesById.put(loadedPriceCategory.getId(), loadedPriceCategory);
            }

            // aggregate ticket requests by section
            // we want to allocate ticket requests that belong to the same section contiguously
            Map<Section, Map<TicketCategory, TicketRequest>> ticketRequestsPerSection = new LinkedHashMap<Section, Map<TicketCategory, TicketRequest>>();
            for (TicketRequest ticketRequest : bookingRequest.getTicketRequests()) {
                final TicketPriceCategory priceCategory = priceCategoriesById.get(ticketRequest.getPriceCategory());
                if (!ticketRequestsPerSection.containsKey(priceCategory.getSection())) {
                    ticketRequestsPerSection.put(priceCategory.getSection(), new LinkedHashMap<TicketCategory, TicketRequest>());
                }
                ticketRequestsPerSection.get(priceCategory.getSection()).put(priceCategoriesById.get(ticketRequest.getPriceCategory()).getTicketCategory(), ticketRequest);
            }

            // create a new booking and fill it with data
            Booking booking = new Booking();
            booking.setContactEmail(bookingRequest.getEmail());

            // iterate sections for which we have requests
            for (Section section : ticketRequestsPerSection.keySet()) {
                int totalTicketsRequestedPerSection = 0;
                final Map<TicketCategory, TicketRequest> ticketRequestsByCategories = ticketRequestsPerSection.get(section);
                // calculate the total quantity of tickets to be allocated in this section
                for (TicketRequest ticketRequest : ticketRequestsByCategories.values()) {
                    totalTicketsRequestedPerSection += ticketRequest.getQuantity();
                }
                // try to allocate seats - if this fails, an exception will be thrown
                List<Seat> seats = seatAllocationService.allocateSeats(section, performance, totalTicketsRequestedPerSection, true);
                // allocation was successful, begin generating tickets
                // associate each allocated seat with a ticket, assigning a price category to it
                int seatCounter = 0;
                for (TicketCategory ticketCategory : ticketRequestsByCategories.keySet()) {
                    final TicketRequest ticketRequest = ticketRequestsByCategories.get(ticketCategory);
                    final TicketPriceCategory ticketPriceCategory = priceCategoriesById.get(ticketRequest.getPriceCategory());
                    for (int i = 0; i < ticketRequest.getQuantity(); i++) {
                        Ticket ticket = new Ticket(seats.get(seatCounter + i), ticketCategory, ticketPriceCategory.getPrice());
                        getEntityManager().persist(ticket);
                        booking.getTickets().add(ticket);
                    }
                    seatCounter += ticketRequest.getQuantity();
                }
            }
            // everything went ok, now we can persist the booking
            booking.setPerformance(performance);
            booking.setCancellationCode("abc");
            getEntityManager().persist(booking);
            return Response.ok().entity(booking).type(MediaType.APPLICATION_JSON_TYPE).build();
        } catch (ConstraintViolationException e) {
            Map<String, Object> errors = new HashMap<String, Object>();
            List<String> errorMessages = new ArrayList<String>();
            for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
                errorMessages.add(constraintViolation.getMessage());
            }
            errors.put("errors", errorMessages);
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<String, Object>();
            errors.put("errors", Collections.singletonList(e.getMessage()));
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
    }

}
