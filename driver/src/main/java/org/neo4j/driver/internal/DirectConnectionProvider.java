/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.internal;

import java.util.concurrent.CompletionStage;

import org.neo4j.driver.internal.spi.Connection;
import org.neo4j.driver.internal.spi.ConnectionPool;
import org.neo4j.driver.internal.spi.ConnectionProvider;
import org.neo4j.driver.v1.AccessMode;

import static org.neo4j.driver.v1.AccessMode.READ;

/**
 * Simple {@link ConnectionProvider connection provider} that obtains connections form the given pool only for
 * the given address.
 */
public class DirectConnectionProvider implements ConnectionProvider
{
    private final BoltServerAddress address;
    private final ConnectionPool connectionPool;

    DirectConnectionProvider( BoltServerAddress address, ConnectionPool connectionPool )
    {
        this.address = address;
        this.connectionPool = connectionPool;
    }

    @Override
    public CompletionStage<Connection> acquireConnection( AccessMode mode )
    {
        return connectionPool.acquire( address );
    }

    @Override
    public CompletionStage<Void> verifyConnectivity()
    {
        return acquireConnection( READ ).thenCompose( Connection::release );
    }

    @Override
    public CompletionStage<Void> close()
    {
        return connectionPool.close();
    }

    public BoltServerAddress getAddress()
    {
        return address;
    }
}
