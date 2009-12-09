package contextawaremodel.ontology;


import edu.stanford.smi.protegex.owl.model.*;
import java.util.*;

/**
 * Generated by Protege-OWL  (http://protege.stanford.edu/plugins/owl).
 *
 * @version generated on Sun Apr 26 18:19:44 EEST 2009
 */
public class MyFactory {

    private OWLModel owlModel;


    public MyFactory(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    public RDFSNamedClass getcontext_elementClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#context-element";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public context_element createcontext_element(String name) {
        final RDFSNamedClass cls = getcontext_elementClass();
        return (context_element) cls.createInstance(name).as(context_element.class);
    }

    public context_element getcontext_element(String name) {
        return (context_element) owlModel.getRDFResource(name).as(context_element.class);
    }

    public Collection<context_element> getAllcontext_elementInstances() {
        return getAllcontext_elementInstances(false);
    }

    public Collection<context_element> getAllcontext_elementInstances(boolean transitive) {
        Collection<context_element> result = new ArrayList<context_element>();
        final RDFSNamedClass cls = getcontext_elementClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (context_element) owlIndividual.as(context_element.class) );
        }
        return result;
    }


    public RDFSNamedClass getactorClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#actor";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public actor createactor(String name) {
        final RDFSNamedClass cls = getactorClass();
        return (actor) cls.createInstance(name).as(actor.class);
    }

    public actor getactor(String name) {
        return (actor) owlModel.getRDFResource(name).as(actor.class);
    }

    public Collection<actor> getAllactorInstances() {
        return getAllactorInstances(false);
    }

    public Collection<actor> getAllactorInstances(boolean transitive) {
        Collection<actor> result = new ArrayList<actor>();
        final RDFSNamedClass cls = getactorClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (actor) owlIndividual.as(actor.class) );
        }
        return result;
    }


    public RDFSNamedClass getpolicyClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#policy";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public policy createpolicy(String name) {
        final RDFSNamedClass cls = getpolicyClass();
        return (policy) cls.createInstance(name).as(policy.class);
    }

    public policy getpolicy(String name) {
        return (policy) owlModel.getRDFResource(name).as(policy.class);
    }

    public Collection<policy> getAllpolicyInstances() {
        return getAllpolicyInstances(false);
    }

    public Collection<policy> getAllpolicyInstances(boolean transitive) {
        Collection<policy> result = new ArrayList<policy>();
        final RDFSNamedClass cls = getpolicyClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (policy) owlIndividual.as(policy.class) );
        }
        return result;
    }


    public RDFSNamedClass getresourceClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#resource";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public resource createresource(String name) {
        final RDFSNamedClass cls = getresourceClass();
        return (resource) cls.createInstance(name).as(resource.class);
    }

    public resource getresource(String name) {
        return (resource) owlModel.getRDFResource(name).as(resource.class);
    }

    public Collection<resource> getAllresourceInstances() {
        return getAllresourceInstances(false);
    }

    public Collection<resource> getAllresourceInstances(boolean transitive) {
        Collection<resource> result = new ArrayList<resource>();
        final RDFSNamedClass cls = getresourceClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (resource) owlIndividual.as(resource.class) );
        }
        return result;
    }


    public RDFSNamedClass getactor_resourceClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#actor-resource";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public actor_resource createactor_resource(String name) {
        final RDFSNamedClass cls = getactor_resourceClass();
        return (actor_resource) cls.createInstance(name).as(actor_resource.class);
    }

    public actor_resource getactor_resource(String name) {
        return (actor_resource) owlModel.getRDFResource(name).as(actor_resource.class);
    }

    public Collection<actor_resource> getAllactor_resourceInstances() {
        return getAllactor_resourceInstances(false);
    }

    public Collection<actor_resource> getAllactor_resourceInstances(boolean transitive) {
        Collection<actor_resource> result = new ArrayList<actor_resource>();
        final RDFSNamedClass cls = getactor_resourceClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (actor_resource) owlIndividual.as(actor_resource.class) );
        }
        return result;
    }


    public RDFSNamedClass getphysical_resourceClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#physical-resource";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public physical_resource createphysical_resource(String name) {
        final RDFSNamedClass cls = getphysical_resourceClass();
        return (physical_resource) cls.createInstance(name).as(physical_resource.class);
    }

    public physical_resource getphysical_resource(String name) {
        return (physical_resource) owlModel.getRDFResource(name).as(physical_resource.class);
    }

    public Collection<physical_resource> getAllphysical_resourceInstances() {
        return getAllphysical_resourceInstances(false);
    }

    public Collection<physical_resource> getAllphysical_resourceInstances(boolean transitive) {
        Collection<physical_resource> result = new ArrayList<physical_resource>();
        final RDFSNamedClass cls = getphysical_resourceClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (physical_resource) owlIndividual.as(physical_resource.class) );
        }
        return result;
    }


    public RDFSNamedClass getactuatorClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#actuator";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public actuator createactuator(String name) {
        final RDFSNamedClass cls = getactuatorClass();
        return (actuator) cls.createInstance(name).as(actuator.class);
    }

    public actuator getactuator(String name) {
        return (actuator) owlModel.getRDFResource(name).as(actuator.class);
    }

    public Collection<actuator> getAllactuatorInstances() {
        return getAllactuatorInstances(false);
    }

    public Collection<actuator> getAllactuatorInstances(boolean transitive) {
        Collection<actuator> result = new ArrayList<actuator>();
        final RDFSNamedClass cls = getactuatorClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (actuator) owlIndividual.as(actuator.class) );
        }
        return result;
    }


    public RDFSNamedClass getsensorClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#sensor";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public sensor createsensor(String name) {
        final RDFSNamedClass cls = getsensorClass();
        return (sensor) cls.createInstance(name).as(sensor.class);
    }

    public sensor getsensor(String name) {
        return (sensor) owlModel.getRDFResource(name).as(sensor.class);
    }

    public Collection<sensor> getAllsensorInstances() {
        return getAllsensorInstances(false);
    }

    public Collection<sensor> getAllsensorInstances(boolean transitive) {
        Collection<sensor> result = new ArrayList<sensor>();
        final RDFSNamedClass cls = getsensorClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (sensor) owlIndividual.as(sensor.class) );
        }
        return result;
    }


    public RDFSNamedClass gethumidity_sensorClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#humidity-sensor";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public humidity_sensor createhumidity_sensor(String name) {
        final RDFSNamedClass cls = gethumidity_sensorClass();
        return (humidity_sensor) cls.createInstance(name).as(humidity_sensor.class);
    }

    public humidity_sensor gethumidity_sensor(String name) {
        return (humidity_sensor) owlModel.getRDFResource(name).as(humidity_sensor.class);
    }

    public Collection<humidity_sensor> getAllhumidity_sensorInstances() {
        return getAllhumidity_sensorInstances(false);
    }

    public Collection<humidity_sensor> getAllhumidity_sensorInstances(boolean transitive) {
        Collection<humidity_sensor> result = new ArrayList<humidity_sensor>();
        final RDFSNamedClass cls = gethumidity_sensorClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (humidity_sensor) owlIndividual.as(humidity_sensor.class) );
        }
        return result;
    }


    public RDFSNamedClass getmovement_sensorClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#movement-sensor";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public movement_sensor createmovement_sensor(String name) {
        final RDFSNamedClass cls = getmovement_sensorClass();
        return (movement_sensor) cls.createInstance(name).as(movement_sensor.class);
    }

    public movement_sensor getmovement_sensor(String name) {
        return (movement_sensor) owlModel.getRDFResource(name).as(movement_sensor.class);
    }

    public Collection<movement_sensor> getAllmovement_sensorInstances() {
        return getAllmovement_sensorInstances(false);
    }

    public Collection<movement_sensor> getAllmovement_sensorInstances(boolean transitive) {
        Collection<movement_sensor> result = new ArrayList<movement_sensor>();
        final RDFSNamedClass cls = getmovement_sensorClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (movement_sensor) owlIndividual.as(movement_sensor.class) );
        }
        return result;
    }


    public RDFSNamedClass gettemperature_sensorClass() {
        final String uri = "http://www.owl-ontologies.com/Ontology1240758175.owl#temperature-sensor";
        final String name = owlModel.getResourceNameForURI(uri);
        return owlModel.getRDFSNamedClass(name);
    }

    public temperature_sensor createtemperature_sensor(String name) {
        final RDFSNamedClass cls = gettemperature_sensorClass();
        return (temperature_sensor) cls.createInstance(name).as(temperature_sensor.class);
    }

    public temperature_sensor gettemperature_sensor(String name) {
        return (temperature_sensor) owlModel.getRDFResource(name).as(temperature_sensor.class);
    }

    public Collection<temperature_sensor> getAlltemperature_sensorInstances() {
        return getAlltemperature_sensorInstances(false);
    }

    public Collection<temperature_sensor> getAlltemperature_sensorInstances(boolean transitive) {
        Collection<temperature_sensor> result = new ArrayList<temperature_sensor>();
        final RDFSNamedClass cls = gettemperature_sensorClass();
        RDFResource owlIndividual;
        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {
            owlIndividual = (RDFResource) it.next();
            result.add( (temperature_sensor) owlIndividual.as(temperature_sensor.class) );
        }
        return result;
    }
}