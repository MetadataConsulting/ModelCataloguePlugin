/* Copyright 2012-2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.cache.util;

import grails.util.GrailsNameUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

/**
 * @author Jeff Brown
 */
public class ClassUtils {

    /**
     * This method will try to retrieve the value of the named property from the
     * object using a corresponding getter method.  If no getter method is found
     * then this method will look for the corresponding field and return its value.
     *
     * @param object object to inspect
     * @param propertyOrFieldName the name of the field or property to retrieve
     * @return the value of the field or property, null if neither is found
     */
    public static Object getPropertyOrFieldValue(Object object, String propertyOrFieldName) {
        final String getterName = GrailsNameUtils.getGetterName(propertyOrFieldName);
        final Class<? extends Object> objectClass = object.getClass();
        try {
            final Method method = objectClass.getMethod(getterName, new Class[0]);
            if (method != null) {
                ReflectionUtils.makeAccessible(method);
                return method.invoke(object, new Object[0]);
            }
        } catch (Exception e) {
        }
        try {
            final Field field = ReflectionUtils.findField(objectClass, propertyOrFieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return field.get(object);
            }
        } catch (Exception e) {
        }
        return null;
    }
}
