package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class ParseXML {
	private String path;
	private Document document;
	private Element root;
	private String fileName;
	private FileWriter writer;

	public ParseXML(String path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}

	public void parseXMLFile() {
		SAXBuilder sxb = new SAXBuilder();
		try {
			document = sxb.build(new File(path));
			root = document.getRootElement();
			//insideParse();
			insideWritingParse();

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insideParse() {
		List<Element> listModule = root.getChildren();

		int i = 0;
		Iterator<Element> itModule = listModule.iterator();
		while (itModule.hasNext()) {
			Element module = itModule.next();

			i++;

			// List<Attribute> att = module.getAttributes();
			// Iterator<Attribute> itAtt = att.iterator();
			// while(itAtt.hasNext()){
			// System.out.println(itAtt.next().getName());
			// }
			Namespace ns = Namespace
					.getNamespace("http://www.w3.org/2001/XMLSchema-instance");
			System.out.println("type : " + module.getAttributeValue("type", ns)
					+ i);
			System.out.println("nom : " + module.getAttributeValue("nom"));
			System.out.println("nombre d'états : "
					+ module.getAttributeValue("nbEtat"));

			List<Element> listTransition = module.getChildren();
			Iterator<Element> itTransition = listTransition.iterator();
			while (itTransition.hasNext()) {
				Element transition = itTransition.next();

				System.out.println("type : "
						+ transition.getAttributeValue("type", ns));
				System.out.println("nom : "
						+ transition.getAttributeValue("nom"));
				System.out.println("source : "
						+ transition.getAttributeValue("source"));
				System.out.println("destination : "
						+ transition.getAttributeValue("destination"));
			}
		}
	}

	private void insideWritingParse() {
		Namespace ns = Namespace
				.getNamespace("http://www.w3.org/2001/XMLSchema-instance");
		List<Element> listModule = root.getChildren();

		Iterator<Element> itModule = listModule.iterator();
		while (itModule.hasNext()) {
			Element module = itModule.next();

			insertFile("module " + module.getAttributeValue("nom") + " : ");
			insertFile("\tvar s : [0.." + (Integer.parseInt(module.getAttributeValue("nbEtat")) - 1) + "]");
//			insertFile("\tinitial : s = " + module.getAttributeValue("localetat"));
			insertFile("");
			
			List<Element> listTransition = module.getChildren();
			Iterator<Element> itTransition = listTransition.iterator();
			while (itTransition.hasNext()) {
				Element transition = itTransition.next();
				
				int s, d;
				if(transition.getAttributeValue("source").equals("environnement")){
					s = 1;
					d = 0;
				} else if (transition.getAttributeValue("destination").equals("environnement")){
					s = 0;
					d = 1;
				} else {
					s = 0; 
					d = 0;
				}
				
				String type = transition.getAttributeValue("type", ns);
				if(type.equals("LocalInput")){
					insertFile("\tinput " + transition.getAttributeValue("nom") + " : {");
					insertFile("\t\tlocal: ");
					insertFile("\t\t\ts = " + s + " ==> s' := " + d);
					
				} else if(type.equals("Output")){
					insertFile("\toutput " + transition.getAttributeValue("nom") + " : {");
					insertFile("\t\t\ts = " + s + " ==> s' = " + d);
				}
				
				insertFile("\t}");
				insertFile("");
			}
			
			insertFile("endmodule");
			insertFile("");
		}
	}

	private void insertFile(String text) {
		try {
			System.out.println(text);
			text += "\n";
			writer = new FileWriter(fileName, true);
			writer.write(text, 0, text.length());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeFile() {
		File fichier = new File(fileName);
		if (fichier.exists()) {
			fichier.delete();
		}
	}
}
