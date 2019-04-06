   
import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import AST.*;
import IR.*;
import MIPS.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AST_PROGRAM AST;
		FileReader file_reader;
		PrintWriter file_writer;
		String inputFilename = argv[0];
		String outputFilename = argv[1];
        StringBuilder errorHolder = new StringBuilder();

		try {

			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFilename);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			file_writer = new PrintWriter(outputFilename);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l,errorHolder );

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			AST = (AST_PROGRAM) p.parse().value;
			
			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			AST.PrintMe();

			/**************************/
			/* [7] Semant the AST ... */
			/**************************/
			AST.SemantMe();
			
			/**********************/
			/* [8] IR the AST ... */
			/**********************/
			AST.IRme();
            System.out.println("\n++++++++++++++++++++++++++++");

            System.out.println("         IR code         ");
            System.out.println("++++++++++++++++++++++++++++");
            System.out.print(IR.getInstance());
            System.out.println("++++++++++++++++++++++++++++");
            System.out.println("++++++++++++++++++++++++++++");
            System.out.println("    IR code + registers     ");
            System.out.println("++++++++++++++++++++++++++++");
            IR.getInstance().Analyze();
            System.out.print(IR.getInstance());

            /***********************/
			/* [9] MIPS the IR ... */
			/***********************/

			MIPSER.getInstance().setOutputFileWriter(file_writer);
			IR.getInstance().MIPSme();

			/**************************************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/**************************************/
			AST_GRAPHVIZ.getInstance().finalizeFile();			

			/***************************/
			/* [11] Finalize MIPS file */
			/***************************/
			MIPSER.getInstance().finalizeFile();

			/**************************/
			/* [12] Close output file */
			/**************************/
			file_writer.close();
    	}
			     
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


