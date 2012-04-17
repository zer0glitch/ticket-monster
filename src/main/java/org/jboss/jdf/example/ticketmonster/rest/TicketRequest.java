package org.jboss.jdf.example.ticketmonster.rest;

/**
 * <p>
 *     Support class for unmarshalling booking requests received by {@link BookingService}
 * </p>
 * @author Marius Bogoevici
 */
public class TicketRequest {

    private long priceCategory;

    private int quantity;


    public long getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(long priceCategory) {
        this.priceCategory = priceCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
