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

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface MongoDBSession {

    public void close();

    public void updateDocument(String collection, String filterKey, Object filterValue, String updatedKey, Object updatedValue);

    public void updateDocument(String collection, Document filter, String updatedKey, Object updatedValue);

    public void updateDocument(String collection, String filterKey, Object filterValue, Document update);

    public void updateDocument(String collection, Document filter, Document update);

    public void insertOne(String collection, String key, Object value);

    public void insertOne(String collection, Document document);

    public void deleteOne(String collection, String filterKey, String filterValue);

    public void deleteOne(String collection, Document filter);

    public Document getDocument(String collection, String filterKey, Object filterValue);

    public Document getDocument(String collection, Document filter);

    public MongoCollection<?> getMongoCollection(String collection);

}
