package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

/**
 * represent an object withing template wiring
 */
public class WiringObject {

    private boolean isOperation;
    private String name;
    private String query;
    private String inStream;
    private String outStreamLeft;
    private String outStreamRight;
    private WiringObject left;
    private WiringObject right;
    private WiringObject parent;
    private String type;

    /**
     * constructor
     *
     * @param isOperation true if it is an operation, else false for a template
     * @param name        template name
     * @param type        template type
     */
    public WiringObject(boolean isOperation, String name, String type) {
        super();
        this.isOperation = isOperation;
        this.name = name;
        this.query = "";
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
    public String getType() {
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

    /**
     * get template query
     *
     * @return template query
     */
    public String getQuery() {
        return query;
    }

    /**
     * set template query
     *
     * @param query template query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * return whether the object is an operation or not
     *
     * @return true if it is an operation, else false
     */
    public boolean isOperation() {
        return isOperation;
    }

    /**
     * set whether the object is an operation or not
     *
     * @param isOperation object is an operation or not
     */
    public void setOperation(boolean isOperation) {
        this.isOperation = isOperation;
    }

    /**
     * get template name
     *
     * @return template name
     */
    public String getName() {
        return name;
    }

    /**
     * set template name
     *
     * @param name template name
     */
    public void setName(String name) {
        this.name = name;
    }
}
