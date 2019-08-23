/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.resources;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author mheimer
 */
//@ApplicationPath("resources")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();
        s.add(com.oracle.stcurr.ide.resources.Javac.class);
        return s;
    }

    @Override
    public Set<Object> getSingletons() {
        HashSet<Object> set = new HashSet<>(1);
        try {
            
//            set.add();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return set;
    }
}