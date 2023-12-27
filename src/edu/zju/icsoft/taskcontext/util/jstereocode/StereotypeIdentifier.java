package edu.zju.icsoft.taskcontext.util.jstereocode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;



public class StereotypeIdentifier {
    private static final String TypeDeclaration = null;
	private JParser parser;
    private List<StereotypedElement> stereotypedElements;
    double methodsMean;
    double methodsStdDev;

    public StereotypeIdentifier() {
        this.stereotypedElements = new LinkedList<StereotypedElement>();
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
            Iterator<?> var2 = this.parser.getElements().iterator();
            while(var2.hasNext()) {
                ASTNode element = (ASTNode)var2.next();
                try {
                	StereotypedElement stereoElement;
                    if (element instanceof TypeDeclaration) {
//                    	System.out.println("analyzing a class: ");
                        stereoElement = new StereotypedType((TypeDeclaration)element, this.methodsMean, this.methodsStdDev);
                    } else {
                        if (!(element instanceof MethodDeclaration)) {
                            continue;
                        }
//                        System.out.println("analyzing a method: ");
                        stereoElement = new StereotypedMethod((MethodDeclaration)element);
                    }
                    ((StereotypedElement)stereoElement).findStereotypes();
                    this.stereotypedElements.add((StereotypedElement) stereoElement);
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
