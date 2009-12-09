package contextawaremodel;

import java.util.HashMap;
import java.util.Map;

public class GlobalVars {
    public static final String XML_ELEMENT = "int";
    public static final String CONTEXT_INSTANCE_MODIFIED = "Context instance modified";
    public static final String base = "http://www.owl-ontologies.com/Ontology1230214892.owl";
    public static final String CIAGENT_NAME = "CIAgent";
    public static final String CMAGENT_NAME = "CMAgent";
    public static final String RPAGENT_NAME = "RPAgent";
    public static final String EMAGENT_NAME = "EMAgent";
    public static final String GUIAGENT_NAME = "GUIAgent";
    public static final String RLAGENT_NAME = "ReinforcementLearningAgent";
    public static final String PHYSICAL_RESOURCE_VALUE_NAME = "valueOfService";
    public static final String PHYSICAL_RESOURCE_NAME_NAME = "nameOfService";
    public static final String AGENT_REQUEST = "request";
    public static final String WORLD_FILE = "./ontology/worldFile.xml";
    public static final String WORLD_FILE_SCHEMA = "./ontology/worldFile.xsd";
    public static final String ONTOLOGY_CLASS_NAME = "ontology-class-name";
    public static final String ONTOLOGY_INDIVIDUAL_NAME = "ontology-individual-name";
    public static final String DATA_NAME = "data-name";
    public static final String DATA_VALUE = "data-value";
    public static final String CONTEXT_ELEMENT = "context-element";
    public static final String ONTOLOGY_FILE = "ontology/contextOntology.owl";
    public static final String POLICIES_FILE = "ontology/TestPolicies.xml";
    public static final String MEMORY_FILE = "memory/memory.dat";
    public static final int INDIVIDUAL_DELETED = 0;
    public static final int INDIVIDUAL_CREATED = 1;
    public static final int INDIVIDUAL_MODIFIED = 2;
    private static final Map<String, String> brokenResources = new HashMap<String, String>();
    private static final Map<String, String> validResources = new HashMap<String, String>();
    
    public static synchronized Map<String, String> getBrokenResources(){
        return brokenResources;
    }
    
      public static synchronized Map<String, String> getValidResources(){
        return validResources;
    }

}
