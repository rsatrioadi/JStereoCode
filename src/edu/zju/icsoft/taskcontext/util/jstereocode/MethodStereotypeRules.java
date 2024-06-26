//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.zju.icsoft.taskcontext.util.jstereocode;


import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;


public class MethodStereotypeRules {
    protected MethodAnalyzer methodAnalyzer;

    public MethodStereotypeRules() {
    }

    protected MethodStereotype checkForAbstract() {
        return !this.methodAnalyzer.hasBody() ? MethodStereotype.ABSTRACT : null;
    }

    protected MethodStereotype checkForEmpty() {
        return !this.methodAnalyzer.hasStatements() ? MethodStereotype.EMPTY : null;
    }

    protected MethodStereotype checkForMutatorStereotype() {
        if (!this.methodAnalyzer.getSetFields().isEmpty()) {
            if (!this.isVoid(this.methodAnalyzer.getReturnType())
                    && !this.isBoolean(this.methodAnalyzer.getReturnType())) {
                return MethodStereotype.NON_VOID_COMMAND;
            } else {
                return (this.methodAnalyzer.getSetFields().size() == 1)
                        ? MethodStereotype.SET
                        : MethodStereotype.COMMAND;
            }
        } else {
            return null;
        }
    }

    protected MethodStereotype checkForAccessorStereotype() {
        if (this.methodAnalyzer.getSetFields().isEmpty()) {
            if (!this.isVoid(this.methodAnalyzer.getReturnType())) {
                if (!this.methodAnalyzer.getGetFields().isEmpty()
                        && this.methodAnalyzer.getPropertyFields().isEmpty()) {
                    return MethodStereotype.GET;
                }

                if (this.isBoolean(this.methodAnalyzer.getReturnType())) {
                    if (!this.methodAnalyzer.getPropertyFields().isEmpty()) {
                        return MethodStereotype.PREDICATE;
                    }
                } else if (!this.methodAnalyzer.getPropertyFields().isEmpty()) {
                    return MethodStereotype.PROPERTY;
                }
            } else if (!this.methodAnalyzer.getVoidAccessorFields().isEmpty()) {
                return MethodStereotype.VOID_ACCESSOR;
            }
        }

        return null;
    }

    protected MethodStereotype checkForCreationalStereotype() {
        if (this.methodAnalyzer.isConstructor()) {
            return MethodStereotype.CONSTRUCTOR;
        } else if (this.methodAnalyzer.overridesClone()) {
            return MethodStereotype.COPY_CONSTRUCTOR;
        } else if (this.methodAnalyzer.overridesFinalize()) {
            return MethodStereotype.DESTRUCTOR;
        } else {
            return this.methodAnalyzer.isInstantiatedReturn() ? MethodStereotype.FACTORY : null;
        }
    }

    protected MethodStereotype checkForCollaborationalStereotype(boolean asPrimaryStereotype) {
        boolean allPrimitiveParameters = true;
        boolean allPrimitiveVariables = true;
        int returnedFieldVariables = 0;
        int modifiedObjectParameters = 0;

        for (VariableInfo variableInfo : this.methodAnalyzer.getParameters()) {
            if (!this.isPrimitive(variableInfo.getVariableBinding())) {
                allPrimitiveParameters = false;
            }

            if (variableInfo.isReturned() && !variableInfo.getAssignedFields().isEmpty()) {
                ++returnedFieldVariables;
            }

            if (variableInfo.isModified() && !this.isPrimitive(variableInfo.getVariableBinding())) {
                ++modifiedObjectParameters;
            }
        }

        for (VariableInfo variableInfo : this.methodAnalyzer.getVariables()) {
            if (!this.isPrimitive(variableInfo.getVariableBinding())) {
                allPrimitiveVariables = false;
            }

            if (variableInfo.isReturned() && !variableInfo.getAssignedFields().isEmpty()) {
                ++returnedFieldVariables;
            }
        }

        if (!asPrimaryStereotype) {
            if (((!this.methodAnalyzer.getParameters().isEmpty()
                    && (modifiedObjectParameters > 0))
                    || (!this.methodAnalyzer.getVariables().isEmpty() && !allPrimitiveVariables))
                    && ((this.methodAnalyzer.getParameters().size() + this.methodAnalyzer.getVariables().size()) > returnedFieldVariables)) {
                return MethodStereotype.COLLABORATOR;
            }
        } else if ((!this.methodAnalyzer.getParameters().isEmpty() && !allPrimitiveParameters)
                || (!this.methodAnalyzer.getVariables().isEmpty() && !allPrimitiveVariables)) {
            return MethodStereotype.COLLABORATOR;
        }

        if (!this.methodAnalyzer.getInvokedLocalMethods().isEmpty()
                && !this.methodAnalyzer.getInvokedExternalMethods().isEmpty()) {
            return MethodStereotype.COLLABORATOR;
        } else if (!this.methodAnalyzer.getInvokedExternalMethods().isEmpty()
                && this.methodAnalyzer.usesFields()) {
            return MethodStereotype.COLLABORATOR;
        } else {
            if (allPrimitiveParameters && allPrimitiveVariables) {
                if (!this.methodAnalyzer.getInvokedLocalMethods().isEmpty()
                        && this.methodAnalyzer.getInvokedExternalMethods().isEmpty()) {
                    return MethodStereotype.LOCAL_CONTROLLER;
                }

                if (!this.methodAnalyzer.getInvokedExternalMethods().isEmpty()
                        && this.methodAnalyzer.getInvokedLocalMethods().isEmpty()
                        && !this.methodAnalyzer.usesFields()
                        && this.methodAnalyzer.getGetFields().isEmpty()
                        && this.methodAnalyzer.getPropertyFields().isEmpty()
                        && this.methodAnalyzer.getSetFields().isEmpty()) {
                    return MethodStereotype.CONTROLLER;
                }
            }

            return null;
        }
    }

    private boolean isVoid(Type type) {
        if (type.isPrimitiveType()) {
            PrimitiveType primitive = (PrimitiveType) type;
            return primitive.getPrimitiveTypeCode().equals(PrimitiveType.VOID);
        }

        return false;
    }

    private boolean isBoolean(Type type) {
        if (type.isPrimitiveType()) {
            PrimitiveType primitive = (PrimitiveType) type;
            return primitive.getPrimitiveTypeCode().equals(PrimitiveType.BOOLEAN);
        }

        return false;
    }

    private boolean isPrimitive(IVariableBinding binding) {
        return binding.getType().isPrimitive();
    }
}
