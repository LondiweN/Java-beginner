/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.ast.scanners;

import com.oracle.stcurr.ide.ast.model.CompilationUnitNode;
import com.oracle.stcurr.ide.ast.model.ImportNode;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mheimer
 */
public class NodeConvertingScanner extends SimpleTreeVisitor<Object, Trees> {

    private static final Logger logger = Logger.getLogger("treeapi");
    private CompilationUnitTree compilationUnitTree;
    
    List<CompilationUnitNode> compilationUnits = new ArrayList<>();
    
    @Override
    public Object visitCompilationUnit(CompilationUnitTree node, Trees trees) {
        compilationUnitTree = node;
        String fileName = node.getSourceFile().getName();
        logger.log(Level.INFO, "visitCompilationUnit for {0}", fileName);
        CompilationUnitNode compilationUnitNode = new CompilationUnitNode();
        compilationUnitNode.setFileName(fileName);
        
        List<? extends AnnotationTree> packageAnnotations = node.getPackageAnnotations();
        //TODO
        System.out.println("Annotation size: " + packageAnnotations.size());
        for(AnnotationTree packageAnnotation : packageAnnotations) {
            Tree annotationType = packageAnnotation.getAnnotationType();
            for(ExpressionTree annotationArgument : packageAnnotation.getArguments()) {
                
            }
        }
        
        ExpressionTree packageName = node.getPackageName();
        if(packageName != null && Tree.Kind.IDENTIFIER == packageName.getKind()) {
            compilationUnitNode.setPackageName(((IdentifierTree)packageName).getName().toString());
        }
        
        List<? extends ImportTree> imports = node.getImports();
        for(ImportTree importTree : imports) {
            Tree it = importTree.getQualifiedIdentifier();
            String importIdent = it.toString();
            boolean isStatic = importTree.isStatic();
            ImportNode importNode = new ImportNode();
            importNode.setStatic(isStatic);
            importNode.setIdentifier(importIdent);
            compilationUnitNode.addImportNode(importNode);
        }
        
        
        return super.visitCompilationUnit(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitImport(ImportTree node, Trees trees) {
        return super.visitImport(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitClass(ClassTree node, Trees trees) {
        String className = node.getSimpleName().toString();
        logger.log(Level.INFO, "visitClass for {0}", className);
        return super.visitClass(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitMethod(MethodTree node, Trees trees) {
        return super.visitMethod(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitVariable(VariableTree node, Trees trees) {
        return super.visitVariable(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitEmptyStatement(EmptyStatementTree node, Trees trees) {
        return super.visitEmptyStatement(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitBlock(BlockTree node, Trees trees) {
        return super.visitBlock(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitDoWhileLoop(DoWhileLoopTree node, Trees trees) {
        return super.visitDoWhileLoop(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitWhileLoop(WhileLoopTree node, Trees trees) {
        return super.visitWhileLoop(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitForLoop(ForLoopTree node, Trees trees) {
        return super.visitForLoop(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitEnhancedForLoop(EnhancedForLoopTree node, Trees trees) {
        return super.visitEnhancedForLoop(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitLabeledStatement(LabeledStatementTree node, Trees trees) {
        return super.visitLabeledStatement(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitSwitch(SwitchTree node, Trees trees) {
        return super.visitSwitch(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitCase(CaseTree node, Trees trees) {
        return super.visitCase(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitSynchronized(SynchronizedTree node, Trees trees) {
        return super.visitSynchronized(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitTry(TryTree node, Trees trees) {
        return super.visitTry(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitCatch(CatchTree node, Trees trees) {
        return super.visitCatch(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitConditionalExpression(ConditionalExpressionTree node, Trees trees) {
        return super.visitConditionalExpression(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitIf(IfTree node, Trees trees) {
        return super.visitIf(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitExpressionStatement(ExpressionStatementTree node, Trees trees) {
        return super.visitExpressionStatement(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitBreak(BreakTree node, Trees trees) {
        return super.visitBreak(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitContinue(ContinueTree node, Trees trees) {
        return super.visitContinue(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitReturn(ReturnTree node, Trees trees) {
        return super.visitReturn(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitThrow(ThrowTree node, Trees trees) {
        return super.visitThrow(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitAssert(AssertTree node, Trees trees) {
        return super.visitAssert(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Trees trees) {
        return super.visitMethodInvocation(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitNewClass(NewClassTree node, Trees trees) {
        return super.visitNewClass(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitNewArray(NewArrayTree node, Trees trees) {
        return super.visitNewArray(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitParenthesized(ParenthesizedTree node, Trees trees) {
        return super.visitParenthesized(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitAssignment(AssignmentTree node, Trees trees) {
        return super.visitAssignment(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitCompoundAssignment(CompoundAssignmentTree node, Trees trees) {
        return super.visitCompoundAssignment(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitUnary(UnaryTree node, Trees trees) {
        return super.visitUnary(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitBinary(BinaryTree node, Trees trees) {
        return super.visitBinary(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitTypeCast(TypeCastTree node, Trees trees) {
        return super.visitTypeCast(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitInstanceOf(InstanceOfTree node, Trees trees) {
        return super.visitInstanceOf(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitArrayAccess(ArrayAccessTree node, Trees trees) {
        return super.visitArrayAccess(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitMemberSelect(MemberSelectTree node, Trees trees) {
        return super.visitMemberSelect(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitIdentifier(IdentifierTree node, Trees trees) {
        return super.visitIdentifier(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitLiteral(LiteralTree node, Trees trees) {
        return super.visitLiteral(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitPrimitiveType(PrimitiveTypeTree node, Trees trees) {
        return super.visitPrimitiveType(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitArrayType(ArrayTypeTree node, Trees trees) {
        return super.visitArrayType(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitParameterizedType(ParameterizedTypeTree node, Trees trees) {
        return super.visitParameterizedType(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitUnionType(UnionTypeTree node, Trees trees) {
        return super.visitUnionType(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitTypeParameter(TypeParameterTree node, Trees trees) {
        return super.visitTypeParameter(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitWildcard(WildcardTree node, Trees trees) {
        return super.visitWildcard(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitModifiers(ModifiersTree node, Trees trees) {
        return super.visitModifiers(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitAnnotation(AnnotationTree node, Trees trees) {
        return super.visitAnnotation(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitOther(Tree node, Trees trees) {
        return super.visitOther(node, trees); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object visitErroneous(ErroneousTree node, Trees trees) {
        return super.visitErroneous(node, trees); //To change body of generated methods, choose Tools | Templates.
    }
}
