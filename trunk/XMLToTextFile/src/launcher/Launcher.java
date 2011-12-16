package launcher;

import tools.ParseXML;

public class Launcher {
	public static void main(String[] args) {
		ParseXML parse = new ParseXML(
				"/afs/deptinfo-st.univ-fcomte.fr/users/bmeilhac/workspace/Transformation2/Resultat/Resultat.xmi",
				"/afs/deptinfo-st.univ-fcomte.fr/users/bmeilhac/Bureau/ticcFile.txt");
		parse.parseXMLFile();
	}
}
