package edu.zju.icsoft.taskcontext.util.jstereocode;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class JParser {
    private final ASTParser parser;
    private final CompilationUnit unit;
    private final List<ASTNode> elements;
    private IMember member;

    public JParser(ICompilationUnit unit) {
        this.parser = ASTParser.newParser(AST.JLS19);
        parser.setResolveBindings(true);         // open binding
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);  // K_COMPILATION_UNIT: Compilation unit, that is, a Java file, is the type of code that needs to be parsed
        // The two most important steps
        parser.setEnvironment(null, null, null, true);  //setEnvironment（classPath,sourcePath,encoding,true）
        parser.setSource(unit);
        parser.setUnitName(unit.getElementName());
        this.unit = (CompilationUnit) parser.createAST(null);
        this.elements = new ArrayList<>();
    }

    public JParser(IMember member) {
        this(member.getCompilationUnit());
        this.member = member;

    }

    public void parse() {
        if (this.member != null) {
            if (this.member instanceof IType type) {
                this.elements.add(this.unit.findDeclaringNode((type).getKey()));
            } else if (this.member instanceof IMethod method) {
                parser.setProject(method.getJavaProject());
                IBinding binding = parser.createBindings(new IJavaElement[]{method}, null)[0];
                if (binding instanceof IMethodBinding) {
                    ASTNode declaringNode = this.unit.findDeclaringNode(binding.getKey());
                    this.elements.add(declaringNode);
                }
            } else {
                parser.setProject(this.member.getJavaProject());
                IBinding binding = parser.createBindings(new IJavaElement[]{this.member}, null)[0];
                ASTNode field = this.unit.findDeclaringNode(binding.getKey());
                this.elements.add(field);
            }
        } else {
            for (Object o : this.unit.types()) {
                if (o instanceof TypeDeclaration td) {
                    this.elements.add(td);
                }
            }
        }
    }

    public List<ASTNode> getElements() {
        return this.elements;
    }

    public CompilationUnit getCompilationUnit() {
        return this.unit;
    }


}
