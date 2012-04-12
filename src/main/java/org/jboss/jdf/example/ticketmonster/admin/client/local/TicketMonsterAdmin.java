package org.jboss.jdf.example.ticketmonster.admin.client.local;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.jdf.example.ticketmonster.admin.client.shared.TicketMonsterAdminService;
import org.jboss.jdf.example.ticketmonster.model.Booking;
import org.jboss.jdf.example.ticketmonster.model.Show;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The entry point into the TicketMonster administration console.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@EntryPoint
public class TicketMonsterAdmin {
    @Inject
    private Caller<TicketMonsterAdminService> adminService;

    @AfterInitialization
    public void createAndShowUI() {
        adminService.call(new RemoteCallback<List<Show>>() {
            @Override
            public void callback(List<Show> shows) {
                for (Show show : shows) {
                    RootPanel.get("content").add(new ShowStatusWidget(show));
                }
            }
        }).listAllShows();
    }

    public void onBooking(@Observes Booking booking) {
        String text = booking.getContactEmail() + " created booking id:" + booking.getId();
        RootPanel.get().add(new Label(text));
    }
}