// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// File: Localizer.java
// Classes: Localizer
// Original Author: Thorsten Sturm, Luc Maisonobe
// $Id$

package org.tigris.gef.util;

import java.awt.Toolkit;

import java.util.StringTokenizer;

import java.util.*;
import javax.swing.*;

/**
 *  This class manages the resource bundle files needed to localize the
 *  application. All registered resource files are searched in order
 *  to find the localization of a given string.
 *
 */

public class Localizer {
    private static Localizer instance = null;
    
    private Map locales;
    private Map resourceNames;
    private Locale defaultLocale;
    private Map defaultResources;
    
    private Localizer() {
        locales = new HashMap();
        resourceNames = new HashMap();
        defaultLocale = Locale.getDefault();
        defaultResources = new HashMap();
        locales.put(defaultLocale,defaultResources);
    }
    
    protected static Localizer getInstance() {
        if ( instance == null )
            instance = new Localizer();
        
        return instance;
    }
    
    public static void initialize() {
        instance = new Localizer();
    }
    
    public static String localize(String key) {
        return key;
    }
    
    public static KeyStroke getShortcut(String key) {
        return null;
    }
    
    public static String getMnemonic(String key) {
        return key;
    }
    
    /**
     * This method tests, if a resource with the given name is registered.
     *
     * @param resource Name of the resource to be tested.
     * @return True, if a resource with the given name is registered, otherwise false.
     */
    public static boolean containsResource(String resource) {
        return getInstance().doContainsResource(resource);
    }
    
    protected boolean doContainsResource(String resource) {
        return resourceNames.containsValue(resource);
    }
    
    /**
     * This method tests, if the given locale is registered.
     *
     * @param locale Locale to be tested.
     * @return True, if the given locale is registered, otherwise false.
     */
    public static boolean containsLocale(Locale locale) {
        return getInstance().doContainsLocale(locale);
    }
    
    protected boolean doContainsLocale(Locale locale) {
        return locales.containsKey(locale);
    }
    
    /**
     * The method addLocale adds a new locale to the set of known locales
     * for the application. For a new locale, all known ResourceBundles are
     * added when possible.
     *
     * @see java.util.ResourceBundle
     * @see java..util.Locale
     */
    public static void addLocale(Locale locale) {
        getInstance().doAddLocale(locale);
    }
    
    protected synchronized void doAddLocale(Locale locale) {
        Map resources = new HashMap();
        Iterator iter = resourceNames.keySet().iterator();
        
        while (iter.hasNext()) {
            try {
                String binding = (String)iter.next();
                String resourceName = (String)resourceNames.get(binding);
                ResourceBundle bundle = ResourceBundle.getBundle(resourceName,locale);
                if (bundle == null)
                    continue;
                
                if (bundle instanceof ResourceBundle)
                    resources.put(binding,bundle);
            }
            catch (MissingResourceException missing) {
                continue;
            }
        }
        locales.put(locale,resources);
    }
    
    /**
     * The method changes the current locale to the given one. The resources bound
     * to the given locale are also preloaded. If the given locale is not already registered,
     * it will be registered automatically.
     *
     * @see java..util.Locale
     */
    public static void switchCurrentLocale(Locale locale) {
        getInstance().doSwitchCurrentLocale(locale);
    }
    
    protected synchronized void doSwitchCurrentLocale(Locale locale) {
        if (!locales.containsKey(locale))
            doAddLocale(locale);
        
        if (!defaultLocale.equals(locale)) {
            defaultLocale = locale;
            defaultResources = (Map)locales.get(locale);
        }
    }
    
    /**
     * The method returns the current locale.
     *
     * @return The current locale
     */
    public static Locale getCurrentLocale() {
        return getInstance().doGetCurrentLocale();
    }
    
    protected Locale doGetCurrentLocale() {
        return defaultLocale;
    }
    
    /**
     * The method returns all resources for the given locale.
     *
     * @param locale Resources are searched for this locale.
     * @return Map of all resources and their names bound to the given locale.
     */
    public static Map getResourcesFor(Locale locale) {
        return getInstance().doGetResourcesFor(locale);
    }
    
    protected Map doGetResourcesFor(Locale locale) {
        if (!doContainsLocale(locale))
            return null;
        
        return (Map)locales.get(locale);
    }
    
    /**
     * The method adds a new resource under the given name. The resource is preloaded
     * and bound to a given locale or to every registered locale, if no locale is given.
     *
     * @param resourceName Name of the resource to be registered.
     * @param binding Name under which the resource should be registered.
     * @param locale Locale to which the resource should be bound.
     */
    public static void addResource(String binding, String resourceName)
    throws MissingResourceException {
        getInstance().doAddResource(binding,resourceName);
    }
    
    public static void addResource(String binding, String resourceName, ClassLoader loader)
    throws MissingResourceException {
        getInstance().doAddResource(binding,resourceName,loader);
    }
    
    protected synchronized void doAddResource(String binding, String resourceName)
    throws MissingResourceException {
        doAddResource(binding, resourceName, getClass().getClassLoader());
    }
    
    protected synchronized void doAddResource(String binding, String resourceName, ClassLoader loader)
    throws MissingResourceException {
        if ( doContainsResource(resourceName) )
            return;
        
        Iterator iter = locales.keySet().iterator();
        
        while (iter.hasNext()) {
            doAddResource(binding,resourceName,(Locale)iter.next(), loader);
        }
    }
    
    public static void addResource(String binding, String resourceName, Locale locale)
    throws MissingResourceException{
        getInstance().doAddResource(binding,resourceName,locale);
    }
    
    public static void addResource(String binding, String resourceName, Locale locale, ClassLoader loader)
    throws MissingResourceException{
        getInstance().doAddResource(binding,resourceName,locale,loader);
    }
    
    protected synchronized void doAddResource(String binding, String resourceName, Locale locale)
    throws MissingResourceException {
        doAddResource(binding, resourceName, locale, getClass().getClassLoader());
    }
    
    protected synchronized void doAddResource(String binding, String resourceName, Locale locale, ClassLoader loader)
    throws MissingResourceException {
        ResourceBundle resource = null;
        if (doContainsLocale(locale) ) {
            Map resources = (Map)locales.get(locale);
            resource = ResourceBundle.getBundle(resourceName,locale, loader);
            resources.put(binding,resource);
            if (!resourceNames.containsValue(resourceName))
                resourceNames.put(binding,resourceName);
        }
        else
            throw new MissingResourceException("Locale not found!", locale.toString(), resourceName);
    }
    
    /**
     * The method removes the given locale from the list of known locales. If the locale
     * is the current locale, the current locale is switched to the systems default locale.
     *
     * @param locale Locale to be removed.
     */
    public static void removeLocale(Locale locale) {
        getInstance().doRemoveLocale(locale);
    }
    
    protected synchronized void doRemoveLocale(Locale locale) {
        if ( defaultLocale.equals(locale) )
            doSwitchCurrentLocale(Locale.getDefault());
        
        locales.remove(locale);
    }
    
    /**
     * The method removes the given resource from the list of used resources.
     * Any binding from any locale to that resource is also removed.
     *
     * @param binding Name under which the resource to be removed is registered.
     */
    public static void removeResource(String binding) {
        getInstance().doRemoveResource(binding);
    }
    
    protected synchronized void doRemoveResource(String binding) {
        Iterator iter = locales.keySet().iterator();
        
        while (iter.hasNext()) {
            Locale tmpLocale = (Locale)iter.next();
            ((Map)locales.get(tmpLocale)).remove(binding);
        }
        resourceNames.remove(binding);
    }
    
    /**
     *    This function returns a localized string corresponding
     *    to the specified key. Searching goes through all registered
     *    ResourceBundles
     *
     *    @param key String to be localized.
     *    @param locale Language to be localized to.
     *    @param resources Set of ResourceBundles to searched in.
     *    @return First localization for the given string found in the registered
     *    ResourceBundles, the key itself if no localization has been found.
     */
    public static String localize(String binding, String key) {
        return getInstance().doLocalize(binding,key);
    }
    
    private final String doLocalize(String binding, String key) {
        return doLocalize(binding, key, defaultLocale, defaultResources);
    }
    
    public static String localize(String binding, String key, Locale locale, Map resources) {
        return getInstance().doLocalize(binding, key, locale, resources);
    }
    
    private final String doLocalize(String binding, String key, Locale locale, Map resources) {
        if (locale == null || resources == null || !doContainsLocale(locale))
            return key;
        
        String localized = null;
        
        ResourceBundle resource = (ResourceBundle)resources.get(binding);
        if ( resource == null ) {
            //System.out.println("[Localizer] localization failed for key " + key + " (binding: " + binding + ")");
            return key;
        }
        try {
            localized = resource.getString(key);
        }
        catch (MissingResourceException e) {}
        if (localized == null) {
            //System.out.println("[Localizer] localization failed for key " + key + " (binding: " + binding + ")");
            localized = key;
        }
        
        return localized;
    }
    
    /**
     * This function returns a localized menu shortcut key
     * to the specified key.
     *
     * @param binding Name of resource to be searched.
     * @param key Shortcut string to be localized.
     * @return Localized KeyStroke object.
     */
    public static KeyStroke getShortcut(String binding, String key) {
        return getInstance().doGetShortcut(binding,key);
    }
    
    private final KeyStroke doGetShortcut(String binding, String key) {
        return doGetShortcut(binding, key, defaultLocale, defaultResources);
    }
    
    public static KeyStroke getShortcut(String binding, String key, Locale locale, Map resources) {
        return getInstance().doGetShortcut(binding, key, locale, resources);
    }
    
    protected final static String SHORTCUT_MODIFIER = "shortcut";
    
    private final KeyStroke doGetShortcut(String binding, String key, Locale locale, Map resources) {
        if (locale == null || resources == null || !doContainsLocale(locale))
            return null;
        
        KeyStroke stroke = null;
        ResourceBundle resource = (ResourceBundle)resources.get(binding);
        try {
            Object obj = resource.getObject(key);
            if (obj instanceof KeyStroke) {
                stroke = (KeyStroke) obj;
            }
            else if (obj instanceof String) {
                boolean hasShortcutModifier = false;
                StringBuffer shortcutBuf = new StringBuffer();
                
                StringTokenizer tokenizer = new StringTokenizer((String) obj);
                while(tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    
                    if (token.equals(SHORTCUT_MODIFIER)) {
                        hasShortcutModifier = true;
                    }
                    else {
                        shortcutBuf.append(token);
                        shortcutBuf.append(" ");
                    }
                }
                stroke = KeyStroke.getKeyStroke(shortcutBuf.toString());
                int modifiers = stroke.getModifiers() 
                    | (hasShortcutModifier ? Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() : 0);
                int keyCode = stroke.getKeyCode();
                stroke = KeyStroke.getKeyStroke(keyCode, modifiers);
            }
        }
        catch (MissingResourceException e) {}
        catch (ClassCastException e) {

        }
        catch(NullPointerException e) {}
        return stroke;
    }
    
} /* end class Localizer */

