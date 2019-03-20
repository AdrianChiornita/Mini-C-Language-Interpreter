package IMPInterpreter;

%%

%public
%class IMPLexer
%line
%type IMPToken
%throws IMPLexerException
%{
    @SuppressWarnings("serial")
	class IMPLexerException extends Exception {
		public IMPLexerException(String message) {
			super(message);
		}
	}
%}

bool = "true" | "false"
number = [1-9][0-9]* | 0
variable = [a-zA-Z]+

comma = ","
semicolon = ";"
whitespaces = \s+

assign = "="
add = "+"
divide = "/"
and = "&&"
greater = ">"
not = "!"

bracket_open = "("
bracket_close = ")"
block_open = "{"
block_close = "}"

if = "if"
else = "else"
while = "while"
declaration = "int"

%%

{if}                                {return new IMPToken(IMPTokenType.IF, yyline);}
{else}                              {return new IMPToken(IMPTokenType.ELSE, yyline);}
{while}                             {return new IMPToken(IMPTokenType.WHILE, yyline);}
{declaration}                       {return new IMPToken(IMPTokenType.START_DECLARATION, yyline);}

{variable} {whitespaces}? {assign}  {return new IMPToken(IMPTokenType.START_ASSIGN, yyline, yytext());}

{add}                               {return new IMPToken(IMPTokenType.ADD, yyline);}
{divide}                            {return new IMPToken(IMPTokenType.DIVIDE, yyline);}
{and}                               {return new IMPToken(IMPTokenType.AND, yyline);}
{greater}                           {return new IMPToken(IMPTokenType.GREATER, yyline);}
{not}                               {return new IMPToken(IMPTokenType.NOT, yyline);}

{block_open}                        {return new IMPToken(IMPTokenType.START_BLOCK, yyline);}
{block_close}                       {return new IMPToken(IMPTokenType.STOP_BLOCK, yyline);}
{bracket_open}                      {return new IMPToken(IMPTokenType.START_BRACKET, yyline);}
{bracket_close}                     {return new IMPToken(IMPTokenType.STOP_BRACKET, yyline);}
{semicolon}                         {return new IMPToken(IMPTokenType.STOP_ASSIGN_OR_DECLARATION, yyline);}

{whitespaces}                       {/* skip */}
{comma}                             {/* skip */}

{number}                            {return new IMPToken(IMPTokenType.NUMBER, yyline, yytext());}
{bool}                              {return new IMPToken(IMPTokenType.BOOLEAN, yyline, yytext());}
{variable}                          {return new IMPToken(IMPTokenType.VARIABLE, yyline, yytext());}

<<EOF>>                             {return new IMPToken(IMPTokenType.EOF, yyline);}
.                                   {throw new IMPLexerException("Lexical error" + yyline);}
