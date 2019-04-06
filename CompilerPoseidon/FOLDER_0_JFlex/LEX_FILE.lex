/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/***************************/
/* AUTHOR: OREN ISH SHALOM */
/***************************/

/*************/
/* USER CODE */
/*************/
   
import java_cup.runtime.*;



/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/
      
%%
   
/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will beq written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can beq accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column
    
/*******************************************************************************/
/* Note that this has to beq the EXACT smae name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup
   
/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must beq at the beginning of a line, */
/* will beq copied letter to letter into the Lexer class code.                */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine()    { return yyline + 1; } 
	public int getCharPos() { return yycolumn;   } 
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator = \r|\n|\r\n

OneLineCommentChar = [a-zA-Z0-9 \t\(\)\[\]\{\}\?\!\+\-\*\/\.;]
NotOneLineCommentChar = [^a-zA-Z0-9 \t\(\)\[\]\{\}\?\!\+\-\*\/\.;]

CommentCharacter = [a-zA-Z0-9 \r\n\t\(\)\[\]\{\}\?\!\+\-\.;]
NotCommnetCharacter = [^a-zA-Z0-9 \r\n\t\(\)\[\]\{\}\?\!\+\-\.;]

StringCharacter = [A-Za-z]
NotStringCharacter = [^A-Za-z]

WhiteSpace = {LineTerminator} | [ \t\f]


NUMBERP         = [1-9][0-9]*
BAD_INTEGER     = 0[0-9]+
INTEGER		= {NUMBERP} | 0{1}
ID		= [A-Za-z][A-Za-z0-9]*
ArrayP          = array
ClassP          = class
ExtendsP        = extends
ReturnP         = return
WhileP          = while
IfP             = if
NillP           = nil
NewP            = new
BAD_STRING   =[\"][A-Za-z]*[^A-Za-z\"] | [\"][^\"]* 
STRING    = [\"][A-Za-z]*[\"]

/******************************/
/* DECLARE MORE STATES		  */
/******************************/

%state STRING, COMMENT, ONELINECOMMENT

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/
   
/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only beq matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> 
{
"\/\*"              {yybegin(COMMENT);}
"\/\/"				{yybegin(ONELINECOMMENT);}
"+"					{ return symbol(TokenNames.PLUS);}
"-"					{ return symbol(TokenNames.MINUS);}
"*" 				{ return symbol(TokenNames.TIMES);}
"/"					{ return symbol(TokenNames.DIVIDE);}
"="                 { return symbol(TokenNames.EQ);}
"<"                 { return symbol(TokenNames.LT);}
">"                 { return symbol(TokenNames.GT);}
"["                 { return symbol(TokenNames.LBRACK);}
"]"                 { return symbol(TokenNames.RBRACK);}
"."                 { return symbol(TokenNames.DOT);}
";"                 { return symbol(TokenNames.SEMICOLON);}
":="                { return symbol(TokenNames.ASSIGN);}
"{"                 { return symbol(TokenNames.LBRACE);}
"}"                 { return symbol(TokenNames.RBRACE);}
"("		    { return symbol(TokenNames.LPAREN);}
")"		    { return symbol(TokenNames.RPAREN);}
","                 { return symbol(TokenNames.COMMA); }
{BAD_STRING}		{throw new RuntimeException("Illegal String");}
{STRING}			{return symbol(TokenNames.STRING, new String(yytext()).replace("\"", ""));}
{WhiteSpace}		{ /* just skip what was found, do nothing */ }
{ArrayP}            {return symbol(TokenNames.ARRAY);}
{ClassP}            {return symbol(TokenNames.CLASS);}
{ExtendsP}          {return symbol(TokenNames.EXTENDS);}
{ReturnP}           {return symbol(TokenNames.RETURN);}
{WhileP}            {return symbol(TokenNames.WHILE);}
{IfP}               {return symbol(TokenNames.IF);}
{NillP}             {return symbol(TokenNames.NIL);}
{NewP}              {return symbol(TokenNames.NEW);}
{BAD_INTEGER}       {throw new RuntimeException("Malformed integer");}
"32768"		    {return symbol(TokenNames.MIN_INT, 32768); }
{INTEGER}			{
					Integer out = new Integer(yytext());
					if (out < 32768) return symbol(TokenNames.INT, out);
					throw new RuntimeException("Integer Too Large");
				}
{ID}				{return symbol(TokenNames.ID, new String(yytext()));}

<<EOF>>				{ return symbol(TokenNames.EOF);}

}

<COMMENT> {
  "*\/"                             { yybegin(YYINITIAL);}
  {CommentCharacter}+             { /*skip*/ }
  "\*"                             { /*skip*/ }
  "\/"                            { /*skip*/ }
  {NotCommnetCharacter}          { throw new RuntimeException("Invalid comment char");}
  <<EOF>>		                 { throw new RuntimeException("Unterminated comment at end of file"); }
}

<ONELINECOMMENT> {
  {OneLineCommentChar}		{/*skip*/}
  {LineTerminator}			{yybegin(YYINITIAL);}
  <<EOF>>					{return symbol(TokenNames.EOF);}
  {NotOneLineCommentChar} 	{ throw new RuntimeException("Invalid comment char");}
}
