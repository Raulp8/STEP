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

package com.google.sps.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;



public class Data {

    
    String text;

    static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public Data(String text) {
        this.text = text;
    }

    public static String fetchComments(Query query, int numComments) {

        Gson gson = new Gson();
        List<Entity> queryResults = datastore.
                prepare(query).asList(FetchOptions.Builder.withLimit(numComments));;
        return gson.toJson(queryResults);
    }

    public static void addToData(String input) {
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("value", input);
        commentEntity.setProperty("timestamp", System.currentTimeMillis());
        datastore.put(commentEntity); 
    
    }

    public static void DeleteData (Query query) {
        PreparedQuery queryResults = datastore.prepare(query);
        for (Entity entry: queryResults.asIterable()) {
            datastore.delete(entry.getKey());
        }
        
    }


}

