package edu.zju.icsoft.taskcontext.util.jstereocode;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.LinkedList;
import java.util.List;


public class StereotypeIdentifier {
    private JParser parser;
    private final List<StereotypedElement> stereotypedElements;
    double methodsMean;
    double methodsStdDev;

    public StereotypeIdentifier() {
        this.stereotypedElements = new LinkedList<>();
    }

    public void setParameters(ICompilationUnit unit, double methodsMean, double methodsStdDev) {
        this.parser = new JParser(unit);
        this.methodsMean = methodsMean;
        this.methodsStdDev = methodsStdDev;
    }

    public void setParameters(IMember member, double methodsMean, double methodsStdDev) {
        this.parser = new JParser(member);
        this.methodsMean = methodsMean;
        this.methodsStdDev = methodsStdDev;
    }

    public void identifyStereotypes() {
        if (this.parser != null) {
            this.parser.parse();
            for (ASTNode element : this.parser.getElements()) {
                try {
                    StereotypedElement stereoElement;
                    if (element instanceof TypeDeclaration type) {
//                    	System.out.println("analyzing a class: ");
                        stereoElement = new StereotypedType(type, this.methodsMean, this.methodsStdDev);
                    } else if (element instanceof MethodDeclaration method) {
//                        System.out.println("analyzing a method: ");
                        stereoElement = new StereotypedMethod(method);
                    } else {
                        continue;
                    }
                    stereoElement.findStereotypes();
                    this.stereotypedElements.add(stereoElement);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<StereotypedElement> getStereotypedElements() {
        return this.stereotypedElements;
    }

    public JParser getParser() {
        return this.parser;
    }
}
