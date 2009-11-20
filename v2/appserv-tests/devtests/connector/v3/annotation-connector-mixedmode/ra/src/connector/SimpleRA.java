/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package connector;

import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.*;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;


//@README : test : this @Connector should not be considered as component definition annotation.
// if there are multiple @Connector annotations,one that is a class as specified in ra.xml should be considered

/**
 * This is a sample resource adapter
 *
 * @author	Qingqing Ouyang
 */
@Connector(
   displayName = "Simple RA",
   vendorName = "Java Software Simple RA",
   eisType = "Generic Type",
   version = "1.0Alpha"
)
public class SimpleRA
implements ResourceAdapter, java.io.Serializable {

    private BootstrapContext ctx;
    private WorkManager wm;
    private String testName;
    @ConfigProperty(
            type = java.lang.String.class
    )
    private String testName1="testName1";

    private boolean debug = true;
    private Work work;

    public SimpleRA () {
        debug ("constructor...");
    }

    public void
    start(BootstrapContext ctx) throws ResourceAdapterInternalException{

        debug("001. Simple RA start...");

        this.ctx = ctx;
        debug("002. Simple RA start...");
        this.wm  = ctx.getWorkManager();
        debug("003. Simple RA start...");

        //testing creat timer
        Timer timer = null;
	  try{
	      timer = ctx.createTimer();
	  } catch(UnavailableException ue) {
	      System.out.println("Error");
	      throw new ResourceAdapterInternalException("Error form bootstrap");
	  }
        debug("004. Simple RA start...");
        debug("005. Simple RA start...");
    }

    public void
    stop() {
        debug("999. Simple RA stop...");
        if (work != null) {
            ((WorkDispatcher) work).stop();

            synchronized (Controls.readyLock) {
                Controls.readyLock.notify();
            }

        }
    }

    public void
    endpointActivation ( MessageEndpointFactory factory, ActivationSpec spec)
        throws NotSupportedException {
        try {
            debug("B.000. Create and schedule Dispatcher");
            spec.validate();
            work = new WorkDispatcher("DISPATCHER", ctx, factory, spec);
            wm.scheduleWork(work, 4*1000, null, null);
            debug("B.001. Scheduled Dispatcher");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void
    endpointDeactivation (
            MessageEndpointFactory endpointFactory,
            ActivationSpec spec) {
        debug ("endpointDeactivation called...");

        ((WorkDispatcher) work).stop();
    }

    public String getTestName() {
        return testName;
    }

    @ConfigProperty(
            defaultValue = "ConfigPropertyForRA",
            type = java.lang.String.class
    )
    public void setTestName(String name) {
        debug("setTestName called... name = " + name);
        testName = name;
        throw new RuntimeException("Not supposed to be invoked");
    }

  public String getTestName1() {
        return testName1;
    }

    public void setTestName1(String name) {

        debug("setTestName1 called... name = " + name);
        testName1 = name;
        throw new RuntimeException("Not supposed to be invoked");
    }


    public void
    debug (String message) {
        if (debug)
            System.out.println("[SimpleResourceAdapterImpl] ==> " + message);
    }

    public XAResource[] getXAResources(ActivationSpec[] specs)
        throws ResourceException {
        throw new UnsupportedOperationException();
    }
}
