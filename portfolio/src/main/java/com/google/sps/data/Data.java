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
import com.google.gson.JsonObject;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Data {

    
    String text;

    static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    static BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    
    public Data(String text) {
        this.text = text;
    }

    /*
     * Fetches all comments in datastore
     */
    public static List<Entity> fetchComments(Query query, int numComments) {
        List<Entity> queryResults = datastore.
                prepare(query).asList(FetchOptions.Builder.withLimit(numComments));;
        return queryResults;
    }


    /*
     * Formats new message
     */
    private static JsonObject newMessage(String input) {

        JsonObject jsonRep = new JsonObject();
        jsonRep.addProperty("text", input);
        jsonRep.add("replies", new JsonArray());
        return jsonRep;

    }

    /*
     * Creates new Thread and adds to datastore
     */
    public static void addToData(String input, String imageUrl, BlobKey bKey) {
        Gson gson = new Gson();
        Entity commentEntity = new Entity("Comment");
        JsonObject jsonRep = newMessage(input);
        commentEntity.setProperty("value", gson.toJson(jsonRep));
        commentEntity.setProperty("imageUrl", imageUrl);
        commentEntity.setProperty("blobKey", gson.toJson(bKey));
        commentEntity.setProperty("timestamp", System.currentTimeMillis());
        commentEntity.setProperty("like", 0);
        commentEntity.setProperty("views", 0);
        datastore.put(commentEntity); 
    
    }

    public static void updateViews(List<Entity> commentEntities) {
        for (Entity commentEntity: commentEntities) {
            commentEntity.setProperty("views", ((long)commentEntity.getProperty("views")) + 1);
            datastore.put(commentEntity);
        }
    }

    /*
     * Deletes Thread
     */
    public static void deleteKey(Key key) {
        Gson gson = new Gson();
        try {
        Entity entity = datastore.get(key);
        BlobKey bKey = gson.fromJson((String) entity.getProperty("blobKey"), BlobKey.class);
        datastore.delete(key);
        blobstoreService.delete(bKey);
        } catch(Exception EntityNotFoundException) {
            System.out.println(key.toString() + " null property");
        }
    }

    /*
     *Deletes all threads in a query
     */
    public static void DeleteData (Query query) {
        PreparedQuery queryResults = datastore.prepare(query);
        for (Entity entry: queryResults.asIterable()) {
            deleteKey(entry.getKey());
        }
        
    }
    /*
     * Adds reply to a thread
     */
    public static void reply(Key key, String reply, String path) {
        Gson gson = new Gson();
        try {
            Entity entry = datastore.get(key);
            String objectString = (String) entry.getProperty("value");
            JsonElement jsonElement = new JsonParser().parse(objectString);
            JsonObject thread = gson.toJsonTree(jsonElement).getAsJsonObject();
            JsonObject replyRep = newMessage(reply);
            JsonArray replies = thread.getAsJsonArray("replies");
            JsonArray pnt = replies;
            for(int i = 0; i < path.length(); i++) {
                int index = Character.getNumericValue(path.charAt(i));
                pnt = (JsonArray) pnt.get(index).getAsJsonObject().getAsJsonArray("replies");
            }
            pnt.add((JsonElement) replyRep);
            entry.setProperty("value", gson.toJson(thread));
            datastore.put(entry);

        } catch(Exception EntityNotFoundException) {
            System.out.println(key.toString() + "not found");
        }

    }


    public static void like (Key key) {
        try {
            Entity entry = datastore.get(key);
            entry.setProperty("like", (long) (entry.getProperty("like")) + 1);
            datastore.put(entry);
        } catch (Exception EntityNotFoundException) {
            System.out.println(key.toString() +  " not found");
        }
    }


}

