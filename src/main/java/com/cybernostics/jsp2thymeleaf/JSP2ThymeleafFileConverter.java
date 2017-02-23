/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.*;
import static com.cybernostics.jsp2thymeleaf.converters.AvailableConverters.scanForConverters;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafFileConverter
{

    public static final Logger logger = Logger.getLogger(JSP2ThymeleafFileConverter.class.getName());
    private boolean showBanner = true;
    private JSPElementNodeConverter elementConverter = new CopyElementConverter();

    public JSP2ThymeleafFileConverter()
    {
        scanForConverters();

    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    public void convert(TokenisedFile file, File toWrite)
    {

        try
        {
            CommonTokenStream tokens = new CommonTokenStream(file.getLexer());
            // Pass the tokens to the parser
            JSPParser parser = new JSPParser(tokens);
            // Specify our entry point
            JSPParser.JspDocumentContext documentContext = parser.jspDocument();
            // Walk it and attach our listener
            ParseTreeWalker walker = new ParseTreeWalker();
            JSP2ThymeleafTransformerListener listener = new JSP2ThymeleafTransformerListener();
            listener.setShowBanner(showBanner);
            walker.walk(listener, documentContext);

            listener.write(new FileOutputStream(toWrite));
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void convert(InputStream inputStream, OutputStream outputStream)
//    {
//        try
//        {
//
//            JSPLexer lexer = new JSPLexer(new ANTLRInputStream(inputStream));
//            // Get a list of matched tokens
//            CommonTokenStream tokens = new CommonTokenStream(lexer);
//            // Pass the tokens to the parser
//            JSPParser parser = new JSPParser(tokens);
//            // Specify our entry point
//            JSPParser.JspDocumentContext documentContext = parser.jspDocument();
//            // Walk it and attach our listener
//            ParseTreeWalker walker = new ParseTreeWalker();
//            JSP2ThymeleafTransformerListener listener = new JSP2ThymeleafTransformerListener();
//            walker.walk(listener, documentContext);
//            listener.write(outputStream);
//        } catch (IOException ex)
//        {
//            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
