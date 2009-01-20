/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.javaee.core.deployment;

import java.net.URLClassLoader;
import java.net.URL;
import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Simplistic class loader which will delegate to each module class loader in the order
 * they were added to the instance
 *
 * @author Jerome Dochez
 */
public class EarClassLoader extends URLClassLoader {

    private final List<ClassLoader> delegates = new LinkedList<ClassLoader>();
    private final Method findClass; 

    public EarClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
        try {
            findClass = ClassLoader.class.getDeclaredMethod("findClass", new Class[] {String.class});
            findClass.setAccessible(true);
        } catch(NoSuchMethodException e) {
            // this is impossible.
            throw new RuntimeException(e);
        }
    }

    public void addModuleClassLoader(ClassLoader cl) {
        delegates.add(cl);
    }

    @Override
    protected Class<?> findClass(String s) throws ClassNotFoundException {
        for (ClassLoader cl : delegates) {
            try {
                return (Class<?>) findClass.invoke(cl, s);
            } catch(IllegalAccessException e) {
                
            } catch(InvocationTargetException e) {
                // not found most likely.   
            }
        }
        throw new ClassNotFoundException(s);
    }
}
