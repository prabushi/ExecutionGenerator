package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

import com.sun.xml.bind.marshaller.DataWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.executionplan.*;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.executionplan.ObjectFactory;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.temperatureanalysis.TemplateDomain;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.templateconfiguration.*;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.util.ExecutionGeneratorConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/*
   Generate the execution plan's content
 */

public class GenerateExecutionPlan {

    private String inputStream;
    private String outputStream;
    private static List<WiredTemplates> wiredTemplatesList;
    private WiringObject root;
    private String tempStream;
    private String compositeQuery;
    private ConditionTree conditionTree;
    private static List<DirectParam> directParameterList;
    private int counter = 0;
    private static final Log log = LogFactory.getLog(GenerateExecutionPlan.class);
    public static GenerateExecutionPlan instance =null;

    private GenerateExecutionPlan(){

    }

    /**
     * give the composite query
     *
     * @return composite query after template wiring
     */
    private String getCompositeQuery() {
        return compositeQuery;
    }

    /**
     * set composite query of the input configuration
     *
     * @param compositeQuery composite query after template wiring
     */
    private void setCompositeQuery(String compositeQuery) {
        this.compositeQuery = compositeQuery;
    }

    /**
     * initialize GENERATE_EXECUTION_PLAN of the template configurations and give the final content of the execution plan
     *
     * @param fileContent template configuration content
     * @return final content of the execution plan
     */
    public String getExecutionPlan(String fileContent) throws Exception {
        TemplateConfig templateConfig;
        ObjectFactory objectFactory = new ObjectFactory();
        ExecutionPlan executionPlan = objectFactory.createExecutionPlan();

        this.setCompositeQuery("");
        try {
            ReadTemplateStructures readTemplateConfiguration = ExecutionGeneratorConstants.readTemplateStructures;
            templateConfig = readTemplateConfiguration.getTemplateConfig(fileContent);
        } catch (JAXBException e) {
            throw new Exception("JAXBException at: "+e+" "+fileContent);
        }
        this.setAttributes(executionPlan, templateConfig, fileContent);
        this.setDescription(executionPlan, templateConfig);
        this.setSiddhiConfiguration(executionPlan, objectFactory);
        this.setStreams(executionPlan, objectFactory, templateConfig);
        this.setQueryExpressions(executionPlan, templateConfig);


        return this.getExecutionPlanContent(executionPlan, objectFactory);
    }

    /**
     * save template configuration content in the registry and set attributes for the execution plan object
     *
     * @param executionPlan  execution plan object
     * @param templateConfig template configuration object
     * @param fileContent    template configuration content
     */
    private void setAttributes(ExecutionPlan executionPlan,
                              TemplateConfig templateConfig, String fileContent) {
        ConfigInformationCollector configInformationCollector = ExecutionGeneratorConstants.configInformationCollector;
        String fileName = templateConfig.getName();

        configInformationCollector.saveTemplateConfig(fileName, fileContent, templateConfig.getDescription(), templateConfig.getFrom());
        executionPlan.setName(fileName);
        executionPlan.setStatistics("disable");
        executionPlan.setTrace("disable");
        executionPlan.setXmlns("http://wso2.org/carbon/eventprocessor");
    }

    /**
     * set description for the execution plan object
     *
     * @param executionPlan  execution plan object
     * @param templateConfig template configuration object
     */
    private void setDescription(ExecutionPlan executionPlan,
                               TemplateConfig templateConfig) {
        executionPlan.setDescription(templateConfig.getDescription());
    }

    /**
     * set siddhi configuration for the execution plan object
     *
     * @param executionPlan execution plan object
     * @param factory       execution plan object factory object
     */
    private void setSiddhiConfiguration(ExecutionPlan executionPlan,
                                       ObjectFactory factory) {

        List<Property> propertyList = new ArrayList<>();

        Property property1 = factory.createProperty();
        property1.setName("siddhi.enable.distributed.GENERATE_EXECUTION_PLAN");
        property1.setValue("false");

        Property property2 = factory.createProperty();
        property2.setName("siddhi.persistence.snapshot.time.interval.minutes");
        property2.setValue("0");

        propertyList.add(property1);
        propertyList.add(property2);

        SiddhiConfiguration siddhiConfiguration = factory.createSiddhiConfiguration();
        siddhiConfiguration.setProperty(propertyList);
        executionPlan.setSiddhiConfiguration(siddhiConfiguration);

    }

    /**
     * set stream details for the execution plan object
     *
     * @param executionPlan  execution plan object
     * @param factory        execution plan object factory object
     * @param templateConfig template configuration object
     */
    private void setStreams(ExecutionPlan executionPlan,
                           ObjectFactory factory, TemplateConfig templateConfig) throws Exception {
        try {
            ReadTemplateStructures readTemplateDomain = ExecutionGeneratorConstants.readTemplateStructures;
            TemplateDomain templateDomain = readTemplateDomain.getTemplateDomain(templateConfig.getFrom());
            List<org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.temperatureanalysis.Stream> streamList = templateDomain.getStream();

            for (org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.temperatureanalysis.Stream streamTemp : streamList) {

                if (streamTemp.getName().equals("inStream")) {

                    Stream stream = factory.createStream();
                    stream.setName(streamTemp.getStreamName());
                    stream.setVersion(streamTemp.getStreamVersion());
                    String streamName = "inputStream";
                    stream.setAs(streamName);
                    this.setInputStream(streamName);
                    List<Stream> streams = new ArrayList<>();
                    streams.add(stream);
                    ImportedStreams importedStreams = factory.createImportedStreams();
                    importedStreams.setStream(streams);
                    executionPlan.setImportedStreams(importedStreams);

                } else if (streamTemp.getName().equals("outStream")) {

                    Stream stream = factory.createStream();
                    stream.setName(streamTemp.getStreamName());
                    stream.setVersion(streamTemp.getStreamVersion());
                    String streamName = "outputStream";
                    stream.setValueOf(streamName);
                    this.setOutputStream(streamName);
                    List<Stream> streams = new ArrayList<>();
                    streams.add(stream);
                    ExportedStreams exportedStreams = factory.createExportedStreams();
                    exportedStreams.setStream(streams);
                    executionPlan.setExportedStreams(exportedStreams);

                }
            }
        } catch (JAXBException e) {
            throw new Exception("JAXBException at setting streams for execution plan: "+e);
        }
    }

    /**
     * give input stream name
     *
     * @return input stream name
     */
    private String getInputStream() {
        return inputStream;
    }

    /**
     * set input stream name
     *
     * @param inputStream input stream name
     */
    private void setInputStream(String inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * give output stream name
     *
     * @return output stream name
     */
    private String getOutputStream() {
        return outputStream;
    }

    /**
     * set output stream name
     *
     * @param outputStream output stream name
     */
    private void setOutputStream(String outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * initialize setting query expression for the execution plan object
     *
     * @param executionPlan  execution plan object
     * @param templateConfig template configuration object
     */
    private void setQueryExpressions(ExecutionPlan executionPlan,
                                    TemplateConfig templateConfig) throws Exception {
        this.setWiredTemplates(templateConfig);
        this.setTemplateCondition();
        this.createCompositeQuery(templateConfig);
        executionPlan.setQueryExpressions("<![CDATA[" + this.getCompositeQuery() + "]]>");
    }

    /**
     * set input and output streams for each wiring object
     *
     * @param wiringObject wiring object
     */
    private void treeTraversal(WiringObject wiringObject) {

        if (wiringObject != null) {

            if (wiringObject.getInStream().equals("")) {
                wiringObject.setInStream(this.getTempStream());
            }

            if (wiringObject instanceof TemplateWiringObject) {

                if (wiringObject.getParent() != null) {

                    if (wiringObject.getParent() instanceof AndWiringObject && wiringObject.getType().equals(WiringObject.Type.left)) {

                        String stream = this.generateRandomStream();
                        wiringObject.setOutStreamLeft(stream);
                        wiringObject.setOutStreamRight(stream);
                        wiringObject.getParent().getRight().setInStream(stream);

                    } else if (wiringObject.getParent() instanceof AndWiringObject
                            && wiringObject.getType().equals(WiringObject.Type.right)) {

                        if (!havingANDParent(wiringObject.getParent()) || isARightChild(wiringObject)) {

                            wiringObject.setOutStreamLeft(this
                                    .getOutputStream());
                            wiringObject.setOutStreamRight(this
                                    .getOutputStream());

                        } else {

                            String stream = this.generateRandomStream();
                            wiringObject.setOutStreamLeft(stream);
                            wiringObject.setOutStreamRight(stream);
                            this.setTempStream(stream);

                        }

                    } else if (wiringObject.getParent() instanceof OrWiringObject
                            && wiringObject.getType().equals(WiringObject.Type.left)) {

                        if (!havingANDParent(wiringObject.getParent()) || isARightChild(wiringObject)) {

                            wiringObject.setOutStreamLeft(this
                                    .getOutputStream());
                            wiringObject.setOutStreamRight(this
                                    .getOutputStream());

                        } else {

                            String stream2 = this.generateRandomStream();
                            wiringObject.setOutStreamLeft(stream2);
                            wiringObject.setOutStreamRight(stream2);
                            this.setTempStream(stream2);

                            if (wiringObject.getParent().getRight() instanceof TemplateWiringObject) {

                                wiringObject.getParent().getRight().setOutStreamLeft(stream2);
                                wiringObject.getParent().getRight().setOutStreamRight(stream2);

                            }

                        }

                    } else if (wiringObject.getParent() instanceof OrWiringObject
                            && wiringObject.getType().equals(WiringObject.Type.right)
                            && wiringObject.getOutStreamLeft().equals("")) {

                        if (!havingANDParent(wiringObject.getParent()) || isARightChild(wiringObject)) {

                            wiringObject.setOutStreamLeft(this.getOutputStream());
                            wiringObject.setOutStreamRight(this.getOutputStream());

                        } else {

                            String stream = this.generateRandomStream();
                            wiringObject.setOutStreamLeft(stream);
                            wiringObject.setOutStreamRight(stream);
                            this.setTempStream(stream);

                        }

                    }

                } else {

                    wiringObject.setOutStreamLeft(this.getOutputStream());
                    wiringObject.setOutStreamRight(this.getOutputStream());
                }

            } else {

                if (wiringObject instanceof AndWiringObject) {

                    if (wiringObject.getParent() != null) {

                        if (wiringObject.getInStream().equals("") && wiringObject.getParent() instanceof AndWiringObject) {

                            wiringObject.setInStream(this.getTempStream());

                        }

                    }

                    String stream = wiringObject.getInStream();
                    wiringObject.setOutStreamLeft(stream);
                    wiringObject.getLeft().setInStream(stream);

                } else if (wiringObject instanceof OrWiringObject) {

                    if (wiringObject.getParent() != null) {

                        if (wiringObject.getInStream().equals("") && wiringObject.getParent() instanceof AndWiringObject) {

                            wiringObject.setInStream(this.getTempStream());

                        }

                    }

                    String stream = wiringObject.getInStream();
                    wiringObject.setOutStreamLeft(stream);
                    wiringObject.setOutStreamRight(stream);
                    wiringObject.getLeft().setInStream(stream);
                    wiringObject.getRight().setInStream(stream);

                }

            }

            treeTraversal(wiringObject.getLeft());
            treeTraversal(wiringObject.getRight());

        }

    }

    /**
     * give highest grand parent with a and operation while the wiring object is a descendant of that
     *
     * @param wiringObject wiring object
     * @return highest and parent
     */
    private WiringObject getHighestAndOperation(WiringObject wiringObject) {

        WiringObject and = null;

        while (wiringObject.getParent() != null) {

            if (wiringObject.getParent() instanceof AndWiringObject) {
                and = wiringObject.getParent();
            }

            wiringObject = wiringObject.getParent();
        }

        return and;
    }

    /**
     * check whether the wiring object is in the tree
     *
     * @param wiringObject     wiring object checking for
     * @param wiringObjectTemp current tree object
     * @param isInTree         true if the wiring object is in the tree, else false
     * @return whether the wiring object is in the tree
     */
    private boolean isInTree(WiringObject wiringObject,
                            WiringObject wiringObjectTemp, boolean isInTree) {

        boolean isInTreeTemp = isInTree;

        if (isInTreeTemp) {
            return true;
        }

        if (wiringObjectTemp != null) {

            if (wiringObjectTemp == wiringObject) {
                return true;
            } else {
                isInTreeTemp = isInTree(wiringObject,
                        wiringObjectTemp.getLeft(), isInTreeTemp);
                isInTreeTemp = isInTree(wiringObject,
                        wiringObjectTemp.getRight(), isInTreeTemp);
            }

        }

        return isInTreeTemp;

    }

    /**
     * check whether the wiring object is in the right sub tree
     *
     * @param wiringObject wiring object
     * @return true if the wiring object is in the right sub tree, else false
     */
    private boolean isARightChild(WiringObject wiringObject) {

        boolean isInRightSubTree = false;
        WiringObject highestAnd = this.getHighestAndOperation(wiringObject.getParent());

        if (highestAnd != null) {

            if (highestAnd.getRight() != null) {
                isInRightSubTree = this.isInTree(wiringObject,
                        highestAnd.getRight(), false);
            }

        }

        return isInRightSubTree;

    }

    /**
     * check whether the wiring object has a and parent
     *
     * @param wiringObject wiring object
     * @return true if having an and parent, else false
     */
    private boolean havingANDParent(WiringObject wiringObject) {
        return wiringObject.getParent() != null && (wiringObject.getParent() instanceof AndWiringObject || havingANDParent(wiringObject.getParent()));
    }

    /**
     * initialize creating the composite query for the execution plan
     *
     * @param templateConfig template configuration object
     */
    private void createCompositeQuery(TemplateConfig templateConfig) {

        TemplateWiring templateWiring = templateConfig.getTemplateWiring();
        WiringObject wiringObject = null;
        And and = templateWiring.getAnd();
        Or or = templateWiring.getOr();
        TemplateObject template = templateWiring.getTemplate();

        if (template != null) {
            wiringObject = new TemplateWiringObject(template.getValue(),
                    template.getType());

        } else if (and != null) {
            wiringObject = new AndWiringObject(and.getType());
            wiringObject.setParent(null);
            this.setAndWiringObject(and, wiringObject);

        } else if (or != null) {
            wiringObject = new OrWiringObject(or.getType());
            wiringObject.setParent(null);
            this.setOrWiringObject(or, wiringObject);
        }

        this.setRoot(wiringObject);
        this.getRoot().setInStream(this.getInputStream());
        treeTraversal(this.getRoot());
        preOrderQueryProcessing(this.getRoot());

    }

    /**
     * give temporary stream name
     *
     * @return temporary stream name
     */
    private String getTempStream() {
        return tempStream;
    }

    /**
     * set temporary stream name
     *
     * @param tempStream temporary stream name
     */
    private void setTempStream(String tempStream) {
        this.tempStream = tempStream;
    }

    /**
     * pre-order tree traversal and setting input and output streams to each wiring object's query
     *
     * @param wiringObject wiring object
     */
    private void preOrderQueryProcessing(WiringObject wiringObject) {

        if (wiringObject != null) {

            if (wiringObject instanceof TemplateWiringObject) {

                ((TemplateWiringObject) wiringObject).setQuery(this.getTemplateQuery(((TemplateWiringObject) wiringObject).getName()));
                String query = this.setInputStream(((TemplateWiringObject) wiringObject).getQuery(),
                        wiringObject.getInStream());
                query = this.setOutputStream(query,
                        wiringObject.getOutStreamLeft());
                this.appendCompositeQuery(query);

            }

            preOrderQueryProcessing(wiringObject.getLeft());
            preOrderQueryProcessing(wiringObject.getRight());

        }

    }

    /**
     * give the root object of the wiring objects tree
     *
     * @return root object
     */
    private WiringObject getRoot() {
        return root;
    }

    /**
     * set root object of the wiring objects tree
     *
     * @param root root object
     */
    private void setRoot(WiringObject root) {
        this.root = root;
    }

    /**
     * set wiring object with an and operation into the wiring objects tree
     *
     * @param and          and object
     * @param wiringObject parent wiring object
     */
    private void setAndWiringObject(And and, WiringObject wiringObject) {

        List<And> andList = and.getAnd();
        List<Or> orList = and.getOr();
        List<TemplateObject> templateList = and.getTemplate();

        if (!templateList.isEmpty()) {

            for (TemplateObject template : templateList) {

                String type = template.getType();
                WiringObject tempWiringObject = new TemplateWiringObject(
                        template.getValue(), type);
                tempWiringObject.setParent(wiringObject);

                if (type.equals(WiringObject.Type.left.name())) {

                    wiringObject.setLeft(tempWiringObject);

                } else if (type.equals(WiringObject.Type.right.name())) {

                    wiringObject.setRight(tempWiringObject);

                }

            }

        }

        if (!orList.isEmpty()) {

            for (Or orObject : orList) {

                String type = orObject.getType();
                WiringObject tempWiringObject = new OrWiringObject(
                        type);
                tempWiringObject.setParent(wiringObject);

                if (type.equals(WiringObject.Type.left.name())) {

                    wiringObject.setLeft(tempWiringObject);

                } else if (type.equals(WiringObject.Type.right.name())) {

                    wiringObject.setRight(tempWiringObject);

                }

                this.setOrWiringObject(orObject, tempWiringObject);

            }

        }

        if (!andList.isEmpty()) {

            for (And andObject : andList) {

                String type = andObject.getType();
                WiringObject tempWiringObject = new AndWiringObject(
                        type);
                tempWiringObject.setParent(wiringObject);

                if (type.equals(WiringObject.Type.left.name())) {

                    wiringObject.setLeft(tempWiringObject);

                } else if (type.equals(WiringObject.Type.right.name())) {

                    wiringObject.setRight(tempWiringObject);

                }

                this.setAndWiringObject(andObject, tempWiringObject);

            }

        }

    }

    /**
     * set wiring object with an or operation into the wiring objects tree
     *
     * @param or           or object
     * @param wiringObject parent wiring object
     */
    private void setOrWiringObject(Or or, WiringObject wiringObject) {

        List<And> andList = or.getAnd();
        List<Or> orList = or.getOr();
        List<TemplateObject> templateList = or.getTemplate();

        if (!templateList.isEmpty()) {

            for (TemplateObject template : templateList) {

                String type = template.getType();
                WiringObject tempWiringObject = new TemplateWiringObject(
                        template.getValue(), type);
                tempWiringObject.setParent(wiringObject);

                if (type.equals(WiringObject.Type.left.name())) {

                    wiringObject.setLeft(tempWiringObject);

                } else if (type.equals(WiringObject.Type.right.name())) {

                    wiringObject.setRight(tempWiringObject);

                }

            }

        }

        if (!orList.isEmpty()) {

            for (Or orObject : orList) {

                String type = orObject.getType();
                WiringObject tempWiringObject = new OrWiringObject(
                        type);
                tempWiringObject.setParent(wiringObject);

                if (type.equals(WiringObject.Type.left.name())) {

                    wiringObject.setLeft(tempWiringObject);

                } else if (type.equals(WiringObject.Type.right.name())) {

                    wiringObject.setRight(tempWiringObject);

                }

                this.setOrWiringObject(orObject, tempWiringObject);

            }

        }

        if (!andList.isEmpty()) {

            for (And andObject : andList) {

                String type = andObject.getType();
                WiringObject tempWiringObject = new AndWiringObject(type);
                tempWiringObject.setParent(wiringObject);

                if (type.equals(WiringObject.Type.left.name())) {

                    wiringObject.setLeft(tempWiringObject);

                } else if (type.equals(WiringObject.Type.right.name())) {

                    wiringObject.setRight(tempWiringObject);

                }

                this.setAndWiringObject(andObject, tempWiringObject);
            }

        }

    }

    /**
     * append to the composite query of the execution plan
     *
     * @param query query to append to the composite query
     */
    private void appendCompositeQuery(String query) {
        this.compositeQuery += query;
    }

    /**
     * give a randomly generated stream
     *
     * @return random stream name
     */
    private String generateRandomStream() {
        return "stream" + ++counter;
    }

    /**
     * set  queries to each template and add it to wired template list
     *
     * @param templateConfig template configuration object
     */
    private void setWiredTemplates(TemplateConfig templateConfig) throws Exception {
        try {
            wiredTemplatesList = new ArrayList<>();
            Templates templates = templateConfig.getTemplates();
            String templateDomain = templateConfig.getFrom();
            ReadTemplateStructures readTemplateDomain = ExecutionGeneratorConstants.readTemplateStructures;
            TemplateDomain templateDomainObj = readTemplateDomain.getTemplateDomain(templateDomain);

            for (Template template : templates.getTemplate()) {

                String tempQuery = readTemplateDomain.getTemplateQuery(
                        templateDomainObj, template.getType());
                WiredTemplates wiredTemplateObj = new WiredTemplates();
                wiredTemplateObj.setTemplate(template);
                wiredTemplateObj.setTemplateQuery(tempQuery);
                wiredTemplatesList.add(wiredTemplateObj);

            }
        } catch (JAXBException e) {
            throw new Exception("JAXBException at setting Wired Templates.: "+e);
        }
    }

    /**
     * set condition of query of each wired template
     */
    private void setTemplateCondition() {

        for (WiredTemplates wiredTemplates : wiredTemplatesList) {

            conditionTree = new ConditionTree();
            Template currentTemp = wiredTemplates.getTemplate();
            processTemplateConditions(currentTemp);
            String templateQuery = wiredTemplates.getTemplateQuery();
            templateQuery = templateQuery.replaceAll("\\$condition", conditionTree.getFinalString());
            wiredTemplates.setTemplateQuery(templateQuery);

            if (currentTemp.getParameters() != null) {

                for (DirectParam aDirectParameterList : directParameterList) {

                    String name = aDirectParameterList.getName();
                    String value = aDirectParameterList.getValue();
                    String oldQuery = wiredTemplates.getTemplateQuery();
                    oldQuery = oldQuery.replaceAll("\\$" + name, value);
                    wiredTemplates.setTemplateQuery(oldQuery);

                }

            }

        }

    }

    /**
     * process template conditions for a given template
     *
     * @param currentTemplate input template
     */
    private void processTemplateConditions(Template currentTemplate) {

        ConditionParameters conditionParam = currentTemplate
                .getConditionParameters();
        Parameters parameters = currentTemplate.getParameters();
        processConditionParameters(conditionParam);

        if (parameters != null) {
            processDirectParameters(parameters);
        }

    }

    /**
     * process condition parameters according to its and, or operations
     *
     * @param conditionParam condition parameter object
     */
    private void processConditionParameters(ConditionParameters conditionParam) {

        AND andParam = conditionParam.getAND();
        OR orParam = conditionParam.getOR();
        Parameter param = conditionParam.getParameter();

        if (param != null) {
            processParameterCondition(param, null);
        } else if (andParam != null) {
            processANDCondition(andParam, null);
        } else if (orParam != null) {
            processORCondition(orParam, null);
        }

    }

    /**
     * process direct parameters within templates
     *
     * @param parameters parameters object
     */
    private void processDirectParameters(Parameters parameters) {

        List<DirectParameter> directList = parameters.getDirectParameter();
        directParameterList = new ArrayList<>();

        for (DirectParameter directParameter : directList) {

            DirectParam param = new DirectParam();
            param.setName(directParameter.getName());
            param.setValue(directParameter.getValue());
            directParameterList.add(param);

        }

    }

    /**
     * process 'and' condition in template conditions
     *
     * @param andParam and operation
     * @param parent   parent node
     */
    private void processANDCondition(AND andParam, ConditionNode parent) {

        List<AND> andList = andParam.getAND();
        List<OR> orList = andParam.getOR();
        List<Parameter> parameterList = andParam.getParameter();
        ConditionNode conditionNode = new ConditionNode();
        conditionNode.setOrder(andParam.getOrder());
        conditionNode.setParent(parent);
        conditionNode.setLeft(null);
        conditionNode.setRight(null);
        conditionNode.setCondition("");
        conditionNode.setType(ConditionNode.Type.AND);
        conditionTree.insertNode(conditionNode);

        if (!andList.isEmpty()) {

            processANDCondition(andList.get(0), conditionNode);

            if (andList.size() == 2) {
                processANDCondition(andList.get(1), conditionNode);
            }

        }

        if (!orList.isEmpty()) {

            processORCondition(orList.get(0), conditionNode);

            if (orList.size() == 2) {
                processORCondition(orList.get(1), conditionNode);
            }

        }

        if (!parameterList.isEmpty()) {

            processParameterCondition(parameterList.get(0), conditionNode);

            if (parameterList.size() == 2) {
                processParameterCondition(parameterList.get(1), conditionNode);
            }

        }

    }

    /**
     * process 'or' condition in template conditions
     *
     * @param orParam or operation
     * @param parent  parent node
     */
    private void processORCondition(OR orParam, ConditionNode parent) {

        List<AND> andList = orParam.getAND();
        List<OR> orList = orParam.getOR();
        List<Parameter> parameterList = orParam.getParameter();
        ConditionNode conditionNode = new ConditionNode();
        conditionNode.setOrder(orParam.getOrder());
        conditionNode.setParent(parent);
        conditionNode.setLeft(null);
        conditionNode.setRight(null);
        conditionNode.setCondition("");
        conditionNode.setType(ConditionNode.Type.OR);
        conditionTree.insertNode(conditionNode);

        if (!andList.isEmpty()) {

            processANDCondition(andList.get(0), conditionNode);

            if (andList.size() == 2) {
                processANDCondition(andList.get(1), conditionNode);
            }

        }

        if (!orList.isEmpty()) {

            processORCondition(orList.get(0), conditionNode);

            if (orList.size() == 2) {
                processORCondition(orList.get(1), conditionNode);
            }

        }

        if (!parameterList.isEmpty()) {

            processParameterCondition(parameterList.get(0), conditionNode);

            if (parameterList.size() == 2) {
                processParameterCondition(parameterList.get(1), conditionNode);
            }

        }

    }

    /**
     * process condition values
     *
     * @param param parameter object
     * @param node  node object
     */
    private void processParameterCondition(Parameter param, ConditionNode node) {

        String currentCondition = param.getValue();
        currentCondition = currentCondition.trim();
        ConditionNode conditionNode = new ConditionNode();
        conditionNode.setOrder(param.getOrder());
        conditionNode.setParent(node);
        conditionNode.setLeft(null);
        conditionNode.setRight(null);
        conditionNode.setType(ConditionNode.Type.PARAMETER);
        conditionNode.setCondition(currentCondition);
        conditionTree.insertNode(conditionNode);

    }

    /**
     * generate and return execution plan xml content using JAXB
     *
     * @param executionPlan execution plan object
     * @param factory       execution plan factory object
     * @return xml content of the execution plan as a string
     */
    private String getExecutionPlanContent(ExecutionPlan executionPlan,
                                          ObjectFactory factory) throws Exception {

        String executionPlanContent = "";
        try {
            JAXBContext context = JAXBContext
                    .newInstance(org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.executionplan.ObjectFactory.class);

            JAXBElement<ExecutionPlan> element = factory
                    .createExecutionPlan(executionPlan);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");


            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            DataWriter dataWriter = new DataWriter(printWriter, "UTF-8",
                    new JaxbCharacterEscapeHandler());

            marshaller.marshal(element, dataWriter);
            executionPlanContent = stringWriter.toString();


        } catch (JAXBException e) {
            throw new Exception("JAXBException at getting Execution Plan: "+e);
        }

        return executionPlanContent;

    }

    /**
     * set input stream to the query
     *
     * @param query    template query
     * @param inStream input stream name
     * @return updated query
     */
    private String setInputStream(String query, String inStream) {
        query = query.replaceAll("\\$inStream", inStream);
        return query;
    }

    /**
     * set output stream to the query
     *
     * @param query     template query
     * @param outStream output stream name
     * @return updated query
     */
    private String setOutputStream(String query, String outStream) {

        query = query.replaceAll("\\$outStream", outStream);

        return query;

    }

    /**
     * get template query when the template name is given
     *
     * @param templateName template name
     * @return relevant template query
     */
    private String getTemplateQuery(String templateName) {

        String query = "";

        for (WiredTemplates template : wiredTemplatesList) {

            if (template.getTemplate().getName().equals(templateName)) {
                query = template.getTemplateQuery();
            }
        }
        return query;
    }

    public static GenerateExecutionPlan getInstance(){
        if(instance == null){
            instance=new GenerateExecutionPlan();
        }
        return instance;
    }
}
