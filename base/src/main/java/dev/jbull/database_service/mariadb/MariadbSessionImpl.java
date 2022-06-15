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

package dev.jbull.database_service.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.jbull.database_service.DatabaseServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MariadbSessionImpl implements MariadbSession {

    private HikariConfig config;
    private HikariDataSource hikari;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public MariadbSessionImpl(String session){
        config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl("jdbc:mariadb://" + DatabaseServiceImpl.get().getDatabaseconfig().
                getString("mariadb.sessions." + session + ".host") + ":" + DatabaseServiceImpl.get().getDatabaseconfig()
                .getString("mariadb.sessions." + session + ".port") + "/" + DatabaseServiceImpl.get().getDatabaseconfig()
                .getString("mariadb.sessions." + session + ".database") );
        config.setUsername(DatabaseServiceImpl.get().getDatabaseconfig().getString("mariadb.sessions." + session + ".username"));
        config.setPassword(DatabaseServiceImpl.get().getDatabaseconfig().getString("mariadb.sessions." + session + ".password") );
        config.setMaximumPoolSize(Runtime.getRuntime().availableProcessors());
        config.setMinimumIdle(3);
        config.setIdleTimeout(Duration.ofMinutes(1).toMillis());
        config.setMaxLifetime(Duration.ofMinutes(10).toMillis());
        config.setConnectionTimeout(30000);
        config.setValidationTimeout(30000);
        hikari = new HikariDataSource(config);
    }

    @Override
    public Future<Void> openConnectionAsync(Callback<Connection> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = hikari.getConnection()) {
                callback.call(connection);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        }, executor);
    }

    @Override
    public <T> T openConnection(IThrowableCallback<Connection, T> callback) {
        try(Connection connection = hikari.getConnection()) {
            return callback.call(connection);
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
