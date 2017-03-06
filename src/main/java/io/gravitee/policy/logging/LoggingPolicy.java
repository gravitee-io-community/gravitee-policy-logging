/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.policy.logging;

import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.stream.BufferedReadWriteStream;
import io.gravitee.gateway.api.stream.ReadWriteStream;
import io.gravitee.gateway.api.stream.SimpleReadWriteStream;
import io.gravitee.policy.api.annotations.OnRequestContent;
import io.gravitee.policy.api.annotations.OnResponseContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LoggingPolicy {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPolicy.class);

    @OnRequestContent
    public ReadWriteStream onRequestContent(Request request) {
        return new BufferedReadWriteStream() {
            @Override
            public SimpleReadWriteStream<Buffer> write(Buffer content) {
                LOGGER.info(">>> {}/{}: {} {}", request.id(), request.transactionId(), request.method(), request.uri());
                request.headers().forEach((headerName, headerValues) -> LOGGER.info(">>> {}/{}: {}: {}",
                        request.id(), request.transactionId(), headerName, headerValues.stream().collect(Collectors.joining(","))));

                LOGGER.info(">>> {}: {}", request.id(), content);
                return super.write(content);
            }
        };
    }

    @OnResponseContent
    public ReadWriteStream onResponseContent(Request request, Response response) {
        return new BufferedReadWriteStream() {
            @Override
            public SimpleReadWriteStream<Buffer> write(Buffer content) {
                LOGGER.info("<<< {}/{}: HTTP Status - {}", request.id(), request.transactionId(), response.status());
                LOGGER.info("<<< {}: {}", request.id(), content);
                request.headers().forEach((headerName, headerValues) -> LOGGER.info("<<< {}/{}: {}: {}",
                        request.id(), request.transactionId(), headerName, headerValues.stream().collect(Collectors.joining(","))));
                return super.write(content);
            }
        };
    }
}
