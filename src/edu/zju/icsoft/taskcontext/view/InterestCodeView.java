package edu.zju.icsoft.taskcontext.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import edu.zju.icsoft.taskcontext.util.jstereocode.ProjectInformation;
import edu.zju.icsoft.taskcontext.util.jstereocode.StereotypeIdentifier;
import edu.zju.icsoft.taskcontext.util.jstereocode.StereotypedMethod;
import edu.zju.icsoft.taskcontext.util.jstereocode.StereotypedType;


public class InterestCodeView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.zju.icsoft.taskcontext.view.InterestCodeView";
	
	@Inject IWorkbench workbench;
	private IJavaProject project = null;
	protected HashMap<String, ProjectInformation> projectsInfo = new HashMap<String, ProjectInformation>();
	

	private ArrayList<String> stereotypeResults = new ArrayList<String>();

	
	@Override
	public void createPartControl(Composite parent) {
		selectionListener();
	}
	


	private void selectionListener() {
				
		ISelectionListener selectionListener = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
				if(arg1 instanceof IStructuredSelection) {
					if(((IStructuredSelection) arg1).getFirstElement() instanceof IMember) {
						IMember selected = (IMember) ((IStructuredSelection) arg1).getFirstElement();
						if(selected instanceof IType) {
							try {
								if (!((IType)selected).isAnonymous()) {
									captureElement(selected);
								}
							} catch (JavaModelException e) {
								e.printStackTrace();
							}
						}
						else captureElement(selected);
					}

				}
			}
		};
		ISelectionService service = (ISelectionService) getSite().getService(ISelectionService.class);
		service.addSelectionListener(selectionListener);
	}
	
    public void getJavaProjectFromRootPath() {
        try {
            IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
            // 使用 IJavaModel 的 create 方法加载 IJavaProject 对象
            IJavaProject[] javaProjects = javaModel.getJavaProjects();
            for (IJavaProject javaProject : javaProjects) {
            	analyzeProject(javaProject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void captureElement(IMember selected) {
//		IJavaProject pro = ((IMember)selected).getJavaProject();
		System.out.println("selected element: " + selected.getElementName());
		this.getJavaProjectFromRootPath();
//		System.out.println("IJavaProject" + pro);
//		if(!(pro == null)) {
////			analyzeProject(pro);
//			System.out.println("project name" + pro.getElementName());
//		}
//		analyzeProject(pro);
	}

	private void analyzeProject(IJavaProject pro) {
		String projectName = pro.getElementName();
		String modelName = pro.getProject().getLocation().toString().substring(17, pro.getProject().getLocation().toString().lastIndexOf("/"));
		project = pro;
		stereotypeResults.clear();
		IPackageFragment[] var8;
		int var7;
		try {
			var7 = (var8 = project.getPackageFragments()).length;
			for(int var6 = 0; var6 < var7; ++var6) {
		        IPackageFragment packFragment = var8[var6];
		        if (packFragment.getCompilationUnits().length == 0) {
		        	continue;
		        }
		        System.out.println("IPackageFragment -- " + packFragment.getElementName() +"--"+ packFragment.getPath());
				ICompilationUnit[] var9;
		        int var10;
				var10 = (var9 = packFragment.getCompilationUnits()).length;
				for(int var11 = 0; var11 < var10; ++var11) {
		            ICompilationUnit unit = var9[var11];
//		            System.out.println("ICompilationUnit -- " + unit.getElementName() + "--" + unit.getPath());
		            StereotypeIdentifier identifier = new StereotypeIdentifier();
		    		ProjectInformation projectInfo = this.getProjectInfo(unit.getJavaProject());
		    		identifier.setParameters(unit,projectInfo.getMethodsMean(), projectInfo.getMethodsStdDev());
		            identifier.identifyStereotypes();
		            this.updateResult(identifier.getStereotypedElements(), projectName, modelName);
		        }
		    }
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
        System.out.println("----------output file start--------------");
//        System.out.println(map0);
      //创建FileWriter对象
        FileWriter writer;
		try {
			File file = new File("D:/jstereocode/4000.txt");
			if (!file.exists()) {
				writer = new FileWriter(file);
			} else {
				writer = new FileWriter(file, true);
			}
	        //向文件中写入内容
			for (int i=0; i < stereotypeResults.size(); i++) {
				writer.write(stereotypeResults.get(i) + "\n");
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
            projectInformation = (ProjectInformation)this.projectsInfo.get(project.getElementName());
        }
        return projectInformation;
    }
	
	
	public void updateResult(List<?> elements, String projectName, String modelName) {
		for(int i = 0; i < elements.size(); i++) {
			StereotypedType st = (StereotypedType) elements.get(i);
			System.out.println("Stereotype Type----------->" + st.getQualifiedName() + "=" + st.getStereotypesName());
			this.stereotypeResults.add(modelName + " " + projectName + " " + st.getQualifiedName() + " " + st.getStereotypesName());
			for (int j = 0; j < st.getStereotypedMethods().size(); j++) {
				StereotypedMethod method = st.getStereotypedMethods().get(j);
				if (!method.getQualifiedName().startsWith(".")) {
					this.stereotypeResults.add(modelName + " " + projectName + " " + method.getQualifiedName() + " " + method.getStereotypesName());
				}
				System.out.println("Stereotype method ----------->" + method.getQualifiedName() + "=" + method.getStereotypesName());
			}
			this.updateResult(st.getStereotypedSubTypes(), projectName, modelName);
		}
	}



	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}

