/*import java.util.*;*/
import java.util.regex.*;
String LDirec;
String path = "//home//dsypasit//sketchbook//Dir//test";;
String SearchWord = ".jpg";
String[] listDirec;
String word = "";
String readbuffer;
boolean notFounded = true,finished = false;
String w = ".";
void setup() {
  println("Word : " + SearchWord);
  String[] lines = loadStrings("Backup.txt");
  if(lines.length!=0){
    for (int i = 0 ; i < lines.length; i++) {
      LDirec = lines[i];
      String[] readbuf = split(lines[i],"//");
      /*IsMatch(readbuf[readbuf.length-1],SearchWord);*/
    }
    if(notFounded){
      showDirectory(path);
    }
  }
  else{
    showDirectory(path);
  }
  if(notFounded){
    println("Don't have file " + SearchWord);
  }
  println("--------------");
}

void showDirectory(String Path){
  File file = new File(Path);
  if(file.isDirectory()){
    String names[] = file.list();
    for(int i = 0; i<names.length ; i++){
      word +=Path + "//" + names[i]+",";
      listDirec = split(word,",");
      saveStrings("Backup.txt", listDirec);
      LDirec = Path + "//" + names[i];
      IsMatch(names[i],SearchWord);
      showDirectory(Path+"//"+names[i]);
    }
  }
}

void IsMatch(String word,String pattern){
  //Pattern pt = Pattern.compile(pattern);
  Pattern pt = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
  Matcher mc = pt.matcher(word);
  if(mc.find()){
    OP();
  }
}

void OP(){
  LDirec = LDirec.replace("//","/");
  println("Directory : " + LDirec);
  notFounded = false;
  exit();
}
