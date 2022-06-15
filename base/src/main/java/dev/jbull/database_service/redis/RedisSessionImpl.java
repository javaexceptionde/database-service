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

package dev.jbull.database_service.redis;

import dev.jbull.database_service.DatabaseServiceImpl;
import dev.jbull.database_service.mariadb.Callback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.PipedOutputStream;
import java.util.concurrent.CompletableFuture;

public class RedisSessionImpl implements RedisSession {
    private JedisPool jedisPool;
    private JedisPoolConfig poolConfig;

    public RedisSessionImpl(String session){
        poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(3);
        poolConfig.setMaxTotal(Runtime.getRuntime().availableProcessors());
        System.out.println(DatabaseServiceImpl.get().getDatabaseconfig().getString("redis.sessions." + session + ".host"));
        jedisPool = new JedisPool(poolConfig, DatabaseServiceImpl.get().getDatabaseconfig().getString("redis.sessions." + session + ".host"), 6379);
    }

    @Override
    public void openConnection(Callback<Jedis> jedisCallback) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedisCallback.call(jedis);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    @Override
    public void openConnectionAsync(Callback<Jedis> jedisCallback) {
        CompletableFuture.runAsync(() -> {
            try(Jedis jedis = jedisPool.getResource()){
                jedisCallback.call(jedis);
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }

        });
    }
}
