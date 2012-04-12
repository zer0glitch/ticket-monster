/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.jdf.example.ticketmonster.admin.client.local;

import org.jboss.jdf.example.ticketmonster.model.Performance;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class PerformanceStatusWidget extends Composite {

    private Label bookingStatusLabel = new Label();
    private HorizontalPanel progressBar = new HorizontalPanel();
    private Performance performance;

    public PerformanceStatusWidget(Performance performance) {
        this.performance = performance;

        setBookingStatus(2000, performance.getShow().getVenue().getCapacity());
        setProgress(2000f, (float)performance.getShow().getVenue().getCapacity());
        
        HorizontalPanel performancePanel = new HorizontalPanel();
        String date = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT).format(performance.getDate());
        performancePanel.add(new Label(date));
        performancePanel.add(progressBar);
        performancePanel.add(bookingStatusLabel);
        performancePanel.getElement().setAttribute("cellpadding", "5");
        initWidget(performancePanel);
    }

    private void setBookingStatus(int sold, int capacity) {
        bookingStatusLabel.setText(sold + " of " + capacity + " tickets booked");
    }
    
    private void setProgress(float sold, float capacity) {
        int soldPercent = Math.round((sold / capacity) * 100);
        
        Label soldPercentLabel = new Label();
        soldPercentLabel.getElement().setAttribute(
                "style", "height: 18px; width: " + soldPercent + "px;" + " background-color: #cc0000");
        
        Label availablePercentLabel = new Label();
        availablePercentLabel.getElement().setAttribute(
                "style", "height: 18px; width: " + (100 - soldPercent) + "px;" + " background-color: #009900");
    
        progressBar.add(soldPercentLabel);
        progressBar.add(availablePercentLabel);
    }
}