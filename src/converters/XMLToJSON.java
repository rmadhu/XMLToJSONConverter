package converters;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLToJSON {


	public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException{

		File xmlFile = new File("Xml/sample.xml");
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(xmlFile);
		Element root = doc.getDocumentElement();
		System.out.println("\""+root.getNodeName()+"\":");
		root.normalize();
		if(root.hasChildNodes()){
			int depth = 0;
			System.out.println("{");
			printResult(root.getChildNodes(), depth);
			System.out.println("}");

		}

	}

	private static void printResult(NodeList nodeList, int depth) {
		depth++;
		boolean open = false, endOfArray = false, isArray = false;
		if(depth==2 && nodeList.getLength()>2){
			System.out.println("{");
			open = true;
		}
		int sizeOfArray = 0;
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if(tempNode.getNodeType()==1){
				//similar elements - start
				String myName = "";
				myName = tempNode.getNodeName();
				if(count+2<nodeList.getLength()){
					Node nextNode = nodeList.item(count+2);
					String nextOne = nextNode.getNodeName();
					if(myName.equalsIgnoreCase(nextOne)){
						sizeOfArray++;
						if(!isArray){
							System.out.print("\""+tempNode.getNodeName()+"\":[");
						}
						isArray = true;
					}
					else if(sizeOfArray>0){//means atleast 2 items are same
						endOfArray = true;
					}
				}
				else if(sizeOfArray>0){
					endOfArray = true;
				}
				

				if (tempNode.hasAttributes()) {
					open = handleNodeWithAttributes(depth, isArray, tempNode);
				}

				//else if only element node without attributes
				else{
					if(isArray){
						System.out.print("\""+"text"+"\"");
					}
					else{
						System.out.print("\""+tempNode.getNodeName()+"\"");
					}

					if(tempNode.hasChildNodes()){
						System.out.print(":");
						printResult(tempNode.getChildNodes(), depth);
					}
				}
			}

			if(tempNode.getNodeName().contains("#")){
				if(!tempNode.getTextContent().trim().isEmpty()){
					System.out.print("\""+tempNode.getTextContent()+"\"");
					if(tempNode.getParentNode().hasAttributes()){
						System.out.println("}");
					}
				}
				continue;
			}

			if(endOfArray){
				System.out.print("]");
				endOfArray = false;
				isArray = false;
				sizeOfArray = 0;
			}

			if(count<nodeList.getLength()-2){
				System.out.println(",");
			}
			else if(count==nodeList.getLength()-2 && open){

				System.out.println("}");
			}
		}
	}

	private static boolean handleNodeWithAttributes(int depth, boolean isArray,
			Node tempNode) {
		boolean open;
		// get attributes names and values
		NamedNodeMap nodeMap = tempNode.getAttributes();
		open=true;
		if(!isArray){
		System.out.println("\""+tempNode.getNodeName()+"\"");
		}
		System.out.println("{");
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);
			System.out.print("\"" + node.getNodeName()+"\"");
			System.out.println(":\"" + node.getNodeValue()+"\",");
		}
		System.out.print("\""+"text"+"\"");
		if(tempNode.hasChildNodes()){
			System.out.print(":");
		}
		printResult(tempNode.getChildNodes(), depth);
		return open;
	}


}
