package org.tigris.gef.presentation;

import org.apache.log4j.Logger;

/**
 * A default Fig factory with convenience methods
 * for creating Figs given their class.
 * 
 * @author Bob Tarling
 */
final public class FigFactory extends AbstractFigFactory {

    private static final FigFactory instance = new FigFactory();
    
    private FigFactory() {}

    private static final Logger LOG = Logger.getLogger(FigFactory.class);
    
    public static FigFactory getInstance() {
        return instance;
    }
    
    /**
     * Create a new fig of the given classname
     * @param figClass the class of the Fig to create
     * @return A newly constructed Fig
     * @throws FigInstantiationException if the class is not a
     *         known Fig
     */
    public Fig createFig(Class figClass) throws FigInstantiationException {
        Fig fig = null;
        try {
            fig = (Fig)figClass.newInstance();
        } catch (InstantiationException e) {
            throw new FigInstantiationException();
        } catch (IllegalAccessException e) {
            throw new FigInstantiationException();
        } catch (ClassCastException e) {
            // Catch the case that we may have been given a class
            // name that is not a Fig
            throw new FigInstantiationException();
        }
        init(fig);
        if (LOG.isDebugEnabled()) LOG.debug("Factory created fig " + fig);
        return fig;
    }
}
