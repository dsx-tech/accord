package uk.dsx.accord.common;


public interface Processor<C extends Container> {

    void process(C container);

}
