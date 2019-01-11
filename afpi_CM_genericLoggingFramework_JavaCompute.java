package com.accenture.afpi.logging;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;

/**
* Copyright 2015 Accenture. All Rights Reserved. Accenture Confidential and
* Proprietary.
* 
* Disclosure and use of this source code is governed by contract with
* Accenture. Any open source or third party source code referenced in this
* source code is governed by their respective license terms.
*/

public class afpi_CM_genericLoggingFramework_JavaCompute extends
		MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		
		MbOutputTerminal out = getOutputTerminal("out");
				
		Logging logging = null;
		
		//Set logMessage & logLevel
		MbElement mbLogMessage = null;
		MbElement mbLogLevel = null;		
				
		//Declaring String variable to store reference of mbLogMessage & mbLogLevel 
		String logMessage = null;
		String logLevel = null;
		
		//Declaring Default Log Severity Levels
		String logErrorVal = "ERROR";
		String logWarningVal = "WARNING";
		String logInfoVal = "INFO";
		String logDebugVal = "DEBUG"; 
		String logFatalVal = "FATAL";
		
		//Set Message Flow Name
		String messageFlowName = getMessageFlow().getName();
		
		MbElement root = inAssembly.getMessage().getRootElement();
		MbElement body = root.getLastChild().getFirstChild();
		mbLogMessage = body.getFirstChild();
		mbLogLevel = body.getLastChild();							
										
		if (mbLogMessage != null)
		{
			logMessage =  mbLogMessage.getValue().toString();
		}
				
		if (mbLogLevel != null)
		{			
			logLevel =  mbLogLevel.getValue().toString();
		}				
		
		//Create String Buffer to set Logging message
		StringBuffer logMsg = null;
		
		// Calling Static Method on logging to set logger properties file from UDP
		String propFilePath = getUserDefinedAttribute("Log4jPropertiesFile").toString();
		
		boolean retValue = propFilePath.contains(":/");				
		if ( retValue )
		{			
			propFilePath = propFilePath.replace('/','\\');
		}
				
		Logging.setLogPropertiesFile(propFilePath);
						
		// Getting the instance of logging class.
		logging = Logging.getInstance();
										
		java.util.Date date= new java.util.Date();
		Date currentTimestamp = (new Timestamp(date.getTime()));
					
		logMsg = new StringBuffer();
				
		logMsg.append("<LOG4J_LOGGING><LOG4J_LEVEL>" + logLevel + "</LOG4J_LEVEL><MESSAGE>"  + logMessage + "</MESSAGE><SERVICE>"  + messageFlowName +  "</SERVICE><TIMESTAMP>"  + currentTimestamp + "</TIMESTAMP></LOG4J_LOGGING>" + "\n");
		logMsg.append("\n");
		
		//Log Error Message
		if (logLevel.equals(logErrorVal))	
		{			
			logging.logError(logMsg.toString());
		}
		
		//Log Warning Message
		if (logLevel.equals(logWarningVal))
		{			
			logging.logWarning(logMsg.toString());
		}
		
		//Log Info Message
		if (logLevel.equals(logInfoVal))
		{			
			logging.logInfo(logMsg.toString());
		}
		
		//Log Debug Message
		if (logLevel.equals(logDebugVal))
		{			
			logging.logDebug(logMsg.toString());
		}
		
		//Log Fatal Message
		if (logLevel.equals(logFatalVal))
		{			
			logging.logFatal(logMsg.toString());
		}
		
		out.propagate(inAssembly);

	}

}
