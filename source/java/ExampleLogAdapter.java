package org.rsna.test;

import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.rsna.ctp.objects.DicomObject;
import org.rsna.ctp.pipeline.Status;
import org.rsna.ctp.pipeline.Status;
import org.rsna.ctp.stdstages.logger.*;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Element;

/**
 * An example LogAdapter implementation.
 */
public class ExampleLogAdapter implements LogAdapter {
	
	static final Logger logger = Logger.getLogger(ExampleLogAdapter.class);
	Element element = null;
	String cohortName = "cohort";
	
	/**
	 * Instantiate the LogAdapter.
	 * @param element the XML element from the configuration file
	 * specifying the configuration of the pipeline stage that
	 * will use this LogAdapter to export differences in DicomObjects.
	 * Any configuration information must be contained in attributes
	 * of that stage's element.
	 */
	public ExampleLogAdapter(Element element) {
		this.element = element;
		
		//If you need to capture anything from the configuration element,
		//do it here. Note: the configuration element of the LogAdapter is a child
		//of element. By convention, the name of the child element is the name of
		//the LogAdapter class (in this case, ExampleLogAdapter). 
		//
		//In more comlex situations, you may have to construct the cohortName by
		//interrogating either or both of the current and cached objects.
		String className = getClass().getName();
		className = className.substring(className.lastIndexOf(".")+1);
		Element child = XmlUtil.getFirstNamedChild(element, className);
		if (child != null) {
			cohortName = child.getAttribute("cohortName");
		}
	}

	/**
	 * Get the name of the cohort to which the current object belongs.
	 * @param currentObject the object that has madve it down the pipeline
	 * to the calling stage.
	 * @param cachedObject the object that was cached at the head end of the pipe.
	 * @return the name of the cohort to which the objects belong.
	 */
	public String getCohortName(DicomObject currentObject, DicomObject cachedObject) {
		//In this example, we will return the cohortName attribute value 
		//obtained from the configuration element
		return cohortName;
	}

	/**
	 * Connect to the external logging database.
	 * This method is called whenever the exporter starts a sequence of exports.
	 * It is not called again until the exporter empties the export queue,
	 * disconnects, and then receives another QueueEntry to export.
	 * @return Status.OK or Status.FAIL
	 */
	public synchronized Status connect() {
		//Add code here to connect to the external database
		//...
		return Status.OK;
	}
	
	/**
	 * Disconnect from the external logging database.
	 * This method is called when the exporter empties the export queue
	 * or when the pipeline tells the stage to shut down.
	 * This method should commit the database and then disconnect.
	 * @return Status.OK or Status.FAIL
	 */
	public synchronized Status disconnect() {
		//Add code here to commit and disconnect.
		//...
		return Status.OK;
	}
	
	/**
	 * Export one QueueEntry to the external logging database.
	 * This method is called from the exporter thread.
	 * @return Status.OK, Status.RETRY, or Status.FAIL
	 */
	public synchronized Status export(QueueEntry queueEntry) {
		logger.info("Cohort name: "+queueEntry.cohortName);
		LinkedList<LoggedElement> list = queueEntry.list;
		for (LoggedElement el : list) {
			logger.info(el.getElementTag() + ": " + el.name + ": \"" + el.currentValue + "\"/\"" + el.cachedValue + "\"");
		}
		return Status.OK;
	}
	
}