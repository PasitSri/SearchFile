import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.*;

public class SearchN{

	public static void main(String[] args)throws IOException, NoSuchAlgorithmException{
		try{
			SearchN s = new SearchN();
			s.Searcher(args[0], args[1]);
		}catch(Exception e){
			System.out.println("Error, Try to new input");
		}
	}

	public void Searcher(String path, String text) throws IOException, NoSuchAlgorithmException{
		File backup = new File("Backup.xml");
		System.out.println("word : "+text);
		if(backup.exists()){
			boolean found = searchXml(path, text);
			if(found){
				System.out.println("---end of process---");
				return;
			}
			System.out.println("xml not found....");
			System.out.println();
		}
		System.out.println("try to find without xml....");
		boolean found = textSearch(path, text);
		if(!found){
			System.out.println("not found ");
		}
		System.out.println("---end of process---");
		saveXml(path);
	}

	public boolean textSearch(String path, String text){
		File file = new File(path);
		File[] files = file.listFiles();
		boolean check = false;
		if(files != null ){
			for(File f: files){
				if(isMatch(f.getName(), text)){
					System.out.println(f.getPath());
					check = true;
				}
				if(file.isDirectory()){
					check = textSearch(f.getPath(), text);
				}
			}
		}
		return check;
	}

	public String convertXml(String path) throws IOException, NoSuchAlgorithmException{
		File file = new File(path);
		String root = "<folder name=\"" + file.getName() +"\">";
		File[] files = file.listFiles();
		if(files != null){
			for(File f: files){
				if(!f.isDirectory()){
					String size = String.valueOf(file.length());
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String date = dateFormat.format(f.lastModified());
					String md5 = getFileChecksum(f);
					String openT = "<file size=\"" + size + "\" md5=\"" + md5 + "\" date=\"" + date + "\">";
					String name = openT + f.getName() + "</file>";
					root += name;
				}
				else{
					root += convertXml(f.getPath());
				}
			}
		}
		root += "</folder>";
		return root;
	}

	public void saveXml(String path) throws IOException, NoSuchAlgorithmException{
		String xml = convertXml(path);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try{
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File("./Backup.xml"));
			transformer.transform(domSource, streamResult);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean searchXml(String path, String text){
		Document doc = loadXml("Backup.xml");
		NodeList folder = doc.getElementsByTagName("folder");
		File p = new File(path);
		String pathText = p.getParent();
		boolean check = false;

		for(int i=0; i<folder.getLength(); i++){
			Node fol = folder.item(i);
			if(fol.getNodeType() == Node.ELEMENT_NODE){
				Element folEl = (Element) fol;
				String name = folEl.getAttribute("name");
				if(isMatch(name, text)){
					System.out.println(name);
					check = true;
				}
				NodeList file = folEl.getChildNodes();
				for(int j=0; j<file.getLength(); j++){
					Node fileN = file.item(j);
					if(fileN.getNodeType() == Node.ELEMENT_NODE){
						Element fileEl = (Element) fileN;
						String nameF = fileEl.getTextContent();
						if(isMatch(nameF, text) && fileEl.getTagName().equals("file")){
							System.out.println(name+"/"+nameF);
							check = true;
						}
					}
				}
			}
		}
		return check;
	}

	private Document loadXml(String filename){
		File file = new File(filename);
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			doc.getDocumentElement().normalize();
			return doc;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private boolean isMatch(String path, String pattern){
		Pattern pt = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher mc = pt.matcher(path);
		if(mc.find()){
			return true;
		}
		return false;
	}

	private static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");

		//Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		//Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0; 

		//Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		};

		//close the stream; We don't need it now.
		fis.close();

		//Get the hash's bytes
		byte[] bytes = digest.digest();

		//This bytes[] has bytes in decimal format;
		//Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
		{
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		//return complete hash
		return sb.toString();
	}


}
