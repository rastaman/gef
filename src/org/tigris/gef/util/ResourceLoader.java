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

// File: ResourceLoader.java
// Classes: ResourceLoader
// Original Author: Thorsten Sturm
// $Id$

package org.tigris.gef.util;

import java.util.*;
import javax.swing.*;

/**
 *  This class manages the resource locations needed within the
 *  application. Already loaded resources are cached. The resources
 *  can be searched in different locations.
 *
 */

public class ResourceLoader
{
    private HashMap resourceCache;
    private List resourceLocations;
    private List resourceExtensions;
    
    private static ResourceLoader instance = null;

    private ResourceLoader() {
        resourceCache = new HashMap();
        resourceLocations = new ArrayList();
        resourceExtensions = new ArrayList();
    }
    
    private static ResourceLoader getInstance() {
        if ( instance == null )
            instance = new ResourceLoader();
        return instance;
    }
    
    /**
     * This method tries to find an ImageIcon for the given name
     * in all known locations. The file extension of the used image
     * file can be any of the known extensions.
     *
     * @param resource Name of the image to be looked after.
     * @param desc A description for the ImageIcon.
     * @return ImageIcon for the given name, null if no image could be found.
     */
    public static ImageIcon lookupIconResource(String resource) {
        return getInstance().doLookupIconResource(resource);
    }

    public static ImageIcon lookupIconResource(String resource, String desc) {
        return getInstance().doLookupIconResource(resource,desc);
    }

    /**
     * This method adds a new location to the list of known locations. 
     *
     * @param location String representation of the new location.
     */
    public static void addResourceLocation(String location) {
        getInstance().doAddResourceLocation(location);
    }

    /**
     * This method adds a new extension to the list of known extensions. 
     *
     * @param ext String representation of the new extension.
     */
    public static void addResourceExtension(String ext) {
        getInstance().doAddResourceExtension(ext);
    }

    /**
     * This method removes a location from the list of known locations. 
     *
     * @param location String representation of the location to be removed.
     */
    public static void removeResourceLocation(String location) {
        getInstance().doRemoveResourceLocation(location);
    }

    /**
     * This method removes a extension from the list of known extensions. 
     *
     * @param ext String representation of the extension to be removed.
     */
    public static void removeResourceExtension(String ext) {
        getInstance().doRemoveResourceExtension(ext);
    }

    public static boolean containsExtension(String ext) {
        return getInstance().doContainsExtension(ext);
    }
    
    public static boolean containsLocation(String location) {
        return getInstance().doContainsLocation(location);
    }
    
    public static boolean isInCache(String resource) {
        return getInstance().doIsInCache(resource);
    }
    
    protected synchronized void doAddResourceLocation(String location) {
        if ( resourceLocations == null ) 
            return;
        
        if ( !doContainsLocation(location) )
            resourceLocations.add(location);
    }

    protected synchronized void doAddResourceExtension(String extension) {
        if ( resourceExtensions == null ) 
            return;
        
        if ( !doContainsExtension(extension) )
            resourceExtensions.add(extension);
    }

    protected synchronized void doRemoveResourceLocation(String location) {
        if ( resourceLocations == null ) 
            return;

        for ( Iterator iter = resourceLocations.iterator(); iter.hasNext();) {
            String loc = (String)iter.next();
            if ( loc.equals(location) ) {
                resourceLocations.remove(loc);
                break;
            }
        }
    }

    protected synchronized void doRemoveResourceExtension(String extension) {
        if ( resourceExtensions == null ) 
            return;
        
        for ( Iterator iter = resourceExtensions.iterator(); iter.hasNext();) {
            String ext = (String)iter.next();
            if ( ext.equals(extension) ) {
                resourceExtensions.remove(ext);
                break;
            }
        }
    }

    protected boolean doContainsExtension(String extension) {
        if ( resourceExtensions == null ) 
            return false;
        
        return resourceExtensions.contains(extension);
    }

    protected boolean doContainsLocation(String location) {
        if ( resourceLocations == null ) 
            return false;
        
        return resourceLocations.contains(location);
    }

    protected boolean doIsInCache(String resource) {
        if ( resourceCache == null ) 
            return false;
        
        return resourceCache.containsKey(resource);
    }

    protected ImageIcon doLookupIconResource(String resource) {
        return doLookupIconResource(resource, resource);
    }

    protected ImageIcon doLookupIconResource(String resource, String desc) {
        String strippedName = Util.stripJunk(resource);
        if ( resourceCache.containsKey(strippedName) )
            return (ImageIcon)resourceCache.get(strippedName);
        
        ImageIcon res = null;
        java.net.URL imgURL = null;
        try {
            for (Iterator extensions = resourceExtensions.iterator(); extensions.hasNext();) {
                String tmpExt = (String)extensions.next();
                for (Iterator locations = resourceLocations.iterator(); locations.hasNext();) {
                    String imageName = (String)locations.next()+"/"+strippedName+"."+tmpExt;
                    imgURL = getClass().getResource(imageName);
                    if (imgURL != null)
                        break;
                }
                if (imgURL != null)
                    break;
            }
            if ( imgURL == null )
                return null;
            res = new ImageIcon(imgURL, desc);
            synchronized(resourceCache) {
                resourceCache.put(strippedName,res);
            }
            return res;
        }
        catch (Exception ex) {
            System.out.println("Exception in looking up IconResource");
            ex.printStackTrace();
            return new ImageIcon(strippedName);
        }
    }
        
} /* end class ResourceLoader */

