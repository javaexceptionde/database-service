/*
 *
 *  *     Copyright (C) 2022  Jonathan Benedikt Bull<jonathan@jbull.dev>
 *  *
 *  *     This program is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU General Public License
 *  *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package dev.jbull.database_service.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBSessionImpl implements MongoDBSession{

    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;
    private final String session;

    public MongoDBSessionImpl(String connectionUrl, String session){
        this.session = session;
        mongoClient = MongoClients.create(MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionUrl)).build());
        mongoDatabase = mongoClient.getDatabase(session);
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    @Override
    public void updateDocument(String collection, String filterKey, Object filterValue, String updatedKey, Object updatedValue) {
        updateDocument(collection, new Document(filterKey, filterValue), new Document(updatedKey, updatedValue));
    }

    @Override
    public void updateDocument(String collection, Document filter, String updatedKey, Object updatedValue) {
        updateDocument(collection, filter, new Document(updatedKey, updatedValue));
    }

    @Override
    public void updateDocument(String collection, String filterKey, Object filterValue, Document update) {
        updateDocument(collection, new Document(filterKey, filterValue), update);
    }

    @Override
    public void updateDocument(String collection, Document filter, Document update) {
        getMongoCollection(collection).replaceOne(filter, update);
    }

    @Override
    public void insertOne(String collection, String key, Object value) {
        insertOne(collection, new Document(key, value));
    }

    @Override
    public void insertOne(String collection, Document document) {
        getMongoCollection(collection).insertOne(document);
    }

    @Override
    public void deleteOne(String collection, String filterKey, String filterValue) {
        deleteOne(collection, new Document(filterKey, filterValue));
    }

    @Override
    public void deleteOne(String collection, Document filter) {
        getMongoCollection(collection).deleteOne(filter);
    }

    @Override
    public Document getDocument(String collection, String filterKey, Object filterValue) {
        return getDocument(collection, new Document(filterKey, filterValue));
    }

    @Override
    public Document getDocument(String collection, Document filter) {
        return getMongoCollection(collection).find(filter).first();
    }

    @Override
    public MongoCollection<Document> getMongoCollection(String collection) {
        return mongoDatabase.getCollection(collection);
    }
}