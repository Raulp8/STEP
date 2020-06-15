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
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
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
@WebServlet("/stats")
public class statServlet extends HttpServlet {

    static Gson gson = new Gson();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      System.out.println("heyp!");
      response.sendRedirect("/stats.html"); 
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.getWriter().print(getData());

  }


  public String getData() {

      Query query =  new Query("Comment");
      HashMap<String, Integer> returnMap =  new HashMap();
      //Word Count
      List<Entity> threads =  Data.fetchComments(query, 100);
      for(Entity thread: threads) {
          String gsonString = (String) thread.getProperty("value");
          System.out.println(gsonString);
          JsonObject threadJsonRep = new JsonParser().parse(gsonString).getAsJsonObject();
          HashMap<String, Integer> replyRes = wordCount(threadJsonRep);
          replyRes.forEach((String key, Integer count) -> {
           returnMap.put(key, 
           returnMap.containsKey(key) ? returnMap.get(key) + count : 
            count);
            });
      }
      JsonObject jResponse = new JsonObject();
      jResponse.addProperty("wCount", gson.toJson(returnMap));
      return gson.toJson(jResponse);
      

  }

public HashMap<String, Integer> wordCount(JsonObject threadJRep) {

    HashMap<String, Integer> returnMap =  new HashMap();
    
    //comment
    String comment = threadJRep.get("text").getAsString();
    String [] commentWord = comment.split(" ");
    for (String word :commentWord) {
        returnMap.put(word, 
        returnMap.containsKey(word) ? returnMap.get(word) + 1: 
        1);
    }

    //replies
    JsonArray replies = threadJRep.get("replies").getAsJsonArray();
    java.util.Iterator<JsonElement> itReplies = replies.iterator();
    while(itReplies.hasNext()) {
        JsonElement reply = itReplies.next();
        HashMap<String, Integer> replyRes = 
            wordCount(reply.getAsJsonObject());
        replyRes.forEach((String key, Integer count) -> {
           returnMap.put(key, 
           returnMap.containsKey(key) ? returnMap.get(key) + count : 
        count);
        });
    }
    return returnMap;
}


}

