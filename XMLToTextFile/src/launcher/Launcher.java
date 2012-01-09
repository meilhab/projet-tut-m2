package launcher;

import tools.ParseXML;

public class Launcher {
	public static void main(String[] args) {
		ParseXML parse = new ParseXML(
				"/media/SPOUZ_KEY/workspaceTopCased/Transformation/Resultat/Resultat.xmi",
				"/afs/deptinfo-st.univ-fcomte.fr/users/bmeilhac/Bureau/ticcFile.txt");
		parse.parseXMLFile();
	}
}
