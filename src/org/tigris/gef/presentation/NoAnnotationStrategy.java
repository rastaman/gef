package org.tigris.gef.presentation;

public class NoAnnotationStrategy extends AnnotationStrategy {
    private static NoAnnotationStrategy _instance = new NoAnnotationStrategy();

    public static NoAnnotationStrategy getInstance() {
        return _instance;
    }

    // Diese Klasse ist nur ein Dummy fuer die Figs, die keine Annotations
    // haben sollen. Sie wird standardmaessig fuer jede Fig angelegt.
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
