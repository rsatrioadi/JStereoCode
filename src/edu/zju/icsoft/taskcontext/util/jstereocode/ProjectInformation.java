package edu.zju.icsoft.taskcontext.util.jstereocode;

import org.eclipse.jdt.core.*;

import java.util.Vector;

public class ProjectInformation {
    private int totalIUnits = 0;
    private int totalTypes = 0;
    private int totalMethods = 0;
    private final Vector<Integer> methodsCounter = new Vector<Integer>();
    private double methodsMean;
    private double methodsStdDev;
    private final IJavaProject project;

    public ProjectInformation(IJavaProject project) {
        this.project = project;
    }

    public void compute() {
        try {
            IPackageFragment[] projectPackageFragments = this.project.getPackageFragments();

            for (IPackageFragment element : projectPackageFragments) {
                ICompilationUnit[] compilationUnits = element.getCompilationUnits();

                for (ICompilationUnit junit : compilationUnits) {
                    boolean typeCounted = false;
                    IType[] allTypes  = junit.getAllTypes();

                    for (IType type : allTypes) {
                        if (type.isInterface() || type.isClass()) {
                            if (!typeCounted) {
                                ++this.totalIUnits;
                                typeCounted = true;
                            }

                            ++this.totalTypes;
                            this.methodsCounter.add(type.getMethods().length);
                            this.totalMethods += type.getMethods().length;
                        }
                    }
                }
            }

            if (!this.methodsCounter.isEmpty()) {
                this.methodsMean = this.totalMethods / (double) this.methodsCounter.size();
                double sumOfSquareDifference = 0.0D;

                for (Integer numMethods : this.methodsCounter) {
                    sumOfSquareDifference += Math.pow((double) numMethods - this.methodsMean, 2.0D);
                    this.methodsStdDev = Math.sqrt(sumOfSquareDifference / (double) this.methodsCounter.size());
                }
            } else {
                this.methodsMean = 0.0D;
                this.methodsStdDev = 0.0D;
            }
        } catch (JavaModelException var14) {
            System.err.println("Oops! An error occurred when computing project information");
        }
    }

    public int getTotalUnits() {
        return this.totalIUnits;
    }

    public int getTotalTypes() {
        return this.totalTypes;
    }

    public Vector<Integer> getMethodsCounter() {
        return this.methodsCounter;
    }

    public double getMethodsMean() {
        return this.methodsMean;
    }

    public double getMethodsStdDev() {
        return this.methodsStdDev;
    }

    public String getName() {
        return this.project.getElementName();
    }
}

