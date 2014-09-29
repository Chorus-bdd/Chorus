/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

/**
 * Created by nick on 23/09/2014.
 */
public abstract class AbstractConfigValidator<E extends HandlerConfig> implements HandlerConfigValidator<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(AbstractConfigValidator.class);

    public boolean isValid(E config) {
        boolean valid = true;
        if ( ! isSet(config.getConfigName())) {
            valid = logInvalidConfig(log, "config groupName was null or empty", config);
        }

        if ( valid ) {
            valid = checkValid(config);
        }
        return valid;
    }

    protected abstract boolean checkValid(E config);

    protected boolean logInvalidConfig(ChorusLog log, String message, E config) {
        log.warn("Invalid " + config.getClass().getSimpleName() + " " + config.getConfigName() + " - " + message);
        return false;
    }

    protected boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }
}
