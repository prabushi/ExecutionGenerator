package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

/**
 * represent an object withing template wiring
 */
public class WiringObject {
    public enum Type{left,right}
    private String inStream;
    private String outStreamLeft;
    private String outStreamRight;
    private WiringObject left;
    private WiringObject right;
    private WiringObject parent;
    private Type type;

    /**
     * constructor
     *
     * @param type        template type
     */
    public WiringObject(Type type) {
        super();
        this.inStream = "";
        this.outStreamLeft = "";
        this.outStreamRight = "";
        this.type = type;
    }

    /**
     * get left child
     *
     * @return left child
     */
    public WiringObject getLeft() {
        return left;
    }

    /**
     * set left child
     *
     * @param left left child
     */
    public void setLeft(WiringObject left) {
        this.left = left;
    }

    /**
     * get right child
     *
     * @return right child
     */
    public WiringObject getRight() {
        return right;
    }

    /**
     * set right child
     *
     * @param right right child
     */
    public void setRight(WiringObject right) {
        this.right = right;
    }

    /**
     * get parent wiring object
     *
     * @return parent object
     */
    public WiringObject getParent() {
        return parent;
    }

    /**
     * set parent wiring object
     *
     * @param parent parent object
     */
    public void setParent(WiringObject parent) {
        this.parent = parent;
    }

    /**
     * get template type
     *
     * @return template type
     */
    public Type getType() {
        return type;
    }

    /**
     * get input stream name
     *
     * @return input stream
     */
    public String getInStream() {
        return inStream;
    }

    /**
     * set input stream name
     *
     * @param inStream input stream name
     */
    public void setInStream(String inStream) {
        this.inStream = inStream;
    }

    /**
     * get left output stream name
     *
     * @return left output stream name
     */
    public String getOutStreamLeft() {
        return outStreamLeft;
    }

    /**
     * set left output stream name
     *
     * @param outStreamLeft left output stream name
     */
    public void setOutStreamLeft(String outStreamLeft) {
        this.outStreamLeft = outStreamLeft;
    }

    /**
     * get right output stream name
     *
     * @return right output stream
     */
    public String getOutStreamRight() {
        return outStreamRight;
    }

    /**
     * set right output stream name
     *
     * @param outStreamRight right output stream name
     */
    public void setOutStreamRight(String outStreamRight) {
        this.outStreamRight = outStreamRight;
    }

}
