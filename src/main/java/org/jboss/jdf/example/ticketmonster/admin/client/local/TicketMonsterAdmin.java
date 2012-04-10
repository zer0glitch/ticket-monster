package org.jboss.jdf.example.ticketmonster.admin.client.local;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.jdf.example.ticketmonster.model.Booking;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The entry point into the TicketMonster administration console.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
@EntryPoint
public class TicketMonsterAdmin {

    @PostConstruct
    public void createAndShowUI() {
        RootPanel.get().add(new Label("This is Errai receiving CDI events for created bookings!"));
    }
    
    public void onBooking(@Observes Booking booking) {
      String text = booking.getContactEmail() + " created booking id:" + booking.getId();
      RootPanel.get().add(new Label(text));
    }
}
