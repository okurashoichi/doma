/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.converter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author taedium
 * 
 */
public class StringConverter implements Converter<String> {

    protected final ConversionSupport conversionSupport = new ConversionSupport();

    @Override
    public String convert(Object value, String pattern) {
        if (value == null) {
            return null;
        }
        if (pattern != null) {
            if (Number.class.isInstance(value)) {
                return format(Number.class.cast(value), pattern);
            }
            if (Date.class.isInstance(value)) {
                return format(Date.class.cast(value), pattern);
            }
        }
        if (BigDecimal.class.isInstance(value)) {
            BigDecimal decimal = BigDecimal.class.cast(value);
            return decimal.toPlainString();
        }
        return value.toString();
    }

    protected String format(Number value, String pattern) {
        return conversionSupport.formatFromNumber(value, pattern);
    }

    protected String format(Date value, String pattern) {
        return conversionSupport.formatFromDate(value, pattern);
    }
}
