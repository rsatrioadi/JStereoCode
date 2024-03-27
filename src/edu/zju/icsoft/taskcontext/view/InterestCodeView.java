package edu.zju.icsoft.taskcontext.view;

import edu.zju.icsoft.taskcontext.util.jstereocode.ProjectInformation;
import edu.zju.icsoft.taskcontext.util.jstereocode.StereotypeIdentifier;
import edu.zju.icsoft.taskcontext.util.jstereocode.StereotypedMethod;
import edu.zju.icsoft.taskcontext.util.jstereocode.StereotypedType;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InterestCodeView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "edu.zju.icsoft.taskcontext.view.InterestCodeView";

    @Inject
    IWorkbench workbench;
    private IJavaProject project = null;
    protected HashMap<String, ProjectInformation> projectsInfo = new HashMap<>();


    private final ArrayList<String> stereotypeResults = new ArrayList<>();


    @Override
    public void createPartControl(Composite parent) {
        selectionListener();
    }


    private void selectionListener() {

        ISelectionListener selectionListener = (workbenchPart, selection) -> {
            if (selection instanceof IStructuredSelection structuredSelection) {
                if (structuredSelection.getFirstElement() instanceof IMember member) {
                    if (member instanceof IType type) {
                        try {
                            if (!type.isAnonymous()) {
                                captureElement(type);
                            }
                        } catch (JavaModelException e) {
                            e.printStackTrace();
                        }
                    } else captureElement(member);
                }

            }
        };
        ISelectionService service = getSite().getService(ISelectionService.class);
        service.addSelectionListener(selectionListener);
    }

    public void getJavaProjectFromRootPath() {
        try {
            IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
            // Use the create method of IJavaModel to load the IJavaProject object
            IJavaProject[] javaProjects = javaModel.getJavaProjects();
            for (IJavaProject javaProject : javaProjects) {
                analyzeProject(javaProject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void captureElement(IMember selected) {
        System.out.printf("selected element: %s%n", selected.getElementName());
        this.getJavaProjectFromRootPath();
    }

    private void analyzeProject(IJavaProject pro) {
        String projectName = pro.getElementName();
        String projectLocation = pro.getProject().getLocation().toString();
        String modelName = projectLocation.substring(17, projectLocation.lastIndexOf("/"));
        project = pro;
        stereotypeResults.clear();
        try {
            IPackageFragment[] projectPackageFragments = project.getPackageFragments();
            for (IPackageFragment packFragment : projectPackageFragments) {
                if (packFragment.getCompilationUnits().length == 0) {
                    continue;
                }
                System.out.printf("IPackageFragment -- %s--%s%n",
                        packFragment.getElementName(),
                        packFragment.getPath());
                ICompilationUnit[] compilationUnits = packFragment.getCompilationUnits();
                for (ICompilationUnit unit : compilationUnits) {
//                    System.out.printf("ICompilationUnit -- %s--%s%n", unit.getElementName(), unit.getPath());
                    StereotypeIdentifier identifier = new StereotypeIdentifier();
                    ProjectInformation projectInfo = this.getProjectInfo(unit.getJavaProject());
                    identifier.setParameters(unit, projectInfo.getMethodsMean(), projectInfo.getMethodsStdDev());
                    identifier.identifyStereotypes();
                    this.updateResult(identifier.getStereotypedElements(), projectName, modelName);
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        System.out.println("----------output file start--------------");
//        System.out.println(map0);
        // Create FileWriter object
        FileWriter writer;
        try {
            File file = new File("D:/jstereocode/4000.txt");
            if (!file.exists()) {
                writer = new FileWriter(file);
            } else {
                writer = new FileWriter(file, true);
            }
            // Write content to file
            for (String stereotypeResult : stereotypeResults) {
                writer.write("%s\n".formatted(stereotypeResult));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("---------output file done-------------");
    }


    protected ProjectInformation getProjectInfo(IJavaProject project) {
        ProjectInformation projectInformation;
        if (!this.projectsInfo.containsKey(project.getElementName())) {
            projectInformation = new ProjectInformation(project);
            projectInformation.compute();
            this.projectsInfo.put(project.getElementName(), projectInformation);
        } else {
            projectInformation = this.projectsInfo.get(project.getElementName());
        }
        return projectInformation;
    }


    public void updateResult(List<?> elements, String projectName, String modelName) {
        for (Object element : elements) {
            StereotypedType st = (StereotypedType) element;
            System.out.printf("Stereotype Type----------->%s=%s%n", st.getQualifiedName(), st.getStereotypesName());
            this.stereotypeResults.add("%s %s %s %s".formatted(
                    modelName,
                    projectName,
                    st.getQualifiedName(),
                    st.getStereotypesName()));
            for (StereotypedMethod method : st.getStereotypedMethods()) {
                if (!method.getQualifiedName().startsWith(".")) {
                    this.stereotypeResults.add("%s %s %s %s".formatted(
                            modelName,
                            projectName,
                            method.getQualifiedName(),
                            method.getStereotypesName()));
                }
                System.out.printf("Stereotype method ----------->%s=%s%n",
                        method.getQualifiedName(),
                        method.getStereotypesName());
            }
            this.updateResult(st.getStereotypedSubTypes(), projectName, modelName);
        }
    }


    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }
}

