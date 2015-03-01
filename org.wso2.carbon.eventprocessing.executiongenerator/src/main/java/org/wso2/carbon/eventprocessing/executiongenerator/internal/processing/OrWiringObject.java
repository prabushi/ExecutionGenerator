package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

/**
 * Created by prabushi on 3/1/15.
 */
public class OrWiringObject extends WiringObject {
    /**
     * constructor
     *
     * @param type template type
     */
    public OrWiringObject(String type) {
        super(WiringObject.Type.valueOf(type));
    }
}
