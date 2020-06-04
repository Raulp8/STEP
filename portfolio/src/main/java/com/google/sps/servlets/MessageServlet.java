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

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.Data;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;



/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

    static Gson gson = new Gson();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    int querySize = Integer.parseInt(request.getParameter("query-size"));
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    JsonArray queryResults = Data.fetchComments(query, querySize);

    response.setContentType("application/json;");
    response.getWriter().println(queryResults);
    System.out.println("fetched comments ");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String comment = request.getParameter("text-input");
      if (!comment.equals("")) {
        Data.addToData(comment);
      }
    //   response.sendRedirect("/");
  }

}

