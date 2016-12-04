package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * Creates an HTTP Server on the robot that can change the 
 * settings of it.  It is initially just used to change robot
 * values.  Hopefully this will be later updated to support 
 * graphing and even small changes in the robot's actual code.
 * 
 * Can be reached at:
 * 	roboRio-766.local:8000/values
 */

//TODO Look at robotValues arrays dirrectly

public class HTTPServer extends Filter implements Runnable{

	private static String code = "";

	private static HashMap<String, String> values = new HashMap<String, String>();
	
	private String[] AUTONS;
	
	public HTTPServer(String[] autons){
		AUTONS = autons;
	}
	
	public void run(){
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(5800), 0);
		
		
		@SuppressWarnings("unused")
		HttpContext context2 = server.createContext("/display", new HttpHandler(){
			public void handle(HttpExchange exchange) throws IOException {
				String response = getHTML();
				
				exchange.sendResponseHeaders(200, response.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
    	    }
		});
		
		for(Logger log : LogFactory.getLogs().values()){
			server.createContext("/logs/" + log.getName(), new HttpHandler(){
				public void handle(HttpExchange exchange) throws IOException {
					String response = log.getHTML() + "<form action=\"values\"><button name=\"subject\" type=\"submit\" value=\"clearLog" + log.getName() + "\">Clear</button></form></html>";
					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}
			});
			System.out.println("Posting log: " + log.getName());
		}
		
		HttpContext valueSite = server.createContext("/values", new HttpHandler(){
			@SuppressWarnings("rawtypes")
			public void handle(HttpExchange exchange) throws IOException {
				String r = "<html><form action=\"values\">";
						
						//Loop through the hashMap to display values
						r +=  "<p>" + buildForm("Time of Match", 12) + "</p>"
						+ "<p>" + buildForm("Auton") + "</p>"
						+ "<p>" + buildDropDown("AutoMode", AUTONS[RobotValues.AutonMode], AUTONS) + "</p>";
												
						r += "<input type=\"submit\" value=\"Submit\" onclick \"myFunction()\"></form>"
						+ "<input type=\"submit\" value=\"Go to /display\" "
						+ "onclick=\"window.location='/display';\" />"
						+ "<script>function myFunction() {location.reload();}</script></html>";
				
				exchange.sendResponseHeaders(200, r.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(r.getBytes());
				os.close();
				
				@SuppressWarnings("unchecked")
				Map<String, Object> params =
		    	           (Map<String, Object>)exchange.getAttribute("parameters");
				System.out.println(params.values());
    	        
			    Iterator it = params.entrySet().iterator();
			    if(params.size() > 1){
			    	while (it.hasNext()) {
			    		Map.Entry pair = (Map.Entry)it.next();
		        		code += "<p><strong>Key:</strong> " + pair.getKey() + " <strong>Value: </strong>" + pair.getValue() + "</p>";
		        		
		        		if(pair.getKey().equals("AutoMode")){
		        			for(int i = 0; i < AUTONS.length; i++){
		        				if(AUTONS[i].equals(pair.getValue()))
		        					RobotValues.AutonMode = i;
		        			}
		        		}
		        			
		        		values.put((String)pair.getKey(), (String)pair.getValue());
		        		 it.remove(); // avoids a ConcurrentModificationException
	        		}
    	        }else if(params.size() == 1 && params.values().toArray()[0].toString().contains("clearLog")){
    	        	LogFactory.getInstance(params.values().toArray()[0].toString().substring(8)).clearHTML();
    	        	System.out.println("HTTP Server:\tClearing log - " + params.values().toArray()[0].toString().substring(8));
    	        }
	        	else {
	        		System.out.println("ERROR: Did not recive enough parameters: " + params.size());
	        	}
			    
    	    }
		});
		valueSite.getFilters().add(new HTTPServer(AUTONS));

		server.start();
		} catch (IOException e) {
			System.out.println("HTTP Server failed to open");
		}
	}
	
	private static String getHTML(){
		String out = "<!DOCTYPE html><meta http-equiv=\"refresh\" content=\"1\"><html><body>";
		out += code;
		out += "</body></html>";
		return out;
	}
	
	private static String buildDropDown(String valueName, String current, String... options){
		String id = valueName.replace(' ', '_');
		String out = "<select name=\"" + id + "\">";
		
		for(String s : options){
			if(s.equals(current))
				out += "<option value=\"" + s + "\" selected >" + s + "</option>";
			else
				out += "<option value=\"" + s + "\">" + s + "</option>";
		}
				
		return out + "</select>";
	}
	
	private static String buildForm(String valueName, double v){
		String id = valueName.replace(' ', '_');
		return "<label for=\"" + id.toUpperCase() + "\">" + valueName + ":</label>"
				+ "<input name=\"" + id.toLowerCase() + "\" id=\"" + id.toUpperCase() + "\" type=\"text\" value=\"" + v + "\"/>";
	}
	
	private static String buildForm(String valueName){
		return buildForm(valueName, 0.0);
	}
	
    @Override
    public String description() {
        return "Parses the requested URI for parameters";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain)
        throws IOException {
        parseGetParameters(exchange);
        parsePostParameters(exchange);
        chain.doFilter(exchange);
    }    

    private void parseGetParameters(HttpExchange exchange)
        throws UnsupportedEncodingException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        exchange.setAttribute("parameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange)
        throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters =
                (Map<String, Object>)exchange.getAttribute("parameters");
            InputStreamReader isr =
                new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
        }
    }

     @SuppressWarnings("unchecked")
     private void parseQuery(String query, Map<String, Object> parameters)
         throws UnsupportedEncodingException {

         if (query != null) {
             String pairs[] = query.split("[&]");

             for (String pair : pairs) {
                 String param[] = pair.split("[=]");

                 String key = null;
                 String value = null;
                 if (param.length > 0) {
                     key = URLDecoder.decode(param[0],
                         System.getProperty("file.encoding"));
                 }

                 if (param.length > 1) {
                     value = URLDecoder.decode(param[1],
                         System.getProperty("file.encoding"));
                 }

                 if (parameters.containsKey(key)) {
                     Object obj = parameters.get(key);
                     if(obj instanceof List<?>) {
                         List<String> values = (List<String>)obj;
                         values.add(value);
                     } else if(obj instanceof String) {
                         List<String> values = new ArrayList<String>();
                         values.add((String)obj);
                         values.add(value);
                         parameters.put(key, values);
                     }
                 } else {
                     parameters.put(key, value);
                 }
             }
         }
     }
     
}

