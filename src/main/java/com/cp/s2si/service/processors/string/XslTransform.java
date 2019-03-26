package com.cp.s2si.service.processors.string;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;
import java.util.UUID;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.ProcessorData;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class XslTransform extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XslTransform.class);
	
	private String xsl;
	private Templates xslt;

	public XslTransform(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds, String xsl) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
		this.xsl = xsl;
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		XmlMapper xm = new XmlMapper();
		if(message.isStringData()) {
			try {
				xm.readTree(message.getStringData());
			} catch (IOException e) {
				throw new MessageValidationException("String data is not a valid xml", e);
			}
		} else {
			throw new MessageValidationException("ProcessData is not a valid xml");
		}
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		try {
            // Use the template to create a transformer
            Transformer xformer = xslt.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(new StringReader(message.getStringData()));
            StringWriter sw = new StringWriter();
            Result result = new StreamResult(sw);

            // Apply the xsl file to the source file and write the result to the output file
            xformer.transform(source, result);
            
            // Set the result string onto message and return
            ProcessorData pd = new ProcessorData();
            pd.setStringData(sw.toString());
            pd.setPriority(message.getPriority());
            return pd;
        } catch (TransformerException e) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = e.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            LOGGER.error("Transformation exception on col {}, line {}, publicid {}, systemid {}", col, line, publicId, systemId);
            throw new MessageProcessingContinueException(e);
        }
	}

	@Override
	public void initProcessor() throws Exception {
		// Use the factory to create a template containing the xsl file
		xslt = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(xsl)));
	}

	@Override
	public void cleanUp() {
		// do nothing
	}

}
