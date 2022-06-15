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

package dev.jbull.database_service.api;

import dev.jbull.database_service.DatabaseServiceImpl;
import dev.jbull.database_service.exceptions.SessionException;
import dev.jbull.database_service.exceptions.UnknownSessionException;
import dev.jbull.database_service.mongodb.MongoDBSession;
import dev.jbull.database_service.mongodb.MongoDBSessionImpl;

import java.util.HashMap;
import java.util.Map;

public class MongoDBServiceAPIImpl implements MongoDBServiceAPI{
    private Map<String, MongoDBSession> sessionMap = new HashMap<>();

    @Override
    public MongoDBSession startSession(String session) {
        if (sessionMap.containsKey(session)){
            throw new SessionException("The session is already in use");
        }else {
            if (!DatabaseServiceImpl.get().getDatabaseconfig().exists("mongodb.sessions." + session)) {
                throw new SessionException("The session doesn't exists in the config");
            }
            MongoDBSession mongoDBSession = new MongoDBSessionImpl(DatabaseServiceImpl.get().getDatabaseconfig().getString("mongodb.sessions." + session + ".connection_string"), session);
            sessionMap.put(session, mongoDBSession);
            return mongoDBSession;
        }
    }

    @Override
    public void destroySession(String session) {
        if (sessionMap.containsKey(session)){
            sessionMap.get(session).close();
            sessionMap.remove(session);
        }
        throw new UnknownSessionException(session);
    }

    @Override
    public MongoDBSession getSession(String session) {
        if (sessionMap.containsKey(session)) {
            return sessionMap.get(session);
        }
        throw new UnknownSessionException(session);
    }
}
