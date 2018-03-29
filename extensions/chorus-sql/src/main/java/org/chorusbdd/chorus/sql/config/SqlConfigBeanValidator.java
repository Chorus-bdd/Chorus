/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBeanValidator;
import org.chorusbdd.chorus.util.ChorusException;

/**
 * Created by nickebbutt on 27/02/2018.
 */
public class SqlConfigBeanValidator extends AbstractConfigBeanValidator<SqlConfig> {

    protected boolean checkValid(SqlConfig sqlConfig) {
        boolean valid = true;

        if (! isSet(sqlConfig.getUrl())) {
            logInvalidConfig(SqlConfigBuilderFactory.url + " cannot be null", sqlConfig);
            valid = false;
        } else if ( ! isSet(sqlConfig.getDriverClassName())) {
            logInvalidConfig(SqlConfigBuilderFactory.driverClassName + " cannot be null", sqlConfig);
            valid = false;
        }
        return valid;
    }

}
