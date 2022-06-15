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

import dev.jbull.database_service.exceptions.SessionException;
import dev.jbull.database_service.exceptions.UnknownSessionException;
import dev.jbull.database_service.mariadb.MariadbSession;
import dev.jbull.database_service.mariadb.MariadbSessionImpl;

import java.util.HashMap;
import java.util.Map;

public class MariadbServiceAPIImpl implements MariadbServiceAPI{
    private Map<String, MariadbSession> mariadbSessionMap = new HashMap<>();

    @Override
    public MariadbSession startSession(String session) {
        if (mariadbSessionMap.containsKey(session)) {
            throw new SessionException("The session is already in use");
        }else {
            mariadbSessionMap.put(session, new MariadbSessionImpl(session));
            return mariadbSessionMap.get(session);
        }
    }

    @Override
    public MariadbSession getSession(String session) {
        if (mariadbSessionMap.containsKey(session)){
            return mariadbSessionMap.get(session);
        }
        throw new UnknownSessionException(session);
    }

}
