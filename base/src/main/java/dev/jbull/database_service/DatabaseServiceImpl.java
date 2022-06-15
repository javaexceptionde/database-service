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

package dev.jbull.database_service;

import dev.jbull.configservice.ConfigService;
import dev.jbull.configservice.api.ConfigServiceAPI;
import dev.jbull.configservice.config.Config;
import dev.jbull.core.Core;
import dev.jbull.core.service.CoreService;
import dev.jbull.core.service.Service;
import dev.jbull.database_service.api.*;

import java.io.File;

@CoreService(
        name = "database-service",
        requiredServices = {
                "config-service"
        },
        autoLoad = true
)
public class DatabaseServiceImpl extends Service implements DatabaseService{
    private ConfigService configService;
    private ConfigServiceAPI configServiceAPI;
    private Config databaseconfig;
    private static DatabaseServiceImpl instance;
    private RedisServiceAPI redisServiceAPI;
    private MongoDBServiceAPI mongoDBServiceAPI;
    private MariadbServiceAPI mariadbServiceAPI;

    @Override
    public void onEnable() {
        configService = (ConfigService) Core.getCore().getCoreBridge().getService(ConfigService.class);
        configServiceAPI = configService.getConfigServiceAPI();
        File file = new File(Core.getCore().getDefaultPath() + "/config/");
        file.mkdir();
        file = new File(Core.getCore().getDefaultPath() + "/config/database-service.yml");
        databaseconfig = configServiceAPI.createConfig(file);
        databaseconfig.addDefault("mongodb.use", true);
        databaseconfig.addDefault("mongodb.sessions.core.connection_string", "mongodb://myDBReader:D1fficultP%40ssw0rd@127.0.0.1:27017/?authSource=admin");
        databaseconfig.addDefault("mariadb.use", false);
        databaseconfig.addDefault("mariadb.sessions.core.host", "127.0.0.1");
        databaseconfig.addDefault("mariadb.sessions.core.port", "3306");
        databaseconfig.addDefault("mariadb.sessions.core.database", "core");
        databaseconfig.addDefault("mariadb.sessions.core.user", "root");
        databaseconfig.addDefault("mariadb.sessions.core.password", "12345");
        databaseconfig.addDefault("redis.use", true);
        databaseconfig.addDefault("redis.sessions.core.host", "127.0.0.1");
        databaseconfig.addDefault("redis.sessions.core.port", "6379");
        databaseconfig.addDefault("redis.sessions.core.password", "12345");
        databaseconfig.setDefaults();
        if (!databaseconfig.getBoolean("mongodb.use") && !databaseconfig.getBoolean("mariadb.use")){
            System.err.println("Couldn't start database service, at least one storage database(mongodb and mariadb) must be enabled.");
            onDisable();
            return;
        }
        if (databaseconfig.getBoolean("mongodb.use")) {
            mongoDBServiceAPI = new MongoDBServiceAPIImpl();
        }
        if (databaseconfig.getBoolean("mariadb.use")) {
            mariadbServiceAPI = new MariadbServiceAPIImpl();
        }
        if (databaseconfig.getBoolean("redis.use")) {
            redisServiceAPI = new RedisServiceAPIImpl();
        }
        System.out.println("Successfully finished database service initialization");
        instance = this;
    }

    @Override
    public void onDisable() {
        databaseconfig = null;
        configServiceAPI = null;
        configService = null;
        System.out.println("Database service was shutdown");
    }

    public Config getDatabaseconfig() {
        return databaseconfig;
    }

    public static DatabaseServiceImpl get() {
        return instance;
    }

    @Override
    public MongoDBServiceAPI getMongoDBServiceAPI() {
        return mongoDBServiceAPI;
    }

    @Override
    public MariadbServiceAPI getMariadbServiceAPI() {
        return mariadbServiceAPI;
    }

    @Override
    public RedisServiceAPI getRedisServiceAPI() {
        return redisServiceAPI;
    }
}
