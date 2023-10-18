/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.logging.log4j2.filter;

import io.microsphere.logging.filter.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.util.function.Function;

import static org.apache.logging.log4j.core.util.Watcher.ELEMENT_TYPE;

/**
 * {@link Filter} Adapter for Log4j2
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Plugin(name = "Log4j2FilterAdapter", category = Node.CATEGORY, elementType = ELEMENT_TYPE, printObject = true)
public class Log4j2FilterAdapter extends AbstractFilter {

    private final Filter filter;

    public Log4j2FilterAdapter(Filter filter) {
        super(toResult(filter, io.microsphere.logging.filter.AbstractFilter::getOnMatch),
                toResult(filter, io.microsphere.logging.filter.AbstractFilter::getOnMismatch));
        this.filter = filter;
    }

    @Override
    public Result filter(LogEvent event) {
        String loggerName = event.getLoggerName();
        String level = event.getLevel().name();
        String message = event.getMessage().getFormattedMessage();
        // delegate maybe some Filter or CompositeFilter
        Filter.Result result = filter.filter(loggerName, level, message);
        return toResult(result);
    }

    protected static Result toResult(Filter filter, Function<io.microsphere.logging.filter.AbstractFilter, Filter.Result> resultResolver) {
        if (filter instanceof io.microsphere.logging.filter.AbstractFilter) {
            io.microsphere.logging.filter.AbstractFilter abstractFilter =
                    (io.microsphere.logging.filter.AbstractFilter) filter;
            return toResult(resultResolver.apply(abstractFilter));
        }
        return Result.NEUTRAL;
    }

    protected static Result toResult(Filter.Result result) {
        switch (result) {
            case ACCEPT:
                return Result.ACCEPT;
            case DENY:
                return Result.DENY;
            default:
                return Result.NEUTRAL;
        }
    }
}
