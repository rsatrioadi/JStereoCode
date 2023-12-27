package edu.zju.icsoft.taskcontext.util.jstereocode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;

public class JParser {
	private ASTParser parser;
    private CompilationUnit unit;
    private List<ASTNode> elements;
    private IMember member;
    public JParser(ICompilationUnit unit){
        this.parser = ASTParser.newParser(AST.JLS19);
        parser.setResolveBindings(true);         //打开绑定
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);  //K_COMPILATION_UNIT：编译单元，即一个Java文件,是所需解析的代码的类型
        //最重要的两步
        parser.setEnvironment(null, null, null, true);  //setEnvironment（classPath,sourcePath,encoding,true）
        parser.setSource(unit);
        parser.setUnitName(unit.getElementName());
        this.unit = (CompilationUnit)parser.createAST((IProgressMonitor)null);
        this.elements = new ArrayList();
    }

    public JParser(IMember member) {
        this(member.getCompilationUnit());
        this.member = member;
        
    }

    public void parse() {
        if (this.member != null) {
            if (this.member instanceof IType) {
                this.elements.add(this.unit.findDeclaringNode(((IType)this.member).getKey()));
            } else if (this.member instanceof IMethod) {
                parser.setProject(((IMethod)this.member).getJavaProject());
                IBinding binding = parser.createBindings(new IJavaElement[]{(IMethod)this.member}, (IProgressMonitor)null)[0];
                if (binding instanceof IMethodBinding) {
                    ASTNode method = this.unit.findDeclaringNode(((IMethodBinding)binding).getKey());
                    this.elements.add(method);
                }
            }
            else {
                parser.setProject(((IField)this.member).getJavaProject());
                IBinding binding = parser.createBindings(new IJavaElement[]{(IField)this.member}, (IProgressMonitor)null)[0];
                ASTNode field = this.unit.findDeclaringNode(binding.getKey());
                this.elements.add(field);
            }
        } else {
            Iterator var5 = this.unit.types().iterator();
            while(var5.hasNext()) {
                Object o = var5.next();
                if (o instanceof TypeDeclaration) {
                    this.elements.add((TypeDeclaration)o);
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
