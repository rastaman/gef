package org.tigris.gef.presentation;

public class NoAnnotationStrategy extends AnnotationStrategy {
    private static NoAnnotationStrategy _instance = new NoAnnotationStrategy();

    public static NoAnnotationStrategy getInstance() {
        return _instance;
    }

    /**
     * TODO - get this translated
     * Diese Klasse ist nur ein Dummy fuer die Figs, die keine Annotations
     * haben sollen. Sie wird standardmaessig fuer jede Fig angelegt.
     * @deprecated 0.10 will become private on 0.11
     */
    public NoAnnotationStrategy() {
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
