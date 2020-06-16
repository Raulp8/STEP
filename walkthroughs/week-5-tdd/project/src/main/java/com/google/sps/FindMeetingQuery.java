// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import com.google.sps.TimeRange.*;
import com.google.sps.Event.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

      ArrayList<TimeRange> mtIntervals = new ArrayList();
      //Remove events with irrelevant people
      ArrayList<Event> eventsList = new ArrayList(events);
      eventsList.removeIf((Event event) -> {
          for (String attendee: request.getAttendees()) {
              if (event.getAttendees().contains(attendee)) {
                  //keep, atleast one relevant person
                  return false;
              }
          }
          //remove, no relevant people
          return true;
      });
      eventsList.sort(Event.ORDER_BY_START);

      //Keep Track of Latest Endpoint
      int latestEnd = TimeRange.START_OF_DAY;
      if (eventsList.size() > 0) {
          Event first = eventsList.get(0);
          if (request.getDuration() < first.getWhen().start()) {
              mtIntervals.add(TimeRange.fromStartEnd(latestEnd, first.getWhen().start(), false));
          }
          latestEnd = eventsList.get(0).getWhen().end();
      }
      for(int i = 1; i < eventsList.size(); i++) {
          Event currEvent = eventsList.get(i);
          int startCurr = currEvent.getWhen().start();
          if (startCurr - latestEnd >= request.getDuration()) {
              mtIntervals.add(TimeRange.fromStartEnd(latestEnd, startCurr, false));
          }
          if(currEvent.getWhen().end() > latestEnd) {
              latestEnd = currEvent.getWhen().end();
          }
      }
      //End of day
      if (request.getDuration() < TimeRange.END_OF_DAY - latestEnd) {
          mtIntervals.add(TimeRange.fromStartEnd(latestEnd, TimeRange.END_OF_DAY, true));
      }
      return mtIntervals;
  }

}
