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
import java.util.List;
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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory.Builder;



/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/reply")
public class ReplyServlet extends HttpServlet {

    static Gson gson = new Gson();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String kind = request.getParameter("kind");
      String id = request.getParameter("id");
      String replyText = request.getParameter("reply-text");
      String path = request.getParameter("path");
      if (path == null) {
          path = "";
      }
      System.out.println(path);
      if (kind != null && id != null && replyText  != null) {
          Data.reply(new Builder(kind, Long.parseLong(id)).getKey(), replyText, path);
      }
  }

}

