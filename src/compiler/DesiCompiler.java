package compiler;

import desi.DesiGrammarParser.AssignmentBooleanContext;
import desi.DesiGrammarParser.AssignmentIntegerContext;
import desi.DesiGrammarParser.BlockContext;
import desi.DesiGrammarParser.CommandContext;
import desi.DesiGrammarParser.Cond_expressnContext;
import desi.DesiGrammarParser.ElseExpressnContext;
import desi.DesiGrammarParser.ElseIfExpressnContext;
import desi.DesiGrammarParser.ExpressionBooleanConnectorContext;
import desi.DesiGrammarParser.ExpressionNumberComparisonContext;
import desi.DesiGrammarParser.ExpressionBooleanComparisonContext;
import desi.DesiGrammarParser.ExpressionBooleanContext;
import desi.DesiGrammarParser.ExpressionBooleanParenthesesContext;
import desi.DesiGrammarParser.ExpressionNumberIdentifierOnlyContext;
import desi.DesiGrammarParser.ExpressionNumberMultiplyDivideContext;
import desi.DesiGrammarParser.ExpressionNumberOnlyContext;
import desi.DesiGrammarParser.ExpressionNumberParenthesesContext;
import desi.DesiGrammarParser.ExpressionNumberPlusMinusContext;
import desi.DesiGrammarParser.IfExpressnContext;
import desi.DesiGrammarParser.PrintContext;
import desi.DesiGrammarParser.ProgramContext;
import desi.DesiGrammarParser.WhileExpressnContext;
import runtime.DesiRuntimeConstants;
import desi.DesiGrammarBaseVisitor;
import desi.DesiGrammarParser;

public class DesiCompiler extends DesiGrammarBaseVisitor{

	private class IntermediateCodeGenerator {
        private String IntermediateOutput = "";
        private void addIntermediateOutput(String output) {
            this.IntermediateOutput += output + "\n";
        }
    }
	
	private IntermediateCodeGenerator intermediateCodeGenerator = new IntermediateCodeGenerator();
	
	public String getOutput() {
		return intermediateCodeGenerator.IntermediateOutput;
	}
	
	@Override
	public Object visitProgram(ProgramContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Object visitBlock(BlockContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Object visitCommand(CommandContext ctx) {
		return visitChildren(ctx);
	}

	
	@Override
	public Object visitAssignmentInteger(AssignmentIntegerContext ctx) {
		
		String indentifier = ctx.IDENTIFIER().getText();
		if(ctx.EQUALSto()!= null)
		{
			//if initialization and assignment done in the same statement
			visit(ctx.num_expressn());
			intermediateCodeGenerator.addIntermediateOutput("LOAD " + indentifier + " ACC");
		}
		else {
			//if no assignment to the variable is done. Default value is can be 0
			intermediateCodeGenerator.addIntermediateOutput("LOAD " + indentifier + " NULL");
		}
		return null;
	}
	
	@Override
	public Object visitExpressionNumberPlusMinus(ExpressionNumberPlusMinusContext ctx) {
		visit(ctx.num_expressn(0));
		intermediateCodeGenerator.addIntermediateOutput("LOAD A ACC");
		visit(ctx.num_expressn(1));
		intermediateCodeGenerator.addIntermediateOutput("LOAD B ACC");
		
		switch(ctx.op.getType()) {
			case DesiGrammarParser.ADD:
				intermediateCodeGenerator.addIntermediateOutput("ADD ACC A B");
				break;
			case DesiGrammarParser.SUB:
				intermediateCodeGenerator.addIntermediateOutput("SUB ACC B A");
				break;
		}
		return null; 
	}
	
	
	@Override
	public Object visitExpressionNumberMultiplyDivide(ExpressionNumberMultiplyDivideContext ctx) {
//		System.out.println(ctx.getText());

		visit(ctx.num_expressn(0));
		intermediateCodeGenerator.addIntermediateOutput("LOAD A ACC");
		visit(ctx.num_expressn(1));
		intermediateCodeGenerator.addIntermediateOutput("LOAD B ACC");
		
		switch(ctx.op.getType()) {
			case DesiGrammarParser.MUL:
				intermediateCodeGenerator.addIntermediateOutput("MUL ACC A B");
				break;
			case DesiGrammarParser.DIV:
				intermediateCodeGenerator.addIntermediateOutput("DIV ACC C B");
				break;
		}

		return null;
	}
	

	@Override
	public Object visitExpressionNumberIdentifierOnly(ExpressionNumberIdentifierOnlyContext ctx) {
		String identifier = ctx.IDENTIFIER().getText();
		intermediateCodeGenerator.addIntermediateOutput("LOAD ACC " + identifier);
		if(ctx.SUB() != null)
		{
			intermediateCodeGenerator.addIntermediateOutput("UNARY ACC");
		}
		return null;
	}
	
	@Override
	public Object visitExpressionNumberOnly(ExpressionNumberOnlyContext ctx) {
		String value = ctx.DIGITS().getText();
		Double doubleValue = Double.parseDouble(value);
		if(ctx.SUB() != null) {
			doubleValue = -doubleValue;
		}
		intermediateCodeGenerator.addIntermediateOutput("LOAD ACC "+ doubleValue);
		return null; 
	}
	
	@Override
	public Object visitWhileExpressn(WhileExpressnContext ctx) {
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.WHILE_START);
		visit(ctx.cond_expressn());
        visit(ctx.block());
        intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.WHILE_END);
		return null; 
	}
	
	
	@Override
	public Object visitCond_expressn(Cond_expressnContext ctx) {
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.CONDITION_START);
		visit(ctx.bool_expressn());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.CONDITION_END);
		return null; 
		
	}
	
	@Override
	public Object visitIfExpressn(IfExpressnContext ctx) {
//		System.out.println("in if");
//		System.out.println(ctx.getText());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.IF_ELSE_START);
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.IF_START);
		visit(ctx.cond_expressn());
		visit(ctx.block());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.IF_END);
		for(int i=0; i < ctx.elseIfExpressn().size(); i++) {
			visit(ctx.elseIfExpressn(i));
		}
		if(ctx.elseExpressn() != null) {
			visit(ctx.elseExpressn());
		}
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.IF_ELSE_END);
		return null; 
//		return super.visitIfExpressn(ctx);
	}
	
	@Override
	public Object visitElseExpressn(ElseExpressnContext ctx) {
//		System.out.println(ctx.getText());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.ELSE_START);
		visit(ctx.block());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.ELSE_END);
		return null; 
	}
	
	@Override
	public Object visitElseIfExpressn(ElseIfExpressnContext ctx) {
//		System.out.println(ctx.getText());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.ELSE_IF_START);
		visit(ctx.cond_expressn());
		visit(ctx.block());
		intermediateCodeGenerator.addIntermediateOutput(DesiRuntimeConstants.ELSE_IF_END);
		return null; 
	}
	
	// after this ..
	
	@Override
	public Object visitAssignmentBoolean(AssignmentBooleanContext ctx) {
		String identifier = ctx.IDENTIFIER().getText();
		if(ctx.EQUALSto() != null) {
            visit(ctx.bool_expressn());
            intermediateCodeGenerator.addIntermediateOutput("LOAD " + identifier + " ACC");
        }
        else {
        	intermediateCodeGenerator.addIntermediateOutput("LOAD " + identifier + " NULL");
        }
        return null;
	}
	

	@Override
	public Object visitExpressionBooleanConnector(ExpressionBooleanConnectorContext ctx) {
		visit(ctx.bool_expressn(0));
        intermediateCodeGenerator.addIntermediateOutput("LOAD A ACC");
        visit(ctx.bool_expressn(1));
        intermediateCodeGenerator.addIntermediateOutput("LOAD B ACC");

        switch(ctx.op.getType()) {
            case DesiGrammarParser.AND:
            	intermediateCodeGenerator.addIntermediateOutput("AND ACC A B");
                break;
            case DesiGrammarParser.OR:
            	intermediateCodeGenerator.addIntermediateOutput("OR ACC A B");
                break;
        }
		return null; 
	}
	
	
	@Override
	public Object visitExpressionNumberComparison(ExpressionNumberComparisonContext ctx) {
		visit(ctx.num_expressn(0));
		intermediateCodeGenerator.addIntermediateOutput("LOAD A ACC");
		visit(ctx.num_expressn(1));
		intermediateCodeGenerator.addIntermediateOutput("LOAD B ACC");
		
		switch(ctx.op.getType()) {
			case DesiGrammarParser.GREATER:
				intermediateCodeGenerator.addIntermediateOutput("GREATER ACC A B");
				break;
			case DesiGrammarParser.MORE_or_EQU:
				intermediateCodeGenerator.addIntermediateOutput("GREATER_OR_EQUAL ACC A B");
				break;
			case DesiGrammarParser.LESSER:
				intermediateCodeGenerator.addIntermediateOutput("LESSER ACC A B");
				break;
			case DesiGrammarParser.LESS_or_EQU:
				intermediateCodeGenerator.addIntermediateOutput("LESSER_OR_EQUAL ACC A B");
				break;
			case DesiGrammarParser.ISEquals:
				intermediateCodeGenerator.addIntermediateOutput("EQUAL_TO ACC A B");
				break;
			case DesiGrammarParser.NotEquals:
				intermediateCodeGenerator.addIntermediateOutput("NOT_EQUAL_TO ACC A B");
				break;
		}
		return null; 
	}
	
	
	@Override
	public Object visitExpressionNumberParentheses(ExpressionNumberParenthesesContext ctx) {
		visit(ctx.num_expressn());
		return null;
	}
	
	
	@Override
	public Object visitPrint(PrintContext ctx) {
		
		if(ctx.IDENTIFIER() != null) {
			intermediateCodeGenerator.addIntermediateOutput("DISPLAY " + ctx.IDENTIFIER().getText());
        }
        else if(ctx.DIGITS() != null) {
        	intermediateCodeGenerator.addIntermediateOutput("DISPLAY " + ctx.DIGITS().getText());            
        }
        else if(ctx.BOOLEAN() != null) {
        	intermediateCodeGenerator.addIntermediateOutput("DISPLAY " + ctx.BOOLEAN().getText());
        }
        else if(ctx.num_expressn() != null) {
            visit(ctx.num_expressn());
            intermediateCodeGenerator.addIntermediateOutput("DISPLAY ACC");
        }
        else if(ctx.bool_expressn() != null) {
            visit(ctx.bool_expressn());
            intermediateCodeGenerator.addIntermediateOutput("DISPLAY ACC");
        }
		return null; 
	}

	public Object visitExpressionBooleanComparison(ExpressionBooleanComparisonContext ctx) {
		visit(ctx.comp_expressn());
		return null; 
	}
	

}

