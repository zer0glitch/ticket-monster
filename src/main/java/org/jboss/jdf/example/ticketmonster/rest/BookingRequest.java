package org.jboss.jdf.example.ticketmonster.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     Support class for unmarshalling booking requests received by {@link BookingService}.
 * </p>
 * @author Marius Bogoevici
 */
public class BookingRequest {

    private List<TicketRequest> ticketRequests = new ArrayList<TicketRequest>();
    private long performance;
    private String email;

    public List<TicketRequest> getTicketRequests() {
        return ticketRequests;
    }

    public void setTicketRequests(List<TicketRequest> ticketRequests) {
        this.ticketRequests = ticketRequests;
    }

    public long getPerformance() {
        return performance;
    }

    public void setPerformance(long performance) {

        this.performance = performance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
