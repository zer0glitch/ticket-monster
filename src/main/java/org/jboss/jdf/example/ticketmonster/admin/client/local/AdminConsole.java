package org.jboss.jdf.example.ticketmonster.admin.client.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.jdf.example.ticketmonster.admin.client.shared.AdminService;
import org.jboss.jdf.example.ticketmonster.model.Booking;
import org.jboss.jdf.example.ticketmonster.model.Performance;
import org.jboss.jdf.example.ticketmonster.model.Show;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * The entry point into the TicketMonster administration console.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@EntryPoint
public class AdminConsole {
    
    @Inject
    private Caller<AdminService> adminService;

    private Map<Show, ShowStatusWidget> shows = new HashMap<Show, ShowStatusWidget>();
    
    private static Map<Long, Long> occupiedCounts;
    
    @AfterInitialization
    public void createAndShowUI() {
        adminService.call(new RemoteCallback<Map<Long, Long>>() {
            @Override
            public void callback(Map<Long, Long> occupiedCounts) {
                AdminConsole.occupiedCounts = occupiedCounts;
                listShows();
            }
        }).retrieveOccupiedCounts();
    }

    private void listShows() {
        adminService.call(new RemoteCallback<List<Show>>() {
            @Override
            public void callback(List<Show> shows) {
                for (Show show : shows) {
                    ShowStatusWidget sw = new ShowStatusWidget(show);
                    AdminConsole.this.shows.put(show, sw);
                    RootPanel.get("content").add(sw);
                }
            }
        }).retrieveShows();
    }
    
    public void onBooking(@Observes Booking booking) {
        ShowStatusWidget sw = shows.get(booking.getPerformance().getShow());
        if (sw != null) {
            Long count = occupiedCounts.get(booking.getPerformance().getId());
            occupiedCounts.put(booking.getPerformance().getId(), count + booking.getTickets().size());
            sw.updatePerformance(booking.getPerformance());
        }
        //String text = booking.getContactEmail() + " created booking id:" + booking.getId();
        //RootPanel.get().add(new Label(text));
    }
    
    public static int getOccupiedCountForPerformance(Performance p) {
      Long count = occupiedCounts.get(p.getId());
      return (count == null) ? 0 : count.intValue();
    }
}