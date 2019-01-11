import java.util.ArrayList;
import java.util.List;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXPath;


public class Afpi_CM_genericErrorHandler_GetParams extends MbJavaComputeNode {

	@SuppressWarnings("unchecked")
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		MbMessage exceptionList = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			
			// ----------------------------------------------------------
			// Add user code below
			exceptionList = inAssembly.getExceptionList();
			MbMessage env = inAssembly.getGlobalEnvironment(); 
			MbElement envRoot = env.getRootElement();
			List <MbElement> PathParams = new ArrayList<MbElement>();
			// Get all the parameter defined from global cache
			MbXPath parameter = new MbXPath("Variables/CacheDataParams/PARAM");
			PathParams = (List<MbElement>) envRoot.evaluateXPath(parameter); 
			//get the error description defined from global cache with %s
			MbElement errordescription = envRoot.getFirstElementByPath("Variables/CacheDataParams/ErrorDesc");
			String errorDescriptionstr= errordescription.getValue().toString();
			Integer listindex = PathParams.size();
			Integer i=listindex;
			while (i > 0) {		
			
			// get value of the xpath index-wise
			String str="";
			
			str = PathParams.get((listindex-i)).getValue().toString(); 						
			MbXPath xp = new MbXPath("//"+str);
			List <MbElement> nodeset = null;
			// Search for xpath in Inputroot
			nodeset = (List <MbElement>)inMessage.evaluateXPath(xp);
			//Search for xpath in exceptionlist
			if (nodeset == null) {
				nodeset= (List <MbElement>)exceptionList.evaluateXPath(xp);				
			}
			String paramvalue= "";
			// if xpath value found retrieve the value and replace it in the description string %s with retrieved value
			if (nodeset!=null){
				MbElement xpaths = nodeset.get(0);
				//outAssembly.setOutputElement(xpaths);
				paramvalue = xpaths.getValueAsString();
				if (errorDescriptionstr.contains ("%S")){
					errorDescriptionstr= errorDescriptionstr.replaceFirst("%S", paramvalue);	
				}				
			}
			i=i-1;
			}
			envRoot.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"ErrorDescriptionwithParam",errorDescriptionstr);
			//out.propagate(outAssembly);
			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
