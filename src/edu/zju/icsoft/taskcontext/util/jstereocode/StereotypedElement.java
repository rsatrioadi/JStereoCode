package edu.zju.icsoft.taskcontext.util.jstereocode;

import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public interface StereotypedElement {
    ASTNode getElement();

    List<CodeStereotype> getStereotypes();

    List<StereotypedElement> getStereoSubElements();

    List<StereotypedElement> getStereoSubFields();

    String getStereotypesName();

    void findStereotypes();

    String getName();

}
