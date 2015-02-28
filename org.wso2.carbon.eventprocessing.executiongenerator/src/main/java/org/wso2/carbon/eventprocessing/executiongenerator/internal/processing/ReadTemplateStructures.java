package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.temperatureanalysis.ObjectFactory;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.temperatureanalysis.Template;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.temperatureanalysis.TemplateDomain;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.templateconfiguration.TemplateConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/*
  class that read template domain structures and template configuration structures using JAXB and return each class object
 */
public class ReadTemplateStructures {
    private static final Log log = LogFactory.getLog(Processing.class);

    /**
     * get template configuration object when the object's xml content is given
     *
     * @param fileContent xml file content
     * @return template configuration object
     */
    public TemplateConfig getTemplateConfig(String fileContent) throws JAXBException {

            JAXBContext jaxbContext = JAXBContext.newInstance(org.wso2.carbon.eventprocessing.executiongenerator.internal.templatestructure.templateconfiguration.ObjectFactory.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            @SuppressWarnings("unchecked")
            JAXBElement<TemplateConfig> unmarshalledObject  =
                    ((JAXBElement<TemplateConfig>) unmarshaller.unmarshal(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))));

        return unmarshalledObject.getValue();
    }

    /**
     * get template domain object when the domain name is given
     *
     * @param fileName domain name
     * @return template domain object
     */
    public TemplateDomain getTemplateDomain(String fileName) throws JAXBException {

            DomainInformation domainInformation = new DomainInformation();
            String fileContent = domainInformation.getSpecificDomainInfo(fileName);

            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            @SuppressWarnings("unchecked")
            JAXBElement<TemplateDomain> unmarshalledObject = ((JAXBElement<TemplateDomain>) unmarshaller.unmarshal(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))));//new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))));             //

        return unmarshalledObject.getValue();
    }

    /**
     * return template query content when the template domain and name is given
     *
     * @param templateDomain template domain object
     * @param templateName   template name
     * @return template query
     */
    public String getTemplateQuery(TemplateDomain templateDomain,
                                   String templateName) {
        String query = "";
        for (Template template : templateDomain.getTemplate()) {
            if (template.getName().equals(templateName)) {
                query = template.getTemplateQuery();
                break;
            }
        }
        return query;
    }
}
