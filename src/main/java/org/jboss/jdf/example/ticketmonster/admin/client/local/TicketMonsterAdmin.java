package org.jboss.jdf.example.ticketmonster.admin.client.local;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.EntryPoint;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The entry point into the TicketMonster administration console.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
@EntryPoint
public class TicketMonsterAdmin {

    @PostConstruct
    public void createAndShowUI() {
        RootPanel.get().add(new Label("This is Errai. No, it's just GWT."));
    }
}
