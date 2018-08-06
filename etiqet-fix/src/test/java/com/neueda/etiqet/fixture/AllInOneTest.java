package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.util.Config;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import com.neueda.etiqet.core.util.Separators;
import com.neueda.etiqet.core.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllInOneTest {

	/* Atribute LOG. */
	private static Logger LOG = LogManager.getLogger(AllInOneTest.class);
	
	public static String TEST_SCENARIO1 = "test.scenario1.";
	public static String TEST_SCENARIO2 = "test.scenario2.";
	public static String TEST_SCENARIO3 = "test.scenario3.";
	
	private static EtiqetFixtures fixtures;
	private static Config configTests;
	
	static {		
		fixtures = new EtiqetFixtures(new EtiqetHandlers());
		//fixture.startServer();
		configTests = new Config();
		try {
			configTests.setProperties(PropertiesFileReader.loadFromClasspath("/junit_test.properties"));
			//configTests = PropertiesFileReader.loadPropertiesFile("/junit_test.properties");
		} catch (Exception e) {
			LOG.error("Error loading junit_test.properties file", e);			
		}
	}
	
	private String[] getTestParams(String key) {
		List<String> params = new ArrayList<String>();
		String paramBase = configTests.getString(key + "labels");
		if (!StringUtils.isNullOrEmpty(paramBase)) {
			params.add(paramBase);
			int index = 1;
			String line = configTests.getString(key + "line" + index);
			while (!StringUtils.isNullOrEmpty(line)) {
				params.add(line);
				line = configTests.getString(key + "line" + ++index);
			}
		}		
		String[] array = new String[params.size()];
		params.toArray(array);
		return array;
	}
	
	private String getTestParamValue(String param, String base, String values) {
		String out = null;
		String[] baseArray = base.split(Separators.FIELD_SEPARATOR);
		List<String> baseList = Arrays.asList(baseArray);
		if (baseList.contains(param)) {
			int position = baseList.indexOf(param);
			String[] valueArray = values.split(Separators.FIELD_SEPARATOR);
			out = valueArray[position];
		}
		return out; 
	}
	
	private String getKeyValues(String keys, String base, String value) {
		String out = "";
		String[] keysArray = keys.split(Separators.PARAM_SEPARATOR);
		String[] basesArray = base.split(Separators.FIELD_SEPARATOR);
		String[] valuesArray = value.split(Separators.FIELD_SEPARATOR);
		List<String> basesList = Arrays.asList(basesArray);
		for (String key: keysArray) {
			if (basesList.contains(key)) {
				int index = basesList.indexOf(key);
				if (!StringUtils.isNullOrEmpty(out)) {
					out += Separators.PARAM_SEPARATOR;
				}
				out += (basesArray[index] + Separators.KEY_VALUE_SEPARATOR + valuesArray[index]);
			}
		}
		return out;
	}	


	// Scenario Outline: messages and clients
	//Examples:
	//|msg |price|side|ordstatus|
	//|msg1|100  |1	|A|
	//|msg2|200  |2	|A|
//	@Test
	public void createClientCreateNewOrderSingleSendMessage() throws Throwable {
//		fixtures.startServer("testServer");
		String clientName = "colinClient";
		String[] paramsTest = getTestParams(TEST_SCENARIO1);
		String base = paramsTest[0];
		for (int execution = 1; execution < paramsTest.length; execution++) {
			String values = paramsTest[execution];
			// Given start a "fix" client "colinClient"
			fixtures.startNamedClient("fix", clientName);
		
			// And wait for client "colinClient" log on
			fixtures.waitForClientLogon(clientName);
			
			// And check if client "colinClient" is logged on
			fixtures.checkIfNamedClientIsLoggedOn(clientName);
			
			// And create a "NEW" "FIX" msg "<msg>" with "Price=<price>, Side=<side>"
			String msgName = getTestParamValue("msg", base, values);			
			String keyValues = getKeyValues("Price,Side", base, values);
			fixtures.createMessage("NEW", "FIX", msgName, keyValues);
			
			// Then send "<msg>" using "colinClient"
			fixtures.sendMsgClient(msgName, clientName);
			
			// Then check "colinClient" receives response to "<msg>"
			fixtures.namedClientNamedResponse(clientName, "EXECUTION_REPORT", "er1");
			
			// Then check for "Price=<price>,Side=<side>,OrdStatus=<ordstatus>"
			fixtures.checkLastResponseContainsKeyValueList(msgName, keyValues);
			
			// Then check "<msg>" for "Price=<price>,Side=<side>,OrdStatus=<ordstatus>"
			
			// Then check contains "Price,Side"
			fixtures.checkResponseContains("er1", "Price,Side");
			
			// Then check "<msg>" contains "Price,Side"
			fixtures.checkResponseContains(msgName, "Price,Side");
			
			// Then create response "resp" to "<msg>" for "ClOrdID"
		
			// Then stop client "colinClient"
			fixtures.stopNamedClient(clientName);
		}
//		fixtures.("testServer");
	
	}

	//	Scenario Outline: Validate that orders are correctly accepted for BME
	//	Examples:
	//	|msg |price|side|ordstatus|
	//	|msg1|100  |1|A|
	//	|msg2|200  |2|A|
//	@Test
	public void validateThatOrdersAreCorrectlyAcceptedForBme() throws EtiqetException {
//		fixtures.startServer();
		String[] paramsTest = getTestParams(TEST_SCENARIO3);
		String base = paramsTest[0];
		for (int execution = 1; execution < paramsTest.length; execution++) {
			String values = paramsTest[execution];
			// Given start a "bme" client
			fixtures.startDefaultClient("fix");
			
			//	And client is logged on
			fixtures.waitForClientLogon();
		
			// Then create a "NEW" "FIX" msg with "Price=<price>, Side=<side>"
			String keyValues = getKeyValues("Price,Side", base, values);			

			fixtures.createMessage("NEW", "FIX", keyValues);
			
			// Then send msg
			fixtures.sendDefaultMessageUsingDefaultClient();
			
			// Then check client receives response Execution Report
			fixtures.defaultClientDefaultResponseOfType("EXECUTION_REPORT");
			
			// Then check that 39=A,150=A # pending New
			
			// Then check client receives response Execution Report
			fixtures.defaultClientDefaultResponseOfType("EXECUTION_REPORT");
			
			// Then check that 39=A,150=A # New

			//	Then create a "MODIFY" "FIX" msg with "Qty=<newQty>"
			keyValues = getKeyValues("Qty", base, values);
			fixtures.createMessage("MODIFY", "FIX", keyValues);
			
			// Then send msg
			fixtures.sendDefaultMessageUsingDefaultClient();
			
			// Then check client receives response Execution Report
			fixtures.defaultClientDefaultResponseOfType("EXECUTION_REPORT");
			
			// Then check that 39=E,150=E # pending modify
			
			// Then check client receives response Execution Report
			fixtures.defaultClientDefaultResponseOfType("EXECUTION_REPORT");
			
			// Then check that 39=0,150=5 # modified

			// Then stop client
			fixtures.stopDefaultClient();
		}
//		fixtures.stopServer();
	}

	// Scenario Outline: messages and clients
	//	Examples:
	//	|msg |price|side|ordstatus|newqty|
	//	|msg1|200.5|1|A|100|
	//	|msg2|200  |2|A|
//	@Test
	public void sendNewOrderCheckModifyCheck() throws EtiqetException {
//		fixtures.startServer();
		String[] paramsTest = getTestParams(TEST_SCENARIO3);
		String base = paramsTest[0];
		for (int execution = 1; execution < paramsTest.length; execution++) {
			String values = paramsTest[execution];
			
			//	Given start a fix client
			fixtures.startDefaultClient("fix");
			
			//	And wait for client logon
			fixtures.waitForClientLogon();
			
			//	Then create a "NEW" FIX msg with "Price=<price>, Side=<side>"
			String keyValues = getKeyValues("Price,Side", base, values);
			fixtures.createMessage("NEW", "fix", keyValues);
			
			//	Then send msg
			fixtures.sendDefaultMessageUsingDefaultClient();
			
			//	Then wait for an EXECUTION_REPORT
			fixtures.defaultClientDefaultResponseOfType("EXECUTION_REPORT");
			
			//	Then check for "Price=<price>,Side=<side>,OrdStatus=<ordstatus>"		
			keyValues = getKeyValues("Price,Side,OrdStatus", base, values);
			fixtures.checkLastResponseContainsKeyValueList(keyValues);
			
			//	Then create a "MODIFY" FIX msg with "Price=<newqty>, OrigClOrdID=msg->ClOrdID"
			String newQty = this.getTestParamValue("Qty", base, values);
			keyValues = "Price=" + newQty + ",OrigClOrdID=msg->ClOrdID";
			fixtures.createMessage("MODIFY", "fix", keyValues);
			
			//	Then send msg
			fixtures.sendDefaultMessageUsingDefaultClient();
			
			//	Then wait for an EXECUTION_REPORT
			fixtures.defaultClientDefaultResponseOfType("EXECUTION_REPORT");
			
			//	Then check for "OrderQty=<newqty>"
			keyValues = "OrderQty=" + newQty;
			fixtures.checkLastResponseContainsKeyValueList(keyValues);
			
		}
//		fixtures.stopServer();
	}
	
	//Scenario: create response er7 to msg1 from er1,er2 by ClOrdID
//	@Test
	public void getResponseEr7ToMsg1FromEr1Er2ByClOrdID() {
		try {
//		fixture.startServer();
		//assertTrue("hola", false);
		//Given start a fix client
		fixtures.startDefaultClient("fix");
		
		//And wait for client logon
		fixtures.waitForClientLogon();
		
		//Then create a "NEW" FIX msg msg1 with "Price=100, Side=1"
		fixtures.createMessage("NEW", "FIX", "msg1", "Price=100, Side=1");
			
		//Then send msg msg1
		fixtures.sendNamedMessageUsingDefaultClient("msg1");
	
		//Then wait for an EXECUTION_REPORT res1
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "res1");
	
		//Then wait for an EXECUTION_REPORT res2
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "res2");
		
		//Then create a "NEW" FIX msg msg2 with "Price=100, Side=1"	
		fixtures.createMessage("NEW", "FIX", "msg2", "Price=100, Side=2");
		
		//Then send msg msg2
		fixtures.sendNamedMessageUsingDefaultClient("msg2");
		
		//Then wait for an EXECUTION_REPORT res4
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "res4");
		
		//Then wait for an EXECUTION_REPORT res5
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "res5");
		
		//Then wait for an EXECUTION_REPORT er1
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "er1");
				
		//Then wait for an EXECUTION_REPORT er2
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "er2");
		
		//Then create response er7 to msg1 from "er1,er2" by ClOrdID
		fixtures.getResponseToMessageFromListByField("er7", "msg1", "er1,er2", "ClOrdID");	
		
		fixtures.stopDefaultClient();
		
//		fixture.stopServer();
		} catch (Exception lex) {
			System.out.println(lex.getMessage());
		}
	}

	//Scenario: check that "Price,Side" match in "msg1,msg2"
//	@Test
	public void checkThatPriceSideMatchInMsg1Msg2() throws EtiqetException {
		
//		fixtures.startServer();
		
		//Given start a fix client
		fixtures.startDefaultClient("fix");
		
		//And wait for client logon
		fixtures.waitForClientLogon();
		
		//Then create a "NEW" FIX msg msg1 with "Price=100, Side=1"
		fixtures.createMessage("NEW", "FIX", "msg1", "Price=100, Side=1");
				
		//Then create a "NEW" FIX msg msg2 with "Price=100, Side=1"
		fixtures.createMessage("NEW", "FIX", "msg2", "Price=100, Side=1");
		
		//Then check that "Price,Side" match in "msg1,msg2"
		fixtures.checkThatListOfParamsMatchInListOfMessages("Price,Side", "msg1,msg2");
		
//		fixtures.stopServer();
	}

	//	Scenario: check if match in "msg1->Price=msg2->Price"
//	@Test
	public void checkIfMatchInMsg1PriceEqualsMsg2Price() throws EtiqetException {

		//Then create a "NEW" FIX msg msg1 with "Price->p->a=100, Side=1"
		fixtures.createMessage("NEW", "FIX", "msg1", "Price->p->a=100, Side=1");
		
		//Then create a "NEW" FIX msg msg2 with "Price=100, Side=1"
		fixtures.createMessage("NEW", "FIX", "msg2", "Price-=100, Side=1");

		//Then check if match in "msg1->Price->p->a=msg2->Price"
		fixtures.checkThatListOfParamsMatchInListOfMessages("Price,Side","msg1,msg2");
	}
	
	//Scenario: check msg creating using tag instead of names
//	@Test
	public void checkMsgCreatingUsingTagInsteadOfNames() throws EtiqetException {
		//Given start a server
//		fixtures.startServer();
		
		//And start a fix client
		fixtures.startDefaultClient("fix");
		
		//And wait for client logon
		fixtures.waitForClientLogon();
		
		//Then create a "NEW" FIX msg msg1 with "44=100, 54=1"
		fixtures.createMessage("NEW", "FIX", "msg1", "44=100, 54=1");
		
		//Then send msg msg1
		fixtures.sendNamedMessageUsingDefaultClient("msg1");
		
		//Then stop client
		fixtures.stopDefaultClient();
		
		//Then stop server
//		fixtures.stopServer();
	}
	
	//Scenario: create a server, create a client and wait for logon
//	@Test
	public void createAServerCreateAClientAndWaitForLogon() throws EtiqetException {
		//Given start a server
//		fixtures.startServer();
		
		//And start a fix client
		fixtures.startDefaultClient("fix");
		
		//And wait for client logon
		fixtures.waitForClientLogon();
		
		//Then create a "NEW" FIX msg msg1 with "Price=100, Side=1"
		fixtures.createMessage("NEW", "FIX", "msg1", "Price=100, Side=1");
		
		//Then send msg msg1
		fixtures.sendNamedMessageUsingDefaultClient("msg1");
		
		//Then wait for an EXECUTION_REPORT res1	
		//fixture.defaultClientNamedResponse("EXECUTION_REPORT", "res1");
		
		//Then wait for an EXECUTION_REPORT res2
		//fixture.defaultClientNamedResponse("EXECUTION_REPORT", "res2");
		
		//Then wait for an EXECUTION_REPORT er1
		fixtures.defaultClientNamedResponse("EXECUTION_REPORT", "er1");
		
		//Then stop client
		fixtures.stopDefaultClient();
		
		//Then stop server
//		fixtures.stopServer();
	}
}
