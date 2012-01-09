package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class ParseXML {
	private String filePath;
	private Document document;
	private Element root;
	private String fileResultPath;
	private String fileResultName;
	private FileWriter writer;
	private String[] composant;

	public ParseXML(String filePath, String fileResultPath,
			String fileResultName) {
		this.filePath = filePath;
		if (fileResultPath.endsWith("/")) {
			this.fileResultPath = fileResultPath.substring(0,
					fileResultPath.length() - 1);
		} else {
			this.fileResultPath = fileResultPath;
		}
		this.fileResultName = fileResultName;
		this.composant = new String[2];
	}

	public void parseXMLFile() {
		SAXBuilder sxb = new SAXBuilder();
		try {
			document = sxb.build(new File(filePath));
			root = document.getRootElement();

			insideWritingParse();
			insideWritingTicc();

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TooManyModuleException e) {
			System.err.println("Trop de modules dans le fichier");
		} catch (NotEnoughModuleException e) {
			System.err.println("Pas assez de modules dans le fichier");
		}
	}

	private void insideWritingTicc() {
		initializeFile(".in");
		
		insertFile("open Ticc;;", ".in");
		insertFile("", ".in");
		insertFile("parse \"" + fileResultName + ".si\";;", ".in");
		insertFile("", ".in");
		insertFile("let composant1 = mk_sym \"" + composant[0] + "\";;", ".in");
		insertFile("let composant2 = mk_sym \"" + composant[1] + "\";;", ".in");
		insertFile("", ".in");
		insertFile("print_symmod composant1;;", ".in");
		insertFile("print_symmod composant2;;", ".in");
		insertFile("", ".in");
		insertFile("let composant3 = compose composant1 composant2;;", ".in");
		insertFile("", ".in");
		insertFile("print_symmod composant3;;", ".in");
	}


	@SuppressWarnings("unchecked")
	private void insideWritingParse() throws TooManyModuleException,
			NotEnoughModuleException {
		initializeFile(".si");
		
		Namespace ns = Namespace
				.getNamespace("http://www.w3.org/2001/XMLSchema-instance");
		List<Element> listModule = root.getChildren();

		if (listModule.size() > 2)
			throw new TooManyModuleException();
		if (listModule.size() < 2)
			throw new NotEnoughModuleException();

		Iterator<Element> itModule = listModule.iterator();
		int cpt = 0;
		while (itModule.hasNext()) {
			Element module = itModule.next();

			insertFile("module " + module.getAttributeValue("nom") + " : ", ".si");
			insertFile("\tvar s : [0.."
					+ (Integer.parseInt(module.getAttributeValue("nbEtat")) - 1)
					+ "]", ".si");
			
			// insertFile("\tinitial : s = " +
			// module.getAttributeValue("localetat"));
			insertFile("", ".si");

			composant[cpt] = module.getAttributeValue("nom");
			cpt++;

			List<Element> listTransition = module.getChildren();
			Iterator<Element> itTransition = listTransition.iterator();
			while (itTransition.hasNext()) {
				Element transition = itTransition.next();

				int s, d;
				if (transition.getAttributeValue("source").equals(
						"Environnement")) {
					s = 1;
					d = 0;
				} else if (transition.getAttributeValue("destination").equals(
						"Environnement")) {
					s = 0;
					d = 1;
				} else {
					s = 0;
					d = 0;
				}

				String type = transition.getAttributeValue("type", ns);
				if (type.equals("LocalInput")) {
					insertFile("\tinput " + transition.getAttributeValue("nom")
							+ " : {", ".si");
					insertFile("\t\tlocal: ", ".si");
					insertFile("\t\t\ts = " + s + " ==> s' := " + d, ".si");

				} else if (type.equals("Output")) {
					insertFile("\toutput "
							+ transition.getAttributeValue("nom") + " : {", ".si");
					insertFile("\t\ts = " + s + " ==> s' = " + d, ".si");
				}

				insertFile("\t}", ".si");
				insertFile("", ".si");
			}

			insertFile("endmodule", ".si");
			insertFile("", ".si");
		}
	}

	private void insertFile(String text, String suffix) {
		try {
			System.out.println(text);
			text += "\n";
			writer = new FileWriter(fileResultPath + "/" + fileResultName + suffix, true);
			writer.write(text, 0, text.length());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeFile(String suffix) {
		File fichier = new File(fileResultPath + "/" + fileResultName + suffix);
		if (fichier.exists()) {
			fichier.delete();
		}
	}
}
