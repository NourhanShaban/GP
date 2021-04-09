package com.example.translator.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;
//import com.example.translator.antlr4.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.translator.ScssLexer;
import com.example.translator.ScssParser;
import com.example.translator.ScssParserBaseListener;
import com.example.translator.dto.Constants;

@Component("Scss2React_NativeConverter")
public class Scss2React_NativeConverter  extends ScssParserBaseListener implements CodeConverter {

	   private  File file;

	
	public void setFile(File file) {
		this.file = file;
	}

	Scss2React_NativeConverter() {
//      property_converter = new HashMap<String, String>();
//      property_converter.put("background-color", "backgroundColor");

    /*  try {
          if (file.createNewFile()) {
              System.out.println("New file is created " + file.getName());
          } else {
              System.out.println("File already exists.");
          }

      } catch (Exception e) {
          e.printStackTrace();
      }*/
  }

      @Override public void enterStylesheet (ScssParser.StylesheetContext ctx){
          System.out.println("enter style");
          try {
              FileWriter outputfile = new FileWriter(file.getName());
              outputfile.write("const styles = StyleSheet.create({" + "\n");
              outputfile.close();
          } catch (Exception e) {
              e.printStackTrace();
          }

      }

      @Override public void exitStylesheet (ScssParser.StylesheetContext ctx){
          try {
              FileWriter outputfile = new FileWriter(file.getName(), true);
              outputfile.write("\n});");
              outputfile.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          System.out.println("exit style");

      }


  @Override public void enterSelectors(ScssParser.SelectorsContext ctx) {
       short selcetor_number= (short) ctx.selector().size();
      if (selcetor_number ==1 ){
          String selector = ctx.selector().get(0).element().get(0).getText();
          selector=selector.replace(".","");
          selector=selector.replace("#","");
          try {
              FileWriter outputfile = new FileWriter(file.getName(), true);
              outputfile.write(selector + ":" +"{\n");
              outputfile.close();
          } catch (Exception e) {
              e.printStackTrace();
          }

      }
      System.out.println("size"+selcetor_number);
  }

  @Override public void exitRuleset(ScssParser.RulesetContext ctx) {
          try {
              FileWriter outputfile = new FileWriter(file.getName(), true);
              outputfile.write("}\n");
              outputfile.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
  }
  public static boolean isNullOrEmpty(ScssParser.MeasurementContext str) {
      if (str != null && !str.isEmpty())
          return false;
      return true;

  }

      public String handleProperty(String property){
          short pos = (short) property.indexOf("-",3);
          String before = property.substring(0,pos);
          String after = property.substring(pos+1);
          after = after.substring(0,1).toUpperCase() ; // capitaliza first letter after (-)
          after = after.concat(property.substring(pos+2));
          property = before + after ;
          return property ;
      }

      @Override public void enterProperty(ScssParser.PropertyContext ctx) {
          String property_value ="";
          String property="";

          /*handel CSS variables*/

          if (ctx.Variable() != null){
              String css_variable = ctx.Variable().getText();
              css_variable=css_variable.substring(2);
              System.out.println("css_variable"+css_variable);
              property = css_variable;
          }
          else {

              property = ctx.identifier().getText();
          }


          /* this part only for the property  */

          if (property.substring(3).contains("-")) {
              property = handleProperty(property) ;
              System.out.println("c"+property);
          }
          else {
              property =property;
          }
          try {
              FileWriter outputfile = new FileWriter(file.getName(), true);
              outputfile.write(property+":");
              outputfile.close();
          } catch (Exception e) {
              e.printStackTrace();
          }

          /* this part for the property values */
          property_value = ctx.values().commandStatement().get(0).expression().get(0).getText();
          String un="";
          if(ctx.values().commandStatement().size()==1){
              ScssParser.MeasurementContext unit = ctx.values().commandStatement().get(0).expression().get(0).measurement();
              if(isNullOrEmpty(unit)) {
//                  System.out.println("yes");
              }
              //
              else {
                  property_value = ctx.values().commandStatement().get(0).expression().get(0).measurement().Number().getText();
              }

              try {
                  FileWriter outputfile = new FileWriter(file.getName(), true);
                  outputfile.write(property_value);
                  outputfile.close();
              } catch (Exception e) {
                  e.printStackTrace();
              }
          System.out.println(property_value);
      }
  }
      @Override public void exitProperty(ScssParser.PropertyContext ctx) {
          try {
              FileWriter outputfile = new FileWriter(file.getName(), true);
              outputfile.write(",\n");
              outputfile.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }

  @Override public void enterValues(ScssParser.ValuesContext ctx) {
      if(ctx.COMMA().isEmpty()){

      }
      else{

      }
  }

  @Override public void exitValues(ScssParser.ValuesContext ctx) { }

	@Override
	public void setFileName(String fileName) {
		file=new File(fileName);

	}

	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return file;
	}

	@Override
	public ParseTree convert(String  uploadedFileName) throws IOException {
		 /* SCSS Part*/
        CharStream charStream_scss= CharStreams.fromFileName(uploadedFileName);
        ScssLexer scsslexer = new ScssLexer(charStream_scss);
        CommonTokenStream scss_tokens = new CommonTokenStream(scsslexer);
        ScssParser scssparser=new ScssParser(scss_tokens);
        scssparser.setBuildParseTree(true); // not neccessary
        ParseTree scss_tree = scssparser.stylesheet();
    
		return scss_tree;
	}

}
