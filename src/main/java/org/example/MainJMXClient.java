/**
 * Run on console:
 * $ rmiregistry 8752
 */
package org.example;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * @author meslin
 *
 */
public class MainJMXClient {
	public static void main(String[] args) throws Exception {
		System.err.println("Starting...");
        String hostname = "localhost";
        int port = 8752;

        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostname + ":" + port + "/jmxrmi");

        System.out.println(url);

        JMXConnector jmxConnector = JMXConnectorFactory.connect(url);

        // Query the hello world bean name
        MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
        ObjectName mbeanName = new ObjectName("com.example:type=HelloWorld");
        for (ObjectName name : connection.queryNames(mbeanName, null)) {
            System.err.println("==> " + name);
        }

        // Construct the ObjectName for the memory MBean
        ObjectName memoryBeanName = new ObjectName("java.lang:type=Memory");

        // Query the attribute value using MBeanServerConnection
        AttributeList attributes = connection.getAttributes(memoryBeanName, new String[]{"HeapMemoryUsage"});
        Attribute heapMemoryUsageAttribute = (Attribute) attributes.get(0);
        CompositeData heapMemoryUsage = (CompositeData) heapMemoryUsageAttribute.getValue();

        // Extract the memory usage values from the CompositeData
        long usedMemory = (long) heapMemoryUsage.get("used");
        long committedMemory = (long) heapMemoryUsage.get("committed");
        long maxMemory = (long) heapMemoryUsage.get("max");

        // Print the memory usage information
        System.out.println("Used Memory: " + usedMemory + " bytes");
        System.out.println("Committed Memory: " + committedMemory + " bytes");
        System.out.println("Max Memory: " + maxMemory + " bytes");

        // Construct the ObjectName for the operating system MBean
        ObjectName osBeanName = new ObjectName("java.lang:type=OperatingSystem");

        // Query the attribute value using MBeanServerConnection
        Double cpuUsage = (Double) connection.getAttribute(osBeanName, "ProcessCpuLoad");

        // Print the CPU usage information
        System.out.println("CPU Usage: " + (cpuUsage * 100) + "%");

        // Query the total physical memory attribute
        long totalMemory = (long) connection.getAttribute(osBeanName, "TotalPhysicalMemorySize");

        // Query the available processors attribute
        int availableProcessors = (int) connection.getAttribute(osBeanName, "AvailableProcessors");

        // Print the OS information
        System.out.println("Total Memory: " + totalMemory + " bytes");
        System.out.println("Available Processors: " + availableProcessors);

        jmxConnector.close();
        System.err.println("Ended");
    }
}