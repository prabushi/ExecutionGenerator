package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

/**
 * Created by prabushi on 3/1/15.
 */
public class TemplateWiringObject extends WiringObject {
    private String query;
    private String name;

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    /**
     * constructor
     *
     * @param type template type
     */
    public TemplateWiringObject(String name,String type) {
        super(WiringObject.Type.valueOf(type));
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

}
