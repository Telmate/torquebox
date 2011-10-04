package org.torquebox.test.as;

import org.junit.Ignore;

import org.projectodd.polyglot.test.as.MockDeploymentPhaseContext;
import org.projectodd.polyglot.test.as.MockDeploymentUnit;

@Ignore
public class AbstractDeploymentProcessorTestCase extends org.projectodd.polyglot.test.as.AbstractDeploymentProcessorTestCase {

    protected MockDeploymentUnit deployResourceAsTorqueboxYml(String resource) throws Exception {
        return deployResourceAs( resource, "torquebox.yml" );   
    }   

    protected MockDeploymentPhaseContext setupResourceAsTorqueboxYml(String resource) throws Exception {
        return setupResourceAs( resource, "torquebox.yml" );
    }
}
