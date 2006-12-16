package org.tigris.gef.presentation;

public class NoAnnotationStrategy extends AnnotationStrategy {
    private static final NoAnnotationStrategy INSTANCE = new NoAnnotationStrategy();

    public static NoAnnotationStrategy getInstance() {
        return INSTANCE;
    }

    /**
     * TODO - get this translated
     * Diese Klasse ist nur ein Dummy fuer die Figs, die keine Annotations
     * haben sollen. Sie wird standardmaessig fuer jede Fig angelegt.
     */
    private NoAnnotationStrategy() {
    }

    public void translateAnnotations(Fig owner) {
        // do nothing in this case
    }

    /*
    protected void restoreAnnotationPosition(Fig annotation){
        // do nothing in this case
    }
    */

    public void storeAnnotationPosition(Fig annotation) {
        // do nothing in this case
    }
}
