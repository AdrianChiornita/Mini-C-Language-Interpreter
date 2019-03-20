grammar IMPGrammar;
@header {package ANTLR;}
                    
program     : 'int ' varlist ';' main;
main        : statement | sequence_statement;
 
statement           : asign_statement 
					| if_statement 
					| while_statement 
					| block 
					;

sequence_statement  : statement (statement | sequence_statement)?;


asign_statement     : variable '=' arithmetic_expr ';';
if_statement        : 'if ' logical_expr then_=block 'else' else_=block;
while_statement     : 'while ' logical_expr block;
block               : '{}' | '{' sequence_statement '}';

varlist             : variable 
                    | variable ',' varlist;


logical_expr		: '!' logical_expr 								# not
					| left=logical_expr '&&' right=logical_expr 	# and
					| comparison_expr               				# compare
					| '(' logical_expr ')'							# logical_brkt
					| bool_primary									# logical_primary	
					;

comparison_expr : left=arithmetic_expr '>' right=arithmetic_expr 	# greater
                | '(' comparison_expr ')' 							# combare_bracket
                ;
 
 
arithmetic_expr		: left=arithmetic_expr '/' right=arithmetic_expr   	 # div
					| left=arithmetic_expr '+' right=arithmetic_expr  	 # plus
					| '(' arithmetic_expr ')'        					 # arth_bracket
					| numeric_entity						 			 # arth_primary
					;
				
numeric_entity : arithmetic_primary     
               | variable
               ;
               
arithmetic_primary  : NUMBER;
bool_primary        : BOOLEAN;
variable            : STRING;


NUMBER       : DIGIT ('0' | DIGIT)* | '0';
BOOLEAN      : 'true' | 'false';
DIGIT		 : '1'..'9';
STRING       : ('a'..'z')+;
WHITESPACES  : (' ' | '\t' | '\n' | '\r')+ -> skip;