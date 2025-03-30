import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public static void main() {
    // Task01
    String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
    List<Employee> list = parseCSV(columnMapping, "data.csv");
    String json = listToJson(list);
    writeString(json, "data.json");

    // Task02
    List<Employee> list2 = parseXML("data.xml");
    String json2 = listToJson(list2);
    writeString(json2, "data2.json");
}

private static List<Employee> parseXML(String fileName) {
    List<Employee> list = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    Document doc;

    try {
        builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
        throw new RuntimeException(e);
    }

    try {
        doc = builder.parse(new File(fileName));
    } catch (SAXException | IOException e) {
        throw new RuntimeException(e);
    }

    Node root = doc.getDocumentElement();
    NodeList nodeList = root.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
        Node childNode = nodeList.item(i);
        if (Node.ELEMENT_NODE == childNode.getNodeType() && "employee".equals(childNode.getNodeName())) {
            Employee employee = new Employee();
            for (int j = 0; j < childNode.getChildNodes().getLength(); j++) {
                Node propertyNode = childNode.getChildNodes().item(j);
                switch (propertyNode.getNodeName()) {
                    case "id":
                        employee.id = Integer.parseInt(propertyNode.getTextContent());
                        break;
                    case "firstName":
                        employee.firstName = propertyNode.getTextContent();
                        break;
                    case "lastName":
                        employee.lastName = propertyNode.getTextContent();
                        break;
                    case "country":
                        employee.country = propertyNode.getTextContent();
                        break;
                    case "age":
                        employee.age = Integer.parseInt(propertyNode.getTextContent());
                        break;
                }
            }
            list.add(employee);
        }
    }
    return list;
}

private static void writeString(String json, String fileName) {
    try {
        FileWriter writer = new FileWriter(fileName);
        writer.write(json);
        writer.close();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

private static String listToJson(List<Employee> list) {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    Gson gson = builder.create();
    return gson.toJson(list, List.class);
}

private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);
        CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                .withMappingStrategy(strategy)
                .build();
        return csv.parse();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
